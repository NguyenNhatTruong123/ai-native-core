# Đặc tả và quy tắc dự án

`docs/` chứa tài liệu định hướng triển khai và các quy tắc dùng chung.
Các bài phân tích chưa được duyệt, prompt đã chạy, review và kết quả kiểm chứng của lab
được quản lý tại [labs/](../labs/README.md).

## Quy tắc dùng chung — rules/

| File | Mục đích | Lý do đặt tại đây |
| --- | --- | --- |
| [coding-rules.md](rules/coding-rules.md) | Quy tắc Java, định dạng, ngoại lệ, logging, DI và ví dụ Đúng/Sai. | Là chỉ dẫn dùng lại khi tạo hoặc review code, dù được xây dựng trong Lab2.1. |
| [api-rules.md](rules/api-rules.md) | Quy tắc REST, request/response, HTTP status và Problem Details. | Chi phối hợp đồng API chung; không phải báo cáo kết quả một lần chạy. |
| [security-rules.md](rules/security-rules.md) | Quy tắc secret, validation, quyền, dữ liệu nhạy cảm và truy cập dữ liệu. | Là ràng buộc bảo mật dùng chung cho các tác vụ. |

## Đặc tả kỹ thuật — specs/

| File | Mục đích | Trạng thái và lý do đặt tại đây |
| --- | --- | --- |
| [work-order-decomposition.md](specs/work-order-decomposition.md) | Thiết kế Work Order WO-201: UI/Data/API, DDL và acceptance criteria. | Là nguồn hợp đồng hiện hành được repo tham chiếu khi triển khai. |
| [api-spec.md](specs/api-spec.md) | Vị trí dành cho đặc tả API riêng. | File khung hiện rỗng; được giữ để phát triển đặc tả, chưa là nguồn schema. |
| [domain-model.md](specs/domain-model.md) | Vị trí dành cho mô hình miền riêng. | File khung hiện rỗng; được giữ để phát triển đặc tả, chưa là model đã chốt. |

Sáu file trên trước đây nằm trực tiếp trong `docs/`; tên file giữ nguyên, chỉ chuyển
vào `rules/` hoặc `specs/` theo chức năng. Bảng ánh xạ các đầu ra lab được chuyển khỏi
`docs/` nằm trong [mục lục lab](../labs/README.md).

Khi một đề xuất trong lab được chấp thuận thành đặc tả, cập nhật tài liệu tương ứng
trong `specs/` và dẫn lại nguồn phân tích. Không tự xem mọi tài liệu được AI sinh là đặc tả.
