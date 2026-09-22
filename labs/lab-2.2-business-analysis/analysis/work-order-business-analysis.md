# Phân tích yêu cầu Work Order của POSCO MCI

Tài liệu này là kết quả thực hiện prompt BA P02 trong [executed-prompts.md](../prompts/executed-prompts.md).
Mục tiêu là phân rã tính năng đăng ký phiếu công việc theo thiết bị, nhận diện dữ liệu cần có
và câu hỏi cần Product Owner làm rõ trước khi đổi hợp đồng API.

## 1. Yêu cầu thô và nguồn thông tin

> Tạo tính năng quản lý thiết bị công trường POSCO MCI cho phép thợ kỹ thuật đăng ký phiếu công việc gồm mã thiết bị, mô tả và mức độ ưu tiên

Đây là yêu cầu ví dụ ở **bước 2 của phần chính Lab2.2**, được dùng để thay placeholder
trong prompt mẫu. Không có câu trả lời bổ sung của Product Owner trong repo.

| Nguồn | Ý nghĩa |
| --- | --- |
| BR | Đoạn yêu cầu thô ở trên; xác nhận tác nhân thợ kỹ thuật, hành vi đăng ký phiếu, mã thiết bị, mô tả và ưu tiên. |
| SPEC | [work-order-decomposition.md](../../../docs/specs/work-order-decomposition.md), mục 3–7; hợp đồng tạo Work Order hiện hành của WO-201. |
| RULES | [API rules](../../../docs/rules/api-rules.md), [coding rules](../../../docs/rules/coding-rules.md), [security rules](../../../docs/rules/security-rules.md). |
| Đề xuất | Phương án phân tích để PO/nhóm kỹ thuật xem xét; chưa phải trường JSON, schema hoặc tính năng được duyệt. |

**Điểm cần chốt:** BR tổ chức phiếu theo mã thiết bị, trong khi SPEC yêu cầu `title` và
`customer_id`, chưa có field thiết bị. Không tự coi mã thiết bị là customer ID, tự sinh title,
hoặc thêm `equipment_code` vào request hiện hành. Tài liệu này không sửa SPEC/API rules.

## 2. Phạm vi nghiệp vụ

| ID | Nội dung đã nêu trong BR | Điều chưa thể suy ra |
| --- | --- | --- |
| BR01 | Thợ kỹ thuật là người đăng ký phiếu. | Tên role/claim, phạm vi công trường và quyền của mỗi người. |
| BR02 | Có hành vi đăng ký phiếu công việc. | Quy trình duyệt, giao việc, sửa/xóa phiếu hoặc chuyển trạng thái. |
| BR03 | Phiếu có mã thiết bị. | Định dạng mã, tính duy nhất, catalog nguồn và quan hệ với khách hàng. |
| BR04 | Phiếu có mô tả. | Mô tả có bắt buộc không và khác `title` như thế nào. |
| BR05 | Phiếu có mức độ ưu tiên. | Có dùng enum/default trong SPEC hay cần phân loại khác. |

Phân tích tập trung vào đăng ký phiếu. Cụm “quản lý thiết bị” chưa đủ để mở rộng sang CRUD
thiết bị, tồn kho, lịch bảo trì, phân công, thông báo hoặc báo cáo; các phần này ngoài phạm vi
hiện tại cho đến khi PO yêu cầu. Không suy ra màn hình danh sách hay API tra cứu mới đã được duyệt.

## 3. Entities và thuộc tính cơ bản

