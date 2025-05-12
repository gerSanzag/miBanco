package com.mibanco.repository.impl;

import com.mibanco.model.Tarjeta;
import com.mibanco.model.enums.TipoOperacionTarjeta;
import com.mibanco.model.enums.TipoTarjeta;
import com.mibanco.repository.AuditoriaRepository;
import com.mibanco.repository.TarjetaRepository;
import com.mibanco.util.AuditoriaUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación del repositorio de Tarjetas usando una lista en memoria
 * Utilizamos enfoque estrictamente funcional con streams y Optional
 * Ahora con soporte para auditoría de operaciones usando AuditoriaUtil simplificado
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
    
    /**
     * Constructor con inyección del repositorio de auditoría
     */
    public TarjetaRepositoryImpl(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }
    
    /**
     * Establece el usuario actual que realiza las operaciones
     */
    public void setUsuarioActual(String usuario) {
        this.usuarioActual = usuario;
    }
    
    @Override
    public Optional<Tarjeta> save(Optional<Tarjeta> tarjeta) {
        return tarjeta.map(t -> {
            // Verificamos si la tarjeta ya existe
            Optional<String> numeroTarjeta = Optional.ofNullable(t.getNumero());
            
            return findByNumero(numeroTarjeta)
                .map(existente -> {
                    // Si existe, la actualizamos
                    tarjetas.removeIf(e -> e.getNumero().equals(t.getNumero()));
                    
                    // Registrar auditoría de modificación directamente
                    AuditoriaUtil.registrarOperacion(
                        auditoriaRepository,
                        TipoOperacionTarjeta.MODIFICAR,
                        t,
                        usuarioActual
                    );
                    
                    tarjetas.add(t);
                    return t;
                })
                .orElseGet(() -> {
                    // Si no existe, la creamos
                    // Registrar auditoría de creación directamente
                    AuditoriaUtil.registrarOperacion(
                        auditoriaRepository,
                        TipoOperacionTarjeta.CREAR,
                        t,
                        usuarioActual
                    );
                    
                    tarjetas.add(t);
                    return t;
                });
        });
    }
    
    @Override
    public Optional<Tarjeta> findByNumero(Optional<String> numero) {
        return numero.flatMap(num -> 
            tarjetas.stream()
                .filter(tarjeta -> tarjeta.getNumero().equals(num))
                .findFirst()
        );
    }
    
    @Override
    public Optional<List<Tarjeta>> findByTitularId(Optional<Long> titularId) {
        return titularId.map(id -> 
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
                .filter(tarjeta -> tarjeta.getTipo() == t)
                .collect(Collectors.toList())
        );
    }
    
    @Override
    public Optional<List<Tarjeta>> findAll() {
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
    public boolean deleteByNumero(Optional<String> numero) {
        return numero.map(num -> {
            Optional<Tarjeta> tarjetaAEliminar = findByNumero(Optional.of(num));
            
            tarjetaAEliminar.ifPresent(tarjeta -> {
                // Removemos la tarjeta de la lista principal
                tarjetas.remove(tarjeta);
                
                // Guardamos la tarjeta en la caché para posible restauración
                tarjetasEliminadas.add(tarjeta);
                
                // Registrar auditoría de eliminación directamente
                AuditoriaUtil.registrarOperacion(
                    auditoriaRepository,
                    TipoOperacionTarjeta.ELIMINAR,
                    tarjeta,
                    usuarioActual
                );
            });
            
            return tarjetaAEliminar.isPresent();
        }).orElse(false);
    }
    
    /**
     * Restaura una tarjeta previamente eliminada
     * @param numero Número de la tarjeta a restaurar
     * @return Tarjeta restaurada o empty si no se encuentra
     */
    public Optional<Tarjeta> restoreDeletedCard(Optional<String> numero) {
        return numero.flatMap(num -> {
            Optional<Tarjeta> tarjetaARestaurar = tarjetasEliminadas.stream()
                    .filter(tarjeta -> tarjeta.getNumero().equals(num))
                    .findFirst();
                    
            tarjetaARestaurar.ifPresent(tarjeta -> {
                // Añadimos de nuevo la tarjeta a la lista principal
                tarjetas.add(tarjeta);
                
                // La quitamos de la caché de eliminados
                tarjetasEliminadas.removeIf(t -> t.getNumero().equals(num));
                
                // Registrar auditoría de restauración directamente
                AuditoriaUtil.registrarOperacion(
                    auditoriaRepository,
                    TipoOperacionTarjeta.ACTIVAR,
                    tarjeta,
                    usuarioActual
                );
            });
            
            return tarjetaARestaurar;
        });
    }
    
    /**
     * Cambia el estado activo de una tarjeta
     * @param numero Número de la tarjeta
     * @param activa Nuevo estado
     * @return Tarjeta actualizada o empty si no existe
     */
    public Optional<Tarjeta> cambiarEstadoActiva(Optional<String> numero, boolean activa) {
        return numero.flatMap(num -> 
            findByNumero(Optional.of(num))
                .map(tarjeta -> {
                    // Creamos una nueva tarjeta con el estado actualizado (inmutabilidad)
                    Tarjeta tarjetaActualizada = tarjeta.withActiva(activa);
                    
                    // Actualizamos en el repositorio
                    tarjetas.removeIf(t -> t.getNumero().equals(num));
                    tarjetas.add(tarjetaActualizada);
                    
                    // Registrar auditoría del cambio de estado directamente
                    TipoOperacionTarjeta tipoOperacion = activa ? 
                            TipoOperacionTarjeta.ACTIVAR : TipoOperacionTarjeta.DESACTIVAR;
                    
                    AuditoriaUtil.registrarOperacion(
                        auditoriaRepository,
                        tipoOperacion,
                        tarjetaActualizada,
                        usuarioActual
                    );
                    
                    return tarjetaActualizada;
                })
        );
    }
    
    @Override
    public long count() {
        return tarjetas.size();
    }
} 