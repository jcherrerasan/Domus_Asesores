# 02_INFRAESTRUCTURA.md

## **Contenido**
1. [Arquitectura General](#1-arquitectura-general)
2. [Docker](#2-docker)


## 1. Arquitectura General

El proyecto crm domus está compuesto por:

* Backend: Spring Boot (Java 21)
* Base de datos: [PostgreSQL](https://www.postgresql.org/about/)
* Automatización: [n8n](https://n8n.io/)
* Almacenamiento documental: [Google Drive](https://drive.google.com/)
* Contenedorización: Docker Compose y [Docker](https://docs.docker.com/desktop/) 

**Arquitectura:**

Cliente → Backend (Spring Boot API REST) → PostgreSQL
Cliente → Backend → Google Drive
n8n → Webhook protegido → Backend

---

## 2. Docker

El proyecto incluye:

* Dockerfile para el backend
  
 ```dockerfile
  FROM eclipse-temurin:21-jdk
  WORKDIR /app
  COPY target/*.jar app.jar
  EXPOSE 8080
  ENTRYPOINT ["java", "-jar", "app.jar"]
 ```
 
* docker-compose.yml con:

  * Servicio postgres
  * Servicio backend
  * Servicio n8n

Para levantar el entorno:
<code>docker compose up --build -d</code>


## 3. Variables de entorno

Se utiliza archivo .env para evitar credenciales hardcodeadas.

Variables principales:

POSTGRES_DB
POSTGRES_USER
POSTGRES_PASSWORD
JWT_SECRET
CRM_N8N_API_KEY

---

## 4. Seguridad

* Autenticación JWT
* Roles: SUPER_ADMIN y AGENT
* Control por asignación
* Rate limiting con Bucket4j
* Auditoría avanzada
* Sistema de alertas de seguridad

---

## 5. Base de datos

PostgreSQL con migraciones Flyway:

V1 → Tablas iniciales
V7 → Deals
V8 → Documentos
V9 → Auditoría extendida
V10 → Security alerts

---

## 6. Despliegue

Preparado para:

* Docker Desktop
* Coolify
* Producción con HTTPS

---

## 7. Justificación técnica

Se eligió esta arquitectura porque:

* Es escalable
* Es segura
* Es mantenible
* Permite evolución a SaaS multi-tenant
* Permite integración con servicios externos
