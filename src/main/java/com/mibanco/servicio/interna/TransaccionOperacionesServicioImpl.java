package com.mibanco.servicio.interna;

import com.mibanco.modelo.Transaccion;
import com.mibanco.modelo.enums.TipoOperacionTransaccion;
import com.mibanco.modelo.enums.TipoTransaccion;
import com.mibanco.repositorio.TransaccionRepositorio;
import com.mibanco.repositorio.interna.RepositorioFactoria;
import com.mibanco.servicio.TransaccionOperacionesServicio;
import com.mibanco.dto.TransaccionDTO;
import com.mibanco.dto.mapeador.TransaccionMapeador;
import com.mibanco.servicio.CuentaServicio;

import static java.util.Map.entry;
import static java.util.Map.ofEntries;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

class TransaccionOperacionesServicioImpl extends BaseServicioImpl<TransaccionDTO, Transaccion, Long, TipoOperacionTransaccion, TransaccionRepositorio> implements TransaccionOperacionesServicio {

    private static final TransaccionRepositorio repositorioTransaccion;
    private static final TransaccionMapeador mapeador;
    private static final CuentaServicio cuentaServicio;
    private final Set<Long> cuentasBloqueadas = new HashSet<>();
    private final Object lock = new Object();

    static {
        repositorioTransaccion = RepositorioFactoria.obtenerInstancia().obtenerRepositorioTransaccion();
        mapeador = new TransaccionMapeador();
        cuentaServicio = new CuentaServicioImpl();
    }

    public TransaccionOperacionesServicioImpl() {
        super(repositorioTransaccion, mapeador);
        
    }

    // Mapa estático de inversiones de tipos de transacción
    private static final Map<TipoTransaccion, TipoTransaccion> INVERSIONES_TIPO = ofEntries(
        entry(TipoTransaccion.DEPOSITO, TipoTransaccion.RETIRO),
        entry(TipoTransaccion.RETIRO, TipoTransaccion.DEPOSITO),
        entry(TipoTransaccion.TRANSFERENCIA_ENVIADA, TipoTransaccion.TRANSFERENCIA_RECIBIDA),
        entry(TipoTransaccion.TRANSFERENCIA_RECIBIDA, TipoTransaccion.TRANSFERENCIA_ENVIADA)
    );
 

    private boolean bloquearCuenta(Optional<Long> idCuenta) {
        return idCuenta.map(id -> {
            synchronized (lock) {
                if (cuentasBloqueadas.contains(id)) {
                    return false;
                }
                cuentasBloqueadas.add(id);
                return true;
            }
        }).orElse(false);
    }

    private void liberarCuenta(Optional<Long> idCuenta) {
        idCuenta.ifPresent(id -> {
            synchronized (lock) {
                cuentasBloqueadas.remove(id);
            }
        });
    }

    @Override
    public Optional<TransaccionDTO> ingresar(Optional<Long> idCuenta, Optional<BigDecimal> monto, Optional<String> descripcion) {
        return idCuenta
            .flatMap(id -> {
                // Intentamos bloquear la cuenta
                if (!bloquearCuenta(Optional.of(id))) {
                    return Optional.empty();
                }
                
                try {
                    // Obtenemos la cuenta
                    return cuentaServicio.obtenerCuentaPorNumero(Optional.of(id))
                        .flatMap(cuentaDTO -> {
                            // Si la cuenta no está activa, la activamos
                            if (!cuentaDTO.isActiva()) {
                                return cuentaServicio.actualizarEstadoCuenta(id, Optional.of(true));
                            }
                            return Optional.of(cuentaDTO);
                        })
                        .flatMap(cuentaDTO -> {
                            // Actualizamos el saldo
                            BigDecimal nuevoSaldo = cuentaDTO.getSaldo().add(monto.get());
                            return cuentaServicio.actualizarSaldoCuenta(id, Optional.of(nuevoSaldo));
                        })
                        .flatMap(cuentaActualizada -> {
                            // Creamos la transacción
                            TransaccionDTO transaccionDTO = TransaccionDTO.builder()
                                .numeroCuenta(cuentaActualizada.getNumeroCuenta())
                                .tipo(TipoTransaccion.DEPOSITO)
                                .monto(monto.get())
                                .fecha(LocalDateTime.now())
                                .descripcion(descripcion.orElse("Ingreso en cuenta"))
                                .build();
                            
                            // Guardamos la transacción
                            return guardarEntidad(TipoOperacionTransaccion.CREAR, Optional.of(transaccionDTO));
                        });
                } finally {
                    // Siempre liberamos el bloqueo
                    liberarCuenta(Optional.of(id));
                }
            });
    }

