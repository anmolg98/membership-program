# Membership Program

Spring Boot backend for membership plans, tier benefits, eligibility checks, and subscription lifecycle management.

## Run

Requires Java 17 or later.

```bash
./mvnw spring-boot:run
```

Run the tests with:

```bash
./mvnw test
```

The application starts at `http://localhost:8080`.

## APIs

### Catalog

```text
GET /api/v1/plans
GET /api/v1/tiers
GET /api/v1/membership-options
GET /api/v1/users/{userId}/membership-options
```

### Users and orders

```text
POST /api/v1/users
POST /api/v1/users/{userId}/orders
```

### Membership

```text
POST   /api/v1/users/{userId}/subscriptions
GET    /api/v1/users/{userId}/membership
PATCH  /api/v1/users/{userId}/subscription/tier
POST   /api/v1/users/{userId}/subscription/evaluate-tier
DELETE /api/v1/users/{userId}/subscription
```

## Design

- Plans define price and duration. Monthly, quarterly, and yearly plans are provided.
- Tiers define benefits and eligibility rules. Silver, Gold, and Platinum tiers are provided.
- A user can subscribe to an eligible tier, upgrade after satisfying the target tier rules, or downgrade without an eligibility check.
- Cancellation is immediate. A cancelled or expired user can subscribe again.
- The highest eligible tier can also be evaluated and applied by the system.

Eligibility rules use the Strategy pattern. The current strategies check completed-order count, monthly order value, and user cohort. A new criterion can be added by introducing a new rule type and strategy implementation.

Subscription updates are transactional. Customer and subscription rows are locked during mutations, and version columns protect against stale updates.

## Local database

The project uses an in-memory H2 database. The console is available at `http://localhost:8080/h2-console`.

```text
JDBC URL: jdbc:h2:mem:firstclub
User: sa
Password: <empty>
```
