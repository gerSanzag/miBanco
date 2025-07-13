package com.mibanco.repositorio.interna;

import com.mibanco.modelo.Identificable;
import com.mibanco.repositorio.AuditoriaRepositorio;
import com.mibanco.repositorio.util.BaseRepositorio;
import com.mibanco.util.AuditoriaUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.Objects;

/**
 * Implementación base abstracta para repositorios con acceso restringido
 * @param <T> Tipo de entidad que debe implementar Identificable
 * @param <ID> Tipo del identificador
 * @param <E> Tipo del enum para operaciones
 */
abstract class BaseRepositorioImpl<T extends Identificable, ID, E extends Enum<E>> implements BaseRepositorio<T, ID, E> {
    
    // Lista para almacenar entidades en memoria
    protected final List<T> entidades = new ArrayList<>();
    
    // Cache para restauración de entidades eliminadas
    protected final List<T> entidadesEliminadas = new ArrayList<>();
    
    // Contador para generar IDs automáticamente
    protected final AtomicLong idContador = new AtomicLong(1);
    
    // Repositorio de auditoría - ahora con lazy loading
    private AuditoriaRepositorio auditoriaRepository;
    
    // Usuario actual
    protected String usuarioActual = "sistema";
    
    // Contador para operaciones CRUD (guardado por lotes)
    private int contadorOperaciones = 0;
    
    // Procesador JSON genérico
    private final BaseProcesadorJson<T> jsonProcesador;
    
    /**
     * Constructor protegido con carga automática desde JSON
     */
    protected BaseRepositorioImpl() {
        // Inicializar procesador JSON
        this.jsonProcesador = new BaseProcesadorJson<>();
        
        // ✅ CARGAR DATOS AUTOMÁTICAMENTE
        cargarDatosDesdeJson();
    }
    
    /**
     * Método lazy para obtener el repositorio de auditoría
     */
    private AuditoriaRepositorio obtenerAuditoria() {
        if (auditoriaRepository == null) {
            auditoriaRepository = RepositorioFactoria.obtenerInstancia().obtenerRepositorioAuditoria();
        }
        return auditoriaRepository;
    }
    
    /**
     * Carga datos desde JSON automáticamente al crear el repositorio
     * Los datos se cargan en la lista entidades
     */
    private void cargarDatosDesdeJson() {
        Map<String, Object> config = obtenerConfiguracion();
        
        // Validación defensiva para campos críticos
        Class<T> tipoClase = Objects.requireNonNull(
            (Class<T>) config.get("tipoClase"), 
            "ERROR CRÍTICO: Tipo de clase no configurado"
        );
        
        Function<T, Long> extractorId = Objects.requireNonNull(
            (Function<T, Long>) config.get("extractorId"), 
            "ERROR CRÍTICO: Extractor de ID no configurado"
        );
        
        // Campo opcional: si es null, no cargar datos
        String ruta = (String) config.get("rutaArchivo");
        if (ruta == null) {
            System.err.println("ADVERTENCIA: Ruta de archivo no configurada, omitiendo carga de datos");
            return;
        }
        
        // Cargar datos desde JSON
        List<T> datosCargados = jsonProcesador.cargarDatosCondicionalmente(ruta, tipoClase);
        
        // Agregar datos cargados a la lista entidades
        entidades.addAll(datosCargados);
        
        // Calcular contador desde datos reales
        Long ultimoId = jsonProcesador.calcularMaximoId(entidades, extractorId);
        idContador.set(ultimoId);
    }
    
    @Override
    public void setUsuarioActual(String usuario) {
        this.usuarioActual = usuario;
    }
    
    @Override
    public Optional<T> crearRegistro(Optional<T> entityOpt, E tipoOperacion) {
        return entityOpt.map(entity -> {
            // ✅ Llamar al método abstracto para asignar ID
            T entidadConId = crearConNuevoId(entity);
            entidades.add(entidadConId);
            registrarAuditoria(entidadConId, tipoOperacion);
            incrementarContadorYGuardar();
            return Optional.of(entidadConId);
        }).orElse(Optional.empty());
    }
    
