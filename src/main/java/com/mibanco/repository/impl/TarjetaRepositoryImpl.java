package com.mibanco.repository.impl;

import com.mibanco.model.RegistroAuditoria;
import com.mibanco.model.Tarjeta;
import com.mibanco.model.enums.TipoOperacionTarjeta;
import com.mibanco.model.enums.TipoTarjeta;
import com.mibanco.repository.AuditoriaRepository;
import com.mibanco.repository.TarjetaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Implementación del repositorio de Tarjetas usando una lista en memoria
 * Utilizamos enfoque estrictamente funcional con streams y Optional
 * Ahora con soporte para auditoría de operaciones
 */
public class TarjetaRepositoryImpl implements TarjetaRepository {
    
    // Lista para almacenar tarjetas en memoria
    private final List<Tarjeta> tarjetas = new ArrayList<>();
    
    // Cache para restauración de tarjetas eliminadas
    private final List<Tarjeta> tarjetasEliminadas = new ArrayList<>();
    
    // Repositorio de auditoría
    private final AuditoriaRepository auditoriaRepository;
    
    // Usuario actual (en un sistema real vendría de un sistema de autenticación)
    private String usuarioActual = "sistema";
    
    // Función para registrar auditoría en un estilo más funcional
    private BiFunction<TipoOperacionTarjeta, Tarjeta, RegistroAuditoria<Tarjeta, TipoOperacionTarjeta>> registrarAuditoria;
    
    /**
     * Constructor con inyección del repositorio de auditoría
     */
    public TarjetaRepositoryImpl(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
        // Inicializar la función después de asignar el repositorio
        this.registrarAuditoria = (operacion, tarjeta) -> auditoriaRepository.registrar(
            RegistroAuditoria.of(operacion, tarjeta, usuarioActual)
        );
    }
    
    /**
     * Establece el usuario actual que realiza las operaciones
     */
    public void setUsuarioActual(String usuario) {
        this.usuarioActual = usuario;
    }
    
    @Override
    public Optional<Tarjeta> save(Optional<Tarjeta> tarjeta) {
        // Si la tarjeta es null (Optional vacío), devolvemos Optional vacío
        return tarjeta.map(t -> {
            // Verificamos si la tarjeta ya existe
            Optional<String> numeroTarjeta = Optional.ofNullable(t.getNumero());
            
            return findByNumero(numeroTarjeta)
                .map(existente -> {
                    // Si existe, la actualizamos
                    tarjetas.removeIf(e -> e.getNumero().equals(t.getNumero()));
                    
                    // Registrar auditoría de modificación
                    registrarAuditoria.apply(TipoOperacionTarjeta.MODIFICAR, t);
                    
                    tarjetas.add(t);
                    return t;
                })
                .orElseGet(() -> {
                    // Si no existe, la creamos
                    // Registrar auditoría de creación
                    registrarAuditoria.apply(TipoOperacionTarjeta.CREAR, t);
                    
                    tarjetas.add(t);
                    return t;
                });
        });
    }
    
    @Override
    public Optional<Tarjeta> findByNumero(Optional<String> numeroTarjeta) {
        return numeroTarjeta.flatMap(numero -> 
            tarjetas.stream()
                .filter(tarjeta -> tarjeta.getNumero().equals(numero))
                .findFirst()
        );
    }
    
    @Override
    public Optional<List<Tarjeta>> findByTitularId(Optional<Long> idTitular) {
        return idTitular.map(id -> 
            tarjetas.stream()
                .filter(tarjeta -> tarjeta.getTitular().getId().equals(id))
                .collect(Collectors.toList())
        );
    }
    
    @Override
    public Optional<List<Tarjeta>> findByCuentaAsociada(Optional<String> numeroCuenta) {
        return numeroCuenta.map(numero -> 
            tarjetas.stream()
                .filter(tarjeta -> tarjeta.getNumeroCuentaAsociada().equals(numero))
                .collect(Collectors.toList())
        );
    }
    
    @Override
    public Optional<List<Tarjeta>> findByTipo(Optional<TipoTarjeta> tipo) {
        return tipo.map(t -> 
            tarjetas.stream()
                .filter(tarjeta -> tarjeta.getTipo().equals(t))
                .collect(Collectors.toList())
        );
    }
    
    @Override
    public Optional<List<Tarjeta>> findAll() {
        // Devolvemos una copia para no exponer la lista interna
        return Optional.of(new ArrayList<>(tarjetas));
    }
    
    @Override
    public Optional<List<Tarjeta>> findAllActivas() {
        return Optional.of(
            tarjetas.stream()
                .filter(Tarjeta::isActiva)
                .collect(Collectors.toList())
        );
    }
    
