# Resultados — Bloque C

## Búsqueda avanzada de propiedades

Se implementó búsqueda con filtros, ordenación y paginación en `/api/propiedades/search`.

```java
 @Operation(summary = "Buscar propiedades con filtros")
@GetMapping("/search")
public Page<PropiedadResponse> searchPropiedades(
        @Parameter(description = "Ciudad de la propiedad") @RequestParam(required = false) String ciudad,
        @Parameter(description = "Precio minimo") @RequestParam(required = false) Double precioMinimo,
        @Parameter(description = "Precio máximo") @RequestParam(required = false) Double precioMaximo,
        @Parameter(description = "Numero de habitaciones") @RequestParam(required = false) Integer habitaciones,
        @Parameter(description = "Numero de baños") @RequestParam(required = false) Integer banios,
        @Parameter(description = "Superficie construida minima") @RequestParam(required = false) Double metrosConstruidosMinimos,
        @Parameter(description = "Tipo de propiedad") @RequestParam(required = false) PropiedadTipo propiedadTipo,
        @Parameter(description = "Tipo de operación") @RequestParam(required = false) OperacionTipo operacionTipo,
        @Parameter(description = "Extras que debe tener la propiedad") @RequestParam(required = false) List<String> extras,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @Parameter(description = "Campo por el que se ordena") @RequestParam(defaultValue = "precio") String sortField,
        @RequestParam(defaultValue = "asc") String sortDirection
) {
    return propiedadService.searchPropiedades(
            ciudad,
            precioMinimo,
            precioMaximo,
            habitaciones,
            banios,
            metrosConstruidosMinimos,
            propiedadTipo,
            operacionTipo,
            extras,
            page,
            size,
            sortField,
            sortDirection
    );
}
```

Prompt usado:

```text
Dame un ejemplo de Pageable que podria usar en mi proyecto para buscar propiedades por ciudad y precio con paginacion y ordenacion.
```

## Implementaciones

El proyecto incorpora:

- DTOs de entrada y salida
- búsqueda global de contactos
- dashboard con métricas
- valoraciones asociadas a propiedades
- documentación Swagger/OpenAPI

Busca una propiedad dependiendo del filtro que le queramos aplicar, al igual que lo hace idealista cuando se hace una búsqueda más exacta sobre las preferencias del piso que queremos encontrar.

Se eligió esta implementación porque es lo adecuado para una inmobiliaria que vende inmuebles por todo el país y tendrá miles de registros. 

Parece más difícil de lo que realmente es, aprendes a controlar variables con muy poco código.
## Evidencia
### Búsqueda avanzada
![Postman](\capturas\Postman_busqueda.png)

### api/leads
![Postman](\capturas\Postman_lead.png)

### api/usuarios
![Postman](\capturas\Postman_usuarios.png)


