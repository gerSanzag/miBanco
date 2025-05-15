package com.mibanco.repository.impl;

import com.mibanco.model.Transaccion;
import com.mibanco.model.enums.TipoOperacionTransaccion;
import com.mibanco.model.enums.TipoTransaccion;
import com.mibanco.repository.AuditoriaRepository;
import com.mibanco.repository.TransaccionRepository;
import com.mibanco.util.AuditoriaUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Implementación del repositorio de Transacciones usando una lista en memoria
 * Utilizamos enfoque estrictamente funcional con streams y Optional
 * Ahora con soporte para auditoría de operaciones usando AuditoriaUtil simplificado
 */
public class TransaccionRepositoryImpl implements TransaccionRepository {
    
    // Lista para almacenar transacciones en memoria
    private final List<Transaccion> transacciones = new ArrayList<>();
    
    // Cache para restauración de transacciones anuladas
    private final List<Transaccion> transaccionesAnuladas = new ArrayList<>();
    
    // Contador para generar IDs automáticamente
    private final AtomicLong idCounter = new AtomicLong(1);
    
    // Repositorio de auditoría
    private final AuditoriaRepository auditoriaRepository;
    
    // Usuario actual (en un sistema real vendría de un sistema de autenticación)
    private String usuarioActual = "sistema";
    
    /**
     * Constructor con inyección del repositorio de auditoría
     */
    public TransaccionRepositoryImpl(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }
    
    /**
     * Establece el usuario actual que realiza las operaciones
     */
    public void setUsuarioActual(String usuario) {
        this.usuarioActual = usuario;
    }
    
    @Override
    public Optional<Transaccion> save(Optional<Transaccion> transaccion) {
        return transaccion.map(t -> {
            // Si la transacción ya existe, la actualizamos
            if (t.getId() != null) {
                Transaccion transaccionActualizada = update(t);
                
                // Registra la auditoría de verificación directamente
                AuditoriaUtil.registrarOperacion(
                    auditoriaRepository,
                    TipoOperacionTransaccion.VERIFICAR, 
                    transaccionActualizada,
                    usuarioActual
                );
                
                return transaccionActualizada;
            }
            
            // Si es nueva, generamos ID y la guardamos
            Transaccion nuevaTransaccion = t.withNuevoId(idCounter.getAndIncrement());
            transacciones.add(nuevaTransaccion);
            
            // Registra la auditoría de creación directamente
            AuditoriaUtil.registrarOperacion(
                auditoriaRepository,
                TipoOperacionTransaccion.CREAR, 
                nuevaTransaccion,
                usuarioActual
            );
            
            return nuevaTransaccion;
        });
    }
    
    /**
     * Método auxiliar para actualizar una transacción existente
     */
    private Transaccion update(Transaccion transaccion) {
        // Buscamos y eliminamos la transacción actual
        transacciones.removeIf(t -> t.getId().equals(transaccion.getId()));
        // Añadimos la transacción actualizada
        transacciones.add(transaccion);
        return transaccion;
    }
    
    @Override
    public Optional<Transaccion> findById(Optional<Long> id) {
        return id.flatMap(idValue -> 
            transacciones.stream()
                .filter(transaccion -> transaccion.getId().equals(idValue))
                .findFirst()
        );
    }
    
    @Override
    public List<Transaccion> findByCuenta(Optional<String> numeroCuenta) {
        return numeroCuenta.map(numero -> 
            transacciones.stream()
                .filter(transaccion -> transaccion.getNumeroCuenta().equals(numero))
                .collect(Collectors.toList())
        ).orElse(new ArrayList<>());
    }
    
    @Override
    public List<Transaccion> findByTipo(Optional<TipoTransaccion> tipo) {
        return tipo.map(t -> 
            transacciones.stream()
                .filter(transaccion -> transaccion.getTipo().equals(t))
                .collect(Collectors.toList())
        ).orElse(new ArrayList<>());
    }
    
    @Override
    public List<Transaccion> findByFecha(Optional<LocalDate> fecha) {
        return fecha.map(f -> 
            transacciones.stream()
                .filter(transaccion -> transaccion.getFecha().toLocalDate().equals(f))
                .collect(Collectors.toList())
        ).orElse(new ArrayList<>());
    }
    
