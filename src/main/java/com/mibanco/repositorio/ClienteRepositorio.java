package com.mibanco.repositorio;

import com.mibanco.modelo.Cliente;
import com.mibanco.modelo.enums.TipoOperacionCliente;
import com.mibanco.repositorio.util.BaseRepositorio;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Interfaz para el repositorio de Clientes
 * Extiende la interfaz base para heredar operaciones CRUD genéricas
 */
public interface ClienteRepositorio extends BaseRepositorio<Cliente, Long, TipoOperacionCliente> {
    
  
} 