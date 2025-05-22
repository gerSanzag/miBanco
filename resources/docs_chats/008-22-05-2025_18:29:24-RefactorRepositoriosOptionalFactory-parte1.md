# Transcripción del chat: 008-22/05/2025-18:29:24-RefactorRepositoriosOptionalFactory (Parte 1)

## Usuario
```
@mibanco @reglageneral.mdc 
@ClienteService.java @CuentaService.java 
Una pregunta, no deberiamos estar usando la factory del repositorio que esta en config, para inyectar los repositorios en el servicio?
```

## Asistente
```
Tienes toda la razón, German. Si existe una factory de repositorios en el paquete config, deberíamos estar utilizándola para la inyección de los repositorios en los servicios.

Vamos a examinar cómo está implementada esta factory para entender mejor cómo integrarla con nuestros servicios.
```

*[El asistente explora el directorio config para encontrar la factory]*

```
Contents of directory:

[dir]  factory/ (? items)
```

*[El asistente examina el directorio factory]*

```
Contents of directory:

[file] RepositoryFactory.java (3.7KB, 100 lines)
```

*[El asistente lee el archivo RepositoryFactory.java]*

```
Contents of src/main/java/com/mibanco/config/factory/RepositoryFactory.java, lines 1-100 (entire file):
```
```java
package com.mibanco.config.factory;

import com.mibanco.repository.AuditoriaRepository;
import com.mibanco.repository.ClienteRepository;
import com.mibanco.repository.CuentaRepository;
import com.mibanco.repository.TarjetaRepository;
import com.mibanco.repository.TransaccionRepository;
import com.mibanco.repository.impl.AuditoriaRepositoryImpl;
import com.mibanco.repository.impl.ClienteRepositoryImpl;
import com.mibanco.repository.impl.CuentaRepositoryImpl;
import com.mibanco.repository.impl.TarjetaRepositoryImpl;
import com.mibanco.repository.impl.TransaccionRepositoryImpl;

import java.util.function.Supplier;

/**
 * Configuración centralizada de repositorios
 * Implementa el patrón Singleton para garantizar una única instancia de cada repositorio
 * También implementa el patrón Factory Method para la creación controlada de instancias
 */
public class RepositoryFactory {
    
    // Instancias únicas de los repositorios
    private static AuditoriaRepository auditoriaRepository;
    private static ClienteRepository clienteRepository;
    private static CuentaRepository cuentaRepository;
    private static TarjetaRepository tarjetaRepository;
    private static TransaccionRepository transaccionRepository;
    
    /**
     * Método genérico para obtener o inicializar un repositorio
     * @param <T> Tipo del repositorio
     * @param instance Instancia actual del repositorio
     * @param creator Función que crea una nueva instancia
     * @return La instancia del repositorio
     */
    private static synchronized <T> T getOrCreate(T instance, Supplier<T> creator) {
        if (instance == null) {
            return creator.get();
        }
        return instance;
    }
    
    /**
     * Obtiene la única instancia del repositorio de auditoría
     * @return Instancia del repositorio
     */
    public static synchronized AuditoriaRepository getAuditoriaRepository() {
        auditoriaRepository = getOrCreate(auditoriaRepository, 
            () -> new AuditoriaRepositoryImpl());
        return auditoriaRepository;
    }
    
    /**
     * Obtiene la única instancia del repositorio de clientes
     * @return Instancia del repositorio
     */
    public static synchronized ClienteRepository getClienteRepository() {
        clienteRepository = getOrCreate(clienteRepository, 
            () -> new ClienteRepositoryImpl(getAuditoriaRepository()));
        return clienteRepository;
    }
    
    /**
     * Obtiene la única instancia del repositorio de cuentas
     * @return Instancia del repositorio
     */
    public static synchronized CuentaRepository getCuentaRepository() {
        cuentaRepository = getOrCreate(cuentaRepository, 
            () -> new CuentaRepositoryImpl(getAuditoriaRepository()));
        return cuentaRepository;
    }
    
    /**
     * Obtiene la única instancia del repositorio de tarjetas
     * @return Instancia del repositorio
     */
    public static synchronized TarjetaRepository getTarjetaRepository() {
        tarjetaRepository = getOrCreate(tarjetaRepository, 
            () -> new TarjetaRepositoryImpl(getAuditoriaRepository()));
        return tarjetaRepository;
    }
    
    /**
     * Obtiene la única instancia del repositorio de transacciones
     * @return Instancia del repositorio
     */
    public static synchronized TransaccionRepository getTransaccionRepository() {
        transaccionRepository = getOrCreate(transaccionRepository, 
            () -> new TransaccionRepositoryImpl(getAuditoriaRepository()));
        return transaccionRepository;
    }
    
    /**
     * Constructor privado para prevenir instanciación
     */
    private RepositoryFactory() {
        // Constructor privado para evitar instanciación
    }
} 
```

