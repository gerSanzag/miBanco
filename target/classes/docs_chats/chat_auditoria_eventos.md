# Chat - Implementación de Sistema de Auditoría de Eventos

## Fecha: Mayo 2023

## Participantes:
- Germán (Cliente)
- Asistente IA

## Contexto:
En este chat se discute y desarrolla un sistema de auditoría de eventos para el proyecto MiBanco, siguiendo un enfoque funcional. Se implementa una solución para registrar y consultar eventos de diversas operaciones realizadas en el sistema.

## Transcripción:

**Germán**: Necesitamos implementar un sistema de auditoría para registrar todas las operaciones que se realizan en el sistema. ¿Cómo podríamos hacerlo?

**Asistente**: Para implementar un sistema de auditoría efectivo, podemos seguir un enfoque basado en eventos. Te propongo la siguiente estructura:

1. **Evento de Auditoría**:
```java
public record EventoAuditoria(
    String id,
    String tipoOperacion,
    String entidad,
    String idEntidad,
    String usuario,
    LocalDateTime fechaHora,
    String detalles
) {}
```

2. **Repositorio de Auditoría**:
```java
public interface AuditoriaRepository {
    Optional<EventoAuditoria> save(Optional<EventoAuditoria> evento);
    Optional<List<EventoAuditoria>> findByTipoOperacion(String tipoOperacion);
    Optional<List<EventoAuditoria>> findByEntidad(String entidad);
    Optional<List<EventoAuditoria>> findByFechaHoraBetween(LocalDateTime inicio, LocalDateTime fin);
}
```

3. **Servicio de Auditoría**:
```java
@Service
public class AuditoriaService {
    private final AuditoriaRepository auditoriaRepository;
    
    public Optional<EventoAuditoria> registrarEvento(
        String tipoOperacion,
        String entidad,
        String idEntidad,
        String usuario,
        String detalles
    ) {
        EventoAuditoria evento = new EventoAuditoria(
            UUID.randomUUID().toString(),
            tipoOperacion,
            entidad,
            idEntidad,
            usuario,
            LocalDateTime.now(),
            detalles
        );
        
        return auditoriaRepository.save(Optional.of(evento));
    }
}
```

4. **Aspecto para Auditoría Automática**:
```java
@Aspect
@Component
public class AuditoriaAspect {
    private final AuditoriaService auditoriaService;
    
    @Around("@annotation(auditable)")
    public Object auditarOperacion(ProceedingJoinPoint joinPoint, Auditable auditable) {
        try {
            Object result = joinPoint.proceed();
            
            auditoriaService.registrarEvento(
                auditable.tipoOperacion(),
                auditable.entidad(),
                obtenerIdEntidad(joinPoint),
                obtenerUsuario(),
                auditable.detalles()
            );
            
            return result;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
```

5. **Anotación para Auditoría**:
```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Auditable {
    String tipoOperacion();
    String entidad();
    String detalles() default "";
}
```

**Ventajas de este enfoque**:

1. **Separación de Responsabilidades**:
   - La lógica de auditoría está separada del código de negocio
   - Fácil de mantener y extender

2. **No Invasivo**:
   - Usa anotaciones para marcar operaciones auditables
   - No modifica la lógica de negocio existente

3. **Flexible**:
   - Permite auditar diferentes tipos de operaciones
   - Fácil de consultar y filtrar eventos

4. **Enfoque Funcional**:
   - Usa `Optional` para manejar valores nulos
   - Inmutabilidad con records
   - Programación declarativa

**Ejemplo de uso**:

```java
@Service
public class ClienteService {
    @Auditable(
        tipoOperacion = "CREAR_CLIENTE",
        entidad = "Cliente",
        detalles = "Creación de nuevo cliente"
    )
    public Optional<ClienteDTO> crearCliente(Optional<ClienteDTO> clienteDTO) {
        // Lógica existente
    }
}
```

¿Qué te parece esta propuesta? ¿Hay algún aspecto específico que quieras que detallemos más?

**Germán**: Me parece bien, pero ¿cómo podríamos implementar la persistencia de los eventos?

**Asistente**: Para la persistencia de eventos, tenemos varias opciones. Te propongo dos enfoques:

1. **Persistencia en Base de Datos**:
```java
@Entity
@Table(name = "eventos_auditoria")
public class EventoAuditoriaEntity {
    @Id
    private String id;
    
    @Column(nullable = false)
    private String tipoOperacion;
    
    @Column(nullable = false)
    private String entidad;
    
    @Column(nullable = false)
    private String idEntidad;
    
    @Column(nullable = false)
    private String usuario;
    
    @Column(nullable = false)
    private LocalDateTime fechaHora;
    
    @Column(columnDefinition = "TEXT")
    private String detalles;
    
    // Constructor, getters, setters
}
```

