# OTP Authentication System

Spring Boot application providing OTP-based authentication via Email or Phone (SMS), with a simple HTML/CSS/JS frontend and MySQL/H2 persistence profiles.

## 🚀 Live Demo

🔗 https://otp-authentication-system-production-9678.up.railway.app

## Features

- Request and verify OTP via REST API
- Email and SMS channels (pluggable providers)
- Rate limiting and TTL for OTPs
- Profiles: `dev` (H2) and `mysql` (MySQL)
- Static frontend served from `/` for quick testing

## Tech Stack

- Java 17, Spring Boot 3
- Spring Web, Spring Data JPA, Spring Mail
- H2 (dev) and MySQL (prod-like)

## Run Locally

1. Build: `mvn -q -DskipTests package`
2. Dev profile (H2): `java -jar target/otp-auth-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev`
3. MySQL profile:
   - Set env vars: `MYSQL_URL`, `MYSQL_USER`, `MYSQL_PASSWORD`
   - Run: `java -jar target/otp-auth-0.0.1-SNAPSHOT.jar --spring.profiles.active=mysql`
4. Open: `http://localhost:8080` (or your chosen port)

## Environment Variables

- Database: `MYSQL_URL`, `MYSQL_USER`, `MYSQL_PASSWORD`
- Email: `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `otp.mail.from`
- SMS: `otp.sms.enabled`, `otp.sms.provider` (`twilio` or `log`), `otp.sms.from`, `otp.twilio.accountSid`, `otp.twilio.authToken`
- General: `SPRING_PROFILES_ACTIVE`

## Deployment

- Includes `Dockerfile` for containerized deploys
- Works on platforms like Railway/Render; set env vars and start command:
  - Build: `mvn -q -DskipTests package`
  - Start: `java -jar target/otp-auth-0.0.1-SNAPSHOT.jar --server.port=$PORT`

## Security Notes

- Do not log OTPs in production
- Keep SMTP/Twilio credentials in environment variables only
- Use HTTPS and consider rate limiting and brute-force protections


