# 007-22/05/2025 00:38:13-Implementación CuentaService - Parte 3

Voy a empezar por crear el interface CuentaService:

The apply model made the following changes to the file:

```
- 
+ package com.mibanco.service;
+ 
+ import com.mibanco.dto.CuentaDTO;
+ import com.mibanco.model.enums.TipoCuenta;
+ 
+ import java.math.BigDecimal;
+ import java.util.List;
+ import java.util.Optional;
+ 
+ /**
+  * Interfaz para servicios relacionados con cuentas bancarias
+  * Utilizando enfoque funcional con Optional
+  */
+ public interface CuentaService {
+ 
+     /**
+      * Crea una nueva cuenta en el sistema
+      * @param cuentaDTO Optional con los datos de la cuenta a crear
+      * @return Optional con la cuenta creada
+      */
+     Optional<CuentaDTO> crearCuenta(Optional<CuentaDTO> cuentaDTO);
+     
+     /**
+      * Obtiene una cuenta por su número
+      * @param numeroCuenta Optional con el número de cuenta a buscar
+      * @return Optional con la cuenta encontrada
+      */
+     Optional<CuentaDTO> obtenerCuentaPorNumero(Optional<String> numeroCuenta);
+     
+     /**
+      * Obtiene todas las cuentas de un titular
+      * @param idTitular Optional con el ID del titular
+      * @return Optional con la lista de cuentas del titular
+      */
+     Optional<List<CuentaDTO>> obtenerCuentasPorTitular(Optional<Long> idTitular);
+     
+     /**
+      * Obtiene todas las cuentas de un tipo específico
+      * @param tipo Optional con el tipo de cuenta
+      * @return Optional con la lista de cuentas del tipo especificado
+      */
+     Optional<List<CuentaDTO>> obtenerCuentasPorTipo(Optional<TipoCuenta> tipo);
+     
+     /**
+      * Obtiene todas las cuentas del sistema
+      * @return Optional con la lista de todas las cuentas
+      */
+     Optional<List<CuentaDTO>> obtenerTodasLasCuentas();
+     
+     /**
+      * Obtiene todas las cuentas activas
+      * @return Optional con la lista de cuentas activas
+      */
+     Optional<List<CuentaDTO>> obtenerCuentasActivas();
+     
+     /**
+      * Actualiza el saldo de una cuenta
+      * @param numeroCuenta Número de la cuenta a actualizar
+      * @param nuevoSaldo Optional con el nuevo saldo
+      * @return Optional con la cuenta actualizada
+      */
+     Optional<CuentaDTO> actualizarSaldoCuenta(String numeroCuenta, Optional<BigDecimal> nuevoSaldo);
+     
+     /**
+      * Activa o desactiva una cuenta
+      * @param numeroCuenta Número de la cuenta a actualizar
+      * @param activa Optional con el nuevo estado
+      * @return Optional con la cuenta actualizada
+      */
+     Optional<CuentaDTO> actualizarEstadoCuenta(String numeroCuenta, Optional<Boolean> activa);
+     
+     /**
+      * Actualiza múltiples campos de una cuenta
+      * @param numeroCuenta Número de la cuenta a actualizar
+      * @param cuentaDTO Optional con los datos a actualizar
+      * @return Optional con la cuenta actualizada
+      */
+     Optional<CuentaDTO> actualizarCuenta(String numeroCuenta, Optional<CuentaDTO> cuentaDTO);
+     
+     /**
+      * Elimina una cuenta
+      * @param numeroCuenta Optional con el número de la cuenta a eliminar
+      * @return true si se eliminó correctamente, false en caso contrario
+      */
+     boolean eliminarCuenta(Optional<String> numeroCuenta);
+ } 
```

Ahora, voy a necesitar ver el mapper de Cuenta antes de implementar el servicio:

Contents of directory:

[file] ClienteMapper.java (3.6KB, 104 lines)
[file] Mapper.java (790B, 27 lines)

Necesito crear el mapper para Cuenta:

The apply model made the following changes to the file:

