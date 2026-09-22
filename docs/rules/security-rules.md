# Security Rules

Áp dụng cùng [coding-rules.md](coding-rules.md) và [api-rules.md](api-rules.md).
Quy tắc liên quan HTTP/database chỉ áp dụng khi task triển khai các thành phần đó.

## S01 — Không hardcode secret

Không đặt password, API key, token hay connection string có credentials trong code, test,
prompt hoặc tài liệu. Ứng dụng đọc từ biến môi trường/secret manager và báo lỗi nếu thiếu;
không có secret dự phòng trong source. Dùng placeholder cho ví dụ cấu hình.

- **Đúng:** `String apiKey = System.getenv("WORK_ORDER_API_KEY");`
- **Sai:** gán trực tiếp giá trị API key thật vào biến hoặc commit file `.env`.

## S02 — Validation ở ranh giới

Luôn kiểm tra null, độ dài, kiểu, định dạng và tập giá trị được cho phép trước khi xử lý.
Không tin dữ liệu từ client hoặc output AI. Dữ liệu sai phải bị từ chối rõ ràng.
Với scratch `sum`, hợp đồng đầu vào nằm trong [draft-generation.md](../../labs/lab-2.1-context-engineering/prompts/draft-generation.md).

- **Đúng:** chỉ chấp nhận ký tự ASCII `0`–`9` cho phép cộng số nguyên không âm.
- **Sai:** xóa ký tự không hợp lệ, trim hoặc đổi null thành 0 để tiếp tục tính.

## S03 — Giới hạn tài nguyên

Xác định giới hạn độ dài trước khi cấp phát buffer hoặc thực hiện tác vụ tốn chi phí.
Không dùng regex có backtracking phức tạp khi vòng lặp đơn giản đủ dùng.
Scratch giới hạn mỗi toán hạng ở 10.000 chữ số, kiểm tra trước khi cộng;
đây là lựa chọn của bản nháp, không phải giới hạn Work Order.

## S04 — Authentication và authorization

API xác thực bearer token và kiểm tra quyền theo hợp đồng trước nghiệp vụ.
Không coi có header là đã xác thực, không bỏ kiểm tra chữ ký/hạn token,
không chấp nhận token mẫu làm đường tắt. Không thêm cơ chế phân quyền chưa được thống nhất.

## S05 — Không lộ PII qua log và lỗi

Không log hoặc echo token, body, email, số điện thoại, dữ liệu khách hàng hay giá trị đầu vào.
Thông báo lỗi chỉ nêu tên trường/constraint; không đưa stack trace, SQL hay đường dẫn máy chủ vào response.

- **Đúng:** `"first must contain only ASCII digits"`.
- **Sai:** `"Invalid number: " + value` hoặc trả `exception.getMessage()` trực tiếp cho client.

## S06 — Truy cập dữ liệu an toàn

Database dùng parameter binding/prepared statements hoặc repository có tham số.
Không nối input vào SQL, shell command, đường dẫn file hoặc URL rồi thực thi.
Không thêm I/O cho method thuần tính toán. Nếu cần I/O, validation phải theo ngữ cảnh đích.

## S07 — Không nhận trường do server quản lý

DTO chỉ chứa trường được phép trong đặc tả. Từ chối trường lạ; không dùng mass assignment
để client ghi `status`, `id`, `created_at` hoặc thuộc tính quyền.
Kiểm tra customer tồn tại/active ở server, không dựa vào một cờ do client gửi lên.

## S08 — Context và kiểm chứng

Không đưa dữ liệu production hoặc secret vào context AI; `.copilotignore` không thay thế
việc chủ động loại dữ liệu nhạy cảm. Review dependency, mã nháp và thông báo lỗi trước khi dùng.
Chạy test dữ liệu sai/biên; không đánh dấu đã secret-scan, đã review bởi người hoặc đã dùng
một công cụ nếu chưa có bằng chứng. Tuân theo [CONTRIBUTING.md](../../CONTRIBUTING.md).
