package com.mibanco.servicio.interna;

import com.mibanco.dto.ClienteDTO;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio especializado en procesar la creación de ClienteDTO
 * Aplica el Principio de Responsabilidad Única (SRP)
 * Se encarga únicamente de transformar datos crudos en ClienteDTO válidos
 * y procesar operaciones relacionadas con el cliente
 */
public class ClienteDtoProcesadorServicio {
    
    /**
     * Procesa los datos crudos y crea un ClienteDTO válido
     * @param datosCrudos Mapa con los datos del cliente
     * @return Optional con el ClienteDTO procesado o vacío si hay errores
     */
    public Optional<ClienteDTO> procesarClienteDto(Map<String, String> datosCrudos) {
        return Optional.of(datosCrudos)
            .flatMap(this::construirClienteDTO);
    }
    
    /**
     * Construye el ClienteDTO a partir de los datos crudos
     */
    private Optional<ClienteDTO> construirClienteDTO(Map<String, String> datosCrudos) {
        try {
            ClienteDTO.ClienteDTOBuilder builder = ClienteDTO.builder();

            // Aplicar transformaciones funcionales con validaciones
            Optional.ofNullable(datosCrudos.get("nombre"))
                .ifPresent(builder::nombre);

            Optional.ofNullable(datosCrudos.get("apellido"))
                .ifPresent(builder::apellido);

            Optional.ofNullable(datosCrudos.get("dni"))
                .ifPresent(builder::dni);

            Optional.ofNullable(datosCrudos.get("email"))
                .ifPresent(builder::email);

            Optional.ofNullable(datosCrudos.get("telefono"))
                .ifPresent(builder::telefono);

            Optional.ofNullable(datosCrudos.get("direccion"))
                .ifPresent(builder::direccion);

            // Procesar fecha de nacimiento con validación
            Optional.ofNullable(datosCrudos.get("fechaNacimiento"))
                .map(fecha -> LocalDate.parse(fecha, DateTimeFormatter.ISO_LOCAL_DATE))
                .ifPresent(builder::fechaNacimiento);

            return Optional.of(builder.build());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
} 