# 🛡 eKYC Backend System

A secure Electronic Know Your Customer (eKYC) backend system built with Spring Boot.
This system supports user registration, KYC submission, document upload, automated review flow, and admin decision handling.

---

# 🎥 Demo Video

> 📌 Demo video link 

```
https://youtu.be/EW1dtgmaYqM
```

---

# 📌 Project Overview

This project simulates a real-world fintech KYC workflow including:

* User account creation
* KYC submission process
* Secure document upload (IC Front, IC Back, Selfie)
* Risk scoring support
* Admin review & decision handling
* Local file storage system
* Status-driven workflow control

The system is designed to mimic compliance-grade identity verification flows.

---

# 🏗 System Architecture

```
User → Submit KYC → Upload Documents → Submit
        ↓
Admin → Acknowledge → Decision (Approve / Reject / Retry / Manual Review)
```

---

# 🚀 Features

## 👤 User Side

* JWT Authentication
* Profile management
* KYC submission
* Upload multiple documents
* Status tracking
* Retry support when required

## 🛠 Admin Side (Postman Simulation)

* Get KYC list by status
* Acknowledge KYC
* Approve
* Reject (with remark required)
* Mark for manual review
* Set retry required
* Stream uploaded documents securely

---

# 🧠 KYC Status Flow

| Status         | Description                    |
| -------------- | ------------------------------ |
| DRAFT          | User started but not submitted |
| SUBMITTED      | User submitted KYC             |
| PROCESSING     | Under automated review         |
| MANUAL_REVIEW  | Requires human verification    |
| APPROVED       | KYC approved                   |
| REJECTED       | Permanently rejected           |
| RETRY_REQUIRED | User must fix and resubmit     |
| EXPIRED        | Request expired                |

---

# 🗂 File Storage

Uploaded files are stored locally:

```
uploads/
└── kyc/
    └── {userId}/
        ├── front.png
        ├── back.png
        └── selfie.png
```

Database stores only relative file paths.

Admin retrieves files using:

```
GET /api/admin/file?path=kyc/{userId}/filename.png
```

---

# 🛠 Tech Stack

* Java 21
* Spring Boot
* Spring Security (JWT)
* Hibernate / JPA
* SQL Server
* Local File Storage
* Postman (Admin simulation)

---

# 🔐 Authentication

JWT-based authentication.

Protected endpoints require:

```
Authorization: Bearer <token>
```

---

# 📡 API Endpoints

## User

```
POST   /api/auth/login
GET    /api/users
POST   /api/kyc/start
POST   /api/kyc/upload
POST   /api/kyc/submit
```

## Admin

```
GET    /api/admin?status=SUBMITTED
POST   /api/admin/kyc/ack/{kycId}
POST   /api/admin/kyc/decision/{kycId}
GET    /api/admin/file?path=...
```

---

# 📬 Example Decision Request

```json
{
  "decision": "APPROVED",
  "remark": null,
  "riskLevel": "LOW",
  "riskScore": 12.5,
  "providerRefId": "PROV-123"
}
```

Allowed decisions:

* APPROVED
* REJECTED
* RETRY_REQUIRED
* MANUAL_REVIEW

---

# 🏁 Running The Project

## 1️⃣ Configure Database

Update `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:sqlserver://localhost:1433;databaseName=ekycDB
```

## 2️⃣ Run Application

```
mvn spring-boot:run
```

Application runs at:

```
http://localhost:8080
```

---

# 📦 Project Structure

```
domain/         → Entities & Enums
application/    → DTO & Services
infrastructure/ → Repositories & Storage
controller/     → REST Controllers
security/       → JWT & Security Config
```

---

# 🔮 Future Improvements

* Add role-based admin system
* Add audit trail table
* Integrate third-party AI provider
* Move file storage to AWS S3
* Add frontend admin dashboard
* Add rate limiting & IP tracking
* Add transaction history logging

---

# 🏆 Key Learning Points

* Secure JWT implementation
* Controlled status transition design
* File storage abstraction
* Clean DTO separation
* Transactional service handling
* State-based validation logic

---

# 📄 License

This project is for demonstration and educational purposes.