2. **Persistencia en Archivo**:
```java
@Component
public class AuditoriaFileRepository implements AuditoriaRepository {
    private final Path archivoAuditoria;
    
    public AuditoriaFileRepository() {
        this.archivoAuditoria = Paths.get("auditoria.log");
    }
    
    @Override
    public Optional<EventoAuditoria> save(Optional<EventoAuditoria> evento) {
        return evento.map(e -> {
            try {
                String linea = String.format("%s|%s|%s|%s|%s|%s|%s%n",
                    e.id(),
                    e.tipoOperacion(),
                    e.entidad(),
                    e.idEntidad(),
                    e.usuario(),
                    e.fechaHora(),
                    e.detalles()
                );
                
                Files.write(
                    archivoAuditoria,
                    linea.getBytes(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
                );
                
                return e;
            } catch (IOException ex) {
                throw new RuntimeException("Error al guardar evento de auditoría", ex);
            }
        });
    }
    
    @Override
    public Optional<List<EventoAuditoria>> findByTipoOperacion(String tipoOperacion) {
        try {
            return Optional.of(
                Files.lines(archivoAuditoria)
                    .map(this::parsearLinea)
                    .filter(e -> e.tipoOperacion().equals(tipoOperacion))
                    .collect(Collectors.toList())
            );
        } catch (IOException e) {
            return Optional.empty();
        }
    }
    
    private EventoAuditoria parsearLinea(String linea) {
        String[] campos = linea.split("\\|");
        return new EventoAuditoria(
            campos[0],
            campos[1],
            campos[2],
            campos[3],
            campos[4],
            LocalDateTime.parse(campos[5]),
            campos[6]
        );
    }
}
```

**Ventajas de cada enfoque**:

1. **Base de Datos**:
   - Mejor rendimiento en consultas
   - Transaccionalidad
   - Índices y búsquedas eficientes
   - Escalable

2. **Archivo**:
   - Simple de implementar
   - Fácil de leer y analizar
   - No requiere configuración adicional
   - Ideal para logs

**Recomendación**:
Para tu caso, dado que es un sistema de auditoría, te recomendaría usar la persistencia en archivo porque:
1. Es más simple de implementar
2. Los logs son más fáciles de analizar
3. No requiere configuración de base de datos
4. Es más fácil de mantener

¿Qué enfoque prefieres? ¿Quieres que profundicemos en alguno de ellos?

**Germán**: Me parece bien el enfoque de archivo, pero ¿cómo podríamos implementar la rotación de logs?

**Asistente**: Para implementar la rotación de logs, podemos crear un sistema que:
1. Cree nuevos archivos cuando el actual alcance un tamaño máximo
2. Mantenga un número máximo de archivos
3. Use fechas en los nombres de los archivos

Aquí te propongo la implementación:

