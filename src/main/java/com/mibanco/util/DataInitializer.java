package com.mibanco.util;

import com.mibanco.config.RepositoryConfig;
import com.mibanco.model.Cliente;
import com.mibanco.model.Cuenta;
import com.mibanco.model.Tarjeta;
import com.mibanco.model.Transaccion;
import com.mibanco.model.enums.TipoCuenta;
import com.mibanco.model.enums.TipoTarjeta;
import com.mibanco.model.enums.TipoTransaccion;
import com.mibanco.repository.ClienteRepository;
import com.mibanco.repository.CuentaRepository;
import com.mibanco.repository.TarjetaRepository;
import com.mibanco.repository.TransaccionRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Utilidad para cargar datos iniciales en los repositorios
 * Útil para pruebas y demos
 */
public class DataInitializer {
    
    // Repositorios
    private final ClienteRepository clienteRepository;
    private final CuentaRepository cuentaRepository;
    private final TarjetaRepository tarjetaRepository;
    private final TransaccionRepository transaccionRepository;
    
    // Constructor que obtiene las instancias singleton de los repositorios
    public DataInitializer() {
        this.clienteRepository = RepositoryConfig.getClienteRepository();
        this.cuentaRepository = RepositoryConfig.getCuentaRepository();
        this.tarjetaRepository = RepositoryConfig.getTarjetaRepository();
        this.transaccionRepository = RepositoryConfig.getTransaccionRepository();
    }
    
    /**
     * Carga datos iniciales de ejemplo
     */
    public void initData() {
        // Si ya hay datos cargados, no hacemos nada
        if (clienteRepository.count() > 0) {
            return;
        }
        
        // Crear clientes de ejemplo
        Cliente cliente1 = crearCliente1();
        Cliente cliente2 = crearCliente2();
        
        // Crear cuentas de ejemplo
        Cuenta cuenta1 = crearCuenta1(cliente1);
        Cuenta cuenta2 = crearCuenta2(cliente2);
        
        // Crear tarjetas de ejemplo
        Tarjeta tarjeta1 = crearTarjeta1(cliente1, cuenta1);
        Tarjeta tarjeta2 = crearTarjeta2(cliente2, cuenta2);
        
        // Crear transacciones de ejemplo
        crearTransacciones(cuenta1, cuenta2);
    }
    
    private Cliente crearCliente1() {
        Cliente cliente = Cliente.of(
                null,  // El ID lo generará el repositorio
                "Juan",
                "Pérez",
                "12345678A",
                Optional.of(LocalDate.of(1985, 5, 15)),
                Optional.of("juan.perez@example.com"),
                Optional.of("600111222"),
                Optional.of("Calle Ejemplo 123")
        );
        return clienteRepository.save(cliente);
    }
    
    private Cliente crearCliente2() {
        Cliente cliente = Cliente.of(
                null,  // El ID lo generará el repositorio
                "María",
                "López",
                "87654321B",
                Optional.of(LocalDate.of(1990, 10, 20)),
                Optional.of("maria.lopez@example.com"),
                Optional.of("600333444"),
                Optional.of("Avenida Muestra 456")
        );
        return clienteRepository.save(cliente);
    }
    
    private Cuenta crearCuenta1(Cliente cliente) {
        Cuenta cuenta = Cuenta.of(
                "ES0123456789012345678901",
                cliente,
                TipoCuenta.AHORRO,
                Optional.of(new BigDecimal("1000.00")),
                Optional.of(LocalDateTime.now().minusMonths(3)),
                Optional.of(true)
        );
        return cuentaRepository.save(cuenta);
    }
    
    private Cuenta crearCuenta2(Cliente cliente) {
        Cuenta cuenta = Cuenta.of(
                "ES9876543210987654321098",
                cliente,
                TipoCuenta.CORRIENTE,
                Optional.of(new BigDecimal("2500.50")),
                Optional.of(LocalDateTime.now().minusMonths(1)),
                Optional.of(true)
        );
        return cuentaRepository.save(cuenta);
    }
    
    private Tarjeta crearTarjeta1(Cliente cliente, Cuenta cuenta) {
        Tarjeta tarjeta = Tarjeta.of(
                "4111111111111111",
                cliente,
                cuenta.getNumeroCuenta(),
                TipoTarjeta.DEBITO,
                Optional.of(LocalDate.now().plusYears(3)),
                Optional.of("123"),
                Optional.of(true)
        );
        return tarjetaRepository.save(tarjeta);
    }
    
    private Tarjeta crearTarjeta2(Cliente cliente, Cuenta cuenta) {
        Tarjeta tarjeta = Tarjeta.of(
                "5555555555554444",
                cliente,
                cuenta.getNumeroCuenta(),
                TipoTarjeta.CREDITO,
                Optional.of(LocalDate.now().plusYears(2)),
                Optional.of("456"),
                Optional.of(true)
        );
        return tarjetaRepository.save(tarjeta);
    }
    
    private void crearTransacciones(Cuenta cuenta1, Cuenta cuenta2) {
        // Depósito en cuenta1
        Transaccion transaccion1 = Transaccion.of(
                null,  // El ID lo generará el repositorio
                cuenta1.getNumeroCuenta(),
                null,
                TipoTransaccion.DEPOSITO,
                new BigDecimal("500.00"),
                Optional.of(LocalDateTime.now().minusDays(30)),
                Optional.of("Depósito inicial")
        );
        transaccionRepository.save(transaccion1);
        
        // Transferencia de cuenta1 a cuenta2
        Transaccion transaccion2 = Transaccion.of(
                null,  // El ID lo generará el repositorio
                cuenta1.getNumeroCuenta(),
                cuenta2.getNumeroCuenta(),
                TipoTransaccion.TRANSFERENCIA_ENVIADA,
                new BigDecimal("200.00"),
                Optional.of(LocalDateTime.now().minusDays(15)),
                Optional.of("Transferencia a María")
        );
        transaccionRepository.save(transaccion2);
        
        // Transferencia recibida en cuenta2
        Transaccion transaccion3 = Transaccion.of(
                null,  // El ID lo generará el repositorio
                cuenta2.getNumeroCuenta(),
                cuenta1.getNumeroCuenta(),
                TipoTransaccion.TRANSFERENCIA_RECIBIDA,
                new BigDecimal("200.00"),
                Optional.of(LocalDateTime.now().minusDays(15)),
                Optional.of("Transferencia recibida de Juan")
        );
        transaccionRepository.save(transaccion3);
        
        // Retiro de cuenta2
        Transaccion transaccion4 = Transaccion.of(
                null,  // El ID lo generará el repositorio
                cuenta2.getNumeroCuenta(),
                null,
                TipoTransaccion.RETIRO,
                new BigDecimal("50.00"),
                Optional.of(LocalDateTime.now().minusDays(5)),
                Optional.of("Retiro de efectivo")
        );
        transaccionRepository.save(transaccion4);
    }
} 