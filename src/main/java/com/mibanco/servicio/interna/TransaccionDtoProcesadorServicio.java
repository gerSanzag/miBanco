package com.mibanco.servicio.interna;

import com.mibanco.dto.TransaccionDTO;
import com.mibanco.modelo.enums.TipoTransaccion;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio especializado en procesar la creación de TransaccionDTO
 * Aplica el Principio de Responsabilidad Única (SRP)
 * Se encarga únicamente de transformar datos crudos en TransaccionDTO válidos
 * y procesar operaciones relacionadas con la transacción
 */
public class TransaccionDtoProcesadorServicio {
    
    /**
     * Procesa los datos crudos y crea un TransaccionDTO válido
     * @param datosCrudos Mapa con los datos de la transacción
     * @return Optional con el TransaccionDTO procesado o vacío si hay errores
     */
    public Optional<TransaccionDTO> procesarTransaccionDto(Map<String, String> datosCrudos) {
        return Optional.of(datosCrudos)
            .flatMap(this::construirTransaccionDTO);
    }
    
    /**
     * Construye el TransaccionDTO a partir de los datos crudos
     */
    private Optional<TransaccionDTO> construirTransaccionDTO(Map<String, String> datosCrudos) {
        try {
            TransaccionDTO.TransaccionDTOBuilder builder = TransaccionDTO.builder();

            // Extraer y validar datos del Map con transformaciones funcionales
            Optional.ofNullable(datosCrudos.get("numeroCuenta"))
                .map(Long::parseLong)
                .ifPresent(builder::numeroCuenta);

            Optional.ofNullable(datosCrudos.get("numeroCuentaDestino"))
                .map(Long::parseLong)
                .ifPresent(builder::numeroCuentaDestino);

            Optional.ofNullable(datosCrudos.get("tipo"))
                .map(TipoTransaccion::valueOf)
                .ifPresent(builder::tipo);

            Optional.ofNullable(datosCrudos.get("monto"))
                .map(BigDecimal::new)
                .ifPresent(builder::monto);

            // Descripción por defecto si no se proporciona
            Optional.ofNullable(datosCrudos.get("descripcion"))
                .or(() -> Optional.of(""))
                .ifPresent(builder::descripcion);

            // Fecha automática si no se proporciona
            Optional.ofNullable(datosCrudos.get("fecha"))
                .map(LocalDateTime::parse)
                .or(() -> Optional.of(LocalDateTime.now()))
                .ifPresent(builder::fecha);

            return Optional.of(builder.build());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
} 