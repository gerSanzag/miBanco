package com.mibanco.model;

import com.mibanco.model.enums.TipoTarjeta;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Clase que representa una tarjeta bancaria
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tarjeta {
    private String numero;
    private Cliente titular;
    private String numeroCuentaAsociada;
    private TipoTarjeta tipo;
    private LocalDate fechaExpiracion;
    private String cvv;
    private boolean activa;
} 