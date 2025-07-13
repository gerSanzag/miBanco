package com.mibanco.repositorio;

import com.mibanco.modelo.Cuenta;
import com.mibanco.modelo.enums.TipoOperacionCuenta;
import com.mibanco.repositorio.util.BaseRepositorio;

/**
 * Interfaz para el repositorio de Cuentas
 * Extiende la interfaz base para heredar operaciones CRUD genéricas
 */
public interface CuentaRepositorio extends BaseRepositorio<Cuenta, Long, TipoOperacionCuenta> {
    
} 