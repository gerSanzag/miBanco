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
import com.mibanco.util.ValidacionException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
        clienteServicio = FactoriaServicio.obtenerInstancia().obtenerServicioCliente();
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
    public Optional<CuentaDTO> actualizarVariosCampos(Long idCuenta, Optional<CuentaDTO> cuentaDTO) {
        Optional<CuentaDTO> actualizaVariosCampos = actualizar(
            idCuenta,
            cuentaDTO,
            tipoActualizar,
            (cuentaExistente, cuentaNueva) -> cuentaExistente.conActualizaciones(
                Optional.ofNullable(cuentaNueva.getSaldo()),
                Optional.ofNullable(cuentaNueva.isActiva())
            )
        );
        guardarEntidad(tipoActualizar, actualizaVariosCampos);
        return actualizaVariosCampos;
    }

    @Override
    public Optional<CuentaDTO> obtenerCuentaPorNumero(Optional<Long> idCuenta) {
        return idCuenta.flatMap(id -> 
            repositorio.buscarPorId(Optional.of(id))
                .flatMap(cuenta -> mapeador.aDto(Optional.of(cuenta)))
        );
    }

    @Override
    public Optional<List<CuentaDTO>> obtenerTodasLasCuentas() {
        return obtenerTodos();
    }

    @Override
    public Optional<CuentaDTO> actualizarSaldoCuenta(Long idCuenta, Optional<BigDecimal> nuevoSaldo) {
        Optional<CuentaDTO> actualizaSaldo = actualizarCampo(
            idCuenta,
            nuevoSaldo,
            CuentaDTO::getSaldo,
            CuentaDTO::conSaldo
        );
        guardarEntidad(tipoActualizar, actualizaSaldo);
        return actualizaSaldo;
    }

    @Override
    public Optional<CuentaDTO> actualizarEstadoCuenta(Long idCuenta, Optional<Boolean> nuevaActiva) {
        Optional<CuentaDTO> actualizaEstado = actualizarCampo(
            idCuenta,
            nuevaActiva,
            CuentaDTO::isActiva,
            CuentaDTO::conActiva
        );
        guardarEntidad(tipoActualizar, actualizaEstado);
        return actualizaEstado;
    }

   
    public Optional<CuentaDTO> actualizarTitularCuenta(Long idCuenta, Optional<CuentaDTO> nuevoTitular) {
        Optional<CuentaDTO> actualizaTitular = nuevoTitular.flatMap(titularDTO -> 
            clienteMapeador.aEntidad(Optional.of(titularDTO.getTitular()))
                .flatMap(titular -> actualizarCampo(
                    idCuenta,
                    Optional.of(titular),
                    CuentaDTO::getTitular,
                    (cuenta, nvoTitular) -> cuenta.toBuilder().titular(titularDTO.getTitular() ).build()
                ))
        );
        guardarEntidad(tipoActualizar, actualizaTitular);
        return actualizaTitular;
    }

    @Override
    public boolean eliminarCuenta(Optional<Long> idCuenta) {
        return eliminarPorId(idCuenta, TipoOperacionCuenta.ELIMINAR);
    }

    @Override
    public Optional<CuentaDTO> eliminarPorNumero(Optional<Long> idCuenta) {
        return repositorio.eliminarPorId(idCuenta, TipoOperacionCuenta.ELIMINAR)
            .flatMap(cuenta -> mapeador.aDto(Optional.of(cuenta)));
    }

    @Override
    public Optional<CuentaDTO> restaurarCuenta(Optional<Long> idCuenta) {
        return restaurar(idCuenta, TipoOperacionCuenta.RESTAURAR);
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
        return repositorio.buscarTodosPorPredicado(cuenta -> cuenta.getTitular().getId().equals(idTitular.get()))        
            .map(cuentas -> cuentas.stream()
                .map(cuenta -> mapeador.aDto(Optional.of(cuenta)).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList())
            );
    }

    @Override
    public Optional<List<CuentaDTO>> buscarPorTipo(Optional<TipoCuenta> tipo) {
        return repositorio.buscarTodosPorPredicado(cuenta -> cuenta.getTipo().equals(tipo.get()))
            .map(cuentas -> cuentas.stream()
                .map(cuenta -> mapeador.aDto(Optional.of(cuenta)).orElse(null))
                .filter(java.util.Objects::nonNull)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll)
            );
    }

    @Override
    public Optional<List<CuentaDTO>> buscarActivas() {
            return repositorio.buscarTodosPorPredicado(cuenta -> cuenta.isActiva())
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
        try {
            return cuentaDtoProcesador.procesarCuentaDto(datosCrudos)
                .flatMap(cuentaDTO -> cuentaDtoProcesador.procesarIngresoInicial(cuentaDTO, montoInicial, transaccionServicio))
                .flatMap(cuentaConSaldo -> {
                    // Validar número de cuenta único antes de guardar
                    validarNumeroCuentaUnico(cuentaConSaldo);
                    return guardarEntidad(TipoOperacionCuenta.CREAR, Optional.of(cuentaConSaldo));
                })
                .or(() -> Optional.empty());
        } catch (ValidacionException e) {
            // Re-lanzar la excepción para que la vista la maneje
            throw e;
        }
    }
    
    /**
     * Método auxiliar para validar número de cuenta único
     * @param dto DTO de la cuenta a validar
     * @throws ValidacionException si el número de cuenta ya existe
     */
    private void validarNumeroCuentaUnico(CuentaDTO dto) {
        // Solo validar si el número no es null (validación básica ya hecha en vista)
        if (dto.getNumeroCuenta() != null) {
            Optional<Cuenta> cuentaExistente = repositorioCuenta.buscarPorPredicado(
                cuenta -> dto.getNumeroCuenta().equals(cuenta.getNumeroCuenta())
            );
            
            if (cuentaExistente.isPresent()) {
                throw new ValidacionException("Ya existe una cuenta con el número: " + dto.getNumeroCuenta());
            }
        }
    }
} 