| Entity / tác nhân | Thuộc tính được nhận diện | Nguồn và mức độ xác nhận |
| --- | --- | --- |
| WorkOrder — Phiếu công việc | BR: mã thiết bị, mô tả, mức độ ưu tiên. SPEC: `id`, `title`, `description`, `priority`, `status`, `customer_id`, `created_at`. | Phiếu và ba thông tin BR đã rõ; việc thêm quan hệ thiết bị vào model SPEC chưa chốt. `id/status/created_at` do server quản lý theo SPEC. |
| Equipment — Thiết bị | Mã thiết bị. Đề xuất cần xác định định danh ổn định, công trường sở hữu và tình trạng được phép tạo phiếu. | Mã thiết bị được BR nêu; tên, trạng thái, ID nội bộ, công trường và nguồn catalog chưa được đặc tả. Không tự tạo bảng/field tương ứng. |
| Technician — Thợ kỹ thuật | Danh tính người thao tác từ cơ chế đăng nhập; quyền đăng ký phiếu cần xác nhận. | Tác nhân được BR nêu; danh tính xác thực kế thừa yêu cầu bearer token của SPEC. Chưa có schema người dùng hay field `created_by` được duyệt. |
| Customer — Khách hàng | `customer_id` dạng UUID, thông tin tồn tại/active do nguồn khách hàng cung cấp. | Có trong SPEC, không xuất hiện trong BR. Không tự tạo bảng customer hay giả định thiết bị thuộc một customer. |

Quan hệ hiện được SPEC xác nhận: một WorkOrder tham chiếu một Customer tồn tại và active.
Quan hệ WorkOrder–Equipment là nhu cầu BR cần làm rõ cách biểu diễn; tính duy nhất của mã
thiết bị và một phiếu có thể liên quan nhiều thiết bị hay không chưa được nêu.
Thợ kỹ thuật tạo phiếu là hành vi được biết; lưu quan hệ người tạo là đề xuất cần PO xác nhận.
“Công trường POSCO MCI” hiện là bối cảnh, chưa đủ cơ sở để tạo một entity/site table mới.

## 4. Open Questions và rủi ro nghiệp vụ

Tất cả câu hỏi dưới đây có trạng thái **Chưa chốt**. Đây là đầu ra phân tích cần bàn với PO,
không phải thiếu sót khiến bài lab chưa hoàn thành.

| ID | Câu hỏi cần làm rõ | Rủi ro nếu tự suy đoán | Người cần trả lời |
| --- | --- | --- | --- |
| Q01 | Mã thiết bị lấy từ nguồn nào, có phân biệt hoa/thường, độ dài/định dạng và duy nhất toàn hệ thống hay theo công trường? | Trùng hoặc gắn nhầm thiết bị; UI/API validation khác nhau. | PO và người quản lý thiết bị |
| Q02 | Thiết bị có quan hệ với customer không? Người dùng chọn `customer_id` hay hệ thống xác định từ thiết bị? | Tạo phiếu sai khách hàng hoặc mất điều kiện active của SPEC. | PO và chủ dữ liệu khách hàng |
| Q03 | Form theo BR có `title` riêng không? Nếu muốn sinh từ mô tả/thiết bị thì quy tắc nghiệp vụ được duyệt là gì? | Bỏ trường bắt buộc hoặc tự sinh nội dung sai ý người dùng. | PO |
| Q04 | `description` là bắt buộc theo BR hay vẫn tùy chọn, tối đa 2000 ký tự như SPEC? | UI chặn không đúng hoặc lưu phiếu thiếu thông tin làm việc. | PO |
| Q05 | Dùng `LOW/MED/HIGH/CRITICAL`, mặc định `MED` như SPEC hay bộ ưu tiên khác? | Gửi enum không hợp lệ, ưu tiên công việc bị hiểu sai. | PO |
| Q06 | Thợ kỹ thuật nào được tạo phiếu, trong công trường/phạm vi thiết bị nào? Quyền này ánh xạ vào claim/role nào? | Người có tài khoản nhưng không có quyền vẫn tạo được phiếu. | PO và người quản trị truy cập |
| Q07 | Một phiếu gắn một hay nhiều thiết bị? Thiết bị ngừng hoạt động/không tồn tại có được đăng ký phiếu không? | Quan hệ dữ liệu sai hoặc từ chối nhầm nhu cầu sửa thiết bị đang hỏng. | PO và người quản lý thiết bị |
| Q08 | BR giữ trạng thái khởi tạo `DRAFT` của SPEC hay cần bước nộp/duyệt riêng? | Bỏ qua phê duyệt hoặc đưa phiếu vào trạng thái không có trong quy trình. | PO |
| Q09 | Có cho phép nhiều phiếu đang mở cho cùng thiết bị/nội dung? Cần xử lý bấm gửi hai lần như thế nào? | Phiếu trùng làm lặp công việc; tự thêm idempotency/dedup không đúng kỳ vọng. | PO |
| Q10 | Có cần lưu người tạo, lịch sử/audit hoặc thời hạn lưu dữ liệu? Những dữ liệu nào có thể chứa PII? | Thiếu truy vết hoặc thu thập dữ liệu không cần thiết. | PO và người phụ trách dữ liệu |
| Q11 | Khi catalog thiết bị/khách hàng không sẵn sàng hoặc đổi trạng thái lúc tạo phiếu, nghiệp vụ muốn từ chối hay thử lại? | Tạo phiếu không hợp lệ hoặc lặp insert sau timeout. | PO và nhóm tích hợp |
| Q12 | Phần “quản lý thiết bị” chỉ là ngữ cảnh tạo phiếu hay bao gồm chức năng thiết bị khác? Có phải version API mới không? | Mở rộng phạm vi và phá client WO-201 hiện hành. | PO và Tech Lead |

