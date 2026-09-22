# Lab2.2 — AI-Assisted Code Review

Kết quả thực hiện prompt P03 trong [executed-prompts.md](../prompts/executed-prompts.md): **14 nhận xét**
theo đúng thứ tự Spec delta → Security → Validation & Testing → Complexity → Style.
P1 là lỗi cần xử lý trước khi dùng mã; P2 là cải thiện cần làm trong đợt sửa.
Thứ tự nhóm tuân theo đề, không có nghĩa lỗi SQL Injection ít nghiêm trọng hơn mọi Spec delta.

**Mã được review:** [WorkOrderController.java](../../../scratch/lab22/unsafe/WorkOrderController.java),
55 dòng. Đây là fixture có lỗi được Codex tạo riêng; không phải code lấy từ phụ lục hoặc
application production. Review tĩnh dựa trên [SPEC](../../../docs/specs/work-order-decomposition.md), Rules Pack
và [BR Analysis](../analysis/work-order-business-analysis.md). Không chạy fixture với database.

SHA-256 của file tại thời điểm review:
`220590b644e6383f62d776d69efff6d02a91fc6b364060d6b826625bbf8eedb4`.

## 1. Spec delta

### RC01 — P1 — Endpoint và success contract lệch SPEC

- **Vị trí:** dòng 17, 42–49, đặc biệt `@RequestMapping("/api/workorders")` và `ResponseEntity.ok(response)`.
- **Nhận xét:** Client đúng SPEC gọi `/api/v1/work-orders` sẽ không vào route này. Nếu gọi route mẫu,
  response trả `200` và thêm `equipment_code`, `customer_id`, `debug_sql`, trái A02/A05 và SPEC mục 5.
- **Hướng sửa:** dùng đúng route và `201 Created`; response DTO chỉ có `id`, `status`, `created_at`,
  thời gian UTC. Không tự mở rộng success schema để khớp payload nội bộ.
- **Kiểm chứng:** contract test route chính thức, status `201`, tập khóa JSON đúng ba khóa;
  parse `created_at` như UTC instant. Không còn `debug_sql` hoặc request echo.

### RC02 — P1 — Cho client điều khiển trạng thái và mặc định sai vòng đời

- **Vị trí:** dòng 31, 37, 44: `request.getOrDefault("status", "APPROVED")` được dùng cho insert và response.
- **Nhận xét:** Không gửi status thì phiếu thành `APPROVED`; gửi status thì client tự chọn trạng thái.
  SPEC yêu cầu mọi phiếu mới là `DRAFT`, status do server quản lý. Điều này bỏ qua vòng đời nghiệp vụ.
- **Hướng sửa:** bỏ status khỏi request DTO, từ chối trường lạ theo A03, gán `DRAFT` phía server/database.
  Không tự bổ sung bước duyệt dựa trên BR chưa chốt (Q08).
- **Kiểm chứng:** payload hợp lệ luôn lưu/trả `DRAFT`; request thêm `status` trả `400` và không insert.

### RC03 — P1 — Echo mã thiết bị tạo cảm giác đã lưu quan hệ nhưng dữ liệu bị mất

- **Vị trí:** dòng 36–38 và 46: insert không có quan hệ thiết bị, response lại echo `equipment_code`.
- **Nhận xét:** Với BR03, người gửi thấy mã thiết bị trong kết quả nhưng phiếu lưu không gắn thiết bị.
  Mặt khác, schema SPEC chưa cho phép field này. Không thể coi đây là triển khai BR hoàn chỉnh
  hoặc tự thêm cột để xử lý khoảng trống đặc tả.
- **Hướng sửa:** chốt Q01/Q02/Q07/Q12 với PO rồi cập nhật đặc tả và migration tương ứng. Trong contract
  hiện hành, từ chối field chưa được duyệt và bỏ echo; giữ rõ việc hỗ trợ thiết bị còn chờ quyết định.
