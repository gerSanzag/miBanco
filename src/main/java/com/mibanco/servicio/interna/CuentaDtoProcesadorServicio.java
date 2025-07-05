package com.mibanco.servicio.interna;

import com.mibanco.dto.ClienteDTO;
import com.mibanco.dto.CuentaDTO;
import com.mibanco.servicio.ClienteServicio;
import com.mibanco.servicio.TransaccionOperacionesServicio;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio especializado en procesar la creación de CuentaDTO
 * Aplica el Principio de Responsabilidad Única (SRP)
 * Se encarga únicamente de transformar datos crudos en CuentaDTO válidos
 * y procesar operaciones relacionadas con la cuenta
 */
public class CuentaDtoProcesadorServicio {
    
    private final ClienteServicio clienteServicio;
    
    public CuentaDtoProcesadorServicio(ClienteServicio clienteServicio) {
        this.clienteServicio = clienteServicio;
    }
    
    /**
     * Procesa los datos crudos y crea un CuentaDTO válido
     * @param datosCrudos Mapa con los datos de la cuenta
     * @return Optional con el CuentaDTO procesado o vacío si hay errores
     */
    public Optional<CuentaDTO> procesarCuentaDto(Map<String, String> datosCrudos) {
        return Optional.of(datosCrudos)
            .flatMap(this::extraerIdTitular)
            .flatMap(this::obtenerClientePorId)
            .flatMap(titular -> construirCuentaDTO(datosCrudos, titular));
    }
    
    /**
     * Procesa el ingreso inicial y prepara la cuenta con el saldo
     * @param cuentaCreada La cuenta ya creada
     * @param montoInicial El monto del ingreso inicial
     * @param transaccionServicio Servicio para procesar transacciones
     * @return Optional con la cuenta preparada con saldo o vacío si falla la validación
     */
    public Optional<CuentaDTO> procesarIngresoInicial(CuentaDTO cuentaCreada, 
                                                      BigDecimal montoInicial, 
                                                      TransaccionOperacionesServicio transaccionServicio) {
        Long numeroCuenta = cuentaCreada.getNumeroCuenta();
        
        return transaccionServicio.ingresar(
                Optional.of(numeroCuenta),
                Optional.of(montoInicial),
                Optional.of("Ingreso inicial de apertura")
            )
            .map(transaccion -> actualizarCuentaConSaldo(cuentaCreada, montoInicial))
            .or(() -> Optional.empty());
    }
    
    /**
     * Actualiza la cuenta con el saldo inicial (sin persistencia)
     * @param cuenta La cuenta a actualizar
     * @param saldo El saldo inicial
     * @return La cuenta actualizada
     */
    public CuentaDTO actualizarCuentaConSaldo(CuentaDTO cuenta, BigDecimal saldo) {
        return cuenta.toBuilder()
            .saldoInicial(saldo)
            .saldo(saldo)
            .build();
    }
    
    /**
     * Extrae el ID del titular desde el mapa de datos
     */
    private Optional<Long> extraerIdTitular(Map<String, String> datosCrudos) {
        return Optional.ofNullable(datosCrudos.get("idTitular"))
            .map(Long::parseLong);
    }
    
    /**
     * Obtiene el cliente por ID usando el servicio
     */
    private Optional<ClienteDTO> obtenerClientePorId(Long idTitular) {
        return clienteServicio.obtenerClientePorId(Optional.of(idTitular));
    }
    
    /**
     * Construye el CuentaDTO a partir de los datos crudos y el titular
     */
    private Optional<CuentaDTO> construirCuentaDTO(Map<String, String> datosCrudos, ClienteDTO titular) {
        try {
            // ✅ Supplier para generar ID automáticamente
            java.util.function.Supplier<Long> idSupplier = () -> 
                System.currentTimeMillis() % 1000000000L;

            CuentaDTO.CuentaDTOBuilder builder = CuentaDTO.builder()
                .titular(titular);

            // ✅ Generar ID directamente
            builder.numeroCuenta(idSupplier.get());

            // Aplicar transformaciones funcionales
            Optional.ofNullable(datosCrudos.get("tipo"))
                .map(com.mibanco.modelo.enums.TipoCuenta::valueOf)
                .ifPresent(builder::tipo);

            Optional.ofNullable(datosCrudos.get("fechaCreacion"))
                .map(java.time.LocalDateTime::parse)
                .ifPresent(builder::fechaCreacion);

            // Estado activo por defecto
            Optional.ofNullable(datosCrudos.get("activa"))
                .map(Boolean::parseBoolean)
                .or(() -> Optional.of(true))
                .ifPresent(builder::activa);

            return Optional.of(builder.build());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
} 