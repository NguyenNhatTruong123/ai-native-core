# Lab2.2 — Kết quả và đối chiếu Done Criteria

**Hoàn thành 2/2 Done Criteria chính thức (100%) của phần chính Lab2.2.**
Codex thực hiện trực tiếp các tác vụ sinh file/phân tích/review theo ba prompt mẫu,
theo yêu cầu người dùng; không yêu cầu chạy một phiên Copilot riêng.

## 1. Done Criteria nguyên văn và bằng chứng

Sau khi hoàn thành lab, người dùng yêu cầu phân loại lại tài liệu: kết quả được chuyển
sang `labs/`, spec/rules được tách trong `docs/`. Cột tiêu chí giữ nguyên câu chữ và đường
dẫn gốc trong đề; các liên kết bằng chứng trỏ tới vị trí hiện tại. DC01 hiện được đáp ứng
bởi `labs/lab-2.2-business-analysis/analysis/work-order-business-analysis.md` theo yêu cầu
tổ chức mới, không còn file kết quả tại `docs/br-analysis-wo.md`.

Nguồn: phần **3. Tiêu chí hoàn thành (Done Criteria)** trong file
`LAB 2.2 - Tối ưu hóa Context & Phân tích Yêu cầu Kinh doanh (BR Analysis).docx`.

| ID | Done Criterion nguyên văn | Bằng chứng đã lưu | Kết quả |
| --- | --- | --- | --- |
| DC01 | Hoàn thành file phân tích nghiệp vụ docs/br-analysis-wo.md. | [BR Analysis](../analysis/work-order-business-analysis.md): 4 thực thể/tác nhân với thuộc tính và nguồn, 12 Open Questions, bảng UI/Data/API gồm 9 dòng nghiệp vụ, phạm vi và acceptance criteria. | PASS |
| DC02 | Hoàn thành danh sách ít nhất 8 góp ý đánh giá mã nguồn (Review comments) đối với đoạn code mẫu có lỗi bảo mật. | [14 review comments](../reviews/security-code-review.md) đối với [fixture Java](../../../scratch/lab22/unsafe/WorkOrderController.java), có dòng mã, tác động, hướng sửa và cách kiểm chứng; đủ cả 5 nhóm theo đúng thứ tự. | PASS |

## 2. Đối chiếu các bước và yêu cầu bổ sung

| Yêu cầu | Kết quả thực hiện | Trạng thái |
| --- | --- | --- |
| Bước 1: Custom Instructions | [.github/copilot-instructions.md](../../../.github/copilot-instructions.md) chứa Role/Core Rules nguyên bản, “Never commit secrets”, “Follow docs/*” và context repo. | PASS |
| Prompt mẫu cấu hình | P01 giữ nội dung Role/Core Rules và đã dùng để tạo workspace instructions. | PASS |
| Prompt mẫu BA | P02 chỉ thay placeholder bằng yêu cầu thô ở phần chính, đã xuất đúng tên file và ba phần được yêu cầu. | PASS |
| Prompt mẫu review | P03 chỉ thay placeholder bằng toàn bộ fixture tự tạo; kết quả là 14 nhận xét có bằng chứng. | PASS |
| Thứ tự review | Spec delta (3) → Security (4) → Validation & Testing (3) → Complexity (2) → Style (2). | PASS |
| Gợi ý cấu trúc sửa lỗi theo cuối prompt P03 | [Safe design](../reviews/proposed-safe-design.md) có cây source, trách nhiệm từng lớp, SQL bind parameters, error mapping và test plan phủ RC01–RC14. | PASS |
| Thực hiện tại chỗ | Đã lưu instructions, BR Analysis, fixture, prompt, review, safe design và kết quả kiểm tra trong repo. | PASS |
| Phạm vi phụ lục | Bỏ qua đáp án/mã mẫu phụ lục; chỉ dùng mục Prompt nằm phía sau tiêu đề phụ lục theo yêu cầu cụ thể của người dùng. | PASS |

Toàn bộ prompt đã điền đầu vào và file đầu ra tương ứng nằm trong
[executed-prompts.md](../prompts/executed-prompts.md). Các câu chữ mẫu được giữ nguyên; khác biệt trình bày
như khoảng trắng cuối dòng không làm thay đổi nội dung prompt.

## 3. Phương án Custom Instructions

Bước 1 của đề cho phép “Cài đặt cấu hình instructions cho IDE **hoặc** tạo tệp cấu hình
workspace phù hợp”. Bài làm chọn phương án workspace bằng `.github/copilot-instructions.md`.
Đây là vị trí repository instructions được GitHub hỗ trợ.
Tham chiếu: [GitHub — repository custom instructions](https://docs.github.com/en/copilot/how-tos/copilot-on-github/customize-copilot/add-custom-instructions/add-repository-instructions).

Không thay cấu hình global trên máy hoặc nhận đã kiểm tra Copilot tải instructions trong UI.
Trong phiên thực hiện này, Codex đã đọc Rules Pack và áp dụng Role/Core Rules để tạo đầu ra.

## 4. Kiểm tra chất lượng và giới hạn bằng chứng

[artifact-verification.txt](artifact-verification.txt) ghi kết quả kiểm tra file, prompt, số lượng
thực thể/câu hỏi/bảng phân rã, review categories, vị trí dòng mã, liên kết và nội dung đối chiếu.
Review kỹ thuật được thực hiện trên source fixture; kiểm tra tự động hỗ trợ truy vết và
tính đầy đủ của bài nộp, không thay thế đánh giá ngữ nghĩa của từng phát hiện.

Lab này không xây dựng hoặc deploy Spring application. Không chạy fixture có lỗi với database,
không cài dependency để chạy nó, không tuyên bố API/security integration tests đã pass.
Các test trong review và safe design là đề xuất kiểm chứng khi triển khai bản sửa.
Open Questions của BA được ghi rõ là chưa chốt; không tự đổi đặc tả WO-201.
