package com.mibanco.dto;

import com.mibanco.model.enums.TipoTransaccion;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * DTO para transferir información de Transacción entre capas
 * Utilizamos enfoque inmutable con @Value para promover la programación funcional
 * Una transacción, una vez creada, no debe modificarse (representa un evento histórico)
 */
@Value
@Builder(toBuilder = true)
public class TransaccionDTO {
    Long id;
    String numeroCuenta;
    String numeroCuentaDestino;
    TipoTransaccion tipo;
    BigDecimal monto;
    LocalDateTime fecha;
    String descripcion;

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
     * Crea una transacción de anulación basada en esta transacción
     * No modifica la transacción original, sino que crea una nueva que la contrarresta
     * @return Una nueva transacción con tipo inverso y referencia a la original
     */
    public TransaccionDTO crearAnulacion(Long nuevoId) {
        TipoTransaccion nuevoTipo;
        switch (this.tipo) {
            case DEPOSITO:
                nuevoTipo = TipoTransaccion.RETIRO;
                break;
            case RETIRO:
                nuevoTipo = TipoTransaccion.DEPOSITO;
                break;
            case TRANSFERENCIA_ENVIADA:
                nuevoTipo = TipoTransaccion.TRANSFERENCIA_RECIBIDA;
                break;
            case TRANSFERENCIA_RECIBIDA:
                nuevoTipo = TipoTransaccion.TRANSFERENCIA_ENVIADA;
                break;
            default:
                nuevoTipo = this.tipo;
        }
        
        return TransaccionDTO.builder()
                .id(nuevoId)
                .numeroCuenta(this.numeroCuenta)
                .numeroCuentaDestino(this.numeroCuentaDestino)
                .tipo(nuevoTipo)
                .monto(this.monto)
                .fecha(LocalDateTime.now())
                .descripcion("ANULACIÓN de " + this.id + ": " + this.descripcion)
                .build();
    }
} 