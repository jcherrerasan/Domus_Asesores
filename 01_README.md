# Trabajo Final — [Jennifer Herrera]‌‌‌​‌​‌‌​﻿‍‍‌‍​‍​﻿​﻿​﻿​‍​﻿‌‌‌‍‌​‌‍​‍‌‍‌‍‌‍​‌​﻿‌‌​﻿‍‌‌‍​‌‌‍​‌​﻿​‍‌‍‌​​﻿‍‌‌‍‌‌

## Blueprint elegido
CRM inmobiliario profesional 

## Descripcion
Es un crm para una inmobiliaria de 10-30 agentes.
Centraliza la gestión de contactos, cartera de propiedades y equipos comerciales para la compraventa y alquiler de propiedades.
Automatiza seguimientos, gestiona documentos, acuerdos, visitas y fotos de su cartera.
* Gestión de Clientes y Contactos(Leads): Almacena historial de interacciones y documentos de leads, compradores y propietarios.
* Gestión de Inmuebles: Permite organizar la cartera de propiedades y conectar automáticamente inmuebles disponibles con clientes interesados.
* Automatización de Marketing y Seguimiento: Envía correos automáticos, recordatorios, campañas de marketing y gestiona la publicación en portales inmobiliarios.
* Análisis y Reportes: Ofrece paneles en tiempo real para medir el rendimiento de los agentes, la conversión de leads a clientes verificados y la efectividad de los anuncios.
* Centralización Operativa: Reduce el papeleo almacenando contratos y documentos de identidad digitalmente.
* Agenda: Tiene su propia agenda donde muestra las visitas, llamadas, reuniones, valoraciones en persona en formato calendario y también en lista. Tiene recordatorios propios para avisar de los eventos.
* Facturación: Almacenamiento de la facturación de los tramites sobre las propiedades. Pagos de exclusividad y contratos de servicios ofrecidos.

## Entidades

| Entidad | Campos principales | Relaciones |
|---------|-------------------|------------|
| [Ej: Pelicula] | titulo, anio, genero | ManyToOne con Director, ManyToMany con Actor |
| ... | ... | ... |

## Endpoints de la API

| Verbo | URL | Descripcion |
|-------|-----|-------------|
| GET | `/api/peliculas` | Listar todas |
| POST | `/api/peliculas` | Crear nueva |
| ... | ... | ... |

## Como ejecutar

```bash
# Con Docker
docker compose up -d

# Sin Docker (H2)
mvn spring-boot:run
```
