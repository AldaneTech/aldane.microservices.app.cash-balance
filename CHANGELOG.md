# Changelog

## [1.0.0] - 2026-10-13

### Added
- Dashboard totals and charts, filtered movement search, and balance-safe movement editing and deletion.
- Authenticated profile management, password changes, and persistent Spanish/English language preferences.
- Versioned Docker images published to GitHub Container Registry when a matching release tag is pushed.

### Changed
- Upgraded the API dependency to 1.5.0.
- Release builds run tests before publishing the image.
- Image publication no longer automatically deploys to Kubernetes; VPS deployment is managed separately.

### Deployment
- Apply `src/main/resources/db.changelog/migrate-user-language.sql` before running this version against an existing database.
- Existing tokens issued before the authentication changes must be renewed.
