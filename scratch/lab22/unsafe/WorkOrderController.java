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