```java
@Component
public class AuditoriaFileRepository implements AuditoriaRepository {
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final int MAX_FILES = 5;
    private static final String LOG_PATTERN = "auditoria-%s.log";
    
    private Path archivoAuditoria;
    private final String directorioLogs;
    
    public AuditoriaFileRepository() {
        this.directorioLogs = "logs";
        this.archivoAuditoria = obtenerArchivoActual();
        crearDirectorioSiNoExiste();
    }
    
    private Path obtenerArchivoActual() {
        return Paths.get(directorioLogs, 
            String.format(LOG_PATTERN, LocalDate.now()));
    }
    
    private void crearDirectorioSiNoExiste() {
        try {
            Files.createDirectories(Paths.get(directorioLogs));
        } catch (IOException e) {
            throw new RuntimeException("Error al crear directorio de logs", e);
        }
    }
    
    @Override
    public Optional<EventoAuditoria> save(Optional<EventoAuditoria> evento) {
        return evento.map(e -> {
            try {
                rotarArchivoSiEsNecesario();
                
                String linea = String.format("%s|%s|%s|%s|%s|%s|%s%n",
                    e.id(),
                    e.tipoOperacion(),
                    e.entidad(),
                    e.idEntidad(),
                    e.usuario(),
                    e.fechaHora(),
                    e.detalles()
                );
                
                Files.write(
                    archivoAuditoria,
                    linea.getBytes(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
                );
                
                return e;
            } catch (IOException ex) {
                throw new RuntimeException("Error al guardar evento de auditoría", ex);
            }
        });
    }
    
    private void rotarArchivoSiEsNecesario() throws IOException {
        if (Files.exists(archivoAuditoria) && 
            Files.size(archivoAuditoria) >= MAX_FILE_SIZE) {
            
            // Crear nuevo archivo con timestamp
            String timestamp = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
            Path nuevoArchivo = Paths.get(directorioLogs,
                String.format(LOG_PATTERN, timestamp));
            
            // Mover archivo actual
            Files.move(archivoAuditoria, nuevoArchivo);
            
            // Actualizar referencia al archivo actual
            archivoAuditoria = obtenerArchivoActual();
            
            // Limpiar archivos antiguos
            limpiarArchivosAntiguos();
        }
    }
    
    private void limpiarArchivosAntiguos() throws IOException {
        try (Stream<Path> archivos = Files.list(Paths.get(directorioLogs))) {
            List<Path> archivosOrdenados = archivos
                .filter(p -> p.toString().endsWith(".log"))
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
            
            // Eliminar archivos que excedan el máximo
            for (int i = MAX_FILES; i < archivosOrdenados.size(); i++) {
                Files.delete(archivosOrdenados.get(i));
            }
        }
    }
    
    @Override
    public Optional<List<EventoAuditoria>> findByTipoOperacion(String tipoOperacion) {
        try {
            return Optional.of(
                obtenerTodosLosArchivos()
                    .flatMap(this::leerArchivo)
                    .filter(e -> e.tipoOperacion().equals(tipoOperacion))
                    .collect(Collectors.toList())
            );
        } catch (IOException e) {
            return Optional.empty();
        }
    }
    
    private Stream<Path> obtenerTodosLosArchivos() throws IOException {
        return Files.list(Paths.get(directorioLogs))
            .filter(p -> p.toString().endsWith(".log"))
            .sorted();
    }
    
    private Stream<EventoAuditoria> leerArchivo(Path archivo) {
        try {
            return Files.lines(archivo)
                .map(this::parsearLinea);
        } catch (IOException e) {
            return Stream.empty();
        }
    }
    
    private EventoAuditoria parsearLinea(String linea) {
        String[] campos = linea.split("\\|");
        return new EventoAuditoria(
            campos[0],
            campos[1],
            campos[2],
            campos[3],
            campos[4],
            LocalDateTime.parse(campos[5]),
            campos[6]
        );
    }
}
```

**Características de esta implementación**:

1. **Rotación por Tamaño**:
   - Crea nuevo archivo cuando el actual alcanza 10MB
   - Mantiene máximo 5 archivos
   - Nombra archivos con fecha y hora

2. **Limpieza Automática**:
   - Elimina archivos antiguos automáticamente
   - Mantiene solo los últimos 5 archivos
   - Ordena archivos por fecha

3. **Búsqueda en Múltiples Archivos**:
   - Busca en todos los archivos de log
   - Mantiene el orden cronológico
   - Maneja errores de lectura

4. **Enfoque Funcional**:
   - Usa Streams para procesar archivos
   - Manejo de Optional para valores nulos
   - Programación declarativa

¿Qué te parece esta implementación? ¿Hay algún aspecto que quieras que ajustemos?

**Germán**: Me parece bien, pero ¿cómo podríamos implementar la compresión de los archivos antiguos?

**Asistente**: Podemos implementar la compresión de archivos antiguos usando GZIP. Aquí te propongo la modificación:

