package com.mibanco.servicio.interna;

import com.mibanco.dto.CuentaDTO;
import com.mibanco.dto.mapeador.CuentaMapeador;
import com.mibanco.dto.mapeador.ClienteMapeador;
import com.mibanco.modelo.Cuenta;
import com.mibanco.repositorio.CuentaRepositorio;
import com.mibanco.servicio.CuentaServicio;
import com.mibanco.modelo.enums.TipoOperacionCuenta;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio de cuentas
 */
class CuentaServicioImpl extends BaseServicioImpl<CuentaDTO, Cuenta, String, TipoOperacionCuenta> 
        implements CuentaServicio {
    
   // private final CuentaMapeador mapeador;
    private final ClienteMapeador clienteMapeador;
    
    public CuentaServicioImpl(CuentaRepositorio repositorio, CuentaMapeador mapeador, ClienteMapeador clienteMapeador) {
        super(repositorio, mapeador);
        //this.mapeador = mapeador;
        this.clienteMapeador = clienteMapeador;
    }

    @Override
    public Optional<CuentaDTO> crearCuenta(Optional<CuentaDTO> cuentaDTO) {
        return guardar(TipoOperacionCuenta.CREAR, cuentaDTO);
    }

    @Override
    public Optional<CuentaDTO> actualizarVariosCampos(String numeroCuenta, Optional<CuentaDTO> cuentaDTO) {
        return actualizar(
            numeroCuenta,
            cuentaDTO,
            TipoOperacionCuenta.ACTUALIZAR,
            (cuentaExistente, cuentaNueva) -> cuentaExistente.toBuilder()
                .numeroCuenta(cuentaExistente.getNumeroCuenta())
                .titular(cuentaNueva.getTitular())
                .tipo(cuentaNueva.getTipo())
                .saldo(cuentaNueva.getSaldo())
                .fechaCreacion(cuentaExistente.getFechaCreacion())
                .activa(cuentaNueva.isActiva())
                .build()
        );
    }

    @Override
    public Optional<CuentaDTO> obtenerCuentaPorNumero(Optional<String> numeroCuenta) {
        return obtenerPorId(numeroCuenta);
    }

    @Override
    public Optional<List<CuentaDTO>> obtenerTodasLasCuentas() {
        return obtenerTodos();
    }

    @Override
    public Optional<CuentaDTO> actualizarSaldoCuenta(String numeroCuenta, Optional<BigDecimal> nuevoSaldo) {
        return actualizarCampo(
            numeroCuenta,
            nuevoSaldo,
            Cuenta::getSaldo,
            (cuenta, saldo) -> cuenta.conSaldo(saldo)
        );
    }

    @Override
    public Optional<CuentaDTO> actualizarEstadoCuenta(String numeroCuenta, Optional<Boolean> nuevaActiva) {
        return actualizarCampo(
            numeroCuenta,
            nuevaActiva,
            Cuenta::isActiva,
            (cuenta, activa) -> cuenta.conActiva(activa)
        );
    }

    @Override
    public Optional<CuentaDTO> actualizarTitularCuenta(String numeroCuenta, Optional<CuentaDTO> nuevoTitular) {
        return nuevoTitular.flatMap(titularDTO -> 
            clienteMapeador.aEntidad(Optional.of(titularDTO.getTitular()))
                .flatMap(titular -> actualizarCampo(
                    numeroCuenta,
                    Optional.of(titular),
                    Cuenta::getTitular,
                    (cuenta, nvoTitular) -> cuenta.toBuilder().titular(titular).build()
                ))
        );
    }

    @Override
    public boolean eliminarCuenta(Optional<String> numeroCuenta) {
        return eliminarPorId(numeroCuenta, TipoOperacionCuenta.ELIMINAR);
    }

    @Override
    public Optional<CuentaDTO> restaurarCuenta(Optional<String> numeroCuenta) {
        return restaurar(numeroCuenta, TipoOperacionCuenta.RESTAURAR);
    }

    @Override
    public List<CuentaDTO> obtenerCuentasEliminadas() {
        return obtenerEliminados();
    }

    @Override
    public long contarCuentas() {
        return contarRegistros();
    }

    @Override
    public void establecerUsuarioActual(String usuario) {
        super.establecerUsuarioActual(usuario);
    }
} 