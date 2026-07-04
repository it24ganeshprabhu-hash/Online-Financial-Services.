# OFS Banking Application

A Spring Boot banking application that integrates MySQL, MongoDB, QR payments, AI advice, Aadhaar verification, paper trading portfolio support, and admin workflows.

## Key Features

- User registration with Aadhaar verification
- Login and profile update endpoints
- Account deposit, withdrawal, transfer, and passbook history
- Admin user management and account status updates
- Support ticket creation and admin replies with notifications
- QR code generation and scan payment support
- Financial AI consultation endpoint
- Paper trading operations with portfolio and holdings support
- MongoDB-backed Aadhaar verification and MySQL-backed transactional data

## Technology Stack

- Java 17
- Spring Boot 3.5.10
- Spring Web
- Spring Data JPA
- Spring Data MongoDB
- Spring Boot Mail
- Spring Validation
- MySQL Connector/J
- PostgreSQL JDBC driver (runtime)
- Lombok
- ZXing QR code generation

## Project Structure

- `src/main/java/com/example/ofs` - application source code
- `src/main/resources/application.properties` - runtime configuration
- `src/main/resources/static` - static HTML front-end pages
- `pom.xml` - Maven project definition

## Requirements

- JDK 17
- Maven (or use the bundled Maven wrapper)
- Local MySQL instance
- MongoDB Atlas or MongoDB connection URI
- SMTP email account for mail delivery
- AI API key for AI advice integration

## Configuration

Update `src/main/resources/application.properties` with your own environment values.

Example configuration values:

```properties
server.port=8081
spring.profiles.active=mysql

spring.config.activate.on-profile=mysql
spring.datasource.url=jdbc:mysql://localhost:3306/ofs_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update

spring.data.mongodb.uri=mongodb+srv://<user>:<password>@<cluster>/Adhaar-demo?retryWrites=true&w=majority

spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@example.com
spring.mail.password=your-email-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

groq.api.key=YOUR_AI_API_KEY
```

## Build and Run

From the project root:

```bash
./mvnw clean package
./mvnw spring-boot:run
```

Or with Maven installed:

```bash
mvn clean package
mvn spring-boot:run
```

The application listens on port `8081` by default.

## API Overview

### User Endpoints

- `POST /register` - register a new user
- `POST /login` - user login
- `GET /get-details/{id}` - get user profile details
- `POST /update` - update user information
- `POST /update-photo` - update user photo URL
- `GET /get-profile-photo/{userId}` - fetch profile photo
- `GET /users` - list customers with sorting
- `GET /users/{keyword}` - search customers by username

### Transaction Endpoints

- `POST /deposit` - deposit funds
- `POST /withdraw` - withdraw funds
- `POST /transfer` - transfer funds between users
- `POST /apply-interest/{id}` - apply monthly interest
- `GET /passbook/{id}` - fetch transaction history
- `POST /trading/deposit-log` - log trading deposit

### Admin Endpoints

- `PUT /admin/update-status/{targetId}` - update user account status (admin only)

### Aadhaar Verification

- `POST /aadhaar/start` - begin Aadhaar verification
- `POST /aadhaar/verifyOtp` - verify Aadhaar OTP
- `GET /aadhaar/fetchUser` - fetch Aadhaar profile
- `GET /aadhaar/userData` - get verified Aadhaar profile data

### AI and QR Endpoints

- `GET /api/ai/consult` - get financial advice from AI
- `GET /api/generate-my-qr/{username}` - generate a QR code for a username
- `POST /api/scan-pay` - process QR payment transfer

### Trading Endpoints

- `POST /api/trade/execute` - execute a trade order
- `POST /api/trade/transfer-funds` - move funds to trading portfolio
- `POST /api/trade/withdraw-funds` - withdraw funds from portfolio
- `GET /api/trade/portfolio-details` - get trading portfolio details
- `GET /api/trade/holdings` - list security holdings

### Support Endpoints

- `POST /api/support/create` - create a support ticket
- `GET /api/support/admin/all` - list support tickets for admin
- `POST /api/support/admin/reply/{ticketId}` - admin reply to ticket

### Alerts

- `POST /api/alerts/send` - send an alert/notification message

### History

- `GET /histories` - list all transaction histories
- `GET /histories/admin/{adminId}` - admin or user-specific history

## Frontend

Static user interface pages are available under `src/main/resources/static`.

Common pages include:

- `home.html`
- `login.html`
- `signup.html`
- `dashboard.html`
- `history.html`
- `services.html`
- `settings.html`
- `trading.html`
- `aadhaar.html`
- `admin.html`

## Notes

- The app uses both MySQL and MongoDB for different data models.
- The default active Spring profile is `mysql`.
- The PostgreSQL profile is included but commented out for Aiven/remote use.
- Use `.env` or externalized config for sensitive values in production.


