# Work Order Domain Technical Design

## 1. Traceability

- **Issue:** WO-201
- **Purpose:** Define the UI, persistence, and REST API contracts for creating work orders.
- **Scope:** Create a work order for an active customer with a title, optional description, and priority.

## 2. Domain Overview

A work order represents a customer-requested piece of work. New work orders are created in `DRAFT` status. The work order references an existing active customer and records its creation timestamp.

### 2.1 In Scope

- Work order creation through `POST /api/v1/work-orders`.
- Client-side and server-side validation of work order fields.
- Persistence of title, description, priority, status, customer, and creation time.
- Authorization through a bearer token.

### 2.2 Non-Goals

- Work order assignment, scheduling, or execution workflows.
- Status transitions beyond the initial `DRAFT` status.
- Customer creation or customer lifecycle management.
- Updating, deleting, or listing work orders.

## 3. UI Validation Matrix

The UI must validate these fields before submitting the create request. The API must repeat the same validation because client-side validation cannot be trusted as an authorization or data-integrity boundary.

| Field Name | Type | Required | Rules / Constraints |
| :--- | :--- | :--- | :--- |
| `title` | String | Yes | Minimum 5 characters; maximum 255 characters. |
| `description` | String | No | Maximum 2000 characters. |
| `priority` | Enum | Yes | Allowed values: `LOW`, `MED`, `HIGH`, `CRITICAL`. Defaults to `MED`. |
| `customer_id` | UUID | Yes | Must reference a valid active customer. |

### 3.1 UI Behavior

- Prevent submission when a required field is missing or invalid.
- Display validation errors next to the associated field.
- Preserve entered values when validation fails.
- Submit `priority: "MED"` when the user leaves priority at its default.
- Treat `customer_id` as a selected customer identifier, not free-form customer text.

Customer activity validation is performed by the API/service layer against the customer source of truth. The UI may filter its customer selector to active customers, but that filter does not replace server-side validation.

## 4. Data Model

### 4.1 Work Order Attributes

| Attribute | PostgreSQL Type | Nullable | Default | Description |
| :--- | :--- | :---: | :--- | :--- |
| `id` | `UUID` | No | `gen_random_uuid()` | Work order identifier. |
| `title` | `VARCHAR(255)` | No | None | Short description of the work. |
| `description` | `TEXT` | Yes | None | Optional detailed description. The API applies the 2000-character limit. |
| `priority` | `VARCHAR(20)` | No | `MED` | Work order priority. |
| `status` | `VARCHAR(20)` | No | `DRAFT` | Initial lifecycle status. |
| `customer_id` | `UUID` | No | None | Identifier of the associated customer. |
| `created_at` | `TIMESTAMPTZ` | No | `CURRENT_TIMESTAMP` | Time at which the work order was created. |

### 4.2 PostgreSQL DDL

```sql
CREATE TABLE work_orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    priority VARCHAR(20) NOT NULL DEFAULT 'MED',
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    customer_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_priority CHECK (priority IN ('LOW', 'MED', 'HIGH', 'CRITICAL'))
);
```

The database definition assumes the PostgreSQL `pgcrypto` extension is available for `gen_random_uuid()`. The supplied schema does not include a foreign-key constraint because the customer table definition is outside WO-201. The application/service layer must verify that `customer_id` identifies an active customer before insertion.

## 5. REST API Contract

### 5.1 Create Work Order

**Method:** `POST`  
**Path:** `/api/v1/work-orders`  
**Authentication:** `Authorization: Bearer <token>`  
**Success status:** `201 Created`

The authenticated caller must be authorized to create work orders. The service must validate the bearer token before processing the request body.

#### Request Headers

```http
Authorization: Bearer <token>
Content-Type: application/json
```

#### Request Body

```json
{
  "title": "HVAC Repair Unit 4",
  "description": "System reporting error code E-42",
  "priority": "HIGH",
  "customer_id": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11"
}
```

#### Request Contract

| Property | JSON Type | Required | Contract |
| :--- | :--- | :---: | :--- |
| `title` | String | Yes | 5 to 255 characters. |
| `description` | String | No | At most 2000 characters. |
| `priority` | String | Yes | One of `LOW`, `MED`, `HIGH`, or `CRITICAL`; default is `MED`. |
| `customer_id` | String | Yes | UUID for an active customer. |

#### Success Response: `201 Created`

```json
{
  "id": "c9bf9e57-1685-4c89-bafb-ff5af830be8a",
  "status": "DRAFT",
  "created_at": "2026-08-30T10:00:00Z"
}
```

The `created_at` value is serialized as an ISO 8601 UTC timestamp. The response does not echo the request body; the created resource identifier, initial status, and creation time are sufficient for this contract.

#### Error Outcomes

| HTTP Status | Condition |
| :--- | :--- |
| `401 Unauthorized` | Authorization header is missing, malformed, or contains an invalid token. |
| `422 Unprocessable Entity` | A request field violates the validation matrix, including an invalid UUID or enum value. |
| `404 Not Found` | The referenced customer does not exist. |
| `409 Conflict` | The customer exists but is not active or cannot accept a new work order. |

Error responses should identify the failing field when the failure is caused by request validation. Authentication and customer existence/activity checks must not disclose sensitive customer details beyond the applicable status code.

## 6. Processing Flow

1. Authenticate the bearer token.
2. Parse the JSON request body.
3. Validate `title`, `description`, `priority`, and `customer_id` against the UI validation matrix.
4. Resolve `customer_id` and verify that the customer is active.
5. Insert the work order with `status = 'DRAFT'` and the database-generated `id` and `created_at` values.
6. Return the created identifier, status, and timestamp with HTTP `201 Created`.

The insert and customer activity check must be handled transactionally so a work order is not created for an invalid or inactive customer.

## 7. Acceptance Criteria

- An authenticated request with a valid payload returns `201 Created`.
- A missing or too-short `title` is rejected with a field-level validation error.
- A `description` longer than 2000 characters is rejected.
- An omitted `priority` resolves to `MED`.
- An unsupported priority is rejected.
- A missing or malformed `customer_id` is rejected.
- A customer that does not exist or is inactive cannot receive a new work order.
- A successful creation persists `DRAFT` status and returns the generated `id` and `created_at`.