*[El asistente examina las implementaciones de los servicios]*

```
Contents of directory:

[file] CuentaServiceImpl.java (6.2KB, 174 lines)
[file] ClienteServiceImpl.java (5.8KB, 166 lines)
```

*[El asistente lee el archivo ClienteServiceImpl.java]*

```
Contents of src/main/java/com/mibanco/service/impl/ClienteServiceImpl.java, lines 1-166 (entire file):
```
```java
package com.mibanco.service.impl;

import com.mibanco.dto.ClienteDTO;
import com.mibanco.dto.mapper.ClienteMapper;
import com.mibanco.model.Cliente;
import com.mibanco.repository.ClienteRepository;
import com.mibanco.service.ClienteService;
import com.mibanco.config.factory.RepositoryFactory;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Implementación de la interfaz de servicio para Clientes
 * Utilizando estrictamente un enfoque funcional con Optional y streams
 */
public class ClienteServiceImpl implements ClienteService {
    
    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    
    /**
     * Constructor para inyección de dependencias
     * @param clienteMapper Mapper para conversión entre entidad y DTO
     */
    public ClienteServiceImpl(ClienteMapper clienteMapper) {
        // Utilizamos la factory para obtener la instancia única del repositorio
        this.clienteRepository = RepositoryFactory.getClienteRepository();
        this.clienteMapper = clienteMapper;
    }
    
    /**
     * Método privado que implementa el flujo común de actualización de clientes
     * @param id ID del cliente a actualizar
     * @param actualizarDatos Función que define cómo actualizar los datos del cliente
     * @return Optional con el DTO del cliente actualizado
     */
    private Optional<ClienteDTO> actualizarClienteGenerico(Long id, Function<Cliente, Cliente> actualizarDatos) {
        return Optional.ofNullable(id)
                .flatMap(clienteRepository::findById)
                .map(actualizarDatos)
                .flatMap(clienteRepository::save)
                .flatMap(clienteMapper::toDtoDirecto);
    }
    
    /**
     * Crea un nuevo cliente en el sistema
     * Utilizando programación funcional con Optional y composición de funciones
     */
    @Override
    public Optional<ClienteDTO> crearCliente(Optional<ClienteDTO> clienteDTO) {
        return clienteDTO
            .map(dto -> {
                // Aquí puedes incluir lógica más compleja o validaciones
                Optional<Cliente> entidad = clienteMapper.toEntity(Optional.of(dto));
                if (!entidad.isPresent()) {
                    return Optional.<ClienteDTO>empty(); 
                }
                
                Optional<Cliente> guardado = clienteRepository.save(entidad.get());
                return clienteMapper.toDto(guardado);
            })
            .orElse(Optional.empty());
    }
    
    /**
     * Obtiene un cliente por su ID
     * Utilizando programación funcional con Optional
     */
    @Override
    public Optional<ClienteDTO> obtenerClientePorId(Optional<Long> id) {
        return id.map(idValue->{
            Optional<Cliente> cliente = clienteRepository.findById(idValue);
            return cliente.map(clienteMapper::toDtoDirecto).orElse(Optional.empty());
        })
        .orElse(Optional.empty());
    }
    
    /**
     * Obtiene un cliente por su DNI
     * Utilizando programación funcional con Optional
     */
    @Override
    public Optional<ClienteDTO> obtenerClientePorDni(Optional<String> dni) {
        return dni.map(dniValue -> {
            Optional<Cliente> cliente = clienteRepository.findByDni(dniValue);
            return cliente.map(clienteMapper::toDtoDirecto).orElse(Optional.empty());
        })
        .orElse(Optional.empty());
    }
    
    /**
     * Obtiene todos los clientes
     * Utilizando programación funcional con Optional
     */
    @Override
    public Optional<List<ClienteDTO>> obtenerTodosLosClientes() {
        return clienteRepository.findAll()
                .flatMap(clienteMapper::toDtoList);
    }
    
    /**
     * Actualiza la información de un cliente existente
     * Utilizando programación funcional con Optional y composición de funciones
     */
    @Override
    public Optional<ClienteDTO> actualizarCliente(Long id, Optional<ClienteDTO> clienteDTO) {
        Function<Cliente, Cliente> actualizarDatos = clienteExistente -> 
            clienteDTO.flatMap(clienteMapper::toEntityDirecto)
                .map(clienteNuevo -> clienteExistente.withDatosContacto(
                    Optional.ofNullable(clienteNuevo.getEmail()),
                    Optional.ofNullable(clienteNuevo.getTelefono()),
                    Optional.ofNullable(clienteNuevo.getDireccion())
                ))
                .orElse(clienteExistente);
        
        return actualizarClienteGenerico(id, actualizarDatos);
    }
    
    /**
     * Actualiza el email de un cliente
     * Utilizando enfoque funcional con map y flatMap
     */
    @Override
    public Optional<ClienteDTO> actualizarEmailCliente(Long id, Optional<String> nuevoEmail) {
        return actualizarClienteGenerico(
            id, 
            cliente -> cliente.withEmail(nuevoEmail.orElse(cliente.getEmail()))
        );
    }
    
    /**
     * Actualiza el teléfono de un cliente
     * Utilizando enfoque funcional con map y flatMap
     */
    @Override
    public Optional<ClienteDTO> actualizarTelefonoCliente(Long id, Optional<String> nuevoTelefono) {
        return actualizarClienteGenerico(
            id,
            cliente -> cliente.withTelefono(nuevoTelefono.orElse(cliente.getTelefono()))
        );
    }
    
    /**
     * Actualiza la dirección de un cliente
     * Utilizando enfoque funcional con map y flatMap
     */
    @Override
    public Optional<ClienteDTO> actualizarDireccionCliente(Long id, Optional<String> nuevaDireccion) {
        return actualizarClienteGenerico(
            id,
            cliente -> cliente.withDireccion(nuevaDireccion.orElse(cliente.getDireccion()))
        );
    }
    
    /**
     * Elimina un cliente por su ID
     * Utilizando programación funcional sin if explícitos
     */
    @Override
    public boolean eliminarCliente(Optional<Long> id) {
        return id
                .flatMap(clienteRepository::deleteById)
                .isPresent();
    }
} 
```

