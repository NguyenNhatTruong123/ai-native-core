# Lab2.2 review fixture

[`unsafe/WorkOrderController.java`](unsafe/WorkOrderController.java) là mã Java/Spring
được tạo riêng cho bước 3, cố ý chứa Spec delta, thiếu kiểm tra quyền, SQL string concatenation,
thiếu validation và xử lý lỗi không an toàn. Không lấy mã mẫu từ phụ lục DOCX.

Giá trị password là marker tổng hợp, không phải credential thật. Không chạy fixture với
database, không đưa vào `src/`, không thêm dependency Spring chỉ để chạy mã có lỗi này.
Repo chưa có Spring application/security configuration; nhận xét chỉ áp dụng cho fixture.

- [Prompt review đã dùng](../../labs/lab-2.2-business-analysis/prompts/executed-prompts.md)
- [Review comments](../../labs/lab-2.2-business-analysis/reviews/security-code-review.md)
- [Cấu trúc sửa lỗi được đề xuất](../../labs/lab-2.2-business-analysis/reviews/proposed-safe-design.md)
- [Đối chiếu Done Criteria](../../labs/lab-2.2-business-analysis/results/done-criteria.md)