```java
@Component
public class AuditoriaFileRepository implements AuditoriaRepository {
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final int MAX_FILES = 5;
    private static final String LOG_PATTERN = "auditoria-%s.log";
    private static final String COMPRESSED_PATTERN = "auditoria-%s.log.gz";
    
    private Path archivoAuditoria;
    private final String directorioLogs;
    
    public AuditoriaFileRepository() {
        this.directorioLogs = "logs";
        this.archivoAuditoria = obtenerArchivoActual();
        crearDirectorioSiNoExiste();
    }
    
    private Path obtenerArchivoActual() {
        return Paths.get(directorioLogs, 
            String.format(LOG_PATTERN, LocalDate.now()));
    }
    
    private void crearDirectorioSiNoExiste() {
        try {
            Files.createDirectories(Paths.get(directorioLogs));
        } catch (IOException e) {
            throw new RuntimeException("Error al crear directorio de logs", e);
        }
    }
    
    @Override
    public Optional<EventoAuditoria> save(Optional<EventoAuditoria> evento) {
        return evento.map(e -> {
            try {
                rotarArchivoSiEsNecesario();
                
                String linea = String.format("%s|%s|%s|%s|%s|%s|%s%n",
                    e.id(),
                    e.tipoOperacion(),
                    e.entidad(),
                    e.idEntidad(),
                    e.usuario(),
                    e.fechaHora(),
                    e.detalles()
                );
                
                Files.write(
                    archivoAuditoria,
                    linea.getBytes(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
                );
                
                return e;
            } catch (IOException ex) {
                throw new RuntimeException("Error al guardar evento de auditoría", ex);
            }
        });
    }
    
    private void rotarArchivoSiEsNecesario() throws IOException {
        if (Files.exists(archivoAuditoria) && 
            Files.size(archivoAuditoria) >= MAX_FILE_SIZE) {
            
            // Crear nuevo archivo con timestamp
            String timestamp = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
            Path nuevoArchivo = Paths.get(directorioLogs,
                String.format(LOG_PATTERN, timestamp));
            
            // Mover archivo actual
            Files.move(archivoAuditoria, nuevoArchivo);
            
            // Comprimir archivo movido
            comprimirArchivo(nuevoArchivo);
            
            // Actualizar referencia al archivo actual
            archivoAuditoria = obtenerArchivoActual();
            
            // Limpiar archivos antiguos
            limpiarArchivosAntiguos();
        }
    }
    
    private void comprimirArchivo(Path archivo) throws IOException {
        Path archivoComprimido = Paths.get(archivo.toString() + ".gz");
        
        try (GZIPOutputStream gzipOut = new GZIPOutputStream(
                Files.newOutputStream(archivoComprimido))) {
            Files.copy(archivo, gzipOut);
        }
        
        // Eliminar archivo original después de comprimir
        Files.delete(archivo);
    }
    
    private Stream<EventoAuditoria> leerArchivo(Path archivo) {
        try {
            if (archivo.toString().endsWith(".gz")) {
                return leerArchivoComprimido(archivo);
            } else {
                return Files.lines(archivo)
                    .map(this::parsearLinea);
            }
        } catch (IOException e) {
            return Stream.empty();
        }
    }
    
    private Stream<EventoAuditoria> leerArchivoComprimido(Path archivo) throws IOException {
        try (GZIPInputStream gzipIn = new GZIPInputStream(
                Files.newInputStream(archivo));
             BufferedReader reader = new BufferedReader(
                new InputStreamReader(gzipIn))) {
            
            return reader.lines()
                .map(this::parsearLinea);
        }
    }
    
    private void limpiarArchivosAntiguos() throws IOException {
        try (Stream<Path> archivos = Files.list(Paths.get(directorioLogs))) {
            List<Path> archivosOrdenados = archivos
                .filter(p -> p.toString().endsWith(".log") || 
                           p.toString().endsWith(".gz"))
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
            
            // Eliminar archivos que excedan el máximo
            for (int i = MAX_FILES; i < archivosOrdenados.size(); i++) {
                Files.delete(archivosOrdenados.get(i));
            }
        }
    }
}
```

**Mejoras implementadas**:

1. **Compresión Automática**:
   - Comprime archivos al rotar
   - Usa formato GZIP para máxima compatibilidad
   - Elimina archivo original después de comprimir

2. **Lectura de Archivos Comprimidos**:
   - Detecta automáticamente si el archivo está comprimido
   - Lee archivos .gz de forma transparente
   - Mantiene la misma interfaz para el usuario

3. **Gestión de Archivos**:
   - Considera archivos .log y .gz en la limpieza
   - Mantiene el orden cronológico
   - Limpia archivos antiguos automáticamente

4. **Ventajas**:
   - Reduce el espacio en disco
   - Mantiene la legibilidad de los logs
   - Proceso transparente para el usuario
   - Fácil de mantener y extender

¿Qué te parece esta implementación? ¿Hay algún aspecto que quieras que ajustemos?

**Germán**: Me parece bien, gracias.

**Asistente**: De nada, Germán. Me alegro de que te parezca bien la solución.

Esta implementación del sistema de auditoría con rotación y compresión de logs es robusta y eficiente porque:
- Separa claramente las responsabilidades
- Sigue un enfoque funcional
- Es fácil de mantener y extender
- Optimiza el uso del espacio en disco
- Mantiene la integridad de los datos

¿Hay algún otro aspecto del sistema de auditoría que quieras que revisemos?