package com.mibanco.util;

import com.mibanco.modelo.Identificable;
import com.mibanco.modelo.RegistroAuditoria;
import com.mibanco.repositorio.AuditoriaRepositorio;

import java.util.Optional;

/**
 * Clase utilitaria para manejar la auditoría de manera centralizada
 * Enfoque simple y directo sin funciones intermedias
 */
public class AuditoriaUtil {
    
    /**
     * Registra una operación de auditoría directamente
     * @param auditoriaRepository Repositorio donde guardar el registro
     * @param tipoOperacion El tipo de operación realizada
     * @param entidad La entidad sobre la que se realizó la operación
     * @param usuario El usuario que realizó la operación
     * @return El registro de auditoría creado y guardado
     */
    public static <T extends Identificable, E extends Enum<E>> 
           RegistroAuditoria<T, E> registrarOperacion(
               AuditoriaRepositorio auditoriaRepository,
               E tipoOperacion, 
               T entidad, 
               String usuario) {
        
        // Crear registro de auditoría
        RegistroAuditoria<T, E> registro = RegistroAuditoria.of(tipoOperacion, entidad, usuario);
        
        // Guardar y devolver
        Optional<RegistroAuditoria<T, E>> registroGuardado = auditoriaRepository.registrar(Optional.of(registro));
        return registroGuardado.orElse(registro);
    }
} 