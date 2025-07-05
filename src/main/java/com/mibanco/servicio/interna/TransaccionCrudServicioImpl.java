package com.mibanco.servicio.interna;

import com.mibanco.dto.TransaccionDTO;
import com.mibanco.dto.mapeador.TransaccionMapeador;
import com.mibanco.modelo.Transaccion;
import com.mibanco.modelo.enums.TipoOperacionTransaccion;
import com.mibanco.modelo.enums.TipoTransaccion;
import com.mibanco.repositorio.TransaccionRepositorio;
import com.mibanco.servicio.TransaccionCrudServicio;
import com.mibanco.repositorio.interna.RepositorioFactoria;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Implementación del servicio de Transacciones
 * Extiende BaseServicioImpl para heredar operaciones CRUD genéricas
 */
class TransaccionCrudServicioImpl extends BaseServicioImpl<TransaccionDTO, Transaccion, Long, TipoOperacionTransaccion, TransaccionRepositorio> implements TransaccionCrudServicio {
    
    private static final TransaccionRepositorio repositorioTransaccion;
    private static final TransaccionMapeador mapeador;
    private static final AtomicLong idContador = new AtomicLong(0);
   
    static {
        repositorioTransaccion = RepositorioFactoria.obtenerInstancia().obtenerRepositorioTransaccion();
        mapeador = new TransaccionMapeador();
       
    }
    
    public TransaccionCrudServicioImpl() {
        super(repositorioTransaccion, mapeador);
    }
    
    @Override
    public Optional<TransaccionDTO> crearTransaccion(Map<String, String> datosTransaccion) {
        return Optional.ofNullable(datosTransaccion)
                .flatMap(datos -> {
                    try {
                        // ✅ Supplier para generar ID secuencial automáticamente
                        Supplier<Long> idSupplier = () -> 
                            idContador.incrementAndGet();
                        
                        // Extraer y validar datos del Map
                        Long numeroCuenta = Optional.ofNullable(datos.get("numeroCuenta"))
                                .map(Long::parseLong)
                                .orElse(null);
                        
                        Long numeroCuentaDestino = Optional.ofNullable(datos.get("numeroCuentaDestino"))
                                .map(Long::parseLong)
                                .orElse(null);
                        
                        TipoTransaccion tipo = Optional.ofNullable(datos.get("tipo"))
                                .map(TipoTransaccion::valueOf)
                                .orElse(null);
                        
                        BigDecimal monto = Optional.ofNullable(datos.get("monto"))
                                .map(BigDecimal::new)
                                .orElse(null);
                        
                        String descripcion = datos.getOrDefault("descripcion", "");
                        
                        // Crear DTO con datos validados y ID generado automáticamente
                        TransaccionDTO transaccionDTO = TransaccionDTO.builder()
                                .id(idSupplier.get()) // ✅ Generar ID secuencial automáticamente
                                .numeroCuenta(numeroCuenta)
                                .numeroCuentaDestino(numeroCuentaDestino)
                                .tipo(tipo)
                                .monto(monto)
                                .fecha(LocalDateTime.now())
                                .descripcion(descripcion)
                                .build();
                        
                        // Guardar usando el método heredado
                        return guardar(TipoOperacionTransaccion.CREAR, Optional.of(transaccionDTO));
                        
                    } catch (IllegalArgumentException e) {
                        // Manejo funcional de errores de parsing
                        return Optional.empty();
                    }
                });
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