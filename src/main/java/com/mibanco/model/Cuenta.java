package com.mibanco.model;

import com.mibanco.model.enums.TipoCuenta;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Clase que representa una cuenta bancaria
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cuenta {
    private String numeroCuenta;
    private Cliente titular;
    private TipoCuenta tipo;
    private BigDecimal saldo;
    private LocalDateTime fechaCreacion;
    private boolean activa;
} 