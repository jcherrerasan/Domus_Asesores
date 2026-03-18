# 04_REFLEXION_IA.md

## 1. Objetivo del proyecto

Desarrollar un CRM inmobiliario completo utilizando:

* Spring Boot
* Hibernate / JPA
* PostgreSQL
* Docker

---
## 2. Retos técnicos

## Bloque A: Infraestructura Docker

### Arranque

Lo primero que pedí fue ayuda para revisar la coherencia entre puertos, nombre del servicio PostgreSQL y URL JDBC dentro de `docker-compose.yml`.

### Error

El principal error fue tener configuraciones incompatibles:

- la app apuntaba a `postgres` cuando el servicio real era `db`
- la base de datos del contenedor era `crm_domus_db` pero la URL usaba otro nombre
- la API estaba mapeada a `8081:8081` aunque Spring corre en `8080`

Aprendí que en Docker Compose no basta con que el YAML tenga los tres servicios: también deben coincidir nombre del servicio, nombre de la base, healthcheck y puertos.

### Aprendizaje

No sabía que `depends_on` no soluciona por si solo la disponibilidad real de PostgreSQL. Aprendí a combinarlo con `condition: service_healthy` y `pg_isready`.

### Prompt clave del bloque A

```text
Dame un ejemplo de docker-compose para mi proyecto. Quiero que la API quede en localhost:8080, Adminer en localhost:8081, que la app espere a la base de datos con healthcheck y que la URL JDBC use el nombre real del servicio de compose.
```

## Bloque B: API REST Spring Boot

### Arranque

Parti de la idea funcional del CRM inmobiliario y la convertí en entidades, relaciones y endpoints REST.

Prompts reales usados:

#### PROMP N1º
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

#### PROMP N2º
```text
USUARIOS (agentes que pueden acceder a la plataforma, el role administrador tendrá todos los permisos)
* idUser
* name
* email
* password
* role (administrador | comercial | coordinador | otro)
* active (boolean)
* createdAt
* deleted
* deleted_at

LEADS--> Accounts (Un lead se convierte en account)
* idLead
* nombre
* teléfono
* email
* tipo (propietario | comprador | alquiler | inquilino)
* estado (nuevo | contactado | interesado | descartado | convertido| oferta realizada )
* descripción
* createdBy
* idUser
* createdAt
* idValoracion

ACCOUNTS (Clientes verificados)
* idAccount
* idlead
* idUser
* nombre
* DNI
* teléfono
* email
* tipo (vendedor | comprador | alquiler | inquilino)
* direccion
* descripcion
* idProperty

PROPERTYES (Propiedades en gestion)
* idProperty
* idAccount
* idUser
* tipoOperacion (venta | alquiler)
* exclusiva (boolean)
* precio
* estado (captacion | publicado | reservado | vendido | alquilado)
* documentos
* tipoInmueble(casa | piso | adosado | solar)
* superficieConstruida
* superficieUtil
* habitaciones
* baños
* planta
* añoConstruccion
* direccion
* ciudad
* codigoPostal
* provincia
* extras
* descripción
* createdAt

VALORACIONES (Valoración sobre una propiedad)
* idValoracion
* idLead
* idPropiedad
* tipo (automatica | presencial)
* valorEstimadoAutomatico
* valorEstimadoReal
* urlValoracion
* createdAt
```

### PROMPT N3º
```text
dame un ejemplo de los exception que debo crear
```

### Error

Tuve un error de mapeo al definir una relación `mappedBy` que no existía en la entidad destino. Lo resolví eliminando la relación incorrecta entre `Lead` y `Valuation` y dejándola correctamente entre `Property` y `Valuation`.

```java
@OneToMany(mappedBy = "lead")
private List<Valoracion> valoraciones;
```
Pero en la entidad Valuation no estaba el atributo Lead. En terminal me daba este error:
```shell
026-03-13T09:53:47.370+01:00 ERROR 10340 --- [  restartedMain] j.LocalContainerEntityManagerFactoryBean : Failed to initialize JPA EntityManagerFactory: Collection 'com.crm.crm_domus.model.Lead.valoraciones' is 'mappedBy' a property named 'lead' which does not exist in the target entity 'com.crm.crm_domus.model.Valoracion'
2026-03-13T09:53:47.370+01:00  WARN 10340 --- [  restartedMain] ConfigServletWebServerApplicationContext : Exception encountered during context initialization - cancelling refresh attempt: org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'entityManagerFactory' defined in class path resource [org/springframework/boot/hibernate/autoconfigure/HibernateJpaConfiguration.class]: Collection 'com.crm.crm_domus.model.Lead.valoraciones' is 'mappedBy' a property named 'lead' which does not exist in the target entity 'com.crm.crm_domus.model.Valoracion'
2026-03-13T09:53:47.372+01:00  INFO 10340 --- [  restartedMain] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown initiated...
2026-03-13T09:53:47.423+01:00  INFO 10340 --- [  restartedMain] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown completed.
2026-03-13T09:53:47.426+01:00  INFO 10340 --- [  restartedMain] o.apache.catalina.core.StandardService   : Stopping service [Tomcat]
2026-03-13T09:53:47.433+01:00  INFO 10340 --- [  restartedMain] .s.b.a.l.ConditionEvaluationReportLogger : 

Error starting ApplicationContext. To display the condition evaluation report re-run your application with 'debug' enabled.
2026-03-13T09:53:47.442+01:00 ERROR 10340 --- [  restartedMain] o.s.boot.SpringApplication               : Application run failed
```
Por error codifiqué la relación entre Lead y Valuation cuando en realidad la relación era entre Property y Valuation.