- **Kiểm chứng:** hiện tại field lạ trả `400`. Sau khi có contract mới, test đọc lại dữ liệu chứng minh
  phiếu gắn đúng thiết bị, không chỉ kiểm tra response có cùng chuỗi.

## 2. Security vulnerabilities

### RC04 — P1 — Nối trực tiếp input vào câu SQL

- **Vị trí:** dòng 35–39, đặc biệt `"VALUES ('" + title` và các biến nối phía sau.
- **Nhận xét:** Các trường từ request đi thẳng vào cú pháp SQL. Chuỗi bình thường có dấu nháy như
  `Pump O'Brien` có thể làm hỏng câu lệnh; input được điều khiển có thể làm thay đổi ý nghĩa truy vấn.
  Validation nghiệp vụ không thay thế parameter binding (S06, Core Rule 3).
- **Hướng sửa:** dùng PreparedStatement/JdbcTemplate/JPA có tham số bind cho mọi giá trị đầu vào;
  không tự escape rồi tiếp tục nối chuỗi. Tên bảng/cột ở đây là SQL tĩnh.
- **Kiểm chứng:** integration test lưu nguyên văn dấu nháy và chuỗi thử injection như dữ liệu;
  xác nhận không phát sinh truy vấn/hành vi ngoài một insert được phép.

### RC05 — P1 — Luồng mẫu chưa có điểm xác thực hoặc kiểm tra quyền tạo

- **Vị trí:** dòng 24–25 và luồng đi thẳng tới dòng 34–39.
- **Nhận xét:** Fixture không nhận/kiểm tra principal, không có guard quyền hoặc security config đi kèm.
  Nếu nối vào route công khai, mọi caller tới được handler có thể insert. Không suy ra rằng mọi
  Spring controller thiếu annotation đều công khai: cấu hình filter bên ngoài có thể bảo vệ,
  nhưng repo này chưa có application/security chain để chứng minh điều đó.
- **Hướng sửa:** thiết lập xác thực bearer và policy tạo phiếu tại security boundary/service;
  role/claim và phạm vi thiết bị/công trường phải theo Q06, không tự nghĩ ra `ROLE_TECHNICIAN`.
- **Kiểm chứng:** gọi qua HTTP/security chain với token thiếu, sai, hết hạn và caller thiếu quyền;
  nhận `401/403` và repository không được gọi. Thêm ca được phép để tránh cấu hình chặn tất cả.

### RC06 — P1 — Lộ request và thông tin nội bộ qua log/response

- **Vị trí:** dòng 26, 48, 51–52: in toàn bộ request, trả SQL và message ngoại lệ.
- **Nhận xét:** Mô tả có thể chứa thông tin cá nhân hoặc dữ liệu công trường; SQL/exception có thể
  lộ schema và giá trị gửi lên. Ngay cả request thành công cũng trả `debug_sql` chứa input.
- **Hướng sửa:** bỏ request dump, SQL echo và `printStackTrace`; dùng thông điệp Problem Details an toàn.
  Nếu cần log, chỉ ghi mã sự kiện/trạng thái được phép, không log raw input hoặc exception message
  chứa dữ liệu. Quy định C09/S05 áp dụng cả nhánh lỗi.
- **Kiểm chứng:** dùng marker dữ liệu tổng hợp trong request, assert marker không có trong log/response lỗi;
  kiểm tra body không có SQL, stack trace, tên máy hay credentials.

### RC07 — P2 — Mẫu cấu hình credential nằm trực tiếp trong source

- **Vị trí:** dòng 19–22 và 34: hằng cấu hình database/password được đưa thẳng vào DriverManager.
- **Nhận xét:** `TRAINING_ONLY_NOT_A_REAL_PASSWORD` là marker giả, không phải secret thật bị lộ.
  Tuy vậy, cách thiết kế sẽ khuyến khích thay marker bằng password trong source, trái Core Rule 1/S01.
  Không có căn cứ để yêu cầu rotate một credential thật trong fixture này.