    /**
     * Método abstracto para asignar nuevo ID a la entidad
     * Cada repositorio implementa su lógica específica
     * @param entidad Entidad sin ID asignado
     * @return Entidad con nuevo ID asignado
     */
    protected abstract T crearConNuevoId(T entidad);
    
    /**
     * Método abstracto que devuelve toda la configuración necesaria
     * @return Map con la configuración del repositorio
     */
    protected abstract Map<String, Object> obtenerConfiguracion();
    
    @Override
    public Optional<T> actualizarRegistro(Optional<T> entityOpt, E tipoOperacion) {
        return entityOpt.flatMap(entidad -> 
            Optional.ofNullable(entidad.getId())
                .map(id -> {
                    entidades.removeIf(e -> e.getId().equals(id));
                    entidades.add(entidad);
                    registrarAuditoria(entidad, tipoOperacion);
                    incrementarContadorYGuardar();
                    return entidad;
                })
        );
    }
    
    @Override
    public Optional<T> buscarPorId(Optional<ID> idOpt) {
        return idOpt.flatMap(id -> 
            entidades.stream()
                .filter(entidad -> entidad.getId().equals(id))
                .findFirst()
        );
    }
    
    /**
     * Método protegido para búsquedas por predicado
     */
    public Optional<T> buscarPorPredicado(Predicate<T> predicado) {
        return entidades.stream()
                .filter(predicado)
                .findFirst();
    }
    
    /**
     * Método protegido para búsquedas de lista por predicado
     */
    public Optional<List<T>> buscarTodosPorPredicado(Predicate<T> predicado) {
        return Optional.of(
            entidades.stream()
                .filter(predicado)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll)
        );
    }
    
    @Override
    public Optional<List<T>> buscarTodos() {
        return Optional.of(new ArrayList<>(entidades));
    }
    
    @Override
    public Optional<T> eliminarPorId(Optional<ID> idOpt, E tipoOperacion) {
        return idOpt.flatMap(id -> {
            Optional<T> entidadAEliminarOpt = buscarPorId(Optional.of(id));
            
            entidadAEliminarOpt.ifPresent(entity -> {
                entidades.remove(entity);
                entidadesEliminadas.add(entity);
                registrarAuditoria(entity, tipoOperacion);
                incrementarContadorYGuardar();
            });
            
            return entidadAEliminarOpt;
        });
    }
    
    @Override
    public long contarRegistros() {
        return entidades.size();
    }
    
    @Override
    public Optional<T> restaurar(Optional<ID> idOpt, E tipoOperacion) {
        return idOpt.flatMap(id -> {
            Optional<T> entidadAResturarOpt = entidadesEliminadas.stream()
                    .filter(entidad -> entidad.getId().equals(id))
                    .findFirst();
                    
            entidadAResturarOpt.ifPresent(entity -> {
                entidades.add(entity);
                entidadesEliminadas.removeIf(e -> e.getId().equals(id));
                registrarAuditoria(entity, tipoOperacion);
            });
            
            return entidadAResturarOpt;
        });
    }
    
    
    /**
     * Método privado para registrar auditoría
     */
    private void registrarAuditoria(T entidad, E tipoOperacion) {
        AuditoriaUtil.registrarOperacion(
            obtenerAuditoria(),
            Optional.of(tipoOperacion),
            Optional.of(entidad),
            Optional.of(usuarioActual)
        );
    }

    @Override
    public List<T> obtenerEliminados() {
        return entidadesEliminadas;
    }
    
    /**
     * Incrementa el contador de operaciones y guarda datos cada 10 operaciones
     */
    protected void incrementarContadorYGuardar() {
        contadorOperaciones++;
        if (contadorOperaciones == 10) {
            Map<String, Object> config = obtenerConfiguracion();
            String ruta = (String) config.get("rutaArchivo");
            if (ruta != null) {
            jsonProcesador.guardarJson(ruta, entidades);
            }
            contadorOperaciones = 0;
        }
    }
} 