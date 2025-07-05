package com.mibanco.servicio.interna;

import com.mibanco.dto.TarjetaDTO;
import com.mibanco.dto.mapeador.TarjetaMapeador;
import com.mibanco.dto.mapeador.ClienteMapeador;
import com.mibanco.modelo.Tarjeta;
import com.mibanco.modelo.enums.TipoOperacionTarjeta;
import com.mibanco.modelo.enums.TipoTarjeta;
import com.mibanco.repositorio.TarjetaRepositorio;
import com.mibanco.servicio.TarjetaServicio;
import com.mibanco.repositorio.interna.RepositorioFactoria;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio de Tarjetas
 * Extiende BaseServicioImpl para heredar operaciones CRUD genéricas
 */
class TarjetaServicioImpl extends BaseServicioImpl<TarjetaDTO, Tarjeta, String, TipoOperacionTarjeta, TarjetaRepositorio> implements TarjetaServicio {
    
    private static final TarjetaRepositorio repositorioTarjeta;
    private static final TarjetaMapeador mapeador;
    private static final ClienteMapeador clienteMapeador;
    private final TipoOperacionTarjeta tipoActualizar = TipoOperacionTarjeta.ACTUALIZAR;
    
    static {
        repositorioTarjeta = RepositorioFactoria.obtenerInstancia().obtenerRepositorioTarjeta();
        clienteMapeador = new ClienteMapeador();
        mapeador = new TarjetaMapeador(clienteMapeador);
    }
    
    public TarjetaServicioImpl() {
        super(repositorioTarjeta, mapeador);
    }
    
    @Override
    public Optional<TarjetaDTO> crearTarjeta(Optional<TarjetaDTO> tarjetaDTO) {
        return guardar(TipoOperacionTarjeta.CREAR, tarjetaDTO);
    }
    
    @Override
    public Optional<TarjetaDTO> actualizarVariosCampos(String numeroTarjeta, Optional<TarjetaDTO> tarjetaDTO) {
        Optional<TarjetaDTO> actualizaVariosCampos = actualizar(
            numeroTarjeta,
            tarjetaDTO,
            tipoActualizar,
            (tarjetaExistente, tarjetaNueva) -> tarjetaExistente.conActualizaciones(
                Optional.ofNullable(tarjetaNueva.getFechaExpiracion()),
                Optional.ofNullable(tarjetaNueva.isActiva())
            )
        );
        guardar(tipoActualizar, actualizaVariosCampos);
        return actualizaVariosCampos;
    }
    
    @Override
    public Optional<TarjetaDTO> obtenerTarjetaPorNumero(Optional<String> numeroTarjeta) {
        return numeroTarjeta.flatMap(numero -> 
            repositorioTarjeta.buscarPorNumero(Optional.of(numero))
                .flatMap(tarjeta -> mapeador.aDto(Optional.of(tarjeta)))
        );
    }
    
    @Override
    public Optional<List<TarjetaDTO>> obtenerTodasLasTarjetas() {
        return obtenerTodos();
    }
    
    @Override
    public Optional<TarjetaDTO> actualizarFechaExpiracion(String numeroTarjeta, Optional<LocalDate> nuevaFecha) {
        Optional<TarjetaDTO> actualizaFecha = actualizarCampo(
            numeroTarjeta,
            nuevaFecha,
            TarjetaDTO::getFechaExpiracion,
            TarjetaDTO::conFechaExpiracion
        );
        guardar(tipoActualizar, actualizaFecha);
        return actualizaFecha;
    }
    
    @Override
    public Optional<TarjetaDTO> actualizarEstadoTarjeta(String numeroTarjeta, Optional<Boolean> nuevaActiva) {
        Optional<TarjetaDTO> actualizaEstado = actualizarCampo(
            numeroTarjeta,
            nuevaActiva,
            TarjetaDTO::isActiva,
            TarjetaDTO::conActiva 
        );
        guardar(tipoActualizar, actualizaEstado);
        return actualizaEstado;
    }
    