- **Hướng sửa:** dùng DataSource cấu hình từ biến môi trường/secret manager, không có fallback password;
  tách cấu hình môi trường khỏi controller và không commit secret.
- **Kiểm chứng:** cấu hình thiếu credential phải bị phát hiện an toàn khi khởi động/tạo kết nối;
  review source và chạy scanner phù hợp khi có application thật.

## 3. Validation & Testing

### RC08 — P1 — Thiếu DTO/validation, default customer che giấu request sai

- **Vị trí:** dòng 25, 27–30: `Map<String, String>`, không `@Valid`, UUID toàn 0 khi thiếu customer.
- **Nhận xét:** Không kiểm tra title 5–255, description ≤2000, enum hoặc UUID. Title thiếu có thể
  bị nối thành chuỗi `"null"`; UUID giả làm mất khả năng báo lỗi thiếu trường. Key lạ được nhận tự do.
- **Hướng sửa:** request DTO có field allowlist và Bean Validation; kiểm tra enum/UUID đúng cách,
  bỏ default customer. Chỉ priority bị bỏ mới dùng `MED` theo SPEC mục 7; không thay enum sai bằng default.
- **Kiểm chứng:** test title 4/5/255/256, description 2000/2001, thiếu/malformed UUID, priority bị bỏ/sai,
  JSON sai kiểu, field lạ. Dữ liệu vi phạm contract trả `422`; lỗi cấu trúc/field lạ trả `400` theo A06.

### RC09 — P1 — Không xác nhận customer tồn tại/active trước khi ghi

- **Vị trí:** dòng 30 và 34–39: customer ID từ client được insert ngay.
- **Nhận xét:** UUID đúng cú pháp vẫn có thể trỏ tới customer không tồn tại hoặc inactive.
  DDL của SPEC chưa có FK vì bảng customer chưa được cung cấp, nên không thể trông chờ database
  tự bảo vệ nghiệp vụ này. Đây là kiểm tra nghiệp vụ riêng với validation hình thức ở RC08.
- **Hướng sửa:** service tra nguồn customer có thẩm quyền, trả `404/409` theo SPEC, bảo vệ check và insert
  bằng transaction/locking phù hợp. Nếu nguồn ngoài database, phải chốt cách đảm bảo nhất quán.
- **Kiểm chứng:** test customer hợp lệ/không tồn tại/inactive, assert không insert ở hai ca lỗi;
  thêm ca đổi trạng thái đồng thời theo cơ chế nhất quán đã được chọn.

### RC10 — P1 — Catch mọi lỗi thành HTTP 200 làm sai kết quả và khó bắt regression

- **Vị trí:** dòng 50–52: `catch (Exception exception)` trả `ResponseEntity.ok(...)` khi lỗi.
- **Nhận xét:** Client/monitor dùng status sẽ coi thao tác thất bại là thành công. Body không phải
  Problem Details, không phân biệt input sai với lỗi server. Tests Lab2.1 chỉ kiểm tra phép cộng;
  repo chưa có test endpoint này, nên các sai lệch trên chưa được test tự động phát hiện.
- **Hướng sửa:** bỏ catch-all thành công; ánh xạ ngoại lệ tập trung và lỗi security về A06/A07,
  thêm contract/integration tests cho từng nhánh được đặc tả. Không khẳng định API tests đã chạy.
- **Kiểm chứng:** lỗi validation `422`, customer `404/409`, auth `401/403`, lỗi server `500` an toàn;
  mọi body lỗi dùng `application/problem+json`, status trong body trùng HTTP status.

## 4. Complexity

### RC11 — P2 — Controller gom HTTP, nghiệp vụ, kết nối và SQL

- **Vị trí:** dòng 25–52, nhất là thao tác DriverManager/Statement trực tiếp trong handler.
- **Nhận xét:** Việc thêm kiểm tra customer, quyền và rollback sẽ khiến method khó kiểm thử độc lập;
  hiện muốn kiểm thử nhánh HTTP cũng phải tác động JDBC trực tiếp.
