package com.mibanco.service.impl;

import com.mibanco.dto.CuentaDTO;
import com.mibanco.dto.mapper.CuentaMapper;
import com.mibanco.model.Cuenta;
import com.mibanco.model.enums.TipoCuenta;
import com.mibanco.repository.CuentaRepository;
import com.mibanco.service.CuentaService;
import com.mibanco.config.factory.RepositoryFactory;

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
     * @param cuentaMapper Mapper para conversión entre entidad y DTO
     */
    public CuentaServiceImpl(CuentaMapper cuentaMapper) {
        // Utilizamos la factory para obtener la instancia única del repositorio
        this.cuentaRepository = RepositoryFactory.obtenerInstancia().obtenerRepositorioCuenta();
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
                .flatMap(numero -> cuentaRepository.buscarPorNumero(Optional.of(numero)))
                .map(actualizarDatos)
                .flatMap(cuenta -> cuentaRepository.guardar(Optional.of(cuenta)))
                .flatMap(cuentaMapper::aDtoDirecto);
    }
    
    /**
     * Crea una nueva cuenta en el sistema
     * Utilizando programación funcional con Optional y composición de funciones
     */
    @Override
    public Optional<CuentaDTO> crearCuenta(Optional<CuentaDTO> cuentaDTO) {
        return cuentaDTO
            .flatMap(dto -> cuentaMapper.aEntidad(Optional.of(dto)))
            .flatMap(cuenta -> cuentaRepository.guardar(Optional.of(cuenta)))
            .flatMap(cuentaMapper::aDtoDirecto);
    }
    
    /**
     * Obtiene una cuenta por su número
     * Utilizando programación funcional con Optional
     */
    @Override
    public Optional<CuentaDTO> obtenerCuentaPorNumero(Optional<String> numeroCuenta) {
        return numeroCuenta.flatMap(numero -> 
            cuentaRepository.buscarPorNumero(Optional.of(numero))
                .flatMap(cuentaMapper::aDtoDirecto)
        );
    }
    
    /**
     * Obtiene todas las cuentas de un titular
     * Utilizando programación funcional con Optional
     */
    @Override
    public Optional<List<CuentaDTO>> obtenerCuentasPorTitular(Optional<Long> idTitular) {
        return idTitular.flatMap(id -> 
            cuentaRepository.buscarPorTitularId(Optional.of(id))
                .flatMap(cuentaMapper::aListaDto)
        );
    }
    
    /**
     * Obtiene todas las cuentas de un tipo específico
     * Utilizando programación funcional con Optional
     */
    @Override
    public Optional<List<CuentaDTO>> obtenerCuentasPorTipo(Optional<TipoCuenta> tipo) {
        return tipo.flatMap(t -> 
            cuentaRepository.buscarPorTipo(Optional.of(t))
                .flatMap(cuentaMapper::aListaDto)
        );
    }
    
    /**
     * Obtiene todas las cuentas del sistema
     * Utilizando programación funcional con Optional
     */
    @Override
    public Optional<List<CuentaDTO>> obtenerTodasLasCuentas() {
        return cuentaRepository.buscarTodos()
                .flatMap(cuentaMapper::aListaDto);
    }
    
    /**
     * Obtiene todas las cuentas activas
     * Utilizando programación funcional con Optional
     */
    @Override
    public Optional<List<CuentaDTO>> obtenerCuentasActivas() {
        return cuentaRepository.buscarActivas()
                .flatMap(cuentaMapper::aListaDto);
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
            cuentaDTO.flatMap(cuentaMapper::aEntidadDirecta)
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
                .flatMap(numero -> cuentaRepository.eliminarPorNumero(Optional.of(numero)))
                .isPresent();
    }
} 