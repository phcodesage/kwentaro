# Security Policy

Kwentaro stores shop records and offline account credentials on the device. Please report vulnerabilities **privately**. Use GitHub's "Report a vulnerability" (Security tab) or email rechceltoledo@gmail.com, and don't open a public issue.

## Scope notes

- Passwords and recovery codes are stored only as salted PBKDF2-HMAC-SHA256 hashes (`data/auth/PasswordHasher.kt`).
- Each account's data is in separate files inside the app's private storage. Accounts separate records between people who share a phone. They are not encryption: anyone with root or physical file access can read the files.
- The `foss` build requests no internet permission. The `full` build includes Google ML Kit, which adds network access for its telemetry.

We aim to acknowledge reports within 7 days.
