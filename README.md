# Cash Balance microservice

Uses the generated Java server contract from Cash Balance API **1.4.0**. Define changes in `../aldane.microservices.libraries.cash-balance-api/openapi.yaml` first.

## Build and verify

Build/install the API locally before the microservice:

```bash
mvn -f ../aldane.microservices.libraries.cash-balance-api/pom.xml install -DskipTests
mvn test
```

Tests use H2 for query integration and Mockito's explicit Java agent. They do not require a running PostgreSQL database. Production still uses PostgreSQL.

## New authenticated endpoints

Paths are relative to `/cash-balance`. Send `Authorization: Bearer <token>`.

| Method | Path | Behavior |
| --- | --- | --- |
| GET | `/me` | Own profile, without password; does not modify last access. |
| GET | `/account-entries` | Own movements in active wallets, filtered and paginated. |
| GET | `/dashboard/summary` | Income, expenses, net, entry count and current balance. |
| GET | `/dashboard/by-category` | Income, expenses, net and count by category, including uncategorized entries. |
| GET | `/dashboard/monthly` | Income, expenses, net and count by month (`YYYY-MM`). |
| POST | `/change-password` | Verify current password, replace it and invalidate previous tokens. Returns 204; log in again. |

Search accepts `walletId`, `from`, `to`, `categoryId`, `typeId`, `search` (literal case-insensitive comment search), `page` (zero-based), `size` (1–100), `sortBy` (`date`, `amount`, `id`) and `direction` (`asc`, `desc`). Default: page 0, size 20, date descending. Ordering uses ID as a tie-breaker. Empty results return 200 with an empty items array and pagination metadata.

Dashboard endpoints accept `walletId`, `from` and `to`. Date bounds are inclusive in UTC. Without bounds, all dates are included. Positive amounts count as income, negative amounts as expenses (reported positively); net is income minus expenses. Summary balance is the **current** balance of selected active wallets, irrespective of the date range. Monthly results contain only months with dated entries.

All new queries use the current user's data, including for admins. Explicitly selecting an inactive or foreign wallet returns 404. Wallets organize movements; transfers and changing an entry's wallet are outside this change.

Password request:

```json
{"currentPassword":"old-password","newPassword":"new-password"}
```

New passwords need at least eight characters, at most 72 UTF-8 bytes, and must differ from the current password. Incorrect current password returns 403; invalid new password returns 400. `/updateUser` rejects a nonempty password; use `/change-password` instead.

The updated JWT includes a keyed credential fingerprint. On first deployment, existing tokens must be renewed by logging in. Password changes subsequently invalidate previously issued tokens without a database schema migration.

## Movement amounts and editing

Creating a movement requires `amount`, `wallet.id` and an active `type_operation.id`. Type **1 (income)** always stores `abs(amount)`; type **2 (expense)** always stores `-abs(amount)`, regardless of the submitted sign. Other active types preserve the submitted sign. Stored signed amounts drive the wallet balance and dashboard totals.

Editing requires `id` and `amount`. It updates comment, category, brand, store, date and type. Omitted or null category/brand/store clears the association. Omitted date or type keeps its existing value. References must be active and belong to the wallet owner. The wallet remains fixed. Changing the type recalculates the signed amount and applies only the difference from the old stored amount to the balance.


## User language preferences

`GET /me/preferences` and `PUT /me/preferences` expose the authenticated user's
language (`es` or `en`). New and existing accounts default to Spanish. The update
body is `{ "language": "en" }`; it cannot specify another user.

Before deploying this version to an existing PostgreSQL database, apply
`src/main/resources/db.changelog/migrate-user-language.sql`. The script is
idempotent and only adds the language column. A matching Liquibase change set is
included for installations that manage their schema through Liquibase. This
repository currently has Liquibase properties but no Liquibase runtime dependency;
the properties alone do not apply the migration.
