package com.mibanco.repository.impl;

import com.mibanco.model.Cuenta;
import com.mibanco.model.enums.TipoCuenta;
import com.mibanco.model.enums.TipoOperacionCuenta;
import com.mibanco.repository.AuditoriaRepository;
import com.mibanco.repository.CuentaRepository;
import com.mibanco.util.AuditoriaUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación del repositorio de Cuentas
 * Extiende la implementación base para heredar funcionalidad CRUD genérica
 */
public class CuentaRepositoryImpl extends BaseRepositoryImpl<Cuenta, String, TipoOperacionCuenta> implements CuentaRepository {
    
    /**
     * Constructor que inicializa el repositorio de auditoría
     */
    public CuentaRepositoryImpl(AuditoriaRepository auditoriaRepository) {
        super(auditoriaRepository);
    }
    
    @Override
    public Optional<Cuenta> findByNumero(Optional<String> numeroCuenta) {
        return numeroCuenta.flatMap(numero -> 
            findByPredicate(cuenta -> cuenta.getNumeroCuenta().equals(numero))
        );
    }
    
    @Override
    public Optional<List<Cuenta>> findByTitularId(Optional<Long> idTitular) {
        return idTitular.flatMap(id -> 
            findAllByPredicate(cuenta -> cuenta.getTitular().getId().equals(id))
        );
    }
    
    @Override
    public Optional<List<Cuenta>> findByTipo(Optional<TipoCuenta> tipo) {
        return tipo.flatMap(t -> 
            findAllByPredicate(cuenta -> cuenta.getTipo() == t)
        );
    }
    
    @Override
    public Optional<List<Cuenta>> findAllActivas() {
        return findAllByPredicate(Cuenta::isActiva);
    }
    
    @Override
    public Optional<Cuenta> deleteByNumero(Optional<String> numeroCuenta) {
        return numeroCuenta.flatMap(numero -> 
            findByNumero(Optional.of(numero))
                .flatMap(cuenta -> deleteById(Optional.of(cuenta.getNumeroCuenta())))
        );
    }
    
    @Override
    protected Cuenta createWithNewId(Cuenta cuenta) {
        return cuenta.toBuilder()
                .numeroCuenta(cuenta.getNumeroCuenta()) // El número de cuenta ya viene asignado
                .build();
    }
    
    @Override
    protected void registrarAuditoria(Cuenta cuenta, TipoOperacionCuenta tipoOperacion) {
                    AuditoriaUtil.registrarOperacion(
                        auditoriaRepository,
                        tipoOperacion,
            cuenta,
                        usuarioActual
        );
    }
    
    @Override
    protected TipoOperacionCuenta getOperationType(OperationType type) {
        return switch (type) {
            case CREATE -> TipoOperacionCuenta.CREAR;
            case UPDATE -> TipoOperacionCuenta.MODIFICAR;
            case DELETE -> TipoOperacionCuenta.ELIMINAR;
            case RESTORE -> TipoOperacionCuenta.ACTIVAR;
        };
    }
} 