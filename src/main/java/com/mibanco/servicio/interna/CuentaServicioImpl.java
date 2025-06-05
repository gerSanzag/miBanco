package com.mibanco.servicio.interna;

import com.mibanco.dto.CuentaDTO;
import com.mibanco.dto.mapeador.CuentaMapeador;
import com.mibanco.dto.mapeador.ClienteMapeador;
import com.mibanco.modelo.Cuenta;
import com.mibanco.modelo.enums.TipoCuenta;
import com.mibanco.repositorio.CuentaRepositorio;
import com.mibanco.servicio.CuentaServicio;
import com.mibanco.modelo.enums.TipoOperacionCuenta;
import com.mibanco.repositorio.interna.RepositorioFactoria;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio de cuentas
 */
class CuentaServicioImpl extends BaseServicioImpl<CuentaDTO, Cuenta, String, TipoOperacionCuenta, CuentaRepositorio> 
        implements CuentaServicio {
    
    private static final CuentaRepositorio repositorioCuenta;
    private static final CuentaMapeador mapeador;
    private static final ClienteMapeador clienteMapeador;
    private final TipoOperacionCuenta tipoActualizar = TipoOperacionCuenta.ACTUALIZAR;

    static {
        repositorioCuenta = RepositorioFactoria.obtenerInstancia().obtenerRepositorioCuenta();
        clienteMapeador = new ClienteMapeador();
        mapeador = new CuentaMapeador(clienteMapeador);
    }

    public CuentaServicioImpl() {
        super(repositorioCuenta, mapeador);
    }

    @Override
    public Optional<CuentaDTO> crearCuenta(Optional<CuentaDTO> cuentaDTO) {
        return guardar(TipoOperacionCuenta.CREAR, cuentaDTO);
    }

    @Override
    public Optional<CuentaDTO> actualizarVariosCampos(String numeroCuenta, Optional<CuentaDTO> cuentaDTO) {
        Optional<CuentaDTO> actualizaVariosCampos = actualizar(
            numeroCuenta,
            cuentaDTO,
            TipoOperacionCuenta.ACTUALIZAR,
            (cuentaExistente, cuentaNueva) -> cuentaExistente.conActualizaciones(
                Optional.ofNullable(cuentaNueva.getSaldo()),
                Optional.ofNullable(cuentaNueva.isActiva())
            )
        );
        guardar(TipoOperacionCuenta.ACTUALIZAR, actualizaVariosCampos);
        return actualizaVariosCampos;
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
        Optional<CuentaDTO> actualizaSaldo = actualizarCampo(
            numeroCuenta,
            nuevoSaldo,
            Cuenta::getSaldo,
            Cuenta::conSaldo
        );
        guardar(TipoOperacionCuenta.ACTUALIZAR, actualizaSaldo);
        return actualizaSaldo;
    }

    @Override
    public Optional<CuentaDTO> actualizarEstadoCuenta(String numeroCuenta, Optional<Boolean> nuevaActiva) {
        Optional<CuentaDTO> actualizaEstado = actualizarCampo(
            numeroCuenta,
            nuevaActiva,
            Cuenta::isActiva,
            Cuenta::conActiva
        );
        guardar(TipoOperacionCuenta.ACTUALIZAR, actualizaEstado);
        return actualizaEstado;
    }

   
    public Optional<CuentaDTO> actualizarTitularCuenta(String numeroCuenta, Optional<CuentaDTO> nuevoTitular) {
        Optional<CuentaDTO> actualizaTitular = nuevoTitular.flatMap(titularDTO -> 
            clienteMapeador.aEntidad(Optional.of(titularDTO.getTitular()))
                .flatMap(titular -> actualizarCampo(
                    numeroCuenta,
                    Optional.of(titular),
                    Cuenta::getTitular,
                    (cuenta, nvoTitular) -> cuenta.toBuilder().titular(titular).build()
                ))
        );
        guardar(TipoOperacionCuenta.ACTUALIZAR, actualizaTitular);
        return actualizaTitular;
    }

    @Override
    public boolean eliminarCuenta(Optional<String> numeroCuenta) {
        return eliminarPorId(numeroCuenta, TipoOperacionCuenta.ELIMINAR);
    }

    @Override
    public Optional<CuentaDTO> eliminarPorNumero(Optional<String> numeroCuenta) {
        return repositorio.eliminarPorNumero(numeroCuenta)
            .flatMap(cuenta -> mapeador.aDto(Optional.of(cuenta)));
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

    @Override
    public Optional<List<CuentaDTO>> buscarPorTitularId(Optional<Long> idTitular) {
        return repositorio.buscarPorTitularId(idTitular)
            .map(cuentas -> cuentas.stream()
                .map(cuenta -> mapeador.aDto(Optional.of(cuenta)).orElse(null))
                .filter(java.util.Objects::nonNull)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll)
            );
    }

    @Override
    public Optional<List<CuentaDTO>> buscarPorTipo(Optional<TipoCuenta> tipo) {
        return repositorio.buscarPorTipo(tipo)
            .map(cuentas -> cuentas.stream()
                .map(cuenta -> mapeador.aDto(Optional.of(cuenta)).orElse(null))
                .filter(java.util.Objects::nonNull)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll)
            );
    }

    @Override
    public Optional<List<CuentaDTO>> buscarActivas() {
        return repositorio.buscarActivas()
            .map(cuentas -> cuentas.stream()
                .map(cuenta -> mapeador.aDto(Optional.of(cuenta)).orElse(null))
                .filter(java.util.Objects::nonNull)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll)
            );
    }
} 