# Lab2.1 — Scorecard đánh giá mã nháp

**Kết quả: 16/16 tiêu chí áp dụng đạt Pass; 552/552 kiểm tra thực thi đạt.**
Đánh giá ngày 22/09/2026 cho [ScratchHandler.java](../../../scratch/ScratchHandler.java),
theo [prompt đã dùng](../prompts/draft-generation.md) và bộ quy tắc trong `docs/rules/`.
Bước 3 được Codex thực hiện trực tiếp theo yêu cầu người dùng.

## Bảng đánh giá

Pass = 1 điểm, Fail = 0 điểm. Mỗi dòng có bằng chứng kiểm tra;
review mã là kiểm tra của AI trong phiên này, không phải phê duyệt của human reviewer.

| # | Tiêu chí | Rule | Cách kiểm tra và bằng chứng | Kết quả |
| --- | --- | --- | --- | --- |
| 1 | Java 17+, chỉ dùng API có thật trong JDK | C01 | Biên dịch bằng `javac --release 17 -Xlint:all -Werror`; không cần dependency ngoài. | Pass |
| 2 | Định dạng nhất quán, UTF-8, 2 spaces, có braces | C02 | Review hai file Java; kiểm tra không tab, không trailing whitespace, không dòng vượt 100 ký tự. | Pass |
| 3 | Tên class/method/biến/hằng đúng quy ước | C03 | Review `ScratchHandler`, `validateNumber`, `firstIndex`, `MAX_DIGITS`. | Pass |
| 4 | Package, tên file và import đúng | C04 | `package lab21`, một class/file; scratch không cần import; test import tường minh hai lớp JDK. | Pass |
| 5 | Không sinh tính năng, JSON field hoặc dependency ngoài task | C05 | Scratch chỉ có `sum`, helper validation và hằng giới hạn; không có DTO, HTTP, database. | Pass |
| 6 | Biến vòng lặp theo quy ước hiện có của repo | C06 | Review: index, `digitSum`, `digit` khai báo ngoài thân vòng lặp; `digitSum = carry` được reset mỗi lượt. | Pass |
| 7 | Public method có hợp đồng rõ ràng | C11 | Javadoc của `sum` ghi params, return, throws, tập đầu vào và độ phức tạp. | Pass |
| 8 | Null/rỗng bị từ chối ở cả hai toán hạng | C07, S02 | `testInvalidInputs` kiểm tra null và chuỗi rỗng ở từng vị trí; bắt đúng `IllegalArgumentException`. | Pass |
| 9 | Chỉ nhận chữ số ASCII, không âm, không tự trim/coerce | C07, S02 | Test dấu, khoảng trắng, thập phân, scientific notation, chữ cái, newline, tab, NUL, chữ số Arabic/fullwidth. | Pass |
| 10 | Kiểm tra giới hạn trước khi tạo buffer kết quả | S03 | Review thứ tự `validateNumber` trước `new StringBuilder`; test 10.000 chữ số hợp lệ, 10.001 bị từ chối ở cả hai vị trí. | Pass |
| 11 | Cộng đúng, xử lý carry và chênh lệch độ dài | C12 | Test `12 + 30 = 42`, `9 + 9 = 18`, `999 + 1 = 1000`, đổi thứ tự và carry bên trong. | Pass |
| 12 | Số lớn không tràn kiểu nguyên thủy | C01, C12 | Test vượt `Long.MAX_VALUE`, hai số dài 10.000 chữ số; 500 đối chiếu với `BigInteger` độc lập, seed 21. | Pass |
| 13 | Chuẩn hóa số 0 đầu chuỗi và kết quả 0 | C07, C12 | Test `00012 + 00008 = 20`, `000 + 0000 = 0`, chuỗi 10.000 số 0. | Pass |
| 14 | Ngoại lệ cụ thể, không nuốt lỗi hoặc trả thành công giả | C08 | Review scratch không catch chung; 38 lần input sai phải ném `IllegalArgumentException`, không trả `0` thay lỗi. | Pass |
| 15 | Không lộ dữ liệu đầu vào trong log/thông báo lỗi | C09, S05 | Scratch không có log/console; test xác nhận lỗi chỉ có thông điệp cho phép, không có cause, kể cả input giống email. | Pass |
| 16 | Không hardcode secret hoặc thêm I/O không cần thiết | S01, S06 | Review toàn bộ scratch/test: chỉ có dữ liệu test tổng hợp, giới hạn và seed; không key/token/credentials, network, file hay database I/O trong scratch. | Pass |

## Các quy tắc không áp dụng cho ví dụ 2

| Quy tắc | Trạng thái | Lý do |
| --- | --- | --- |
| A02–A09: endpoint, request/response JSON, HTTP status, Problem Details, transaction | N/A | Bản nháp là method `sum`, không có HTTP handler. API rules đã được soạn theo yêu cầu bước 2, nhưng chưa có API runtime để test. |
| C10: constructor injection | N/A | Scratch không có dependency cần inject. |
| S04, S07: bearer token, quyền, mass assignment | N/A | Không có request API hay persistence trong bản nháp. |

N/A không tính điểm, không được dùng để khẳng định API đã vượt kiểm thử.
Kiểm tra secret ở tiêu chí 16 là review mã, không phải chạy công cụ secret scanner.
Kiểm tra style ở tiêu chí 2 không phải chạy toàn bộ Google Java formatter/Checkstyle.

## Bằng chứng chạy

Lệnh tại repo root:

```powershell
.\scripts\test-lab21.ps1
```

Kết quả thực tế:

```text
PASS: 552 checks (contract, validation, BigInteger reference).
```

Exit code `0`; biên dịch không có warning/error. Máy chạy dùng JDK `21.0.12.1`,
biên dịch theo target Java 17. Chưa chạy bằng một JVM 17 riêng.
552 kiểm tra gồm 14 ca hợp lệ/biên, 38 ca input sai và 500 đối chiếu độc lập.
Xem [bản ghi kết quả](test-results.txt) và
[mã kiểm tra](../../../tests/unit/ScratchHandlerTest.java).

## Đối chiếu Done Criteria

- [x] Có `coding-rules.md` với 12 quy tắc, bao gồm định dạng, ngoại lệ, logging và ví dụ Đúng/Sai.
- [x] Có `api-rules.md` với REST resource, Problem Details và cấm tự sinh thêm JSON field.
- [x] Có `security-rules.md` với cấm hardcode secret và validation đầu vào.
- [x] Có prompt chứa context/constraints và file mã nháp đã được sinh, lưu, biên dịch.
- [x] Có Scorecard ít nhất 10 tiêu chí, ghi rõ Pass/Fail và bằng chứng.
- [x] Bỏ qua phần phụ lục theo yêu cầu.
