# Lab2.2 — Các prompt mẫu đã thực hiện

Các prompt dưới đây giữ nguyên nội dung và đường dẫn tại thời điểm thực hiện.
Khi chạy lại theo cấu trúc repo hiện tại, thay các đường dẫn context/đầu ra bằng bảng này;
không tạo lại file kết quả trong `docs/`.

| Vai trò | Đường dẫn hiện tại tính từ repo root |
| --- | --- |
| SPEC Work Order | `docs/specs/work-order-decomposition.md` |
| Coding rules | `docs/rules/coding-rules.md` |
| API rules | `docs/rules/api-rules.md` |
| Security rules | `docs/rules/security-rules.md` |
| Đầu ra phân tích của P02 | `labs/lab-2.2-business-analysis/analysis/work-order-business-analysis.md` |
| Đầu ra review của P03 | `labs/lab-2.2-business-analysis/reviews/security-code-review.md` |
| Đề xuất sửa lỗi của P03 | `labs/lab-2.2-business-analysis/reviews/proposed-safe-design.md` |

Nguồn: `LAB 2.2 - Tối ưu hóa Context & Phân tích Yêu cầu Kinh doanh (BR Analysis).docx`, mục **Prompt**.
Theo yêu cầu riêng của người dùng, chỉ lấy ba prompt trong mục này dù mục Prompt
nằm sau tiêu đề Phụ lục. Không sử dụng đáp án BR hoặc mã mẫu còn lại của phụ lục.

Codex thực hiện trực tiếp cả ba tác vụ với các prompt dưới đây, thay vai trò công cụ
sinh kết quả của Copilot. Đây là bản ghi prompt và file đầu ra, không phải transcript
của một phiên Copilot hay một lần gọi API mô hình bên ngoài.

Giữ nguyên văn prompt mẫu; chỉ thay hai placeholder đầu vào bằng yêu cầu thô ở phần
chính và fixture được tạo riêng cho bước review. Các đầu ra được kiểm tra theo
[Done Criteria](../results/done-criteria.md).

## P01 — Cấu hình Custom Instructions

```text
- Role: Bạn là một Senior Solution Architect và Technical Lead chuyên nghiệp trong dự án POSCO MCI.
- Core Rules:
  1. Tuyệt đối không hardcode thông tin nhạy cảm (JWT secret, password, API key) trong mã nguồn.
  2. Luôn tuân thủ các tài liệu đặc tả trong thư mục `docs/*`.
  3. Khi viết code Java/Spring Boot, luôn ưu tiên bảo mật chống SQL Injection (dùng PreparedStatement/JPA) và tuân thủ phân quyền chặt chẽ.
```

**Đã thực hiện:** tạo [workspace instructions](../../../.github/copilot-instructions.md)
với nguyên Role/Core Rules, rồi bổ sung đường dẫn context thật của repo.
Chọn phương án tệp workspace được cho phép ở bước 1; không thay cấu hình global của IDE.

## P02 — Phân tích yêu cầu thô

Context khi thực hiện: P01, `docs/work-order-decomposition.md`, `docs/coding-rules.md`,
`docs/api-rules.md`, `docs/security-rules.md` và yêu cầu thô dưới đây.

```text
Dựa vào Custom Instructions đã thiết lập, hãy đóng vai trò là một Business Analyst (BA) chuyên nghiệp.
Hãy đọc đoạn yêu cầu thô sau về tính năng Quản lý Phiếu công việc (Work Order) của dự án POSCO MCI:
"Tạo tính năng quản lý thiết bị công trường POSCO MCI cho phép thợ kỹ thuật đăng ký phiếu công việc gồm mã thiết bị, mô tả và mức độ ưu tiên"

Hãy giúp tôi phân tích và xuất nội dung thành file markdown lưu vào `docs/br-analysis-wo.md` bao gồm các phần sau:
1. Danh sách các thực thể (Entities) và thuộc tính cơ bản.
2. Các câu hỏi còn bỏ ngỏ / Rủi ro nghiệp vụ cần Product Owner làm rõ (Open Questions).
3. Bảng phân rã chi tiết theo mô hình 3 tầng (UI / Data / API).
```

**Đã thực hiện:** xuất [work-order-business-analysis.md](../analysis/work-order-business-analysis.md), gồm Entities,
Open Questions và bảng UI/Data/API; tách yêu cầu đã biết khỏi đề xuất chưa chốt.

## P03 — AI-Assisted Code Review

Context khi thực hiện: P01, Rules Pack, đặc tả Work Order hiện hành và kết quả P02.
Placeholder mã được thay bằng toàn bộ fixture, giữ nguyên để tái lập các vị trí review.

Hãy đóng vai trò là một Senior Security Code Reviewer. Hãy thực hiện review đoạn mã Java (Spring Boot) sau đây trong Controller của dự án POSCO MCI:

```java
package lab22.unsafe;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Intentionally vulnerable Lab2.2 review fixture. Do not build, deploy, or reuse in production. */
@RestController
@RequestMapping("/api/workorders")
public class WorkOrderController {
  private static final String DB_URL = "jdbc:postgresql://localhost:5432/lab22_training";
  private static final String DB_USER = "lab22_example_user";
  // Synthetic training marker, not a real credential; the configuration pattern is intentional.
  private static final String DB_PASSWORD = "TRAINING_ONLY_NOT_A_REAL_PASSWORD";

  @PostMapping
  public ResponseEntity<Map<String, Object>> create(@RequestBody Map<String, String> request) {
    System.out.println("work-order request: " + request);
    String title = request.get("title");
    String description = request.getOrDefault("description", "");
    String priority = request.getOrDefault("priority", "MED");
    String customerId = request.getOrDefault("customer_id", "00000000-0000-0000-0000-000000000000");
    String status = request.getOrDefault("status", "APPROVED");

    try {
      Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
      Statement statement = connection.createStatement();
      String sql = "INSERT INTO work_orders (title, description, priority, status, customer_id) "
          + "VALUES ('" + title + "', '" + description + "', '" + priority + "', '" + status
          + "', '" + customerId + "') RETURNING id, created_at";
      ResultSet rows = statement.executeQuery(sql);
      rows.next();

      Map<String, Object> response = new HashMap<>();
      response.put("id", rows.getString("id"));
      response.put("status", status);
      response.put("created_at", rows.getString("created_at"));
      response.put("equipment_code", request.get("equipment_code"));
      response.put("customer_id", customerId);
      response.put("debug_sql", sql);
      return ResponseEntity.ok(response);
    } catch (Exception exception) {
      exception.printStackTrace();
      return ResponseEntity.ok(Map.of("success", false, "message", exception.getMessage()));
    }
  }
}
```

Hãy đưa ra tối thiểu 8 nhận xét (review comments) cụ thể, chi tiết, được sắp xếp theo đúng thứ tự ưu tiên sau:
1. Spec delta (Độ lệch đặc tả so với yêu cầu nghiệp vụ thực tế)
2. Bảo mật (Security vulnerabilities)
3. Kiểm thử & Ràng buộc dữ liệu (Validation & Testing)
4. Độ phức tạp (Complexity)
5. Phong cách lập trình (Coding style / Best practices)

Đồng thời, hãy gợi ý cấu trúc mã nguồn đã sửa lỗi an toàn tương ứng.

**Đã thực hiện:** lưu [review comments](../reviews/security-code-review.md) theo đúng năm nhóm
ưu tiên và [cấu trúc sửa lỗi an toàn](../reviews/proposed-safe-design.md). Fixture này chỉ để review,
không phải application được triển khai.
