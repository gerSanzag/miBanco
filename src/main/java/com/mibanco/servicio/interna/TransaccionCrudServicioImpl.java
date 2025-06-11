package com.mibanco.servicio.interna;

import com.mibanco.dto.TransaccionDTO;
import com.mibanco.dto.mapeador.TransaccionMapeador;
import com.mibanco.modelo.Transaccion;
import com.mibanco.modelo.enums.TipoOperacionTransaccion;
import com.mibanco.modelo.enums.TipoTransaccion;
import com.mibanco.repositorio.TransaccionRepositorio;
import com.mibanco.servicio.TransaccionCrudServicio;
import com.mibanco.repositorio.interna.RepositorioFactoria;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio de Transacciones
 * Extiende BaseServicioImpl para heredar operaciones CRUD genéricas
 */
class TransaccionCrudServicioImpl extends BaseServicioImpl<TransaccionDTO, Transaccion, Long, TipoOperacionTransaccion, TransaccionRepositorio> implements TransaccionCrudServicio {
    
    private static final TransaccionRepositorio repositorioTransaccion;
    private static final TransaccionMapeador mapeador;
   
    static {
        repositorioTransaccion = RepositorioFactoria.obtenerInstancia().obtenerRepositorioTransaccion();
        mapeador = new TransaccionMapeador();
       
    }
    
    public TransaccionCrudServicioImpl() {
        super(repositorioTransaccion, mapeador);
    }
    
    @Override
    public Optional<TransaccionDTO> crearTransaccion(Optional<TransaccionDTO> transaccionDTO) {
        return guardar(TipoOperacionTransaccion.CREAR, transaccionDTO);
    }
    
    @Override
    public Optional<TransaccionDTO> obtenerTransaccionPorId(Optional<Long> id) {
        return obtenerPorId(id);
    }
    
    @Override
    public Optional<List<TransaccionDTO>> obtenerTodasLasTransacciones() {
        return obtenerTodos();
    }
    
    @Override
    public Optional<List<TransaccionDTO>> buscarPorCuenta(Optional<String> numeroCuenta) {
        return numeroCuenta.flatMap(numero -> 
            repositorioTransaccion.buscarPorCuenta(Optional.of(numero))
                .flatMap(transacciones -> Optional.of(
                    transacciones.stream()
                        .map(transaccion -> mapeador.aDto(Optional.of(transaccion)).orElse(null))
                        .filter(java.util.Objects::nonNull)
                        .toList()
                ))
        );
    }
    
    @Override
    public Optional<List<TransaccionDTO>> buscarPorTipo(Optional<TipoTransaccion> tipo) {
        return tipo.flatMap(t -> 
            repositorioTransaccion.buscarPorTipo(Optional.of(t))
                .flatMap(transacciones -> Optional.of(
                    transacciones.stream()
                        .map(transaccion -> mapeador.aDto(Optional.of(transaccion)).orElse(null))
                        .filter(java.util.Objects::nonNull)
                        .toList()
                ))
        );
    }
    
    @Override
    public Optional<List<TransaccionDTO>> buscarPorFecha(Optional<LocalDate> fecha) {
        return fecha.flatMap(f -> 
            repositorioTransaccion.buscarPorFecha(Optional.of(f))
                .flatMap(transacciones -> Optional.of(
                    transacciones.stream()
                        .map(transaccion -> mapeador.aDto(Optional.of(transaccion)).orElse(null))
                        .filter(java.util.Objects::nonNull)
                        .toList()
                ))
        );
    }
    
    @Override
    public Optional<List<TransaccionDTO>> buscarPorRangoFechas(Optional<LocalDate> fechaInicio, Optional<LocalDate> fechaFin) {
        return fechaInicio.flatMap(inicio -> 
            fechaFin.flatMap(fin -> 
                repositorioTransaccion.buscarPorRangoFechas(Optional.of(inicio), Optional.of(fin))
                    .flatMap(transacciones -> Optional.of(
                        transacciones.stream()
                            .map(transaccion -> mapeador.aDto(Optional.of(transaccion)).orElse(null))
                            .filter(java.util.Objects::nonNull)
                            .toList()
                    ))
            )
        );
    }
    
    @Override
    public boolean eliminarTransaccion(Optional<Long> id) {
        return eliminarPorId(id, TipoOperacionTransaccion.ELIMINAR);
    }
    
    @Override
    public long contarTransacciones() {
        return contarRegistros();
    }
    
    @Override
    public void establecerUsuarioActual(String usuario) {
        super.establecerUsuarioActual(usuario);
    }

    
} 