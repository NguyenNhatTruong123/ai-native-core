# API Rules

Áp dụng khi viết REST API. Hợp đồng Work Order hiện nằm trong
[work-order-decomposition.md](work-order-decomposition.md), mục 3–7.
`api-spec.md` và `domain-model.md` hiện chưa có nội dung; không dùng chúng để suy đoán schema.

## A01 — Bám đặc tả

Đọc đặc tả task trước khi sinh DTO/handler. Không thêm endpoint, trường JSON hoặc nghiệp vụ
chỉ vì thường thấy ở dự án khác. Nếu thiếu thông tin, ghi câu hỏi/giả định để review.

## A02 — REST resource

Dùng danh từ số nhiều, chữ thường, dấu gạch nối và version trong đường dẫn.
Work Order creation dùng chính xác `POST /api/v1/work-orders`.
Không đổi thành `/api/workorders`, `/createWorkOrder` hoặc tự thêm API list/update/delete.

## A03 — Request schema

Chỉ cho phép các trường sau. Từ chối trường lạ bằng `400`, không bind trực tiếp request vào entity.

| Trường | Kiểu | Quy tắc |
| --- | --- | --- |
| `title` | String | Bắt buộc; 5–255 ký tự. |
| `description` | String | Tùy chọn; tối đa 2000 ký tự. |
| `priority` | String enum | `LOW`, `MED`, `HIGH`, `CRITICAL`; bỏ trường thì dùng `MED`. |
| `customer_id` | String UUID | Bắt buộc; phải tham chiếu khách hàng tồn tại và active. |

Đặc tả vừa ghi priority là required vừa có acceptance criterion cho phép bỏ trường.
Theo mục 7, thiếu priority thì dùng `MED`; không chuyển một giá trị enum sai thành `MED`.
Không nhận `id`, `status`, `created_at` từ client. Không tự đổi snake_case thành camelCase trên wire.

## A04 — Validation

Kiểm tra JSON, kiểu dữ liệu, độ dài, UUID và enum ở server; không tin validation của UI.
DTO Spring dùng Bean Validation và `@Valid`; service kiểm tra khách hàng và transaction.
Phân loại lỗi theo A06, không phụ thuộc hoàn toàn vào status mặc định của framework.

## A05 — Success response

Trả `201 Created`, `Content-Type: application/json`, chỉ có `id`, `status`, `created_at`.
ID và thời gian do hệ thống/database tạo; trạng thái ban đầu là `DRAFT`.
`created_at` dùng ISO 8601 UTC. Không echo request hay thêm envelope `data`, `success`, `message`.

```json
{
  "id": "c9bf9e57-1685-4c89-bafb-ff5af830be8a",
  "status": "DRAFT",
  "created_at": "2026-08-30T10:00:00Z"
}
```

## A06 — HTTP status

| Status | Điều kiện |
| --- | --- |
| `400` | JSON không đọc được, sai cấu trúc/kiểu JSON hoặc có trường ngoài schema. |
| `401` | Thiếu bearer token, sai định dạng hoặc token không hợp lệ. |
| `403` | Token hợp lệ nhưng caller không có quyền tạo Work Order. |
| `404` | Khách hàng tham chiếu không tồn tại. |
| `409` | Khách hàng không active hoặc không thể nhận Work Order mới. |
| `415` | Request dùng media type không được hỗ trợ. |
| `422` | Thiếu trường bắt buộc hoặc vi phạm độ dài, định dạng UUID, giá trị enum. |
| `500` | Lỗi server không dự kiến; trả mô tả chung, không lộ thông tin nội bộ. |

`401/404/409/422` lấy từ đặc tả; `400/403/415/500` là quy tắc giao thức bổ sung của Rules Pack.
Prompt HTTP trong bài lab chỉ là ví dụ. Không áp dụng “mọi lỗi đều 400” để thay đổi
quy định `422` hiện có của repo. Bản nháp Lab2.1 chọn `sum`, không triển khai HTTP handler.

## A07 — Problem Details

Mọi lỗi HTTP của API dùng `Content-Type: application/problem+json`, phong cách RFC 7807.
Profile của repo chỉ dùng 5 trường chuẩn sau; không thêm extension khi chưa có đặc tả:

| Trường | Quy tắc của repo |
| --- | --- |
| `type` | `about:blank` khi chưa có loại lỗi với URI được đặc tả. |
| `title` | Tên ngắn theo status, không chứa dữ liệu người dùng. |
| `status` | Số nguyên, trùng HTTP status thực tế. |
| `detail` | Mô tả an toàn; có thể nêu tên trường và constraint, không echo giá trị đầu vào. |
| `instance` | Path của request, không chứa query string có dữ liệu nhạy cảm. |

```http
HTTP/1.1 422 Unprocessable Entity
Content-Type: application/problem+json

{
  "type": "about:blank",
  "title": "Unprocessable Entity",
  "status": 422,
  "detail": "title must contain between 5 and 255 characters.",
  "instance": "/api/v1/work-orders"
}
```

**Sai:** trả `200` kèm `{"error": "failed"}`, thêm `stackTrace`, hoặc tự tạo `errors[]`.
Spring dùng `ProblemDetail` và ánh xạ ngoại lệ tập trung; cả lỗi parse/validation và
lỗi từ authentication filter cũng phải tuân theo profile, không trả body mặc định khác cấu trúc.

## A08 — Bảo vệ nghiệp vụ

Xác thực và kiểm tra quyền trước khi xử lý nghiệp vụ. Kiểm tra customer active và insert
trong transaction phù hợp với nguồn dữ liệu để tránh tạo Work Order cho customer không hợp lệ.
Không giả lập “mọi UUID đều active”; không thêm bảng/customer service chưa được đặc tả.

## A09 — Kiểm chứng schema

Test API phải kiểm tra status, media type, chính xác tập khóa JSON, các boundary, trường lạ,
token sai và customer không tồn tại/inactive. Quy tắc này chưa được kiểm thử bằng scratch `sum`.

## Tài liệu tham chiếu

[RFC 7807](https://www.rfc-editor.org/rfc/rfc7807) là chuẩn được nêu trong lab;
[RFC 9457](https://www.rfc-editor.org/rfc/rfc9457.html) đã thay thế RFC 7807 và giữ mô hình Problem Details.