    @Override
    public Optional<TransaccionDTO> retirar(Optional<Long> idCuenta, Optional<BigDecimal> monto, Optional<String> descripcion) {
        return idCuenta
            .filter(id -> monto.map(cantidad -> cantidad.compareTo(BigDecimal.ZERO) > 0).orElse(false))
            .flatMap(id -> {
                // Intentamos bloquear la cuenta
                if (!bloquearCuenta(Optional.of(id))) {
                    return Optional.empty();
                }
                
                try {
                    // Obtenemos la cuenta
                    return cuentaServicio.obtenerCuentaPorNumero(Optional.of(id))
                        .flatMap(cuentaDTO -> {
                            // Si la cuenta no está activa, la activamos
                            if (!cuentaDTO.isActiva()) {
                                return cuentaServicio.actualizarEstadoCuenta(id, Optional.of(true));
                            }
                            return Optional.of(cuentaDTO);
                        })
                        .filter(cuentaDTO -> cuentaDTO.getSaldo().compareTo(monto.get()) >= 0) // Verificamos saldo suficiente
                        .flatMap(cuentaDTO -> {
                            // Actualizamos el saldo
                            BigDecimal nuevoSaldo = cuentaDTO.getSaldo().subtract(monto.get());
                            return cuentaServicio.actualizarSaldoCuenta(id, Optional.of(nuevoSaldo));
                        })
                        .flatMap(cuentaActualizada -> {
                            // Creamos la transacción
                            TransaccionDTO transaccionDTO = TransaccionDTO.builder()
                                .numeroCuenta(cuentaActualizada.getNumeroCuenta())
                                .tipo(TipoTransaccion.RETIRO)
                                .monto(monto.get())
                                .fecha(LocalDateTime.now())
                                .descripcion(descripcion.orElse("Retiro de cuenta"))
                                .build();
                            
                            // Guardamos la transacción
                            return guardarEntidad(TipoOperacionTransaccion.CREAR, Optional.of(transaccionDTO));
                        });
                } finally {
                    // Siempre liberamos el bloqueo
                    liberarCuenta(Optional.of(id));
                }
            });
    }
    
    @Override
    public Optional<TransaccionDTO> transferir(Optional<Long> idCuentaOrigen, Optional<Long> idCuentaDestino, Optional<BigDecimal> monto, Optional<String> descripcion) {
        return idCuentaOrigen
            .filter(id -> monto.map(cantidad -> cantidad.compareTo(BigDecimal.ZERO) > 0).orElse(false))
            .flatMap(idOrigen -> idCuentaDestino
                .flatMap(idDestino -> {
                    // Intentamos bloquear ambas cuentas
                    if (!bloquearCuenta(Optional.of(idOrigen)) || !bloquearCuenta(Optional.of(idDestino))) {
                        return Optional.empty();
                    }
                    
                    try {
                        // Construimos las descripciones personalizadas
                        String descripcionRetiro = "Transferencia enviada a cuenta " + idDestino + 
                            descripcion.map(desc -> ": " + desc).orElse("");
                            
                        String descripcionIngreso = "Transferencia recibida de cuenta " + idOrigen + 
                            descripcion.map(desc -> ": " + desc).orElse("");

                        // Realizamos el retiro de la cuenta origen
                        return retirar(Optional.of(idOrigen), monto, Optional.of(descripcionRetiro))
                            .flatMap(transaccionRetiro -> 
                                // Si el retiro fue exitoso, realizamos el ingreso en la cuenta destino
                                ingresar(Optional.of(idDestino), monto, Optional.of(descripcionIngreso))
                                    .map(transaccionIngreso -> transaccionRetiro) // Retornamos la transacción de retiro como la principal
                            );
                    } finally {
                        // Siempre liberamos los bloqueos
                        liberarCuenta(Optional.of(idOrigen));
                        liberarCuenta(Optional.of(idDestino));
                    }
                })
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
                    Transaccion transaccionAnulacion = Transaccion.builder()
                            .id(transaccionOriginal.getId())
                            .numeroCuenta(transaccionOriginal.getNumeroCuentaDestino())
                            .numeroCuentaDestino(transaccionOriginal.getNumeroCuenta())
                            .tipo(nuevoTipo)
                            .descripcion("ANULACIÓN: " + transaccionOriginal.getDescripcion())
                            .build();
                    
                    // Convertir a DTO y guardar usando el servicio base
                    return mapeador.aDto(Optional.of(transaccionAnulacion))
                            .flatMap(dto -> guardarEntidad(TipoOperacionTransaccion.ANULAR, Optional.of(dto)));
                })
        );
    }
}