*[El asistente lee el archivo CuentaServiceImpl.java]*

```
Contents of src/main/java/com/mibanco/service/impl/CuentaServiceImpl.java, lines 1-174 (entire file):
```
```java
package com.mibanco.service.impl;

import com.mibanco.dto.CuentaDTO;
import com.mibanco.dto.mapper.CuentaMapper;
import com.mibanco.model.Cuenta;
import com.mibanco.model.enums.TipoCuenta;
import com.mibanco.repository.CuentaRepository;
import com.mibanco.service.CuentaService;
import com.mibanco.config.factory.RepositoryFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Implementación de la interfaz de servicio para Cuentas
 * Utilizando estrictamente un enfoque funcional con Optional y streams
 */
public class CuentaServiceImpl implements CuentaService {
    
    private final CuentaRepository cuentaRepository;
    private final CuentaMapper cuentaMapper;
    
    /**
     * Constructor para inyección de dependencias
     * @param cuentaMapper Mapper para conversión entre entidad y DTO
     */
    public CuentaServiceImpl(CuentaMapper cuentaMapper) {
        // Utilizamos la factory para obtener la instancia única del repositorio
        this.cuentaRepository = RepositoryFactory.getCuentaRepository();
        this.cuentaMapper = cuentaMapper;
    }
    
    /**
     * Método privado que implementa el flujo común de actualización de cuentas
     * @param numeroCuenta Número de la cuenta a actualizar
     * @param actualizarDatos Función que define cómo actualizar los datos de la cuenta
     * @return Optional con el DTO de la cuenta actualizada
     */
    private Optional<CuentaDTO> actualizarCuentaGenerica(String numeroCuenta, Function<Cuenta, Cuenta> actualizarDatos) {
        return Optional.ofNullable(numeroCuenta)
                .flatMap(numero -> cuentaRepository.findByNumero(Optional.of(numero)))
                .map(actualizarDatos)
                .flatMap(cuenta -> cuentaRepository.save(Optional.of(cuenta)))
                .flatMap(cuentaMapper::toDtoDirecto);
    }
    
    /**
     * Crea una nueva cuenta en el sistema
     * Utilizando programación funcional con Optional y composición de funciones
     */
    @Override
    public Optional<CuentaDTO> crearCuenta(Optional<CuentaDTO> cuentaDTO) {
        return cuentaDTO
            .map(dto -> {
                Optional<Cuenta> entidad = cuentaMapper.toEntity(Optional.of(dto));
                if (!entidad.isPresent()) {
                    return Optional.<CuentaDTO>empty();
                }
                
                Optional<Cuenta> guardada = cuentaRepository.save(entidad);
                return cuentaMapper.toDto(guardada);
            })
            .orElse(Optional.empty());
    }
    
    /**
     * Obtiene una cuenta por su número
     * Utilizando programación funcional con Optional
     */
    @Override
    public Optional<CuentaDTO> obtenerCuentaPorNumero(Optional<String> numeroCuenta) {
        return numeroCuenta.flatMap(numero -> {
            Optional<Cuenta> cuenta = cuentaRepository.findByNumero(Optional.of(numero));
            return cuenta.flatMap(cuentaMapper::toDtoDirecto);
        });
    }
    
    /**
     * Obtiene todas las cuentas de un titular
     * Utilizando programación funcional con Optional
     */
    @Override
    public Optional<List<CuentaDTO>> obtenerCuentasPorTitular(Optional<Long> idTitular) {
        return idTitular.flatMap(id -> {
            Optional<List<Cuenta>> cuentas = cuentaRepository.findByTitularId(Optional.of(id));
            return cuentaMapper.toDtoList(cuentas);
        });
    }
    
    /**
     * Obtiene todas las cuentas de un tipo específico
     * Utilizando programación funcional con Optional
     */
    @Override
    public Optional<List<CuentaDTO>> obtenerCuentasPorTipo(Optional<TipoCuenta> tipo) {
        return tipo.flatMap(tipoCuenta -> {
            Optional<List<Cuenta>> cuentas = cuentaRepository.findByTipo(Optional.of(tipoCuenta));
            return cuentaMapper.toDtoList(cuentas);
        });
    }
    
    /**
     * Obtiene todas las cuentas del sistema
     * Utilizando programación funcional con Optional
     */
    @Override
    public Optional<List<CuentaDTO>> obtenerTodasLasCuentas() {
        return cuentaRepository.findAll()
                .flatMap(cuentaMapper::toDtoList);
    }
    
    /**
     * Obtiene todas las cuentas activas
     * Utilizando programación funcional con Optional
     */
    @Override
    public Optional<List<CuentaDTO>> obtenerCuentasActivas() {
        return cuentaRepository.findAllActivas()
                .flatMap(cuentaMapper::toDtoList);
    }
    
    /**
     * Actualiza el saldo de una cuenta
     * Utilizando enfoque funcional con map y flatMap
     */
    @Override
    public Optional<CuentaDTO> actualizarSaldoCuenta(String numeroCuenta, Optional<BigDecimal> nuevoSaldo) {
        return actualizarCuentaGenerica(
            numeroCuenta,
            cuenta -> cuenta.withSaldo(nuevoSaldo.orElse(cuenta.getSaldo()))
        );
    }
    
    /**
     * Activa o desactiva una cuenta
     * Utilizando enfoque funcional con map y flatMap
     */
    @Override
    public Optional<CuentaDTO> actualizarEstadoCuenta(String numeroCuenta, Optional<Boolean> activa) {
        return actualizarCuentaGenerica(
            numeroCuenta,
            cuenta -> cuenta.withActiva(activa.orElse(cuenta.isActiva()))
        );
    }
    
    /**
     * Actualiza múltiples campos de una cuenta
     * Utilizando programación funcional con Optional y composición de funciones
     */
    @Override
    public Optional<CuentaDTO> actualizarCuenta(String numeroCuenta, Optional<CuentaDTO> cuentaDTO) {
        Function<Cuenta, Cuenta> actualizarDatos = cuentaExistente -> 
            cuentaDTO.flatMap(cuentaMapper::toEntityDirecto)
                .map(cuentaNueva -> cuentaExistente.withActualizaciones(
                    Optional.ofNullable(cuentaNueva.getSaldo()),
                    Optional.ofNullable(cuentaNueva.isActiva())
                ))
                .orElse(cuentaExistente);
        
        return actualizarCuentaGenerica(numeroCuenta, actualizarDatos);
    }
    
    /**
     * Elimina una cuenta
     * Utilizando programación funcional sin if explícitos
     */
    @Override
    public boolean eliminarCuenta(Optional<String> numeroCuenta) {
        return numeroCuenta
                .map(numero -> cuentaRepository.deleteByNumero(Optional.of(numero)))
                .isPresent();
    }
} 
```

