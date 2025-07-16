package com.mibanco.repositorio.interna;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Clase utilitaria para procesar archivos JSON de forma genérica
 * Aplica el Principio de Responsabilidad Única (SRP)
 * Se encarga únicamente de operaciones de lectura y escritura de JSON
 * Solo para uso interno de los repositorios
 * @param <T> Tipo de entidad a procesar
 */
class BaseProcesadorJson<T> {
    
    private final ObjectMapper mapper;
    
    public BaseProcesadorJson() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
    
    /**
     * Carga datos desde JSON si existe el archivo, o devuelve lista vacía
     * Enfoque funcional usando Optional
     * @param ruta Ruta del archivo JSON
     * @param tipoClase Clase de los objetos (ej: Cliente.class)
     * @return Lista de objetos cargados desde JSON o lista vacía
     */
    public List<T> cargarDatosCondicionalmente(String ruta, Class<T> tipoClase) {
        return Optional.ofNullable(ruta)
            .filter(this::existeArchivo)
            .map(archivo -> leerJson(archivo, tipoClase))
            .orElse(new ArrayList<>());
    }
    
    /**
     * Calcula el ID máximo de una lista de entidades
     * Enfoque funcional usando streams
     * @param entidades Lista de entidades
     * @param extractorId Función para extraer el ID de cada entidad
     * @return El ID máximo encontrado, o 0 si la lista está vacía
     */
    public Long calcularMaximoId(List<T> entidades, java.util.function.Function<T, Long> extractorId) {
        return entidades.stream()
            .mapToLong(extractorId::apply)
            .max()
            .orElse(0);
    }
    
    /**
     * Verifica si existe un archivo en el sistema
     * @param ruta Ruta del archivo
     * @return true si existe y es un archivo, false en caso contrario
     */
    private boolean existeArchivo(String ruta) {
        File archivo = new File(ruta);
        return archivo.exists() && archivo.isFile();
    }
    
    /**
     * Lee y convierte datos desde archivo JSON
     * @param ruta Ruta del archivo JSON
     * @param tipoClase Clase de los objetos (ej: Cliente.class)
     * @return Lista de objetos cargados desde JSON
     */
    private List<T> leerJson(String ruta, Class<T> tipoClase) {
        try {
            File archivo = new File(ruta);
            
            return mapper.readValue(archivo, 
                mapper.getTypeFactory().constructCollectionType(List.class, tipoClase));
                
        } catch (Exception e) {
            // Si hay error, devolver lista vacía
            System.err.println("Error al leer JSON: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Guarda una lista de objetos en archivo JSON
     * @param ruta Ruta donde guardar el archivo JSON
     * @param datos Lista de objetos a guardar
     */
    public void guardarJson(String ruta, List<T> datos) {
        try {
            File archivo = new File(ruta);
            
            // Crear directorio si no existe
            archivo.getParentFile().mkdirs();
            
            // Escribir JSON con formato legible
            mapper.writerWithDefaultPrettyPrinter().writeValue(archivo, datos);
            
        } catch (Exception e) {
            // Log del error (por ahora solo imprimir)
            System.err.println("Error al guardar JSON: " + e.getMessage());
        }
    }
} 