    @Override
    public Optional<TarjetaDTO> actualizarTitularTarjeta(String numeroTarjeta, Optional<TarjetaDTO> nuevoTitular) {
        Optional<TarjetaDTO> actualizaTitular = nuevoTitular.flatMap(dto -> 
                Optional.of(dto.getTitular())   
                .flatMap(titular -> actualizarCampo(
                    numeroTarjeta,
                    Optional.of(titular),
                    TarjetaDTO::getTitular,
                    (tarjeta, nvoTitular) -> tarjeta.toBuilder().titular(titular).build()
                ))
        );
        guardar(tipoActualizar, actualizaTitular);
        return actualizaTitular;
    }
    
    @Override
    public boolean eliminarTarjeta(Optional<String> numeroTarjeta) {
        return eliminarPorId(numeroTarjeta, TipoOperacionTarjeta.ELIMINAR);
    }
    
    @Override
    public Optional<TarjetaDTO> eliminarPorNumero(Optional<String> numeroTarjeta) {
        return numeroTarjeta.flatMap(numero -> 
            repositorioTarjeta.eliminarPorId(Optional.of(numero), TipoOperacionTarjeta.ELIMINAR)
                .flatMap(tarjeta -> mapeador.aDto(Optional.of(tarjeta)))
        );
    }
    
    @Override
    public Optional<TarjetaDTO> restaurarTarjeta(Optional<String> numeroTarjeta) {
        return restaurar(numeroTarjeta, TipoOperacionTarjeta.MODIFICAR);
    }
    
    @Override
    public List<TarjetaDTO> obtenerTarjetasEliminadas() {
        return obtenerEliminados();
    }
    
    @Override
    public long contarTarjetas() {
        return contarRegistros();
    }
    
    @Override
    public void establecerUsuarioActual(String usuario) {
        super.establecerUsuarioActual(usuario);
    }
    
    @Override
    public Optional<List<TarjetaDTO>> buscarPorTitularId(Optional<Long> idCliente) {
        return idCliente.flatMap(id -> 
            repositorioTarjeta.buscarPorTitularId(Optional.of(id))
                .flatMap(tarjetas -> Optional.of(
                    tarjetas.stream()
                        .map(tarjeta -> mapeador.aDto(Optional.of(tarjeta)).orElse(null))
                        .filter(java.util.Objects::nonNull)
                        .toList()
                ))
        );
    }
    
    @Override
    public Optional<List<TarjetaDTO>> buscarPorTipo(Optional<TipoTarjeta> tipo) {
        return tipo.flatMap(t -> 
            repositorioTarjeta.buscarPorTipo(Optional.of(t))
                .flatMap(tarjetas -> Optional.of(
                    tarjetas.stream()
                        .map(tarjeta -> mapeador.aDto(Optional.of(tarjeta)).orElse(null))
                        .filter(java.util.Objects::nonNull)
                        .toList()
                ))
        );
    }
    
    @Override
    public Optional<List<TarjetaDTO>> buscarActivas() {
        return repositorioTarjeta.buscarActivas()
            .flatMap(tarjetas -> Optional.of(
                tarjetas.stream()
                    .map(tarjeta -> mapeador.aDto(Optional.of(tarjeta)).orElse(null))
                    .filter(java.util.Objects::nonNull)
                    .toList()
            ));
    }
    
    @Override
    public Optional<List<TarjetaDTO>> buscarPorCuentaAsociada(Optional<String> numeroCuenta) {
        return numeroCuenta.flatMap(numero -> 
            repositorioTarjeta.buscarPorCuentaAsociada(Optional.of(numero))
                .flatMap(tarjetas -> Optional.of(
                    tarjetas.stream()
                        .map(tarjeta -> mapeador.aDto(Optional.of(tarjeta)).orElse(null))
                        .filter(java.util.Objects::nonNull)
                        .toList()
                ))
        );
    }
} 