Borre esa relación en Lead y la codifique correctamente en Property de esta manera:
```java
@OneToMany(mappedBy = "propiedad")
private Set<Valoracion> valoraciones;
```
Con los cambios la aplicación ya no da error y puedo seguir codificando el resto del programa.
```shell
2026-03-13T10:31:35.646+01:00  INFO 12472 --- [  restartedMain] r$InitializeUserDetailsManagerConfigurer : Global AuthenticationManager configured with UserDetailsService bean with name inMemoryUserDetailsManager
2026-03-13T10:31:35.795+01:00  INFO 12472 --- [  restartedMain] o.s.b.a.e.web.EndpointLinksResolver      : Exposing 1 endpoint beneath base path '/actuator'
2026-03-13T10:31:35.811+01:00  INFO 12472 --- [  restartedMain] o.s.boot.tomcat.TomcatWebServer          : Tomcat started on port 8080 (http) with context path '/'
2026-03-13T10:31:35.814+01:00  INFO 12472 --- [  restartedMain] com.crm.crm_domus.CrmDomusApplication    : Started CrmDomusApplication in 0.975 seconds (process running for 22.083)
2026-03-13T10:31:35.815+01:00  WARN 12472 --- [  restartedMain] o.s.core.events.SpringDocAppInitializer  : SpringDoc /v3/api-docs endpoint is enabled by default. To disable it in production, set the property 'springdoc.api-docs.enabled=false'
2026-03-13T10:31:35.815+01:00  WARN 12472 --- [  restartedMain] o.s.core.events.SpringDocAppInitializer  : SpringDoc /swagger-ui.html endpoint is enabled by default. To disable it in production, set the property 'springdoc.swagger-ui.enabled=false'
2026-03-13T10:31:35.816+01:00  INFO 12472 --- [  restartedMain] .ConditionEvaluationDeltaLoggingListener : Condition evaluation unchanged
```
También detecté que mis controladores no estaban realmente en CRUD completo: faltaban endpoints `PUT` y algunos `DELETE`. Eso obligo a alinear controller, service y documentación.

### Aprendizaje

Aprendí que en Spring Boot el CRUD no estaba terminado solo porque existan `POST` y `GET`. 

También entendí mejor cuando introducir una relación `@ManyToMany`. Las tablas que tenía eran todas @ManyToOne o @OneToMany y me faltaba al menos una tabla @ManyToMany.
Me fue un poco complicado buscar una tabla con esas características que cuadrara con el proyecto.

Asi que verificando las tablas que tenía decidí crear una nueva tabla llamada `Extras` donde podría controlar los extras que tiene una propiedad, como:
* piscina
* garaje
* ascensor
* jardín
* trastero
* terraza

Una propiedad tiene muchas extras.
Un extra puede estar en muchas propiedades.

## Bloque C: Funcionalidad avanzada

### Arranque

La opción elegida fue:

- queries avanzadas con `@Query`
- paginación con `Pageable`

#### PROMP:

```text
Dame un ejemplo de Pageable que podria hacer en mi proyecto si quiero utilizarlo para la paginacion en la busqueda de propiedades tal y como lo hace idealista.
```

### Error

El error inicial fue hacer el cambio solo en controller. Para que la funcionalidad funcionara había que tocar también service y repository.

Al hacer cambios en los secretos implementando .env nos aparece este error.

```shell
Error starting ApplicationContext. To display the condition evaluation report re-run your application with 'debug' enabled.
2026-03-17T13:04:44.965+01:00 ERROR 3576 --- [  restartedMain] o.s.boot.SpringApplication               : Application run failed

org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'entityManagerFactory' defined in class path resource [org/springframework/boot/hibernate/autoconfigure/HibernateJpaConfiguration.class]: Unable to build Hibernate SessionFactory  [persistence unit: default] ; nested exception is org.hibernate.exception.JDBCConnectionException: Unable to open JDBC Connection for DDL execution [The server requested SCRAM-based authentication, but no password was provided.] [n/a]
at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.initializeBean(AbstractAutowireCapableBeanFactory.java:1817) ~[spring-beans-7.0.5.jar:7.0.5]
at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:603) ~[spring-beans-7.0.5.jar:7.0.5]
at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:525) ~[spring-beans-7.0.5.jar:7.0.5]
at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:333) ~[spring-beans-7.0.5.jar:7.0.5]
at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:371) ~[spring-beans-7.0.5.jar:7.0.5]
at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:331) ~[spring-beans-7.0.5.jar:7.0.5]
at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:201) ~[spring-beans-7.0.5.jar:7.0.5]
at org.springframework.context.support.AbstractApplicationContext.finishBeanFactoryInitialization(AbstractApplicationContext.java:977) ~[spring-context-7.0.5.jar:7.0.5]
```
El error venía porque se había puesto mal la variable `spring.datasource.password=${SPRING_DATASOURCE_PASSWORD: postgres}`, y la modificamos a `spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}` 

### Aprendizaje

Aprendí a construir una query JPQL con filtros opcionales y devolver un `Page<PropiedadResponse>`. Antes no tenía claro como combinar `@Query` y `Pageable`.

