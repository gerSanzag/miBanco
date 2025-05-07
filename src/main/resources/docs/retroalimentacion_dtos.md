# Retroalimentación sobre los DTOs implementados

## 1. Inmutabilidad y Enfoque Funcional
- He usado la anotación `@Value` de Lombok en lugar de `@Data`. Esto crea clases inmutables (todos los campos son finales y no hay setters), lo que es una buena práctica en programación funcional.
- Los objetos inmutables son más seguros para compartir entre hilos y menos propensos a errores.

## 2. Uso de Optional
- He implementado métodos factory estáticos (`of()`) que reciben parámetros opcionales usando la clase `Optional<T>`.
- Esto permite manejar valores nulos de forma segura y explícita, evitando excepciones `NullPointerException`.
- El patrón funcional `optional.orElse(default)` proporciona valores predeterminados cuando es necesario.

## 3. Builder Pattern
- Utilicé `@Builder` para facilitar la creación de objetos con muchos campos de manera clara.
- Esto hace que el código sea más legible y evita constructores con muchos parámetros.

## 4. DTOs Específicos
- Los DTOs permiten transferir solo los datos necesarios entre capas, ocultando detalles de implementación.
- Por ejemplo, en `TarjetaDTO` omití el campo CVV por razones de seguridad.

## 5. Validación en la Creación
- Agregué validaciones en los métodos factory (como verificar que la fecha de expiración sea futura).
- Esto asegura la integridad de los datos desde su creación.

## 6. Mappers con Enfoque Funcional
- Creé una interfaz genérica `Mapper<E, D>` para estandarizar conversiones.
- Implementé métodos como `toDtoList` usando streams y programación funcional.
- El uso de `Optional.ofNullable()` con streams previene errores de NullPointerException.

## 7. ResponseDTO Genérico
- Creé un DTO de respuesta estándar que puede contener cualquier tipo de dato.
- Proporciona una estructura coherente para todas las respuestas de la aplicación.
- Incluye métodos factory para crear respuestas de éxito y error fácilmente.

---
*Documento creado como referencia para el proyecto miBanco* 