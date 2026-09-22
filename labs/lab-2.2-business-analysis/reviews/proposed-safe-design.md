# Lab2.2 — Cấu trúc sửa lỗi an toàn được đề xuất

Đây là phần trả lời yêu cầu cuối của prompt review P03, tương ứng
[14 review comments](security-code-review.md). Tên lớp bên dưới là thiết kế đề xuất,
chưa phải các dependency/class có sẵn trong repo. Lab2.2 yêu cầu phân tích và review;
tài liệu này không khẳng định đã triển khai hoặc chạy một Spring application hoàn chỉnh.

## 1. Ranh giới đặc tả trước khi sửa

Giữ contract WO-201: `POST /api/v1/work-orders`, request chỉ có
`title/description/priority/customer_id`; success `201` với `id/status/created_at`, status `DRAFT`.
Field thiết bị phải chờ Q01/Q02/Q07/Q12 trong [BR Analysis](../analysis/work-order-business-analysis.md), không tự thêm vào DTO/DDL.
Trước khi tạo quan hệ thiết bị, PO cần thống nhất contract, nguồn catalog và migration.

## 2. Cấu trúc source đề xuất

```text
src/main/java/.../workorder/
  WorkOrderController.java          HTTP mapping, @Valid, trả 201 và response DTO
  CreateWorkOrderRequest.java       Allowlist field, kiểu và constraint theo SPEC
  CreateWorkOrderResponse.java      UUID id, trạng thái, Instant createdAt
  WorkOrderService.java             Quyền nghiệp vụ, customer check, transaction, DRAFT
  CustomerGateway.java              Ranh giới với nguồn customer thật, không giả lập active
  WorkOrderRepository.java          SQL có bind parameters và mapping kết quả
src/main/java/.../config/
  SecurityConfig.java               Bearer authentication và policy tạo phiếu đã thống nhất
src/main/java/.../error/
  ApiExceptionHandler.java          Problem Details theo A06/A07
tests/
  controller/                      Request/response, security chain, errors
  service/                         Customer và nghiệp vụ, transaction boundary
  integration/                     Parameter binding, persistence, rollback/tài nguyên
```

Các dependency được inject qua constructor và giữ trong `private final` field (C10).
Không cần factory, event bus, endpoint phụ hay generic CRUD base class cho một luồng tạo phiếu.
DataSource đọc cấu hình từ môi trường/secret manager; không có credential literal trong Java.

## 3. Trách nhiệm và luồng xử lý

1. Security boundary xác thực bearer token rồi kiểm tra quyền tạo theo policy đã được xác nhận.
   Không tự chọn tên authority hoặc coi “có tài khoản” là “có quyền”. Lỗi `401/403` phải dùng
   cùng Problem Details, kể cả khi phát sinh trước controller.
2. Parser/DTO từ chối field lạ (`400`), dùng kiểu dữ liệu tường minh và Bean Validation.
   Title 5–255, description tối đa 2000, UUID hợp lệ, priority thuộc enum. Missing priority
   được default `MED` theo acceptance criterion; input sai không được âm thầm sửa.
3. Service xác nhận customer tồn tại/active từ nguồn đúng. Nếu cùng database, chọn transaction
   và locking/isolation thích hợp để bảo vệ check và insert. `@Transactional` một mình không
   ngăn mọi race; nếu nguồn customer là service khác, cần cơ chế nhất quán được thống nhất.
4. Repository bind từng input vào SQL tĩnh. Dùng JdbcTemplate/JPA phù hợp stack thật,
   quản lý resource/rollback đúng transaction; không trả SQL hoặc entity nội bộ ra client.
5. Database/service tạo id/thời gian, gán `DRAFT`; DTO phản hồi chỉ ba field được phép,
   `created_at` ánh xạ Instant và serialize UTC. Controller trả `201`.
6. Exception handler ánh xạ lỗi đã định nghĩa sang status A06 và 5 field Problem Details A07.
   Lỗi server có thông điệp chung; không có raw input, SQL, stack trace trong response/log.

Kiểm tra quyền trên mọi request và từ chối theo mặc định là nguyên tắc nền tảng;
policy cụ thể vẫn cần nguồn yêu cầu của dự án.
Tham chiếu: [OWASP Authorization Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Authorization_Cheat_Sheet.html).

## 4. Ví dụ thay SQL nối chuỗi bằng parameter binding

Trích đoạn minh họa trong repository, với `connection` đã thuộc transaction do tầng service
quản lý; các biến input đã validate và `customerId` là UUID. Đoạn này không tự mở/đóng connection
của transaction, nhưng đóng statement/result set. `mapCreatedWorkOrder` là mapper **cần triển khai**
theo response DTO đề xuất, không phải API sẵn có trong repo.

