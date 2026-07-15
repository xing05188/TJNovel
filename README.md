# OurNovel2

This project is a refactored version of [OurNovel](https://github.com/ggyy1122/OurNovel), a comprehensive novel platform that integrates creation, management, and reading functionalities. OurNovel2 represents a complete architectural overhaul, transforming the original monolithic application into a modern, scalable microservices-based system.

## Engineering Highlights

### Engineering Highlight 1: Microservice Architecture Design
The platform has been restructured into a distributed microservices architecture, featuring six independent services:
- **API Gateway**: Centralized entry point using Spring Cloud Gateway for routing and load balancing
- **User Service**: Handles authentication, user profiles, and user management
- **Content Service**: Manages novels, chapters, comments, and content-related operations
- **Transaction Service**: Processes purchases, recharges, rewards, and financial transactions
- **Admin Service**: Provides administrative functions and content moderation
- **Notification Service**: Handles asynchronous messaging and notifications

This architecture enables independent scaling, deployment, and maintenance of each service, significantly improving system resilience and development efficiency.

### Engineering Highlight 2: Containerization
All services are fully containerized using Docker, with each microservice packaged in its own container. The project includes:
- Individual Dockerfiles for each service
- Docker Compose configuration for orchestration
- Automated build scripts for local and production environments
- Consistent deployment across development, testing, and production environments

Containerization ensures environment consistency, simplifies deployment, and enables easy horizontal scaling of services.

### Engineering Highlight 3: API Documentation
Comprehensive API documentation is automatically generated using SpringDoc OpenAPI 3.0. Each microservice exposes interactive Swagger UI interfaces, providing:
- Complete API endpoint documentation
- Request/response schema definitions
- Interactive testing capabilities
- Real-time API exploration

Access Swagger UI at `http://localhost:{port}/swagger-ui.html` for each service, facilitating development, testing, and integration.

### Engineering Highlight 4: Azure Cloud Services Integration
The platform leverages Microsoft Azure cloud services for production-grade infrastructure:
- **Azure MySQL**: Managed database service for reliable data persistence
- **Azure Blob Storage**: Store the unstructured data
- Cloud-native deployment capabilities for high availability and scalability

## Technology Stack

**Backend:**
- Java 17
- Spring Boot 3.3.5
- Spring Cloud 2023.0.3
- Spring Cloud Gateway
- SpringDoc OpenAPI 2.6.0
- Azure Service Bus 7.17.3
- Docker & Docker Compose

**Frontend:**
- Vue.js 3
- Element Plus
- Vue Router
- Pinia

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+
- Docker & Docker Compose
- Node.js 16+ (for frontend)

### Backend Setup
```bash
cd BackEnd/OurNovel2_Backend
docker-compose up -d
```

### Frontend Setup
```bash
cd FrontEnd/OurNovel2_Frontend
npm install
npm run serve
```

## Project Structure
```
OurNovel2/
├── BackEnd/
│   └── OurNovel2_Backend/     # Microservices backend
│       ├── api-gateway/        # API Gateway service
│       ├── user-service/       # User management service
│       ├── content-service/   # Content management service
│       ├── transaction-service/# Transaction processing service
│       ├── admin-service/     # Administration service
│       └── notification-service/ # Notification service
└── FrontEnd/
    └── OurNovel2_Frontend/    # Vue.js frontend application
```

## License

MIT License - Copyright © 2025 OurNovel2 Team

