package com.mibanco.repositorio.interna;

import com.mibanco.dto.TarjetaDTO;
import com.mibanco.dto.mapeador.ClienteMapeador;
import com.mibanco.dto.mapeador.TarjetaMapeador;
import com.mibanco.modelo.Tarjeta;
import com.mibanco.modelo.enums.TipoOperacionTarjeta;
import com.mibanco.modelo.enums.TipoTarjeta;
import com.mibanco.repositorio.TarjetaRepositorio;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

/**
 * Implementación del repositorio de Tarjetas
 * Visibilidad restringida al paquete internal
 */
class TarjetaRepositorioImpl extends BaseRepositorioImpl<Tarjeta, Long, TipoOperacionTarjeta> implements TarjetaRepositorio {
    
    /**
     * Constructor con visibilidad de paquete
     */
    TarjetaRepositorioImpl() {
        super();
    }
   
    /**
     * Implementación específica para asignar nuevo ID a Tarjeta
     * Usa DTOs para mantener la inmutabilidad de la entidad
     * Enfoque funcional puro con Optional
     * Genera un número de tarjeta único de 16 dígitos
     * @param tarjeta Tarjeta sin ID asignado
     * @return Tarjeta con nuevo ID asignado
     */
    @Override
    protected Tarjeta crearConNuevoId(Tarjeta tarjeta) {
        ClienteMapeador clienteMapeador = new ClienteMapeador();
        TarjetaMapeador mapeador = new TarjetaMapeador(clienteMapeador);
        
        return mapeador.aDtoDirecto(tarjeta)
            .map(dto -> {
                Long numero = generarNumeroTarjetaUnico();
                return dto.toBuilder()
                    .numero(numero)
                    .build();
            })
            .flatMap(mapeador::aEntidadDirecta)
            .orElseThrow(() -> new IllegalStateException("No se pudo procesar la entidad Tarjeta"));
    }
    
    /**
     * Genera un número de tarjeta único de 16 dígitos
     * Usa ThreadLocalRandom para mejor distribución y rendimiento
     * @return Long con 16 dígitos aleatorios
     */
    private Long generarNumeroTarjetaUnico() {
        // Generar número entre 1000000000000000L y 9999999999999999L (16 dígitos)
        long min = 1000000000000000L;
        long max = 9999999999999999L;
        return ThreadLocalRandom.current().nextLong(min, max + 1);
    }
    
    /**
     * Implementación específica para obtener la configuración del repositorio
     * @return Map con la configuración necesaria
     */
    @Override
    protected Map<String, Object> obtenerConfiguracion() {
        Map<String, Object> config = new HashMap<>();
        config.put("rutaArchivo", "src/main/resources/data/tarjeta.json");
        config.put("tipoClase", Tarjeta.class);
        config.put("extractorId", (Function<Tarjeta, Long>) Tarjeta::getId);
        return config;
    }
} 