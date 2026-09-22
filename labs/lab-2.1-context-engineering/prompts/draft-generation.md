# Lab2.1 — Prompt và hợp đồng mã nháp

File này lưu prompt đã thực hiện. Các đường dẫn trong khối prompt giữ nguyên theo thời
điểm chạy; khi tái lập sau khi tổ chức lại repo, dùng bảng context hiện tại dưới đây.

| Context | Đường dẫn hiện tại tính từ repo root |
| --- | --- |
| Coding rules | `docs/rules/coding-rules.md` |
| API rules | `docs/rules/api-rules.md` |
| Security rules | `docs/rules/security-rules.md` |
| Hợp đồng và prompt của bài này | `labs/lab-2.1-context-engineering/prompts/draft-generation.md` |

Mã nháp và lệnh kiểm tra vẫn dùng vị trí được chỉ dẫn bên dưới.

Chọn **ví dụ 2** của phần chính: hoàn thiện `sum(String, String)` trong
[`scratch/ScratchHandler.java`](../../../scratch/ScratchHandler.java).
Ảnh minh họa trong đề cộng hai số lớn dạng chuỗi và từ chối null/ký tự không phải chữ số.
Không thực hiện phụ lục hoặc triển khai Work Order API trong bản nháp này.

## Hợp đồng cụ thể của bản nháp

Đề không quy định đầy đủ mọi trường hợp. Các lựa chọn dưới đây được đặt rõ trong prompt
để AI không tự suy đoán; chúng không phải yêu cầu bắt buộc được trích nguyên văn từ đề:

- Chữ ký: `public String sum(String first, String second)`, package `lab21`.
- Mỗi toán hạng có 1–10.000 chữ số ASCII `0`–`9`, biểu diễn số nguyên không âm.
- Cho phép số 0 đầu chuỗi; kết quả chuẩn hóa, `"000" + "0"` trả `"0"`.
- Từ chối null, rỗng, khoảng trắng, dấu `+`/`-`, số thập phân, scientific notation,
  chữ số Unicode không phải ASCII và chuỗi vượt giới hạn bằng `IllegalArgumentException`.
- Không echo input trong thông báo lỗi. Không tự trim hay làm tròn.
- Cộng từng chữ số để không bị tràn `long`; không dùng `BigInteger` trong implementation.
  Test được dùng `BigInteger` làm kết quả tham chiếu độc lập.
- Không I/O, logger, HTTP, database hoặc dependency ngoài JDK trong class scratch.

## Prompt có ngữ cảnh được dùng để sinh mã

```text
Role: Senior Engineer.
Task: Complete public String sum(String first, String second) in
scratch/ScratchHandler.java, package lab21.

Context files:
- docs/coding-rules.md
- docs/api-rules.md
- docs/security-rules.md
- docs/lab21-draft-prompt.md (the draft contract above this prompt)

Constraints:
- Follow Java language rules from Oracle and the Google style conventions
  selected in coding-rules.md. C06 is an explicit repository exception:
  declare loop control/temporary variables immediately before the loop.
- Accept only non-null strings of 1 to 10000 ASCII digits (0-9).
- Add non-negative integers digit by digit without parsing the operands
  into primitive numeric types or using BigInteger in the implementation.
- Accept leading zeros; return a canonical decimal string without leading
  zeros, except that zero must be returned as "0".
- Throw IllegalArgumentException on every invalid input. Error messages
  must not contain raw input. Do not trim or coerce invalid values.
- Use JDK only. Do not add JSON fields, DTOs, endpoints, I/O, or logging.
- Provide Javadoc, validate both inputs before allocating the result, and
  keep time and space complexity linear in the longer operand's length.
- Add executable tests for normal cases, carry, zero, leading zeros,
  numbers beyond long, length boundaries, and invalid inputs on both sides.
- Report evidence against at least 10 criteria from the context rules.
```

## Thực hiện bước 3

Theo yêu cầu trực tiếp của người làm lab, Codex thực hiện sinh mã và lưu kết quả ngay
trong repo, thay vai trò công cụ sinh mã của Copilot Chat. Không cần kết nối Copilot.
Các file kết quả không phải transcript của một phiên Copilot.

- Mã sinh: [ScratchHandler.java](../../../scratch/ScratchHandler.java).
- Kiểm chứng: [ScratchHandlerTest.java](../../../tests/unit/ScratchHandlerTest.java).
- Kết quả chạy và đánh giá: [compliance-scorecard.md](../results/compliance-scorecard.md).

## Chạy kiểm chứng

Từ thư mục gốc repo, dùng PowerShell và JDK 17+:

```powershell
.\scripts\test-lab21.ps1
```

Script biên dịch với `--release 17 -Xlint:all -Werror`, chạy test độc lập không cần Maven,
và ghi file `.class` vào `bin/lab21/` (đã được `.gitignore` loại trừ).