Q01–Q08 và Q12 cần được chốt trước khi bổ sung field thiết bị vào API. Q09–Q11 cần quyết định
để thiết kế vận hành và test phù hợp; hiện không tự thêm nghiệp vụ xử lý tương ứng.

## 5. Bảng phân rã UI / Data / API

| Chức năng / dữ liệu | UI | Data | API | Nguồn / điểm cần chốt |
| --- | --- | --- | --- | --- |
| Đăng ký phiếu | Form cho thợ kỹ thuật nhập/chọn thông tin và gửi. | WorkOrder được tạo theo contract đã chốt. | Giữ `POST /api/v1/work-orders` của SPEC; mở rộng BR phải được duyệt trước. | BR01–BR02, SPEC; Q06, Q12 |
| Mã thiết bị | Đề xuất ô chọn/nhập mã theo catalog; cách tìm kiếm chưa chốt. | Chưa quyết định lưu code hay ID tham chiếu; không thay `customer_id` bằng equipment code. | Chưa thêm field thiết bị hoặc endpoint tra cứu vào contract hiện hành. | BR03; Q01, Q02, Q07 |
| Title | SPEC yêu cầu nhập 5–255 ký tự; BR chưa nêu field này. | `title VARCHAR(255) NOT NULL` theo SPEC. | `title` bắt buộc theo SPEC; không tự lấy từ mô tả. | SPEC; Q03 |
| Mô tả | Nhập mô tả; theo SPEC hiện tại là tùy chọn, tối đa 2000 ký tự. | `description TEXT`, giới hạn 2000 ở API theo SPEC. | `description` hiện có; cần chốt nếu BR muốn bắt buộc. | BR04, SPEC; Q04 |
| Ưu tiên | Đề xuất giữ dropdown `LOW/MED/HIGH/CRITICAL`, chọn sẵn `MED` theo SPEC. | `priority`, default `MED`, constraint enum trong SPEC. | Bỏ trường thì `MED`; enum sai bị từ chối, không âm thầm thay bằng default. | BR05, SPEC mục 7, RULES; Q05 |
| Khách hàng | Selector khách hàng theo SPEC; vai trò trong form thiết bị chờ làm rõ. | `customer_id UUID NOT NULL`; phải kiểm tra tồn tại/active. | `customer_id` vẫn bắt buộc; server kiểm tra trước insert. | SPEC; Q02 |
| Người thao tác và quyền | Hiển thị thao tác khi phù hợp; UI không thay thế authorization. | Nguồn danh tính xác thực; field lưu người tạo chưa được duyệt. | Bearer token hợp lệ và quyền tạo phiếu; role/phạm vi cụ thể chưa chốt. | BR01, SPEC, S04; Q06, Q10 |
| Thành công | Báo tạo thành công sau response; không tự kết luận phiếu đã được duyệt. | Sinh `id`, `created_at`, trạng thái `DRAFT` theo SPEC. | `201`, chỉ `id/status/created_at`, timestamp UTC. | SPEC, A05; Q08 |
| Lỗi và trạng thái khi gửi | Giữ dữ liệu form, chỉ lỗi tại field; phản hồi lỗi an toàn. | Không ghi WorkOrder khi validation/customer check thất bại. | Problem Details; `400/401/403/404/409/415/422/500` theo API rules. | SPEC, RULES; Q09, Q11 |

