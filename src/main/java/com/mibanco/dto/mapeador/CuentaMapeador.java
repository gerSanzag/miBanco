package com.mibanco.dto.mapeador;

import com.mibanco.dto.CuentaDTO;
import com.mibanco.modelo.Cuenta;
import com.mibanco.modelo.Cliente;
import com.mibanco.dto.ClienteDTO;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación de Mapper para Cuenta utilizando enfoque funcional
 */
public class CuentaMapeador implements Mapeador<Cuenta, CuentaDTO> {
    private final Mapeador<Cliente, ClienteDTO> clienteMapeador;
    
    // ✅ Supplier para generar ID secuencial automáticamente
    private final java.util.function.Supplier<Long> idSupplier = () -> 
        new java.util.concurrent.atomic.AtomicLong(0).incrementAndGet();

    public CuentaMapeador(Mapeador<Cliente, ClienteDTO> clienteMapeador) {
        this.clienteMapeador = clienteMapeador;
    }

    /**
     * Convierte una cuenta a CuentaDTO
     * Implementación estrictamente funcional con Optional
     */
    @Override
    public Optional<CuentaDTO> aDto(Optional<Cuenta> cuentaOpt) {
        return cuentaOpt.map(cuenta -> CuentaDTO.builder()
            .numeroCuenta(cuenta.getNumeroCuenta())
            .titular(clienteMapeador.aDto(Optional.of(cuenta.getTitular())).orElse(null))
            .tipo(cuenta.getTipo())
            .saldo(cuenta.getSaldo())
            .fechaCreacion(cuenta.getFechaCreacion())
            .activa(cuenta.isActiva())
            .build());
    }

    /**
     * Convierte un CuentaDTO a Cuenta
     * Implementación estrictamente funcional con Optional
     */
    @Override
    public Optional<Cuenta> aEntidad(Optional<CuentaDTO> dtoOpt) {
        return dtoOpt.map(dto -> Cuenta.builder()
            .numeroCuenta(dto.getNumeroCuenta() != null ? dto.getNumeroCuenta() : idSupplier.get()) // ✅ Generar ID automáticamente si es null
            .titular(clienteMapeador.aEntidad(Optional.of(dto.getTitular())).orElse(null))
            .tipo(dto.getTipo())
            .saldo(dto.getSaldo())
            .fechaCreacion(dto.getFechaCreacion())
            .activa(dto.isActiva())
            .build());
    }
   
    /**
     * Sobrecarga que acepta Cuenta directamente
     * Para facilitar el uso en contextos donde no se trabaja con Optional
     */
    public Optional<CuentaDTO> aDtoDirecto(Cuenta cuenta) {
        return aDto(Optional.ofNullable(cuenta));
    }
    
    /**
     * Sobrecarga que acepta CuentaDTO directamente
     * Para facilitar el uso en contextos donde no se trabaja con Optional
     */
    public Optional<Cuenta> aEntidadDirecta(CuentaDTO dto) {
        return aEntidad(Optional.ofNullable(dto));
    }
    
    /**
     * Convierte una lista de Cuenta a lista de CuentaDTO
     * Utilizando programación funcional con streams
     * @return Optional con la lista de DTOs o Optional.empty() si la entrada es nula
     */
    public Optional<List<CuentaDTO>> aListaDto(Optional<List<Cuenta>> cuentas) {
        return cuentas.map(list -> list.stream()
                        .map(cuenta -> aDto(Optional.of(cuenta)).orElse(null))
                        .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList()));
    }
    
    /**
     * Sobrecarga que acepta List<Cuenta> directamente
     */
    public Optional<List<CuentaDTO>> aListaDto(List<Cuenta> cuentas) {
        return aListaDto(Optional.ofNullable(cuentas));
    }
    
    /**
     * Convierte una lista de CuentaDTO a lista de Cuenta
     * Utilizando programación funcional con streams
     * @return Optional con la lista de entidades o Optional.empty() si la entrada es nula
     */
    public Optional<List<Cuenta>> aListaEntidad(Optional<List<CuentaDTO>> dtos) {
        return dtos.map(list -> list.stream()
                        .map(dto -> aEntidad(Optional.of(dto)).orElse(null))
                        .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList()));
    }
    
    /**
     * Sobrecarga que acepta List<CuentaDTO> directamente
     */
    public Optional<List<Cuenta>> aListaEntidad(List<CuentaDTO> dtos) {
        return aListaEntidad(Optional.ofNullable(dtos));
    }
} 