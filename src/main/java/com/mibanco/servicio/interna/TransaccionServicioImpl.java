package com.mibanco.servicio.interna;

import com.mibanco.dto.TransaccionDTO;
import com.mibanco.dto.mapeador.TransaccionMapeador;
import com.mibanco.modelo.Transaccion;
import com.mibanco.modelo.enums.TipoOperacionTransaccion;
import com.mibanco.modelo.enums.TipoTransaccion;
import com.mibanco.repositorio.TransaccionRepositorio;
import com.mibanco.servicio.TransaccionServicio;
import com.mibanco.repositorio.interna.RepositorioFactoria;
import com.mibanco.servicio.CuentaServicio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static java.util.Map.entry;
import static java.util.Map.ofEntries;

/**
 * Implementación del servicio de Transacciones
 * Extiende BaseServicioImpl para heredar operaciones CRUD genéricas
 */
class TransaccionServicioImpl extends BaseServicioImpl<TransaccionDTO, Transaccion, Long, TipoOperacionTransaccion, TransaccionRepositorio> implements TransaccionServicio {
    
    private static final TransaccionRepositorio repositorioTransaccion;
    private static final TransaccionMapeador mapeador;
    private static final CuentaServicio cuentaServicio;
    
    // Mapa estático de inversiones de tipos de transacción
    private static final Map<TipoTransaccion, TipoTransaccion> INVERSIONES_TIPO = ofEntries(
        entry(TipoTransaccion.DEPOSITO, TipoTransaccion.RETIRO),
        entry(TipoTransaccion.RETIRO, TipoTransaccion.DEPOSITO),
        entry(TipoTransaccion.TRANSFERENCIA_ENVIADA, TipoTransaccion.TRANSFERENCIA_RECIBIDA),
        entry(TipoTransaccion.TRANSFERENCIA_RECIBIDA, TipoTransaccion.TRANSFERENCIA_ENVIADA)
    );
    
    private final Set<String> cuentasBloqueadas = new HashSet<>();
    private final Object lock = new Object();
    
    static {
        repositorioTransaccion = RepositorioFactoria.obtenerInstancia().obtenerRepositorioTransaccion();
        mapeador = new TransaccionMapeador();
        cuentaServicio = new CuentaServicioImpl();
    }
    
    public TransaccionServicioImpl() {
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
    public Optional<TransaccionDTO> anularTransaccion(Optional<Long> idTransaccion) {
        return idTransaccion.flatMap(id -> 
            repositorioTransaccion.buscarPorId(Optional.of(id))
                .flatMap(transaccionOriginal -> {
                    // Obtener el tipo inverso usando el Map
                    TipoTransaccion nuevoTipo = Optional.ofNullable(INVERSIONES_TIPO.get(transaccionOriginal.getTipo()))
                            .orElse(transaccionOriginal.getTipo());
                    
                    // Crear nueva transacción de anulación
                    Transaccion transaccionAnulacion = transaccionOriginal.toBuilder()
                            .id(null)
                            .numeroCuenta(transaccionOriginal.getNumeroCuentaDestino())
                            .numeroCuentaDestino(transaccionOriginal.getNumeroCuenta())
                            .tipo(nuevoTipo)
                            .descripcion("ANULACIÓN: " + transaccionOriginal.getDescripcion())
                            .build();
                    
                    // Convertir a DTO y guardar usando el servicio base
                    return mapeador.aDto(Optional.of(transaccionAnulacion))
                            .flatMap(dto -> guardar(TipoOperacionTransaccion.ANULAR, Optional.of(dto)));
                })
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

    private boolean bloquearCuenta(Optional<String> numeroCuenta) {
        return numeroCuenta.map(numero -> {
            synchronized (lock) {
                if (cuentasBloqueadas.contains(numero)) {
                    return false;
                }
                cuentasBloqueadas.add(numero);
                return true;
            }
        }).orElse(false);
    }

    private void liberarCuenta(Optional<String> numeroCuenta) {
        numeroCuenta.ifPresent(numero -> {
            synchronized (lock) {
                cuentasBloqueadas.remove(numero);
            }
        });
    }

    @Override
    public Optional<TransaccionDTO> ingresar(Optional<String> numeroCuenta, Optional<BigDecimal> monto, Optional<String> descripcion) {
        return numeroCuenta
            .filter(numero -> monto.map(cantidad -> cantidad.compareTo(BigDecimal.ZERO) > 0).orElse(false))
            .flatMap(numero -> cuentaServicio.obtenerCuentaPorNumero(Optional.of(numero)))
            .map(cuentaDTO -> {
                BigDecimal nuevoSaldo = cuentaDTO.getSaldo().add(monto.get());
                return cuentaServicio.actualizarSaldoCuenta(cuentaDTO.getNumeroCuenta(), Optional.of(nuevoSaldo));
            })
            .flatMap(cuentaActualizada -> cuentaActualizada.map(cuenta -> {
                TransaccionDTO transaccionDTO = TransaccionDTO.builder()
                    .numeroCuenta(cuenta.getNumeroCuenta())
                    .tipo(TipoTransaccion.DEPOSITO)
                    .monto(monto.get())
                    .fecha(LocalDateTime.now())
                    .descripcion(descripcion.orElse("Ingreso en cuenta"))
                    .build();
                
                return guardar(TipoOperacionTransaccion.CREAR, Optional.of(transaccionDTO));
            }).orElse(Optional.empty()));
    }
} 