```
Sí, tienes toda la razón, German. Definitivamente deberíamos estar utilizando la `RepositoryFactory` para inyectar los repositorios en nuestros servicios.

Actualmente, las clases `ClienteServiceImpl` y `CuentaServiceImpl` reciben los repositorios a través de sus constructores, pero no están aprovechando la factory que ya está implementada en el proyecto.

Para seguir las mejores prácticas y los patrones de diseño establecidos en el proyecto, deberíamos modificar ambas implementaciones de servicio para utilizar la factory. Esto nos permitiría:

1. Mantener la coherencia en la creación de objetos
2. Aprovechar el patrón Singleton implementado en la factory
3. Asegurar que las dependencias entre repositorios se manejen correctamente

Propongo que modifiquemos ambas implementaciones. ¿Quieres que realice los cambios necesarios en `ClienteServiceImpl` y `CuentaServiceImpl` para utilizar correctamente la `RepositoryFactory`?
```

## Usuario
```
dale
```

## Asistente
```
Procederé a modificar ambas implementaciones para usar la factory. Empecemos con `ClienteServiceImpl`:
```

*[El asistente modifica ClienteServiceImpl.java]*

```
The apply model made the following changes to the file:

```
```diff
  import com.mibanco.repository.ClienteRepository;
  import com.mibanco.service.ClienteService;
+ import com.mibanco.config.factory.RepositoryFactory;
  
  import java.util.List;
...
      /**
       * Constructor para inyección de dependencias
-      * @param clienteRepository Repositorio de clientes
       * @param clienteMapper Mapper para conversión entre entidad y DTO
       */
-     public ClienteServiceImpl(ClienteRepository clienteRepository, ClienteMapper clienteMapper) {
-         this.clienteRepository = clienteRepository;
+     public ClienteServiceImpl(ClienteMapper clienteMapper) {
+         // Utilizamos la factory para obtener la instancia única del repositorio
+         this.clienteRepository = RepositoryFactory.getClienteRepository();
          this.clienteMapper = clienteMapper;
      }
```

