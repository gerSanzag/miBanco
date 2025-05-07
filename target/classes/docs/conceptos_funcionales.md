# Conceptos Funcionales en Java - Guía para miBanco

## 1. DTOs (Data Transfer Objects)
Son objetos que transportan datos entre diferentes capas de la aplicación, como cajas especiales para datos:
- Transportan solo la información necesaria
- Protegen información sensible (como omitir el CVV en TarjetaDTO)
- Dan formato a los datos de manera conveniente

## 2. Anotaciones de Lombok

### @Value
- Similar a una clase `record` de Java 16+
- Crea objetos inmutables (todos los campos son `final`)
- Genera automáticamente getters, pero NO setters
- Genera `equals()`, `hashCode()` y `toString()`
- Es como un lápiz con tinta permanente - una vez que escribes, no puedes cambiar

### @Builder
- Patrón de diseño que facilita la creación de objetos con muchos campos
- Permite construir objetos paso a paso, como un formulario que se llena campo por campo
- Más legible que usar constructores con muchos parámetros

```java
ClienteDTO cliente = ClienteDTO.builder()
        .id(1L)
        .nombre("Juan")
        .apellido("Pérez")
        // más campos...
        .build();
```

## 3. Métodos Factory (como el método `of()`)

Un método factory es simplemente un patrón de diseño donde un método estático crea y devuelve objetos:

```java
// Definición
public static ClienteDTO of(Long id, String nombre, ...) {
    return ClienteDTO.builder()
            // construcción...
            .build();
}

// Uso
ClienteDTO cliente = ClienteDTO.of(1L, "Juan", "Pérez", ...);
```

Ventajas:
- Nombre más descriptivo que solo usar `new`
- Puede incluir validaciones y lógica adicional
- Puede establecer valores predeterminados
- Sigue convenciones de Java (como en `List.of()`, `Optional.of()`)

## 4. Optional para Manejo de Valores Nulos

`Optional<T>` es un contenedor que puede contener un valor o estar vacío:

```java
Optional<String> email = Optional.of("correo@ejemplo.com");  // Con valor
Optional<String> telefono = Optional.empty();                // Sin valor

// Para extraer el valor:
String emailValue = email.orElse("sin correo");             // "correo@ejemplo.com"
String telefonoValue = telefono.orElse("sin teléfono");     // "sin teléfono"
```

Ventajas:
- Hace explícito que un valor puede estar ausente
- Evita NullPointerException
- Promueve un estilo más funcional

## 5. Programación Funcional con Streams

Ejemplo de cómo usamos programación funcional en los mappers:

```java
public List<ClienteDTO> toDtoList(List<Cliente> clientes) {
    return Optional.ofNullable(clientes)
            .map(list -> list.stream()
                    .map(this::toDto)
                    .collect(Collectors.toList()))
            .orElse(List.of());
}
```

Este código:
1. Maneja el caso de que la lista sea null (con Optional)
2. Convierte la lista a un stream para procesamiento funcional
3. Transforma cada elemento usando el método toDto
4. Recolecta los resultados en una nueva lista
5. Devuelve una lista vacía si la entrada era null

## Nota sobre los Modelos vs DTOs

Los modelos (Cliente, Cuenta, etc.) usan `@Data` (mutables) mientras que los DTOs usan `@Value` (inmutables) por:

1. Los modelos pueden necesitar cambiar durante su ciclo de vida (saldo de cuentas, datos de clientes)
2. Posible integración futura con bases de datos mediante JPA/Hibernate (requiere objetos mutables)
3. Los DTOs, al ser solo para transferencia de datos, se benefician de la inmutabilidad (seguridad)

---
*Documento creado como referencia para el proyecto miBanco* 