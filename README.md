# 📰 GNN News Network – Backend System

A **scalable backend system for a digital news aggregation and publishing platform** built using **Spring Boot**.  
The platform allows admins and editors to publish **text, images, videos, and AI-generated Gujarati news**, while providing secure APIs for multiple frontend applications.

This project demonstrates **production-ready backend architecture**, including authentication, media management, caching, and cloud deployment.

---

# 🚀 Features

## 🔐 Authentication & Security
- JWT-based authentication
- Role-based authorization (Admin / Editor / Reporter / Public)
- Secure API endpoints using Spring Security

## 📰 News Management
- Create, update, publish, and delete news articles
- Category-based news classification
- Trending news retrieval
- Pagination and sorting for optimized API responses

## 🖼 Media Management
- Image and video upload support
- Media storage using Cloudinary
- Secure media URL management

## 🌍 External News Integration
- Fetch news from external news APIs
- Aggregate and normalize news content

## ⚡ Performance Optimization
- Redis caching support
- Efficient database queries using JPA
- Pagination and optimized REST responses

## 🧠 AI Integration
- AI-powered Gujarati text-to-speech
- News audio generation using Google Cloud Text-to-Speech

## 📊 Monitoring
- Application health monitoring using Spring Boot Actuator

---

# 🏗 System Architecture

```
Users
   │
   ▼
Frontend Applications
(Web / Admin / Reporter)
   │
   ▼
Spring Boot Backend API
   │
   ├── Authentication (JWT)
   ├── News Management
   ├── Media Upload Service
   ├── External News APIs
   │
   ▼
Database (MySQL - Aiven)
   │
   ▼
Media Storage (Cloudinary)
```

---

# 🛠 Tech Stack

## Backend
- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- Spring Validation
- Spring WebFlux

## Database
- MySQL (Aiven Cloud)

## Media Storage
- Cloudinary

## Caching
- Redis

## Authentication
- JWT (JSON Web Token)

## DevOps & Deployment
- Docker
- Render

## Tools
- Maven
- Git & GitHub
- Postman

---

# 📂 Project Structure

```
src/main/java/com/gnn/newsnetwork

├── config          # Security & application configurations
├── controller      # REST API endpoints
├── service         # Business logic layer
├── repository      # Database access layer
├── model           # Entity models
├── dto             # Data transfer objects
├── security        # JWT authentication & filters
└── util            # Utility classes
```

---

# ⚙️ Environment Configuration

Create an **application-prod.yml** or configure environment variables.

Example:

```yaml
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

cloudinary:
  cloud-name: ${CLOUDINARY_NAME}
  api-key: ${CLOUDINARY_KEY}
  api-secret: ${CLOUDINARY_SECRET}
```

---

# 🐳 Running with Docker

## Build Docker image

```bash
docker build -t gnn-news-backend .
```

## Run Docker container

```bash
docker run -p 8080:8080 gnn-news-backend
```

---

# 🚀 Deployment

Production deployment architecture:

```
Domain (liomax.in)
        │
        ▼
Render Backend (Docker)
        │
        ▼
Aiven MySQL Database
        │
        ▼
Cloudinary Media Storage
```

---

# 📬 API Testing

You can test APIs using **Postman**.

Example endpoints:

```
POST   /api/auth/login
POST   /api/news/create
GET    /api/news/trending
GET    /api/news/category/{id}
```

---

# 📈 Future Improvements

- Elasticsearch for advanced news search
- Real-time notifications
- AI news summarization
- Video streaming optimization
- CDN integration for faster media delivery

---

# 👨‍💻 Author

**Karan**  
Backend Developer | Java | Spring Boot | System Design  

GitHub: [https://github.com/yourusername](https://github.com/karanspatel7075)

---

# ⭐ Contributing

Contributions, issues, and feature requests are welcome.

Feel free to fork the repository and submit a pull request.
