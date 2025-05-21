# 007-22/05/2025 00:38:13-Implementación CuentaService - Parte 4

Veo algunos errores en el mapper. Voy a examinar cómo está implementado el ClienteMapper para corregirlos:

Contents of src/main/java/com/mibanco/dto/mapper/ClienteMapper.java, lines 1-104 (entire file):
```
package com.mibanco.dto.mapper;

import com.mibanco.dto.ClienteDTO;
import com.mibanco.model.Cliente;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación de Mapper para Cliente utilizando enfoque funcional
 */
public class ClienteMapper implements Mapper<Cliente, ClienteDTO> {

    /**
     * Convierte un Cliente a ClienteDTO
     * Implementación estrictamente funcional con Optional
     */
    @Override
    public Optional<ClienteDTO> toDto(Optional<Cliente> clienteOpt) {
        return clienteOpt.map(cliente -> ClienteDTO.builder()
                .id(cliente.getId())
                .nombre(cliente.getNombre())
                .apellido(cliente.getApellido())
                .dni(cliente.getDni())
                .email(cliente.getEmail())
                .telefono(cliente.getTelefono())
                .fechaNacimiento(cliente.getFechaNacimiento())
                .direccion(cliente.getDireccion())
                .build());
    }

    /**
     * Convierte un ClienteDTO a Cliente
     * Implementación estrictamente funcional con Optional
     */
    @Override
    public Optional<Cliente> toEntity(Optional<ClienteDTO> dtoOpt) {
        return dtoOpt.map(dto -> Cliente.builder()
                .id(dto.getId())
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .dni(dto.getDni())
                .email(dto.getEmail())
                .telefono(dto.getTelefono())
                .fechaNacimiento(dto.getFechaNacimiento())
                .direccion(dto.getDireccion())
                .build());
    }
   
    /**
     * Sobrecarga que acepta Cliente directamente
     * Para facilitar el uso en contextos donde no se trabaja con Optional
     */
    public Optional<ClienteDTO> toDtoDirecto(Cliente cliente) {
        return toDto(Optional.ofNullable(cliente));
    }
    
    /**
     * Sobrecarga que acepta ClienteDTO directamente
     * Para facilitar el uso en contextos donde no se trabaja con Optional
     */
    public Optional<Cliente> toEntityDirecto(ClienteDTO dto) {
        return toEntity(Optional.ofNullable(dto));
    }
    
    /**
     * Convierte una lista de Cliente a lista de ClienteDTO
     * Utilizando programación funcional con streams
     * @return Optional con la lista de DTOs o Optional.empty() si la entrada es nula
     */
    public Optional<List<ClienteDTO>> toDtoList(Optional<List<Cliente>> clientes) {
        return clientes.map(list -> list.stream()
                        .map(cliente -> toDto(Optional.of(cliente)).orElse(null))
                        .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList()));
    }
    
    /**
     * Sobrecarga que acepta List<Cliente> directamente
     */
    public Optional<List<ClienteDTO>> toDtoList(List<Cliente> clientes) {
        return toDtoList(Optional.ofNullable(clientes));
    }
    
    /**
     * Convierte una lista de ClienteDTO a lista de Cliente
     * Utilizando programación funcional con streams
     * @return Optional con la lista de entidades o Optional.empty() si la entrada es nula
     */
    public Optional<List<Cliente>> toEntityList(Optional<List<ClienteDTO>> dtos) {
        return dtos.map(list -> list.stream()
                        .map(dto -> toEntity(Optional.of(dto)).orElse(null))
                        .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList()));
    }
    
    /**
     * Sobrecarga que acepta List<ClienteDTO> directamente
     */
    public Optional<List<Cliente>> toEntityList(List<ClienteDTO> dtos) {
        return toEntityList(Optional.ofNullable(dtos));
    }
} 
```

Ahora voy a corregir el CuentaMapper:

The apply model made the following changes to the file:

```
              CuentaDTO.builder()
                  .numeroCuenta(cuenta.getNumeroCuenta())
-                 .titular(clienteMapper.toDtoDirecto(Optional.ofNullable(cuenta.getTitular())).orElse(null))
+                 .titular(clienteMapper.toDtoDirecto(cuenta.getTitular()).orElse(null))
                  .tipo(cuenta.getTipo())
                  .saldo(cuenta.getSaldo())
...
      /**
       * Convierte directamente una entidad Cuenta a DTO sin envolver en Optional
-      * @param entity Entidad a convertir
+      * @param cuenta Entidad a convertir
       * @return Optional con el DTO convertido
       */
-     public Optional<CuentaDTO> toDtoDirecto(Optional<Cuenta> entity) {
-         return toDto(entity);
+     public Optional<CuentaDTO> toDtoDirecto(Cuenta cuenta) {
+         return toDto(Optional.ofNullable(cuenta));
      }
  
...
              Cuenta.builder()
                  .numeroCuenta(cuentaDTO.getNumeroCuenta())
-                 .titular(clienteMapper.toEntityDirecto(Optional.ofNullable(cuentaDTO.getTitular())).orElse(null))
+                 .titular(clienteMapper.toEntityDirecto(cuentaDTO.getTitular()).orElse(null))
                  .tipo(cuentaDTO.getTipo())
                  .saldo(cuentaDTO.getSaldo())
...
      /**
       * Convierte directamente un DTO a entidad Cuenta sin envolver en Optional
-      * @param dto DTO a convertir
+      * @param cuentaDTO DTO a convertir
       * @return Optional con la entidad convertida
       */
-     public Optional<Cuenta> toEntityDirecto(Optional<CuentaDTO> dto) {
-         return toEntity(dto);
+     public Optional<Cuenta> toEntityDirecto(CuentaDTO cuentaDTO) {
+         return toEntity(Optional.ofNullable(cuentaDTO));
      }
  
...
          return entities.map(lista -> 
              lista.stream()
-                 .map(cuenta -> toDtoDirecto(Optional.of(cuenta)).orElse(null))
+                 .map(cuenta -> toDtoDirecto(cuenta).orElse(null))
                  .filter(dto -> dto != null)
                  .collect(Collectors.toList())
          );
      }
+     
+     /**
+      * Sobrecarga que acepta List<Cuenta> directamente
+      */
+     public Optional<List<CuentaDTO>> toDtoList(List<Cuenta> cuentas) {
+         return toDtoList(Optional.ofNullable(cuentas));
+     }
+     
+     /**
+      * Convierte una lista de DTOs a lista de entidades
+      * @param dtos Optional con la lista de DTOs
+      * @return Optional con la lista de entidades
+      */
+     public Optional<List<Cuenta>> toEntityList(Optional<List<CuentaDTO>> dtos) {
+         return dtos.map(lista -> 
+             lista.stream()
+                 .map(dto -> toEntityDirecto(dto).orElse(null))
+                 .filter(entity -> entity != null)
+                 .collect(Collectors.toList())
+         );
+     }
+     
+     /**
+      * Sobrecarga que acepta List<CuentaDTO> directamente
+      */
+     public Optional<List<Cuenta>> toEntityList(List<CuentaDTO> dtos) {
+         return toEntityList(Optional.ofNullable(dtos));
+     }
  } 
```

Ahora, voy a implementar la clase CuentaServiceImpl:

The apply model made the following changes to the file:

```
- 
+ package com.mibanco.service.impl;
+ 
+ import com.mibanco.dto.CuentaDTO;
+ import com.mibanco.dto.mapper.CuentaMapper;
+ import com.mibanco.model.Cuenta;
+ import com.mibanco.model.enums.TipoCuenta;
+ import com.mibanco.repository.CuentaRepository;
+ import com.mibanco.service.CuentaService;
+ 
+ import java.math.BigDecimal;
+ import java.util.List;
+ import java.util.Optional;
+ import java.util.function.Function;
+ 
+ /**
+  * Implementación de la interfaz de servicio para Cuentas
+  * Utilizando estrictamente un enfoque funcional con Optional y streams
+  */
+ public class CuentaServiceImpl implements CuentaService {
+     
+     private final CuentaRepository cuentaRepository;
+     private final CuentaMapper cuentaMapper;
+     
+     /**
+      * Constructor para inyección de dependencias
+      * @param cuentaRepository Repositorio de cuentas
+      * @param cuentaMapper Mapper para conversión entre entidad y DTO
+      */
+     public CuentaServiceImpl(CuentaRepository cuentaRepository, CuentaMapper cuentaMapper) {
+         this.cuentaRepository = cuentaRepository;
+         this.cuentaMapper = cuentaMapper;
+     }
+     
+     /**
+      * Método privado que implementa el flujo común de actualización de cuentas
+      * @param numeroCuenta Número de la cuenta a actualizar
+      * @param actualizarDatos Función que define cómo actualizar los datos de la cuenta
+      * @return Optional con el DTO de la cuenta actualizada
+      */
+     private Optional<CuentaDTO> actualizarCuentaGenerica(String numeroCuenta, Function<Cuenta, Cuenta> actualizarDatos) {
+         return Optional.ofNullable(numeroCuenta)
+                 .flatMap(numero -> cuentaRepository.findByNumero(Optional.of(numero)))
+                 .map(actualizarDatos)
+                 .flatMap(cuenta -> cuentaRepository.save(Optional.of(cuenta)))
+                 .flatMap(cuentaMapper::toDtoDirecto);
+     }
+     
+     /**
+      * Crea una nueva cuenta en el sistema
+      * Utilizando programación funcional con Optional y composición de funciones
+      */
+     @Override
+     public Optional<CuentaDTO> crearCuenta(Optional<CuentaDTO> cuentaDTO) {
+         return cuentaDTO
+             .map(dto -> {
+                 Optional<Cuenta> entidad = cuentaMapper.toEntity(Optional.of(dto));
+                 if (!entidad.isPresent()) {
+                     return Optional.<CuentaDTO>empty();
+                 }
+                 
+                 Optional<Cuenta> guardada = cuentaRepository.save(entidad);
+                 return cuentaMapper.toDto(guardada);
+             })
+             .orElse(Optional.empty());
+     }
+     
+     /**
+      * Obtiene una cuenta por su número
+      * Utilizando programación funcional con Optional
+      */
+     @Override
+     public Optional<CuentaDTO> obtenerCuentaPorNumero(Optional<String> numeroCuenta) {
+         return numeroCuenta.flatMap(numero -> {
+             Optional<Cuenta> cuenta = cuentaRepository.findByNumero(Optional.of(numero));
+             return cuenta.flatMap(cuentaMapper::toDtoDirecto);
+         });
+     }
+     
+     /**
+      * Obtiene todas las cuentas de un titular
+      * Utilizando programación funcional con Optional
+      */
+     @Override
+     public Optional<List<CuentaDTO>> obtenerCuentasPorTitular(Optional<Long> idTitular) {
+         return idTitular.flatMap(id -> {
+             Optional<List<Cuenta>> cuentas = cuentaRepository.findByTitularId(Optional.of(id));
+             return cuentaMapper.toDtoList(cuentas);
+         });
+     }
+     
+     /**
+      * Obtiene todas las cuentas de un tipo específico
+      * Utilizando programación funcional con Optional
+      */
+     @Override
+     public Optional<List<CuentaDTO>> obtenerCuentasPorTipo(Optional<TipoCuenta> tipo) {
+         return tipo.flatMap(tipoCuenta -> {
+             Optional<List<Cuenta>> cuentas = cuentaRepository.findByTipo(Optional.of(tipoCuenta));
+             return cuentaMapper.toDtoList(cuentas);
+         });
+     }
+     
+     /**
+      * Obtiene todas las cuentas del sistema
+      * Utilizando programación funcional con Optional
+      */
+     @Override
+     public Optional<List<CuentaDTO>> obtenerTodasLasCuentas() {
+         return cuentaRepository.findAll()
+                 .flatMap(cuentaMapper::toDtoList);
+     }
+     
+     /**
+      * Obtiene todas las cuentas activas
+      * Utilizando programación funcional con Optional
+      */
+     @Override
+     public Optional<List<CuentaDTO>> obtenerCuentasActivas() {
+         return cuentaRepository.findAllActivas()
+                 .flatMap(cuentaMapper::toDtoList);
+     }
+     
+     /**
+      * Actualiza el saldo de una cuenta
+      * Utilizando enfoque funcional con map y flatMap
+      */
+     @Override
+     public Optional<CuentaDTO> actualizarSaldoCuenta(String numeroCuenta, Optional<BigDecimal> nuevoSaldo) {
+         return actualizarCuentaGenerica(
+             numeroCuenta,
+             cuenta -> cuenta.withSaldo(nuevoSaldo.orElse(cuenta.getSaldo()))
+         );
+     }
+     
+     /**
+      * Activa o desactiva una cuenta
+      * Utilizando enfoque funcional con map y flatMap
+      */
+     @Override
+     public Optional<CuentaDTO> actualizarEstadoCuenta(String numeroCuenta, Optional<Boolean> activa) {
+         return actualizarCuentaGenerica(
+             numeroCuenta,
+             cuenta -> cuenta.withActiva(activa.orElse(cuenta.isActiva()))
+         );
+     }
+     
+     /**
+      * Actualiza múltiples campos de una cuenta
+      * Utilizando programación funcional con Optional y composición de funciones
+      */
+     @Override
+     public Optional<CuentaDTO> actualizarCuenta(String numeroCuenta, Optional<CuentaDTO> cuentaDTO) {
+         Function<Cuenta, Cuenta> actualizarDatos = cuentaExistente -> 
+             cuentaDTO.flatMap(cuentaMapper::toEntityDirecto)
+                 .map(cuentaNueva -> cuentaExistente.withActualizaciones(
+                     Optional.ofNullable(cuentaNueva.getSaldo()),
+                     Optional.ofNullable(cuentaNueva.isActiva())
+                 ))
+                 .orElse(cuentaExistente);
+         
+         return actualizarCuentaGenerica(numeroCuenta, actualizarDatos);
+     }
+     
+     /**
+      * Elimina una cuenta
+      * Utilizando programación funcional sin if explícitos
+      */
+     @Override
+     public boolean eliminarCuenta(Optional<String> numeroCuenta) {
+         return numeroCuenta
+                 .flatMap(cuentaRepository::deleteByNumero)
+                 .isPresent();
+     }
+ } 
```

The edit introduced the following new linter errors:
Line 171: The method flatMap(Function<? super String,? extends Optional<? extends U>>) in the type Optional<String> is not applicable for the arguments (cuentaRepository::deleteByNumero), severity: 1
Line 171: The type CuentaRepository does not define deleteByNumero(String) that is applicable here, severity: 1

Please fix the linter errors if it is clear how to (or you can easily figure out how to). Do not make uneducated guesses. And do not loop more than 3 times on fixing linter errors on the same file. 