Không sinh DDL hoặc request schema thiết bị mới ở bước phân tích này. Bảng trên giữ rõ
phần contract hiện hành và phần còn cần quyết định, để bước sinh code không tự lấp chỗ trống.

## 6. Luồng nghiệp vụ và kiểm tra chấp nhận đề xuất

Luồng hiện hành: xác thực → kiểm tra quyền tạo → parse/validate request theo SPEC →
kiểm tra customer tồn tại/active → insert có bảo vệ transaction phù hợp → trả `201`.
Nếu PO duyệt liên kết thiết bị, thêm kiểm tra định danh/phạm vi/trạng thái thiết bị trước
insert theo các quyết định Q01/Q06/Q07, không coi “thiết bị hỏng” đồng nghĩa bị cấm tạo phiếu.

| ID | Given / When / Then | Trạng thái |
| --- | --- | --- |
| AC01 | Given token/quyền hợp lệ, customer active và payload đúng SPEC; When tạo phiếu; Then `201`, trạng thái `DRAFT`, đúng ba khóa response. | Kế thừa SPEC; có thể dùng làm test contract hiện hành. |
| AC02 | Given thiếu title/customer hoặc sai độ dài/enum/UUID; When gửi; Then `422` Problem Details, không insert. | Kế thừa SPEC/RULES. |
| AC03 | Given priority bị bỏ; When payload còn lại hợp lệ; Then dùng `MED`; giá trị enum sai phải bị từ chối. | Kế thừa acceptance criterion mục 7 và A03. |
| AC04 | Given token thiếu/sai hoặc caller không có quyền; When tạo phiếu; Then `401/403`, không chạy insert. | Kế thừa SPEC/RULES; mapping quyền chờ Q06. |
| AC05 | Given customer không tồn tại/inactive; When tạo; Then `404/409`, không insert. | Kế thừa SPEC. |
| AC06 | Given thiết bị và quyền/phạm vi hợp lệ theo quyết định PO; When đăng ký; Then phiếu gắn đúng thiết bị bằng quan hệ đã được duyệt. | Đề xuất BR; chờ Q01/Q02/Q06/Q07. |
| AC07 | Given mô tả thiếu hoặc thiết bị không hợp lệ theo contract BR được duyệt; When gửi; Then lỗi có thể hành động và không tạo phiếu. | Đề xuất BR; chờ Q01/Q04/Q07, chưa chốt status/constraint mới. |
| AC08 | Given gửi lặp hoặc nguồn dữ liệu ngoài gặp lỗi; When tạo; Then xử lý đúng chính sách PO chọn, không tự suy đoán dedup/retry. | Đề xuất BR; chờ Q09/Q11. |

Các AC ở đây là tiêu chí cho bước triển khai sau, không phải kết quả test API đã chạy.
Đầu ra Lab2.2 được đối chiếu trong [done-criteria.md](../results/done-criteria.md).