```
- 
+ package com.mibanco.dto.mapper;
+ 
+ import com.mibanco.dto.CuentaDTO;
+ import com.mibanco.model.Cuenta;
+ 
+ import java.util.List;
+ import java.util.Optional;
+ import java.util.stream.Collectors;
+ 
+ /**
+  * Mapper para transformar entre Cuenta (entidad) y CuentaDTO
+  * Utilizando enfoque funcional con Optional
+  */
+ public class CuentaMapper implements Mapper<Cuenta, CuentaDTO> {
+ 
+     private final ClienteMapper clienteMapper;
+ 
+     /**
+      * Constructor para inyección de dependencias
+      * @param clienteMapper Mapper para convertir Cliente/ClienteDTO
+      */
+     public CuentaMapper(ClienteMapper clienteMapper) {
+         this.clienteMapper = clienteMapper;
+     }
+ 
+     /**
+      * Convierte una entidad Cuenta a DTO
+      * @param entity Optional con la entidad a convertir
+      * @return Optional con el DTO convertido
+      */
+     @Override
+     public Optional<CuentaDTO> toDto(Optional<Cuenta> entity) {
+         return entity.map(cuenta -> 
+             CuentaDTO.builder()
+                 .numeroCuenta(cuenta.getNumeroCuenta())
+                 .titular(clienteMapper.toDtoDirecto(Optional.ofNullable(cuenta.getTitular())).orElse(null))
+                 .tipo(cuenta.getTipo())
+                 .saldo(cuenta.getSaldo())
+                 .fechaCreacion(cuenta.getFechaCreacion())
+                 .activa(cuenta.isActiva())
+                 .build()
+         );
+     }
+ 
+     /**
+      * Convierte directamente una entidad Cuenta a DTO sin envolver en Optional
+      * @param entity Entidad a convertir
+      * @return Optional con el DTO convertido
+      */
+     public Optional<CuentaDTO> toDtoDirecto(Optional<Cuenta> entity) {
+         return toDto(entity);
+     }
+ 
+     /**
+      * Convierte un DTO a entidad Cuenta
+      * @param dto Optional con el DTO a convertir
+      * @return Optional con la entidad convertida
+      */
+     @Override
+     public Optional<Cuenta> toEntity(Optional<CuentaDTO> dto) {
+         return dto.map(cuentaDTO -> 
+             Cuenta.builder()
+                 .numeroCuenta(cuentaDTO.getNumeroCuenta())
+                 .titular(clienteMapper.toEntityDirecto(Optional.ofNullable(cuentaDTO.getTitular())).orElse(null))
+                 .tipo(cuentaDTO.getTipo())
+                 .saldo(cuentaDTO.getSaldo())
+                 .fechaCreacion(cuentaDTO.getFechaCreacion())
+                 .activa(cuentaDTO.isActiva())
+                 .build()
+         );
+     }
+ 
+     /**
+      * Convierte directamente un DTO a entidad Cuenta sin envolver en Optional
+      * @param dto DTO a convertir
+      * @return Optional con la entidad convertida
+      */
+     public Optional<Cuenta> toEntityDirecto(Optional<CuentaDTO> dto) {
+         return toEntity(dto);
+     }
+ 
+     /**
+      * Convierte una lista de entidades a lista de DTOs
+      * @param entities Optional con la lista de entidades
+      * @return Optional con la lista de DTOs
+      */
+     public Optional<List<CuentaDTO>> toDtoList(Optional<List<Cuenta>> entities) {
+         return entities.map(lista -> 
+             lista.stream()
+                 .map(cuenta -> toDtoDirecto(Optional.of(cuenta)).orElse(null))
+                 .filter(dto -> dto != null)
+                 .collect(Collectors.toList())
+         );
+     }
+ } 
```

The edit introduced the following new linter errors:
Line 36: The method toDtoDirecto(Cliente) in the type ClienteMapper is not applicable for the arguments (Optional<Cliente>), severity: 1
Line 36: Type mismatch: cannot convert from Optional<Cliente> to Cliente, severity: 1
Line 64: The method toEntityDirecto(ClienteDTO) in the type ClienteMapper is not applicable for the arguments (Optional<ClienteDTO>), severity: 1
Line 64: Type mismatch: cannot convert from Optional<ClienteDTO> to ClienteDTO, severity: 1

Please fix the linter errors if it is clear how to (or you can easily figure out how to). Do not make uneducated guesses. And do not loop more than 3 times on fixing linter errors on the same file. 