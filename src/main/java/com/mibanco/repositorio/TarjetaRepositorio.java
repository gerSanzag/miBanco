package com.mibanco.repositorio;

import com.mibanco.modelo.Tarjeta;
import com.mibanco.modelo.enums.TipoOperacionTarjeta;
import com.mibanco.modelo.enums.TipoTarjeta;
import com.mibanco.repositorio.util.BaseRepositorio;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz para el repositorio de Tarjetas
 * Extiende la interfaz base para heredar operaciones CRUD genéricas
 */
public interface TarjetaRepositorio extends BaseRepositorio<Tarjeta, Long, TipoOperacionTarjeta> {
    
    
} 