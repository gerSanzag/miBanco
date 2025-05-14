package com.mibanco.dto;

import com.mibanco.model.enums.TipoTransaccion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import static java.util.Map.entry;
import static java.util.Map.ofEntries;

/**
 * DTO para transferir información de Transacción entre capas
 * Utilizamos enfoque inmutable con @Value para promover la programación funcional
 */
@Value
@Builder(toBuilder = true) // Habilitamos toBuilder para métodos "with"
@AllArgsConstructor
public class TransaccionDTO {
    Long id;
    String numeroCuenta;
    String numeroCuentaDestino;
    TipoTransaccion tipo;
    BigDecimal monto;
    LocalDateTime fecha;
    String descripcion;

    // Mapa estático de inversiones de tipos de transacción
    private static final Map<TipoTransaccion, TipoTransaccion> INVERSIONES_TIPO = ofEntries(
        entry(TipoTransaccion.DEPOSITO, TipoTransaccion.RETIRO),
        entry(TipoTransaccion.RETIRO, TipoTransaccion.DEPOSITO),
        entry(TipoTransaccion.TRANSFERENCIA_ENVIADA, TipoTransaccion.TRANSFERENCIA_RECIBIDA),
        entry(TipoTransaccion.TRANSFERENCIA_RECIBIDA, TipoTransaccion.TRANSFERENCIA_ENVIADA)
    );

    /**
     * Método estático que construye un TransaccionDTO con valores opcionales
     * Aplicando enfoque funcional con Optional para manejar valores nulos
     */
    public static TransaccionDTO of(Long id, String numeroCuenta, 
                                   Optional<String> numeroCuentaDestino,
                                   TipoTransaccion tipo, BigDecimal monto,
                                   Optional<LocalDateTime> fecha,
                                   Optional<String> descripcion) {
        
        return TransaccionDTO.builder()
                .id(id)
                .numeroCuenta(numeroCuenta)
                .numeroCuentaDestino(numeroCuentaDestino.orElse(null))
                .tipo(tipo)
                .monto(monto)
                .fecha(fecha.orElse(LocalDateTime.now()))
                .descripcion(descripcion.orElse(""))
                .build();
    }
    
    /**
     * Crea una copia de la transacción con un nuevo ID
     * Útil para crear transacciones derivadas o correlacionadas
     */
    public TransaccionDTO withId(Long nuevoId) {
        return this.toBuilder()
                .id(nuevoId)
                .build();
    }
    
    /**
     * Crea una copia de la transacción con fecha actualizada
     */
    public TransaccionDTO withFecha(LocalDateTime nuevaFecha) {
        return this.toBuilder()
                .fecha(nuevaFecha)
                .build();
    }
    
    /**
     * Crea una copia de la transacción con descripción actualizada
     */
    public TransaccionDTO withDescripcion(String nuevaDescripcion) {
        return this.toBuilder()
                .descripcion(nuevaDescripcion)
                .build();
    }
    
    /**
     * Crea una copia invertida de la transacción (útil para operaciones de anulación)
     * Por ejemplo: convertir un depósito en un retiro, o viceversa
     */
    public TransaccionDTO withInversion() {
        // Obtener el tipo inverso usando el Map, o mantener el mismo si no hay inverso definido
        TipoTransaccion nuevoTipo = Optional.ofNullable(INVERSIONES_TIPO.get(this.tipo))
                .orElse(this.tipo);
        
        return this.toBuilder()
                .id(null) // Nueva transacción, nuevo ID
                .tipo(nuevoTipo)
                .fecha(LocalDateTime.now())
                .descripcion("ANULACIÓN: " + this.descripcion)
                .build();
    }
} 