```
Ahora modifiquemos `CuentaServiceImpl` de la misma manera:
```

*[El asistente modifica CuentaServiceImpl.java]*

```
The apply model made the following changes to the file:

```
```diff
  import com.mibanco.repository.CuentaRepository;
  import com.mibanco.service.CuentaService;
+ import com.mibanco.config.factory.RepositoryFactory;
  
  import java.math.BigDecimal;
...
      /**
       * Constructor para inyección de dependencias
-      * @param cuentaRepository Repositorio de cuentas
       * @param cuentaMapper Mapper para conversión entre entidad y DTO
       */
-     public CuentaServiceImpl(CuentaRepository cuentaRepository, CuentaMapper cuentaMapper) {
-         this.cuentaRepository = cuentaRepository;
+     public CuentaServiceImpl(CuentaMapper cuentaMapper) {
+         // Utilizamos la factory para obtener la instancia única del repositorio
+         this.cuentaRepository = RepositoryFactory.getCuentaRepository();
          this.cuentaMapper = cuentaMapper;
      }
```

*[El asistente busca código que pueda hacer referencia a estos constructores]*

```
No matches found.
```

```
¡Listo, German! He modificado ambas implementaciones para utilizar correctamente la `RepositoryFactory`:

1. En `ClienteServiceImpl`, ahora el constructor solo recibe el `ClienteMapper` y utiliza `RepositoryFactory.getClienteRepository()` para obtener la instancia única del repositorio.

2. En `CuentaServiceImpl`, he realizado el mismo cambio, utilizando `RepositoryFactory.getCuentaRepository()` para obtener la instancia del repositorio.

Estos cambios nos permiten beneficiarnos de varias ventajas:

1. Aprovechamos el patrón Singleton implementado en la factory para tener una única instancia de cada repositorio.
2. Aseguramos que todas las dependencias entre repositorios (como el `AuditoriaRepository` que se inyecta en los repositorios) se manejen correctamente.
3. Centralizamos la creación de instancias, lo que facilita futuras modificaciones.

¿Quieres que compruebe si hay otras clases en el proyecto que deberían estar utilizando la factory o quieres crear una factory similar para los servicios?
```