    @Override
    public List<Transaccion> findByRangoFechas(Optional<LocalDate> fechaInicio, Optional<LocalDate> fechaFin) {
        // Usando un enfoque más funcional para manejar las diferentes combinaciones de fechas
        
        // Predicado para filtrar por fecha de inicio (si está presente)
        Predicate<Transaccion> filtroInicio = fechaInicio
            .map(inicio -> (Predicate<Transaccion>) t -> 
                !t.getFecha().toLocalDate().isBefore(inicio))
            .orElse(t -> true); // Si no hay fecha inicio, no filtramos
        
        // Predicado para filtrar por fecha de fin (si está presente)
        Predicate<Transaccion> filtroFin = fechaFin
            .map(fin -> (Predicate<Transaccion>) t -> 
                !t.getFecha().toLocalDate().isAfter(fin))
            .orElse(t -> true); // Si no hay fecha fin, no filtramos
        
        // Verificamos si ambos Optionals están vacíos usando un enfoque funcional
        boolean ambasVacias = !fechaInicio.isPresent() && !fechaFin.isPresent();
        
        // Usando operador ternario en lugar de if
        return ambasVacias ? 
            new ArrayList<>() : 
            transacciones.stream()
                .filter(filtroInicio)
                .filter(filtroFin)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<List<Transaccion>> findAll() {
        // Devolvemos una copia para no exponer la lista interna
        return Optional.of(new ArrayList<>(transacciones));
    }
    
    @Override
    public Optional<Transaccion> deleteById(Optional<Long> id) {
        return id.flatMap(idValue -> {
            Optional<Transaccion> transaccionAEliminar = findById(Optional.of(idValue));
            
            transaccionAEliminar.ifPresent(transaccion -> {
                // Guardamos la transacción en la caché para posible restauración
                transaccionesAnuladas.add(transaccion);
                
                // Eliminamos la transacción
                transacciones.removeIf(t -> t.getId().equals(idValue));
                
                // Registramos la anulación directamente
                AuditoriaUtil.registrarOperacion(
                    auditoriaRepository,
                    TipoOperacionTransaccion.ANULAR,
                    transaccion,
                    usuarioActual
                );
            });
            
            return transaccionAEliminar;
        });
    }
    
    /**
     * Restaura una transacción previamente anulada
     * @param id ID de la transacción a restaurar
     * @return Transacción restaurada o empty si no se encuentra
     */
    public Optional<Transaccion> restaurarTransaccion(Optional<Long> id) {
        return id.flatMap(idValue -> {
            Optional<Transaccion> transaccionARestaurar = transaccionesAnuladas.stream()
                    .filter(transaccion -> transaccion.getId().equals(idValue))
                    .findFirst();
                    
            transaccionARestaurar.ifPresent(transaccion -> {
                // Añadimos de nuevo la transacción a la lista principal
                transacciones.add(transaccion);
                
                // La quitamos de la caché de anuladas
                transaccionesAnuladas.removeIf(t -> t.getId().equals(idValue));
                
                // Registra la auditoría de restauración (revertir anulación) directamente
                AuditoriaUtil.registrarOperacion(
                    auditoriaRepository,
                    TipoOperacionTransaccion.REVERSAR,
                    transaccion,
                    usuarioActual
                );
            });
            
            return transaccionARestaurar;
        });
    }
    
    /**
     * Obtiene la lista de transacciones recientemente anuladas
     * @return Lista de transacciones anuladas
     */
    public List<Transaccion> getTransaccionesAnuladas() {
        return new ArrayList<>(transaccionesAnuladas);
    }
    
    /**
     * Autoriza una transacción
     * @param idTransaccion ID de la transacción a autorizar
     * @return La transacción autorizada o empty si no existe
     */
    public Optional<Transaccion> autorizarTransaccion(Optional<Long> idTransaccion) {
        return idTransaccion.flatMap(id -> 
            findById(Optional.of(id)).map(transaccion -> {
                // Podríamos actualizar la transacción si fuera necesario
                
                // Registrar auditoría de autorización directamente
                AuditoriaUtil.registrarOperacion(
                    auditoriaRepository,
                    TipoOperacionTransaccion.AUTORIZAR,
                    transaccion,
                    usuarioActual
                );
                
                return transaccion;
            })
        );
    }
    
    /**
     * Rechaza una transacción
     * @param idTransaccion ID de la transacción a rechazar
     * @param motivo Motivo del rechazo
     * @return La transacción rechazada o empty si no existe
     */
    public Optional<Transaccion> rechazarTransaccion(Optional<Long> idTransaccion, String motivo) {
        return idTransaccion.flatMap(id -> 
            findById(Optional.of(id)).map(transaccion -> {
                // Actualizamos la descripción para incluir el motivo de rechazo
                Transaccion transaccionRechazada = transaccion.withDescripcion(
                    "RECHAZADA: " + motivo + ". " + transaccion.getDescripcion()
                );
                
                // Actualizamos la transacción en la lista
                transacciones.removeIf(t -> t.getId().equals(id));
                transacciones.add(transaccionRechazada);
                
                // Registrar auditoría de rechazo directamente
                AuditoriaUtil.registrarOperacion(
                    auditoriaRepository,
                    TipoOperacionTransaccion.RECHAZAR,
                    transaccionRechazada,
                    usuarioActual
                );
                
                return transaccionRechazada;
            })
        );
    }
    
    @Override
    public long count() {
        return transacciones.size();
    }
} 