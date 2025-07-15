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
import com.mibanco.util.ValidacionException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementación del servicio de Tarjetas
 * Extiende BaseServicioImpl para heredar operaciones CRUD genéricas
 */
class TarjetaServicioImpl extends BaseServicioImpl<TarjetaDTO, Tarjeta, Long, TipoOperacionTarjeta, TarjetaRepositorio> implements TarjetaServicio {
    
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
    public Optional<TarjetaDTO> crearTarjetaDto(Map<String, String> datosTarjeta) {
        try {
            // Usar el procesador especializado para crear el DTO con validaciones
            TarjetaDtoProcesadorServicio procesador = new TarjetaDtoProcesadorServicio(
                new ClienteServicioImpl()
            );
            
            return procesador.procesarTarjetaDto(datosTarjeta)
                .flatMap(tarjetaDto -> {
                    // Validar número de tarjeta único antes de guardar
                    validarNumeroTarjetaUnico(tarjetaDto);
                    return guardarEntidad(TipoOperacionTarjeta.CREAR, Optional.of(tarjetaDto));
                });
        } catch (ValidacionException e) {
            System.err.println("Error de validación: " + e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Método auxiliar para validar número de tarjeta único
     * @param dto DTO de la tarjeta a validar
     * @throws ValidacionException si el número de tarjeta ya existe
     */
    private void validarNumeroTarjetaUnico(TarjetaDTO dto) {
        // Solo validar si el número no es null (validación básica ya hecha en vista)
        if (dto.getNumero() != null) {
            Optional<Tarjeta> tarjetaExistente = repositorioTarjeta.buscarPorPredicado(
                tarjeta -> dto.getNumero().equals(tarjeta.getNumero())
            );
            
            if (tarjetaExistente.isPresent()) {
                throw new ValidacionException("Ya existe una tarjeta con el número: " + dto.getNumero());
            }
        }
    }
    
    
    
    @Override
    public Optional<TarjetaDTO> actualizarVariosCampos(Long numeroTarjeta, Optional<TarjetaDTO> tarjetaDTO) {
        Optional<TarjetaDTO> actualizaVariosCampos = actualizar(
            numeroTarjeta,
            tarjetaDTO,
            tipoActualizar,
            (tarjetaExistente, tarjetaNueva) -> tarjetaExistente.conActualizaciones(
                Optional.ofNullable(tarjetaNueva.getFechaExpiracion()),
                Optional.ofNullable(tarjetaNueva.isActiva())
            )
        );
        guardarEntidad(tipoActualizar, actualizaVariosCampos);
        return actualizaVariosCampos;
    }
    
    @Override
    public Optional<TarjetaDTO> obtenerTarjetaPorNumero(Optional<Long> numeroTarjeta) {
        return numeroTarjeta.flatMap(numero -> 
            repositorioTarjeta.buscarPorId(Optional.of(numero))
                .flatMap(tarjeta -> mapeador.aDto(Optional.of(tarjeta)))
        );
    }
    
    @Override
    public Optional<List<TarjetaDTO>> obtenerTodasLasTarjetas() {
        return obtenerTodos();
    }
    
    @Override
    public Optional<TarjetaDTO> actualizarFechaExpiracion(Long numeroTarjeta, Optional<LocalDate> nuevaFecha) {
        Optional<TarjetaDTO> actualizaFecha = actualizarCampo(
            numeroTarjeta,
            nuevaFecha,
            TarjetaDTO::getFechaExpiracion,
            TarjetaDTO::conFechaExpiracion
        );
        guardarEntidad(tipoActualizar, actualizaFecha);
        return actualizaFecha;
    }
    
    @Override
    public Optional<TarjetaDTO> actualizarEstadoTarjeta(Long numeroTarjeta, Optional<Boolean> nuevaActiva) {
        Optional<TarjetaDTO> actualizaEstado = actualizarCampo(
            numeroTarjeta,
            nuevaActiva,
            TarjetaDTO::isActiva,
            TarjetaDTO::conActiva 
        );
        guardarEntidad(tipoActualizar, actualizaEstado);
        return actualizaEstado;
    }
    
    @Override
    public Optional<TarjetaDTO> actualizarTitularTarjeta(Long numeroTarjeta, Optional<TarjetaDTO> nuevoTitular) {
        Optional<TarjetaDTO> actualizaTitular = nuevoTitular.flatMap(dto -> 
                Optional.of(dto.getTitular())   
                .flatMap(titular -> actualizarCampo(
                    numeroTarjeta,
                    Optional.of(titular),
                    TarjetaDTO::getTitular,
                    (tarjeta, nvoTitular) -> tarjeta.toBuilder().titular(titular).build()
                ))
        );
        guardarEntidad(tipoActualizar, actualizaTitular);
        return actualizaTitular;
    }
    
    @Override
    public boolean eliminarTarjeta(Optional<Long> numeroTarjeta) {
        return eliminarPorId(numeroTarjeta, TipoOperacionTarjeta.ELIMINAR);
    }
    
    @Override
    public Optional<TarjetaDTO> eliminarPorNumero(Optional<Long> numeroTarjeta) {
        return numeroTarjeta.flatMap(numero -> 
            repositorioTarjeta.eliminarPorId(Optional.of(numero), TipoOperacionTarjeta.ELIMINAR)
                .flatMap(tarjeta -> mapeador.aDto(Optional.of(tarjeta)))
        );
    }
    
    @Override
    public Optional<TarjetaDTO> restaurarTarjeta(Optional<Long> numeroTarjeta) {
        return restaurar(numeroTarjeta, TipoOperacionTarjeta.MODIFICAR);
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
            repositorioTarjeta.buscarTodosPorPredicado(tarjeta -> tarjeta.getTitular().getId().equals(id))
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
            repositorioTarjeta.buscarTodosPorPredicado(tarjeta -> tarjeta.getTipo().equals(t))
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
        return repositorioTarjeta.buscarTodosPorPredicado(tarjeta -> tarjeta.isActiva())
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
            repositorioTarjeta.buscarTodosPorPredicado(tarjeta -> tarjeta.getNumeroCuentaAsociada().equals(numero))
                .flatMap(tarjetas -> Optional.of(
                    tarjetas.stream()
                        .map(tarjeta -> mapeador.aDto(Optional.of(tarjeta)).orElse(null))
                        .filter(java.util.Objects::nonNull)
                        .toList()
                ))
        );
    }
} 