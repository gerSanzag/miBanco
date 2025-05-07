package com.mibanco.model;

import com.mibanco.model.enums.TipoTransaccion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Clase que representa una transacción bancaria
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaccion {
    private Long id;
    private String numeroCuenta;
    private String numeroCuentaDestino;
    private TipoTransaccion tipo;
    private BigDecimal monto;
    private LocalDateTime fecha;
    private String descripcion;
} 