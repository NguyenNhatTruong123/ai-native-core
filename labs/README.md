# Bài thực hành và đầu ra

Mỗi bài nằm trong thư mục có số lab và chủ đề. Thư mục con thể hiện mục đích của file:

| Thư mục | Nội dung |
| --- | --- |
| `prompts/` | Prompt đã thực hiện, context, đầu vào và hướng dẫn tái lập. |
| `analysis/` | Kết quả phân tích nghiệp vụ và câu hỏi chưa chốt. |
| `reviews/` | Nhận xét review và đề xuất sửa lỗi. |
| `results/` | Scorecard, đối chiếu hoàn thành, kết quả chạy và bằng chứng kiểm tra. |

## Lab2.1 — Context Engineering

| File cũ trong docs/ | Vị trí hiện tại | Phân loại và mục đích |
| --- | --- | --- |
| `lab21-draft-prompt.md` | [prompts/draft-generation.md](lab-2.1-context-engineering/prompts/draft-generation.md) | Prompt sinh mã và hợp đồng riêng của bài cộng số; chỉ phục vụ thực hiện/tái lập lab. |
| `lab21-scorecard.md` | [results/compliance-scorecard.md](lab-2.1-context-engineering/results/compliance-scorecard.md) | Bảng đánh giá mức tuân thủ của mã nháp, không phải quy tắc mới. |
| `lab21-test-results.txt` | [results/test-results.txt](lab-2.1-context-engineering/results/test-results.txt) | Bằng chứng của lần biên dịch và chạy kiểm tra. |

Mã nháp: [ScratchHandler.java](../scratch/ScratchHandler.java).
Kiểm thử: [ScratchHandlerTest.java](../tests/unit/ScratchHandlerTest.java).
Lệnh chạy: [scripts/test-lab21.ps1](../scripts/test-lab21.ps1).
Ba file rules là tài liệu dùng chung nên đặt trong [docs/rules/](../docs/README.md).

## Lab2.2 — Business Analysis

| File cũ trong docs/ | Vị trí hiện tại | Phân loại và mục đích |
| --- | --- | --- |
| `br-analysis-wo.md` | [analysis/work-order-business-analysis.md](lab-2.2-business-analysis/analysis/work-order-business-analysis.md) | Kết quả BA từ yêu cầu thô; còn Open Questions, chưa thay thế SPEC. |
| `lab22-prompts.md` | [prompts/executed-prompts.md](lab-2.2-business-analysis/prompts/executed-prompts.md) | Bản ghi ba prompt mẫu, đầu vào đã điền và liên kết đầu ra. |
| `lab22-review-comments.md` | [reviews/security-code-review.md](lab-2.2-business-analysis/reviews/security-code-review.md) | 14 nhận xét đối với fixture có lỗi; là kết quả review của lab. |
| `lab22-safe-design.md` | [reviews/proposed-safe-design.md](lab-2.2-business-analysis/reviews/proposed-safe-design.md) | Đề xuất sửa lỗi sau review; chưa là thiết kế được duyệt để triển khai. |
| `lab22-completion.md` | [results/done-criteria.md](lab-2.2-business-analysis/results/done-criteria.md) | Đối chiếu Done Criteria và đường dẫn bằng chứng hoàn thành. |
| `lab22-verification.txt` | [results/artifact-verification.txt](lab-2.2-business-analysis/results/artifact-verification.txt) | Bản ghi kiểm tra tính đầy đủ của các file bài nộp. |

Mã mẫu và hướng dẫn review nằm trong [scratch/lab22/](../scratch/lab22/README.md).
Workspace instructions giữ tại [.github/copilot-instructions.md](../.github/copilot-instructions.md)
vì đây là vị trí cấu hình được công cụ nhận diện.

## Các file còn lại

| Vị trí | Mục đích |
| --- | --- |
| [docs/](../docs/README.md) | Sáu file rules/specs dùng chung; xem bảng phân loại từng file tại đó. |
| [README.md](../README.md), [CONTRIBUTING.md](../CONTRIBUTING.md) | Giới thiệu, cách chạy và quy trình đóng góp ở cấp repo. |
| `.github/` | Workspace instructions, issue templates và PR template. Đây là cấu hình công cụ/quy trình. |
| `scratch/`, `tests/`, `scripts/` | Lần lượt là mã thực hành, mã kiểm thử và lệnh thực thi. README trong scratch giải thích mã tại chỗ. |
| Các file `LAB *.docx` tại gốc repo | Đề bài gốc do người dùng cung cấp. |

## Quy ước cho các lab tiếp theo

Tạo `labs/lab-<số>-<chủ-đề>/` và chỉ tạo các thư mục con thực sự có nội dung.
Tên file mô tả chức năng, không lặp lại số lab đã có trong đường dẫn.
Lưu các kết quả `.txt` cùng `results/`, không chỉ phân loại file Markdown.

Các prompt và trích dẫn Done Criteria giữ nguyên đường dẫn lịch sử trong đề/bản ghi.
Khi chạy lại, dùng đường dẫn hiện tại được chú thích ở đầu file prompt và các bảng trên.
Không tạo file chuyển tiếp tại vị trí `docs/` cũ vì sẽ làm lẫn kết quả với spec/rules.
