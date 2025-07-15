package com.mibanco.repositorio.interna;

import com.mibanco.dto.CuentaDTO;
import com.mibanco.dto.mapeador.ClienteMapeador;
import com.mibanco.dto.mapeador.CuentaMapeador;
import com.mibanco.modelo.Cuenta;
import com.mibanco.modelo.enums.TipoCuenta;
import com.mibanco.modelo.enums.TipoOperacionCuenta;
import com.mibanco.repositorio.CuentaRepositorio;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Implementación del repositorio de Cuentas
 * Visibilidad restringida al paquete internal
 */
class CuentaRepositorioImpl extends BaseRepositorioImpl<Cuenta, Long, TipoOperacionCuenta> implements CuentaRepositorio {
    
    /**
     * Constructor con visibilidad de paquete
     */
    CuentaRepositorioImpl() {
        super();
    }
    
    /**
     * Implementación específica para asignar nuevo ID a Cuenta
     * SIEMPRE genera un nuevo número de cuenta aleatorio
     * Este método solo se llama desde crearRegistro() cuando la entidad no tiene ID
     * Genera números de cuenta en formato IBAN español: ES34 + 20 dígitos aleatorios
     * Usa DTOs para mantener la inmutabilidad de la entidad
     * Enfoque funcional puro con Optional
     * @param cuenta Cuenta sin ID asignado
     * @return Cuenta con nuevo ID asignado
     */
    @Override
    protected Cuenta crearConNuevoId(Cuenta cuenta) {
        ClienteMapeador clienteMapeador = new ClienteMapeador();
        CuentaMapeador mapeador = new CuentaMapeador(clienteMapeador);
        
        return mapeador.aDtoDirecto(cuenta)
            .map(dto -> {
                // SIEMPRE generar un nuevo número de cuenta aleatorio
                StringBuilder numeroAleatorio = new StringBuilder();
                for (int i = 0; i < 20; i++) {
                    numeroAleatorio.append((int) (Math.random() * 10));
                }
                String numeroCuenta = "ES34" + numeroAleatorio.toString();
                
                return dto.toBuilder()
                    .numeroCuenta(numeroCuenta)
                    .build();
            })
            .flatMap(mapeador::aEntidadDirecta)
            .orElseThrow(() -> new IllegalStateException("No se pudo procesar la entidad Cuenta"));
    }
    
    /**
     * Implementación específica para obtener la configuración del repositorio
     * @return Map con la configuración necesaria
     */
    @Override
    protected Map<String, Object> obtenerConfiguracion() {
        Map<String, Object> config = new HashMap<>();
        config.put("rutaArchivo", "src/main/resources/data/cuenta.json");
        config.put("tipoClase", Cuenta.class);
        config.put("extractorId", (Function<Cuenta, Long>) Cuenta::getId);
        return config;
    }
} 