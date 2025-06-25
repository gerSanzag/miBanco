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
 

    private boolean bloquearCuenta(Optional<Long> numeroCuenta) {
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

    private void liberarCuenta(Optional<Long> numeroCuenta) {
        numeroCuenta.ifPresent(numero -> {
            synchronized (lock) {
                cuentasBloqueadas.remove(numero);
            }
        });
    }

    @Override
    public Optional<TransaccionDTO> ingresar(Optional<Long> numeroCuenta, Optional<BigDecimal> monto, Optional<String> descripcion) {
        return numeroCuenta
            .flatMap(numero -> {
                // Intentamos bloquear la cuenta
                if (!bloquearCuenta(Optional.of(numero))) {
                    return Optional.empty();
                }
                
                try {
                    // Obtenemos la cuenta
                    return cuentaServicio.obtenerCuentaPorNumero(Optional.of(numero))
                        .flatMap(cuentaDTO -> {
                            // Si la cuenta no está activa, la activamos
                            if (!cuentaDTO.isActiva()) {
                                return cuentaServicio.actualizarEstadoCuenta(cuentaDTO.getNumeroCuenta(), Optional.of(true));
                            }
                            return Optional.of(cuentaDTO);
                        })
                        .flatMap(cuentaDTO -> {
                            // Actualizamos el saldo
                            BigDecimal nuevoSaldo = cuentaDTO.getSaldo().add(monto.get());
                            return cuentaServicio.actualizarSaldoCuenta(cuentaDTO.getNumeroCuenta(), Optional.of(nuevoSaldo));
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
                            return guardar(TipoOperacionTransaccion.CREAR, Optional.of(transaccionDTO));
                        });
                } finally {
                    // Siempre liberamos el bloqueo
                    liberarCuenta(Optional.of(numero));
                }
            });
    }

    @Override
    public Optional<TransaccionDTO> retirar(Optional<Long> numeroCuenta, Optional<BigDecimal> monto, Optional<String> descripcion) {
        return numeroCuenta
            .filter(numero -> monto.map(cantidad -> cantidad.compareTo(BigDecimal.ZERO) > 0).orElse(false))
            .flatMap(numero -> {
                // Intentamos bloquear la cuenta
                if (!bloquearCuenta(Optional.of(numero))) {
                    return Optional.empty();
                }
                
                try {
                    // Obtenemos la cuenta
                    return cuentaServicio.obtenerCuentaPorNumero(Optional.of(numero))
                        .flatMap(cuentaDTO -> {
                            // Si la cuenta no está activa, la activamos
                            if (!cuentaDTO.isActiva()) {
                                return cuentaServicio.actualizarEstadoCuenta(cuentaDTO.getNumeroCuenta(), Optional.of(true));
                            }
                            return Optional.of(cuentaDTO);
                        })
                        .filter(cuentaDTO -> cuentaDTO.getSaldo().compareTo(monto.get()) >= 0) // Verificamos saldo suficiente
                        .flatMap(cuentaDTO -> {
                            // Actualizamos el saldo
                            BigDecimal nuevoSaldo = cuentaDTO.getSaldo().subtract(monto.get());
                            return cuentaServicio.actualizarSaldoCuenta(cuentaDTO.getNumeroCuenta(), Optional.of(nuevoSaldo));
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
                            return guardar(TipoOperacionTransaccion.CREAR, Optional.of(transaccionDTO));
                        });
                } finally {
                    // Siempre liberamos el bloqueo
                    liberarCuenta(Optional.of(numero));
                }
            });
    }
    
    @Override
    public Optional<TransaccionDTO> transferir(Optional<Long> numeroCuentaOrigen, Optional<Long> numeroCuentaDestino, Optional<BigDecimal> monto, Optional<String> descripcion) {
        return numeroCuentaOrigen
            .filter(numero -> monto.map(cantidad -> cantidad.compareTo(BigDecimal.ZERO) > 0).orElse(false))
            .flatMap(numeroOrigen -> numeroCuentaDestino
                .flatMap(numeroDestino -> {
                    // Intentamos bloquear ambas cuentas
                    if (!bloquearCuenta(Optional.of(numeroOrigen)) || !bloquearCuenta(Optional.of(numeroDestino))) {
                        return Optional.empty();
                    }
                    
                    try {
                        // Construimos las descripciones personalizadas
                        String descripcionRetiro = "Transferencia enviada a cuenta " + numeroDestino + 
                            descripcion.map(desc -> ": " + desc).orElse("");
                            
                        String descripcionIngreso = "Transferencia recibida de cuenta " + numeroOrigen + 
                            descripcion.map(desc -> ": " + desc).orElse("");

                        // Realizamos el retiro de la cuenta origen
                        return retirar(Optional.of(numeroOrigen), monto, Optional.of(descripcionRetiro))
                            .flatMap(transaccionRetiro -> 
                                // Si el retiro fue exitoso, realizamos el ingreso en la cuenta destino
                                ingresar(Optional.of(numeroDestino), monto, Optional.of(descripcionIngreso))
                                    .map(transaccionIngreso -> transaccionRetiro) // Retornamos la transacción de retiro como la principal
                            );
                    } finally {
                        // Siempre liberamos los bloqueos
                        liberarCuenta(Optional.of(numeroOrigen));
                        liberarCuenta(Optional.of(numeroDestino));
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
}
