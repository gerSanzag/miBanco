package com.mibanco.model;

import com.mibanco.model.enums.TipoTransaccion;
import lombok.Value;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Clase que representa una transacción bancaria
 * Una transacción es un evento que no debe modificarse una vez creada
 * por lo que implementamos un enfoque completamente inmutable
 */
@Value
@Builder(toBuilder = true)
public class Transaccion implements Identificable {
    Long id;
    String numeroCuenta;
    String numeroCuentaDestino;
    TipoTransaccion tipo;
    BigDecimal monto;
    LocalDateTime fecha;
    String descripcion;
    
   
    
    /**
     * Método factory para crear transacciones
     */
    public static Transaccion of(Long id, String numeroCuenta, String numeroCuentaDestino,
                               TipoTransaccion tipo, BigDecimal monto, 
                               Optional<LocalDateTime> fecha, Optional<String> descripcion) {
        return Transaccion.builder()
                .id(id)
                .numeroCuenta(numeroCuenta)
                .numeroCuentaDestino(numeroCuentaDestino)
                .tipo(tipo)
                .monto(monto)
                .fecha(fecha.orElse(LocalDateTime.now()))
                .descripcion(descripcion.orElse(""))
                .build();
    }
    
    /**
     * Crea una copia de la transacción pero con un nuevo ID
     * Útil para crear transacciones similares o correlacionadas
     * @return Nueva transacción con ID actualizado
     */
    public Transaccion withNuevoId(Long nuevoId) {
        return this.toBuilder()
                .id(nuevoId)
                .fecha(LocalDateTime.now()) // Actualizamos la fecha también
                .build();
    }
    
    /**
     * Crea una copia de la transacción con descripción actualizada
     * Este es uno de los pocos casos donde puede ser útil "modificar" una transacción
     * @return Nueva transacción con descripción actualizada
     */
    public Transaccion withDescripcion(String nuevaDescripcion) {
        return this.toBuilder()
                .descripcion(nuevaDescripcion)
                .build();
    }
} 