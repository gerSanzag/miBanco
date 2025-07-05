package com.mibanco.servicio.interna;

import com.mibanco.dto.CuentaDTO;
import com.mibanco.dto.mapeador.CuentaMapeador;
import com.mibanco.dto.mapeador.ClienteMapeador;
import com.mibanco.modelo.Cuenta;
import com.mibanco.modelo.enums.TipoCuenta;
import com.mibanco.repositorio.CuentaRepositorio;
import com.mibanco.servicio.ClienteServicio;
import com.mibanco.servicio.CuentaServicio;
import com.mibanco.modelo.enums.TipoOperacionCuenta;
import com.mibanco.repositorio.interna.RepositorioFactoria;
import com.mibanco.servicio.TransaccionOperacionesServicio;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map;

/**
 * Implementación del servicio de cuentas
 */
class CuentaServicioImpl extends BaseServicioImpl<CuentaDTO, Cuenta, Long, TipoOperacionCuenta, CuentaRepositorio> 
        implements CuentaServicio {
    
    private static final CuentaRepositorio repositorioCuenta;
    private static ClienteServicio clienteServicio;
    private static final CuentaMapeador mapeador;
    private static final ClienteMapeador clienteMapeador;
    private final TipoOperacionCuenta tipoActualizar = TipoOperacionCuenta.ACTUALIZAR;
    private static CuentaDtoProcesadorServicio cuentaDtoProcesador;
    
    static {
        repositorioCuenta = RepositorioFactoria.obtenerInstancia().obtenerRepositorioCuenta();
        clienteMapeador = new ClienteMapeador();
        mapeador = new CuentaMapeador(clienteMapeador);
        clienteServicio = ServicioFactoria.obtenerInstancia().obtenerServicioCliente();
        cuentaDtoProcesador = new CuentaDtoProcesadorServicio(clienteServicio);
    }

    public CuentaServicioImpl() {
        super(repositorioCuenta, mapeador);
        
    }

    // @Override
    // public Optional<CuentaDTO> guardarCuenta(Optional<CuentaDTO> cuentaDTO) {
    //     return guardar(TipoOperacionCuenta.CREAR, cuentaDTO);
    // }

    @Override
    public Optional<CuentaDTO> actualizarVariosCampos(Long numeroCuenta, Optional<CuentaDTO> cuentaDTO) {
        Optional<CuentaDTO> actualizaVariosCampos = actualizar(
            numeroCuenta,
            cuentaDTO,
            tipoActualizar,
            (cuentaExistente, cuentaNueva) -> cuentaExistente.conActualizaciones(
                Optional.ofNullable(cuentaNueva.getSaldo()),
                Optional.ofNullable(cuentaNueva.isActiva())
            )
        );
        guardar(tipoActualizar, actualizaVariosCampos);
        return actualizaVariosCampos;
    }

    @Override
    public Optional<CuentaDTO> obtenerCuentaPorNumero(Optional<Long> numeroCuenta) {
        return numeroCuenta.flatMap(numero -> 
            repositorio.buscarPorNumero(Optional.of(numero))
                .flatMap(cuenta -> mapeador.aDto(Optional.of(cuenta)))
        );
    }

    @Override
    public Optional<List<CuentaDTO>> obtenerTodasLasCuentas() {
        return obtenerTodos();
    }

    @Override
    public Optional<CuentaDTO> actualizarSaldoCuenta(Long numeroCuenta, Optional<BigDecimal> nuevoSaldo) {
        Optional<CuentaDTO> actualizaSaldo = actualizarCampo(
            numeroCuenta,
            nuevoSaldo,
            CuentaDTO::getSaldo,
            CuentaDTO::conSaldo
        );
        guardar(tipoActualizar, actualizaSaldo);
        return actualizaSaldo;
    }

    @Override
    public Optional<CuentaDTO> actualizarEstadoCuenta(Long numeroCuenta, Optional<Boolean> nuevaActiva) {
        Optional<CuentaDTO> actualizaEstado = actualizarCampo(
            numeroCuenta,
            nuevaActiva,
            CuentaDTO::isActiva,
            CuentaDTO::conActiva
        );
        guardar(tipoActualizar, actualizaEstado);
        return actualizaEstado;
    }

   
    public Optional<CuentaDTO> actualizarTitularCuenta(Long numeroCuenta, Optional<CuentaDTO> nuevoTitular) {
        Optional<CuentaDTO> actualizaTitular = nuevoTitular.flatMap(titularDTO -> 
            clienteMapeador.aEntidad(Optional.of(titularDTO.getTitular()))
                .flatMap(titular -> actualizarCampo(
                    numeroCuenta,
                    Optional.of(titular),
                    CuentaDTO::getTitular,
                    (cuenta, nvoTitular) -> cuenta.toBuilder().titular(titularDTO.getTitular() ).build()
                ))
        );
        guardar(tipoActualizar, actualizaTitular);
        return actualizaTitular;
    }

    @Override
    public boolean eliminarCuenta(Optional<Long> numeroCuenta) {
        return eliminarPorId(numeroCuenta, TipoOperacionCuenta.ELIMINAR);
    }

    @Override
    public Optional<CuentaDTO> eliminarPorNumero(Optional<Long> numeroCuenta) {
        return repositorio.eliminarPorNumero(numeroCuenta)
            .flatMap(cuenta -> mapeador.aDto(Optional.of(cuenta)));
    }

    @Override
    public Optional<CuentaDTO> restaurarCuenta(Optional<Long> numeroCuenta) {
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

    /**
     * Crea una cuenta DTO con saldo inicial obligatorio usando programación funcional
     * La cuenta se crea una sola vez con el saldo inicial incluido
     * Elimina los if anidados usando Optional chaining y early returns
     * Garantiza consistencia transaccional: valida antes de persistir
     */
    public Optional<CuentaDTO> crearCuentaDto(Map<String, String> datosCrudos, BigDecimal montoInicial, TransaccionOperacionesServicio transaccionServicio) {
        return cuentaDtoProcesador.procesarCuentaDto(datosCrudos)
            .flatMap(cuentaDTO -> cuentaDtoProcesador.procesarIngresoInicial(cuentaDTO, montoInicial, transaccionServicio))
            .flatMap(cuentaConSaldo -> guardar(TipoOperacionCuenta.CREAR, Optional.of(cuentaConSaldo)))
            .or(() -> Optional.empty());
    }
} 