- **Hướng sửa:** tách vừa đủ Controller → Service → Repository; controller chỉ chuyển đổi request/response,
  service điều phối nghiệp vụ/transaction, repository bind SQL. Không thêm factory hoặc tầng trừu tượng không cần.
- **Kiểm chứng:** unit test service với repository/customer gateway giả; integration test repository riêng;
  controller test tập trung status/DTO/media type. Xem cấu trúc đề xuất trong tài liệu sửa lỗi.

### RC12 — P1 — Tài nguyên JDBC không được đóng trên cả nhánh thành công và lỗi

- **Vị trí:** dòng 34–40 và 49–52: Connection, Statement, ResultSet không có close/finally.
- **Nhận xét:** Mỗi request có thể giữ kết nối và tài nguyên server; request lặp hoặc lỗi SQL làm
  cạn tài nguyên. `rows.next()` cũng không kiểm tra có bản ghi trước khi đọc dữ liệu.
- **Hướng sửa:** để JdbcTemplate/DataSource quản lý tài nguyên trong transaction, hoặc dùng
  try-with-resources nếu làm JDBC thủ công; xử lý trường hợp không trả bản ghi bằng lỗi cụ thể.
- **Kiểm chứng:** chạy nhiều lần cả success/error với pool nhỏ, kiểm tra connection được trả lại;
  mô phỏng không có row để xác nhận không đọc ResultSet ở trạng thái sai.

## 5. Coding style / Best practices

### RC13 — P2 — Response Map làm mất hợp đồng kiểu và khả năng kiểm tra lúc biên dịch

- **Vị trí:** dòng 25 và 42–48: `Map<String, Object>` cùng các khóa chuỗi rải rác.
- **Nhận xét:** Typo field hoặc thay đổi kiểu của `id/created_at` chỉ phát hiện lúc runtime;
  `rows.getString("created_at")` cũng không đảm bảo serialize ISO 8601 UTC như SPEC.
  Đây là điểm thiết kế kiểu dữ liệu; tập field sai đã được chỉ ra riêng ở RC01.
- **Hướng sửa:** response DTO/record tường minh với UUID, trạng thái và Instant;
  repository mapper chuyển kiểu một chỗ, serializer xuất đúng tên JSON.
- **Kiểm chứng:** compile DTO/mapper và serialization test đúng tên, kiểu, timestamp;
  review không dùng dynamic Map cho contract đã biết.

### RC14 — P2 — Public handler thiếu tài liệu hợp đồng và ngữ cảnh lỗi

- **Vị trí:** dòng 24–25: method `create` không có Javadoc; comment class chỉ cảnh báo fixture.
- **Nhận xét:** Người đọc method không thấy precondition, trạng thái tạo mới hoặc nhánh lỗi dự kiến,
  trái C11. Tài liệu ngắn giúp review đối chiếu contract mà không phải suy luận từ SQL và catch block.
- **Hướng sửa:** ở bản sửa, bổ sung Javadoc cho public API của controller/service về nhiệm vụ,
  input/output, precondition và ngoại lệ; dẫn tới SPEC, không sao chép toàn bộ đặc tả vào comment.
- **Kiểm chứng:** review public method có `@param`, `@return` và mô tả lỗi phù hợp implementation;
  không ghi thêm hành vi chưa tồn tại.

## Kết luận review và hướng sửa

14 nhận xét đều có vị trí, tác động, hướng sửa và cách kiểm chứng. Fixture cố ý giữ lỗi
để bảo toàn đầu vào review, không được đánh dấu đã sửa hay đã vượt API/security tests.
[proposed-safe-design.md](proposed-safe-design.md) trả lời phần cuối của prompt: cấu trúc mã an toàn
được đề xuất và test plan tương ứng cho toàn bộ RC01–RC14.