```java
String sql = "INSERT INTO work_orders (title, description, priority, status, customer_id) "
    + "VALUES (?, ?, ?, 'DRAFT', ?) RETURNING id, status, created_at";
try (PreparedStatement statement = connection.prepareStatement(sql)) {
  statement.setString(1, title);
  statement.setString(2, description);
  statement.setString(3, priority.name());
  statement.setObject(4, customerId);
  try (ResultSet rows = statement.executeQuery()) {
    if (!rows.next()) {
      throw new SQLException("Work order insert did not return a row");
    }
    return mapCreatedWorkOrder(rows);
  }
}
```

`PreparedStatement`, `ResultSet`, `SQLException` là các kiểu JDK `java.sql`.
Giá trị đầu vào được bind, không nối vào SQL; câu SQL dùng cột đã có trong SPEC.
Prepared statements tách dữ liệu khỏi cú pháp truy vấn; vẫn cần validation nghiệp vụ.
Tham chiếu: [OWASP SQL Injection Prevention](https://cheatsheetseries.owasp.org/cheatsheets/SQL_Injection_Prevention_Cheat_Sheet.html).

## 5. Ánh xạ lỗi và response

| Tình huống | Xử lý ở đâu | Kết quả |
| --- | --- | --- |
| JSON hỏng/sai kiểu, field lạ như `status` hoặc `equipment_code` chưa được duyệt | Parser/DTO + error handler | `400`, Problem Details. |
| Không xác thực / không có quyền | Security boundary | `401/403`, Problem Details; không gọi repository. |
| Field sai constraint, enum hoặc UUID sai định dạng | Validation + error mapping | `422` theo SPEC/RULES, không dùng mặc định `400` của framework nếu không phù hợp. |
| Customer không tồn tại / inactive | Service | `404/409`, không insert. |
| Media type không hỗ trợ | Web boundary | `415`, Problem Details. |
| Database/lỗi nội bộ bất ngờ | Error handler | `500` an toàn; không trả `200`, SQL hay exception message. |
| Tạo thành công | Controller + response DTO | `201 application/json`, đúng `id/status/created_at`. |

Các lỗi UUID/enum bị framework phân loại như deserialize error cần được nhận diện riêng
để giữ `422` đã quy định. Không bắt mọi deserialize error thành `422`: JSON sai cấu trúc vẫn `400`.

## 6. Test plan cho bản sửa

Đây là **các test cần triển khai khi viết application**, không phải test đã chạy trong Lab2.2.

| Nhóm | Ca kiểm tra và kết quả mong đợi | Review được bao phủ |
| --- | --- | --- |
| Contract | Đúng route; `201`; chính xác ba khóa response; timestamp UTC; status luôn DRAFT. | RC01, RC02, RC13 |
| Field thiết bị | Hiện tại từ chối field ngoài SPEC; khi PO duyệt thay đổi, kiểm tra persistence và liên kết thiết bị bằng đọc lại. | RC03 |
| SQL | Lưu chuỗi chứa dấu nháy và input kiểm thử injection như dữ liệu; không thay đổi cấu trúc truy vấn. | RC04 |
| Quyền | Token thiếu/sai/hết hạn → 401; không quyền → 403; không insert; caller hợp lệ đi được happy path. | RC05 |
| Riêng tư | Marker giả trong input không xuất hiện ở response lỗi/log; không có SQL/stack trace. | RC06 |
| Cấu hình | Không fallback credential; trường hợp cấu hình thiếu thất bại có kiểm soát. | RC07 |
| Validation | Boundary 4/5/255/256, 2000/2001; thiếu UUID, UUID sai; enum sai; priority bỏ → MED; field lạ. | RC08 |
| Customer | Tồn tại+active → tạo; thiếu → 404; inactive → 409; không insert khi lỗi; kiểm tra race theo thiết kế đã chốt. | RC09 |
| Lỗi HTTP | Từng nhánh A06; đúng media type và năm field Problem Details; status body trùng HTTP. | RC10 |
| Tách trách nhiệm | Test service bằng test double; test repository và web/security riêng, không mock toàn bộ hành vi cần kiểm chứng. | RC11 |
| Tài nguyên | Success, SQL error, zero-row; connection trả về pool; transaction rollback khi phù hợp. | RC12 |
| Review/Javadoc | Kiểu DTO/mapping biên dịch, Javadoc mô tả đúng input/output và lỗi; không tuyên bố hành vi ngoài code. | RC13, RC14 |

Thực hiện các quyết định đặc tả trước, sửa SQL/quyền/rò rỉ dữ liệu và validation trong cùng
đợt triển khai, rồi chạy test tương ứng. Không đưa fixture `scratch/lab22/unsafe` vào application.
