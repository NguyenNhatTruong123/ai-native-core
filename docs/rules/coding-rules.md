# Java Coding & Logging Rules

Áp dụng cho Java trong repo, gồm bản nháp Lab2.1. Khi viết API, đọc thêm
[api-rules.md](api-rules.md), [security-rules.md](security-rules.md) và đặc tả của task.
Các ví dụ dưới đây là trích đoạn minh họa, không phải các lớp chạy độc lập.

## C01 — Ngôn ngữ và dependency

Dùng Java 17+; module web dùng Spring Boot 3.3+ theo cấu hình dự án khi được thiết lập.
Scratch thuần Java chỉ dùng JDK, không thêm framework hoặc thư viện nếu task không cần.
Chỉ gọi API, package và method đã tồn tại; không đoán dependency.

- **Đúng:** `StringBuilder result = new StringBuilder();`
- **Sai:** gọi `NumberHelper.safeAdd(...)` khi repo không có lớp đó.

## C02 — Định dạng

Dùng UTF-8, thụt lề 2 spaces, không tab, tối đa 100 ký tự mỗi dòng Java.
Dùng dấu ngoặc cho mọi `if`, `else`, `for`, `while`; mỗi câu lệnh một dòng.

```java
// Đúng
if (value == null) {
  throw new IllegalArgumentException("value is required");
}
// Sai
if(value==null) throw new IllegalArgumentException("value is required");
```

## C03 — Đặt tên

Dùng `PascalCase` cho class, `camelCase` cho method/biến, `UPPER_SNAKE_CASE` cho hằng.
Tên phải diễn tả vai trò; không dùng tên một chữ cái ngoài chỉ số đơn giản.

- **Đúng:** `ScratchHandler`, `digitSum`, `MAX_DIGITS`.
- **Sai:** `scratch_handler`, `DigitSum`, `max_digits` cho hằng.

## C04 — Tổ chức file và import

Một top-level class mỗi file; tên file trùng tên class. Khai báo package chữ thường.
Import tường minh, không wildcard, không import thừa.

- **Đúng:** `package lab21;` trong `ScratchHandler.java`.
- **Sai:** `import java.util.*;` hoặc nhiều public class trong cùng file.

## C05 — Phạm vi và độ đơn giản

Mỗi method có một trách nhiệm. Tách validation để luồng chính dễ đọc.
Không thêm factory, framework, endpoint, DTO hay tính năng ngoài task.

- **Đúng:** `sum` gọi `validateNumber` rồi cộng hai chuỗi.
- **Sai:** method cộng số đồng thời ghi database hoặc gửi HTTP request.

## C06 — Biến trong vòng lặp

Giữ quy ước đã có của repo: khai báo biến tạm và biến điều khiển ngay trước vòng lặp,
không khai báo trong thân vòng lặp. Khởi tạo lại biến tạm ở mỗi lượt nếu cần.
Các biến khác đặt gần nơi dùng, không đưa tất cả lên đầu method.

```java
// Đúng theo quy ước repo
int index = 0;
char digit;
while (index < value.length()) {
  digit = value.charAt(index);
  process(digit);
  index++;
}
// Sai theo quy ước repo
for (int index = 0; index < value.length(); index++) {
  char digit = value.charAt(index);
  process(digit);
}
```

Đây là quy ước riêng được giữ lại từ bản nháp, không phải yêu cầu của Google/Oracle.
Không coi việc đưa biến ra ngoài vòng lặp là bảo đảm giảm cấp phát bộ nhớ.

## C07 — Validation trước xử lý

Kiểm tra null, độ dài, định dạng và giới hạn theo hợp đồng trước khi xử lý.
Không tự trim, đổi kiểu hoặc thay dữ liệu sai bằng giá trị mặc định ngoài đặc tả.
API dùng Bean Validation cho DTO và kiểm tra nghiệp vụ ở service.

- **Đúng:** từ chối chuỗi rỗng hoặc chứa chữ trước khi cộng số.
- **Sai:** coi `null` là `"0"` hoặc tự biến `" 12 "` thành `"12"` khi chưa được cho phép.

## C08 — Ngoại lệ cụ thể

Không ném `Exception`/`RuntimeException` chung chung. Dùng ngoại lệ chuẩn phù hợp
(ví dụ `IllegalArgumentException`) hoặc ngoại lệ nghiệp vụ đã định nghĩa.
Chỉ catch loại có thể xử lý; không catch rồi trả kết quả thành công giả hoặc bỏ qua lỗi.

- **Đúng:** `throw new IllegalArgumentException("first must contain only ASCII digits");`
- **Sai:** `catch (Exception exception) { return "0"; }`.

## C09 — Logging không lộ PII

Trong module ứng dụng, dùng SLF4J với
`private static final Logger log = LoggerFactory.getLogger(ClassName.class);`.
Dùng placeholder, chỉ log mã sự kiện/trạng thái đã cho phép; không log body, dữ liệu đầu vào,
mật khẩu, token, email, số điện thoại hay thông tin định danh khách hàng.
Không dùng `System.out`/`printStackTrace` làm application logger.
Scratch thuần tính toán không cần logger; test runner có thể in số lượng kiểm tra.

- **Đúng:** `log.info("event={} status={}", "work_order_created", "DRAFT");`
- **Sai:** `log.info("request={}", request);` hoặc `log.error("token=" + token);`.

## C10 — Dependency injection

Dùng constructor injection, dependency field là `private final`; tránh field injection.
Không thêm dependency chỉ để minh họa DI trong class không cần dependency.

```java
// Đúng — trích đoạn của service trong module Spring
private final WorkOrderRepository repository;

public WorkOrderService(WorkOrderRepository repository) {
  this.repository = repository;
}
// Sai
@Autowired
private WorkOrderRepository repository;
```

## C11 — Hợp đồng method

Public method có Javadoc nêu đầu vào, kết quả và ngoại lệ.
Ghi rõ giả định/giới hạn của bản nháp trong prompt; không giả làm yêu cầu của đề.

- **Đúng:** mô tả chuỗi chữ số ASCII, cách xử lý số 0 đầu chuỗi và `@throws`.
- **Sai:** chỉ ghi `// adds numbers` nhưng không nói số âm/null có hợp lệ không.

## C12 — Kiểm chứng trước khi chấp nhận

Biên dịch không warning và kiểm tra happy path, boundary, dữ liệu sai.
Ghi bằng chứng trong Scorecard; phân biệt review mã, test chạy thật và kiểm tra chưa thực hiện.
Code AI vẫn cần người review theo [CONTRIBUTING.md](../../CONTRIBUTING.md).

- **Đúng:** đối chiếu phép cộng chuỗi với `BigInteger` trong test, ghi kết quả chạy thực tế.
- **Sai:** đánh dấu Pass vì AI nói code đúng hoặc nhận đã chạy một công cụ khi chưa chạy.

## Tài liệu tham chiếu

- [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html): tham chiếu định dạng và tên; C06 là ngoại lệ riêng của repo.
- [Oracle Java Language Specification 21](https://docs.oracle.com/javase/specs/jls/se21/html/index.html): ngữ nghĩa ngôn ngữ Java.
