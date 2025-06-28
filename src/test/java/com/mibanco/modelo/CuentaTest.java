package com.mibanco.modelo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Optional;
import com.mibanco.modelo.enums.TipoCuenta;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para la clase Cuenta
 * Estos tests verifican que los métodos de Cuenta funcionen correctamente
 */
public class CuentaTest {
    
    private Cuenta cuenta;
    private Cliente cliente;
    
    /**
     * Se ejecuta antes de cada test para preparar los datos
     */
    @BeforeEach
    public void setUp() {
        // Crear un cliente de prueba
        cliente = Cliente.of(1L, "Juan", "Pérez", "12345678A", 
                           LocalDate.now(), "juan.perez@email.com", "123456789", "Calle Principal 123");
        
        // Crear una cuenta de prueba
        cuenta = Cuenta.of(1001L, cliente, TipoCuenta.AHORRO, 
                          BigDecimal.valueOf(1000.00), LocalDateTime.now(), true);
    }
    
    /**
     * Test 1: Verificar que se puede actualizar el saldo correctamente
     */
    @Test
    public void testActualizarSaldo() {
        // Arrange (Preparar)
        BigDecimal nuevoSaldo = BigDecimal.valueOf(1500.00);
        
        // Act (Actuar)
        Cuenta cuentaActualizada = cuenta.conSaldo(nuevoSaldo);
        
        // Assert (Verificar)
        assertEquals(nuevoSaldo, cuentaActualizada.getSaldo());
        // Verificar que la cuenta original no cambió (inmutabilidad)
        assertEquals(BigDecimal.valueOf(1000.00), cuenta.getSaldo());
    }
    
    /**
     * Test 2: Verificar que se puede cambiar el estado activo
     */
    @Test
    public void testCambiarEstadoActivo() {
        // Act
        Cuenta cuentaInactiva = cuenta.conActiva(false);
        
        // Assert
        assertFalse(cuentaInactiva.isActiva());
        // Verificar que la cuenta original no cambió
        assertTrue(cuenta.isActiva());
    }
    
    /**
     * Test 3: Verificar que el método getId() devuelve el número de cuenta
     */
    @Test
    public void testGetId() {
        // Assert
        assertEquals(1001L, cuenta.getId());
    }
    
    /**
     * Test 4: Verificar que se pueden actualizar múltiples campos a la vez
     */
    @Test
    public void testActualizarMultiplesCampos() {
        // Arrange
        BigDecimal nuevoSaldo = BigDecimal.valueOf(2000.00);
        
        // Act
        Cuenta cuentaActualizada = cuenta.conActualizaciones(
            Optional.of(nuevoSaldo), 
            Optional.of(false)
        );
        
        // Assert
        assertEquals(nuevoSaldo, cuentaActualizada.getSaldo());
        assertFalse(cuentaActualizada.isActiva());
    }
    
    /**
     * Test 5: Verificar que el método factory 'of' funciona correctamente
     */
    @Test
    public void testMetodoFactory() {
        // Arrange
        Long numeroCuenta = 2001L;
        TipoCuenta tipo = TipoCuenta.CORRIENTE;
        BigDecimal saldo = BigDecimal.valueOf(500.00);
        LocalDateTime fechaCreacion = LocalDateTime.now();
        
        // Act
        Cuenta nuevaCuenta = Cuenta.of(numeroCuenta, cliente, tipo, saldo, fechaCreacion, true);
        
        // Assert
        assertEquals(numeroCuenta, nuevaCuenta.getNumeroCuenta());
        assertEquals(cliente, nuevaCuenta.getTitular());
        assertEquals(tipo, nuevaCuenta.getTipo());
        assertEquals(saldo, nuevaCuenta.getSaldo());
        assertEquals(fechaCreacion, nuevaCuenta.getFechaCreacion());
        assertTrue(nuevaCuenta.isActiva());
    }
} 