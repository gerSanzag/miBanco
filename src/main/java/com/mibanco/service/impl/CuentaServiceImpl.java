package com.mibanco.service.impl;

import com.mibanco.dto.CuentaDTO;
import com.mibanco.dto.mapper.CuentaMapper;
import com.mibanco.model.Cuenta;
import com.mibanco.model.enums.TipoCuenta;
import com.mibanco.repository.CuentaRepository;
import com.mibanco.service.CuentaService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Implementación de la interfaz de servicio para Cuentas
 * Utilizando estrictamente un enfoque funcional con Optional y streams
 */
public class CuentaServiceImpl implements CuentaService {
    
    private final CuentaRepository cuentaRepository;
    private final CuentaMapper cuentaMapper;
    
    /**
     * Constructor para inyección de dependencias
     * @param cuentaRepository Repositorio de cuentas
     * @param cuentaMapper Mapper para conversión entre entidad y DTO
     */
    public CuentaServiceImpl(CuentaRepository cuentaRepository, CuentaMapper cuentaMapper) {
        this.cuentaRepository = cuentaRepository;
        this.cuentaMapper = cuentaMapper;
    }
    
    /**
     * Método privado que implementa el flujo común de actualización de cuentas
     * @param numeroCuenta Número de la cuenta a actualizar
     * @param actualizarDatos Función que define cómo actualizar los datos de la cuenta
     * @return Optional con el DTO de la cuenta actualizada
     */
    private Optional<CuentaDTO> actualizarCuentaGenerica(String numeroCuenta, Function<Cuenta, Cuenta> actualizarDatos) {
        return Optional.ofNullable(numeroCuenta)
                .flatMap(numero -> cuentaRepository.findByNumero(Optional.of(numero)))
                .map(actualizarDatos)
                .flatMap(cuenta -> cuentaRepository.save(Optional.of(cuenta)))
                .flatMap(cuentaMapper::toDtoDirecto);
    }
    
    /**
     * Crea una nueva cuenta en el sistema
     * Utilizando programación funcional con Optional y composición de funciones
     */
    @Override
    public Optional<CuentaDTO> crearCuenta(Optional<CuentaDTO> cuentaDTO) {
        return cuentaDTO
            .map(dto -> {
                Optional<Cuenta> entidad = cuentaMapper.toEntity(Optional.of(dto));
                if (!entidad.isPresent()) {
                    return Optional.<CuentaDTO>empty();
                }
                
                Optional<Cuenta> guardada = cuentaRepository.save(entidad);
                return cuentaMapper.toDto(guardada);
            })
            .orElse(Optional.empty());
    }
    
    /**
     * Obtiene una cuenta por su número
     * Utilizando programación funcional con Optional
     */
    @Override
    public Optional<CuentaDTO> obtenerCuentaPorNumero(Optional<String> numeroCuenta) {
        return numeroCuenta.flatMap(numero -> {
            Optional<Cuenta> cuenta = cuentaRepository.findByNumero(Optional.of(numero));
            return cuenta.flatMap(cuentaMapper::toDtoDirecto);
        });
    }
    
    /**
     * Obtiene todas las cuentas de un titular
     * Utilizando programación funcional con Optional
     */
    @Override
    public Optional<List<CuentaDTO>> obtenerCuentasPorTitular(Optional<Long> idTitular) {
        return idTitular.flatMap(id -> {
            Optional<List<Cuenta>> cuentas = cuentaRepository.findByTitularId(Optional.of(id));
            return cuentaMapper.toDtoList(cuentas);
        });
    }
    
    /**
     * Obtiene todas las cuentas de un tipo específico
     * Utilizando programación funcional con Optional
     */
    @Override
    public Optional<List<CuentaDTO>> obtenerCuentasPorTipo(Optional<TipoCuenta> tipo) {
        return tipo.flatMap(tipoCuenta -> {
            Optional<List<Cuenta>> cuentas = cuentaRepository.findByTipo(Optional.of(tipoCuenta));
            return cuentaMapper.toDtoList(cuentas);
        });
    }
    
    /**
     * Obtiene todas las cuentas del sistema
     * Utilizando programación funcional con Optional
     */
    @Override
    public Optional<List<CuentaDTO>> obtenerTodasLasCuentas() {
        return cuentaRepository.findAll()
                .flatMap(cuentaMapper::toDtoList);
    }
    
    /**
     * Obtiene todas las cuentas activas
     * Utilizando programación funcional con Optional
     */
    @Override
    public Optional<List<CuentaDTO>> obtenerCuentasActivas() {
        return cuentaRepository.findAllActivas()
                .flatMap(cuentaMapper::toDtoList);
    }
    
    /**
     * Actualiza el saldo de una cuenta
     * Utilizando enfoque funcional con map y flatMap
     */
    @Override
    public Optional<CuentaDTO> actualizarSaldoCuenta(String numeroCuenta, Optional<BigDecimal> nuevoSaldo) {
        return actualizarCuentaGenerica(
            numeroCuenta,
            cuenta -> cuenta.withSaldo(nuevoSaldo.orElse(cuenta.getSaldo()))
        );
    }
    
    /**
     * Activa o desactiva una cuenta
     * Utilizando enfoque funcional con map y flatMap
     */
    @Override
    public Optional<CuentaDTO> actualizarEstadoCuenta(String numeroCuenta, Optional<Boolean> activa) {
        return actualizarCuentaGenerica(
            numeroCuenta,
            cuenta -> cuenta.withActiva(activa.orElse(cuenta.isActiva()))
        );
    }
    
    /**
     * Actualiza múltiples campos de una cuenta
     * Utilizando programación funcional con Optional y composición de funciones
     */
    @Override
    public Optional<CuentaDTO> actualizarCuenta(String numeroCuenta, Optional<CuentaDTO> cuentaDTO) {
        Function<Cuenta, Cuenta> actualizarDatos = cuentaExistente -> 
            cuentaDTO.flatMap(cuentaMapper::toEntityDirecto)
                .map(cuentaNueva -> cuentaExistente.withActualizaciones(
                    Optional.ofNullable(cuentaNueva.getSaldo()),
                    Optional.ofNullable(cuentaNueva.isActiva())
                ))
                .orElse(cuentaExistente);
        
        return actualizarCuentaGenerica(numeroCuenta, actualizarDatos);
    }
    
    /**
     * Elimina una cuenta
     * Utilizando programación funcional sin if explícitos
     */
    @Override
    public boolean eliminarCuenta(Optional<String> numeroCuenta) {
        return numeroCuenta
                .map(numero -> cuentaRepository.deleteByNumero(Optional.of(numero)))
                .isPresent();
    }
} 