    @Override
    public boolean deleteByNumero(Optional<String> numeroTarjeta) {
        return numeroTarjeta.map(numero -> {
            Optional<Tarjeta> tarjetaAEliminar = findByNumero(Optional.of(numero));
            
            return tarjetaAEliminar.map(tarjeta -> {
                // Removemos la tarjeta de la lista principal
                tarjetas.removeIf(t -> t.getNumero().equals(numero));
                
                // Guardamos la tarjeta en la caché para posible restauración
                tarjetasEliminadas.add(tarjeta);
                
                // Registrar auditoría de eliminación
                registrarAuditoria.apply(TipoOperacionTarjeta.ELIMINAR, tarjeta);
                
                return true;
            }).orElse(false);
        }).orElse(false);
    }
    
    /**
     * Restaura una tarjeta previamente eliminada
     * @param numeroTarjeta Número de la tarjeta a restaurar
     * @return Tarjeta restaurada o empty si no se encuentra
     */
    public Optional<Tarjeta> restaurarTarjeta(Optional<String> numeroTarjeta) {
        return numeroTarjeta.flatMap(numero -> {
            Optional<Tarjeta> tarjetaARestaurar = tarjetasEliminadas.stream()
                    .filter(tarjeta -> tarjeta.getNumero().equals(numero))
                    .findFirst();
                    
            tarjetaARestaurar.ifPresent(tarjeta -> {
                // Añadimos de nuevo la tarjeta a la lista principal
                tarjetas.add(tarjeta);
                
                // La quitamos de la caché de eliminados
                tarjetasEliminadas.removeIf(t -> t.getNumero().equals(numero));
                
                // Registra la auditoría de restauración
                registrarAuditoria.apply(TipoOperacionTarjeta.ACTIVAR, tarjeta);
            });
            
            return tarjetaARestaurar;
        });
    }
    
    /**
     * Obtiene la lista de tarjetas recientemente eliminadas
     * @return Lista de tarjetas eliminadas
     */
    public List<Tarjeta> getTarjetasEliminadas() {
        return new ArrayList<>(tarjetasEliminadas);
    }
    
    /**
     * Activa una tarjeta previamente desactivada
     * @param numeroTarjeta Número de la tarjeta a activar
     * @return true si se activó la tarjeta, false si no existía
     */
    public boolean activarTarjeta(Optional<String> numeroTarjeta) {
        return numeroTarjeta.map(numero -> 
            findByNumero(Optional.of(numero)).map(tarjeta -> 
                // Usando el enfoque funcional para evitar if
                Optional.of(tarjeta.isActiva())
                    .filter(activa -> activa) // Si ya está activa
                    .map(activa -> false) // No hacemos nada y devolvemos false
                    .orElseGet(() -> {
                        // Si no está activa, la activamos
                        tarjetas.removeIf(t -> t.getNumero().equals(numero));
                        
                        // Creamos una versión activada
                        Tarjeta tarjetaActivada = tarjeta.withActiva(true);
                        
                        // Añadimos la tarjeta activada
                        tarjetas.add(tarjetaActivada);
                        
                        // Registramos la activación
                        registrarAuditoria.apply(TipoOperacionTarjeta.ACTIVAR, tarjetaActivada);
                        
                        return true;
                    })
            ).orElse(false)
        ).orElse(false);
    }
    
    /**
     * Desactiva una tarjeta
     * @param numeroTarjeta Número de la tarjeta a desactivar
     * @return true si se desactivó la tarjeta, false si no existía
     */
    public boolean desactivarTarjeta(Optional<String> numeroTarjeta) {
        return numeroTarjeta.map(numero -> 
            findByNumero(Optional.of(numero)).map(tarjeta -> 
                // Usando el enfoque funcional para evitar if
                Optional.of(!tarjeta.isActiva())
                    .filter(inactiva -> inactiva) // Si ya está inactiva
                    .map(inactiva -> false) // No hacemos nada y devolvemos false
                    .orElseGet(() -> {
                        // Si está activa, la desactivamos
                        tarjetas.removeIf(t -> t.getNumero().equals(numero));
                        
                        // Creamos una versión desactivada
                        Tarjeta tarjetaDesactivada = tarjeta.withActiva(false);
                        
                        // Añadimos la tarjeta desactivada
                        tarjetas.add(tarjetaDesactivada);
                        
                        // Registramos la desactivación
                        registrarAuditoria.apply(TipoOperacionTarjeta.DESACTIVAR, tarjetaDesactivada);
                        
                        return true;
                    })
            ).orElse(false)
        ).orElse(false);
    }
    
    @Override
    public long count() {
        return tarjetas.size();
    }
} 