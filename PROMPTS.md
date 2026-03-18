# Registro de Prompts - Trabajo Final

**Alumno:** Jennifer Herrera
**Fecha:** 18/03/2026
**IA utilizada:** ChatGPT

---
## Parte 1 Prompts reales
### Bloque A

```text
Revisa mi docker-compose para una app Spring Boot con PostgreSQL y Adminer. Quiero que la API quede en localhost:8080, Adminer en localhost:8081, que la app espere a la base de datos con healthcheck y que la URL JDBC use el nombre real del servicio de compose.
```
Funcionó parcialmente, había cosas que arreglar en el código

Aprendí que por poner el nombre diferente en un solo sitio te puede romper la lógica de la API.
## Bloque B: API REST Spring Boot

```text
Es un crm para una inmobiliaria de 10-30 agentes.
Centraliza la gestión de leads, cartera de propiedades y equipos comerciales para la compraventa y alquiler de propiedades.
Automatiza seguimientos, gestiona documentos, acuerdos, visitas y fotos de su cartera.
* Gestión de Clientes y Contactos(Leads): Almacena historial de interacciones y documentos de leads, compradores y propietarios.
* Gestión de Inmuebles: Permite organizar la cartera de propiedades y conectar automáticamente inmuebles disponibles con clientes interesados.
* Automatización de Marketing y Seguimiento: Envía correos automáticos, recordatorios, campañas de marketing y gestiona la publicación en portales inmobiliarios.
* Análisis y Reportes: Ofrece paneles en tiempo real para medir el rendimiento de los agentes, la conversión de leads a clientes verificados y la efectividad de los anuncios.

Lo van a usar agentes inmobiliarios. Debe ser desplegable y conexión a docker desktop.
Si necesitan login y roles. El Role administrador tendrá todos los permisos y asignará propiedades a los agentes y podrá ver las métricas generales del negocio y por agentes. Los usuarios que no sean administrador podrán ver solo las propiedades asignadas a ellos al igual que sus métricas propias y todo lo que tenga relación con su idUser.

Antes de que empieces a crear te pasaré el concepto de base de datos que tengo pensado.

```
Solo me dio la idea de como estructurarlo, había muchas incongruencias en su respuesta.

Se aprende a trabajar con la ia. Esta solo sirve de guía, si la manejas mal y no entiendes lo que tienes no sabrás como arreglarlo después. No vale decirle arregla el problema porque puede causarte uno peor.
### Bloque C

```text
Dame un ejemplo de Pageable que podria hacer en mi proyecto si quiero utilizarlo para la paginacion en la busqueda de propiedades tal y como lo hace idealista.
```
Si funciono.

Esta parte no la había implementado nunca y me ayudo a poder hacerlo sin dificultades.


## Parte 2. Blueprint generado con IA

# 🧩 CRM_DOMUS — Blueprint Profesional

## 📌 Descripción

CRM inmobiliario desarrollado con **Spring Boot** que permite gestionar:

- agentes comerciales
- leads (clientes potenciales)
- clientes verificados (accounts)
- propiedades
- valoraciones inmobiliarias
- extras de propiedades

El sistema cubre el flujo completo:

Lead → Cliente → Propiedad → Venta/Alquiler


---

# 🧱 Entidades del sistema

## 👤 User

Representa a los usuarios del sistema (agentes, admin).

**Campos:**

- idUser (PK)
- name
- email
- password
- role (ADMIN, COMERCIAL, COORDINADOR)
- active
- createdAt
- deletedAt

---

## 📞 Lead

Cliente potencial.

**Campos:**

- idLead (PK)
- name
- phone
- email
- type (propietario, comprador, alquiler, inquilino)
- status (nuevo, contactado, interesado, convertido, etc.)
- description
- createdBy (User)
- assignedUser (User)
- createdAt

---

## 🧑‍💼 Account

Cliente verificado (lead convertido).

**Campos:**

- idAccount (PK)
- lead (FK)
- user (FK)
- name
- dni
- phone
- email
- type
- address
- description

---

## 🏠 Property

Propiedad inmobiliaria.

**Campos:**

- idProperty (PK)
- account (FK)
- user (FK)
- operationType (venta, alquiler)
- exclusive
- price
- status (captación, publicado, vendido…)
- propertyType (piso, casa…)
- builtArea
- usableArea
- rooms
- bathrooms
- floor
- yearBuilt
- address
- city
- postalCode
- province
- extras
- description
- createdAt

---

## ⭐ Feature

Extras de propiedades.

**Campos:**

- idFeature (PK)
- name
- description

**Ejemplos:**

- piscina
- garaje
- terraza
- ascensor

---

## 📊 Valuation

Valoración de propiedades.

**Campos:**

- idValuation (PK)
- lead (FK)
- property (FK)
- type (automática, presencial)
- estimatedValueAuto
- estimatedValueReal
- valuationUrl
- createdAt

---

# 🔗 Relaciones

User → OneToMany → Lead (createdBy, assignedUser)
User → OneToMany → Property

Lead → OneToOne → Account
Lead → OneToMany → Valuation

Account → OneToMany → Property

Property → ManyToMany → Feature
Property → OneToMany → Valuation


---

# 🌐 Endpoints REST

## 👤 Users

GET /api/users

GET /api/users/{id}

POST /api/users

PUT /api/users/{id}

DELETE /api/users/{id}

---

## 📞 Leads


GET /api/leads

GET /api/leads/{id}

POST /api/leads

PUT /api/leads/{id}

DELETE /api/leads/{id}

GET /api/leads/agent/{userId}


---

## 🧑‍💼 Accounts


GET /api/accounts

GET /api/accounts/{id}

POST /api/accounts/convert/{leadId}


---

## 🏠 Properties


GET /api/properties

GET /api/properties/{id}

POST /api/properties

PUT /api/properties/{id}

DELETE /api/properties/{id}

GET /api/properties/agent/{userId}


---

## ⭐ Features


GET /api/features

POST /api/features


---

## 📊 Valuations


GET /api/valuations

POST /api/valuations


---

# 🏗️ Arquitectura


Controller → Service → Repository → Database


**Capas:**

- Controller → expone endpoints REST
- Service → lógica de negocio
- Repository → acceso a datos (JPA)
- Model → entidades

---

# ⚙️ Tecnologías usadas

## Backend

- Java 17+
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- Lombok

## Base de datos

- PostgreSQL

## Infraestructura

- Docker
- Docker Compose
- Adminer

## Testing / API

- Postman
- Swagger (opcional)

---

# 🎯 Funcionalidades clave

- CRUD completo
- Conversión Lead → Account
- Relación Property ↔ Features
- Validación con `@Valid`
- Manejo global de errores (`@ControllerAdvice`)
- Paginación (Pageable)
- Despliegue con Docker

---

# 🚀 Valor profesional

Este proyecto demuestra:

- diseño de API REST real
- modelado relacional correcto
- arquitectura en capas
- uso de JPA/Hibernate
- despliegue con Docker



| Pregunta | Respuesta                                                 |
|----------|-----------------------------------------------------------|
| Total de prompts usados | 10                                                        |
| Herramienta mas usada | ChatGPT                                                   |
| Prompt mas util | El del Bloque 3, no sabia hacerlo                         |
| Prompt que NO funciono | el del docker compose daba errores                        |
| Tiempo total estimado | mejor no quiero saberlo                                   |
| Que harias diferente | estructuraría mejor la idea del proyecto antes de empezar |