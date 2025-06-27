package com.mibanco.modelo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.time.LocalDate;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para la clase Cliente
 * Estos tests verifican que los métodos de Cliente funcionen correctamente
 */
public class ClienteTest {
    
    private Cliente cliente;
    
    /**
     * Se ejecuta antes de cada test para preparar los datos
     */
    @BeforeEach
    public void setUp() {
        // Crear un cliente de prueba
        cliente = Cliente.of(1L, "Juan", "Pérez", "12345678A", 
                           LocalDate.of(1990, 5, 15), "juan.perez@email.com", 
                           "123456789", "Calle Mayor 123");
    }
    
    /**
     * Test 1: Verificar que el método factory 'of' funciona correctamente
     */
    @Test
    public void testMetodoFactory() {
        // Arrange
        Long id = 2L;
        String nombre = "María";
        String apellido = "García";
        String dni = "87654321B";
        LocalDate fechaNacimiento = LocalDate.of(1985, 8, 20);
        String email = "maria.garcia@email.com";
        String telefono = "987654321";
        String direccion = "Avenida Principal 456";
        
        // Act
        Cliente nuevoCliente = Cliente.of(id, nombre, apellido, dni, fechaNacimiento, email, telefono, direccion);
        
        // Assert
        assertEquals(id, nuevoCliente.getId());
        assertEquals(nombre, nuevoCliente.getNombre());
        assertEquals(apellido, nuevoCliente.getApellido());
        assertEquals(dni, nuevoCliente.getDni());
        assertEquals(fechaNacimiento, nuevoCliente.getFechaNacimiento());
        assertEquals(email, nuevoCliente.getEmail());
        assertEquals(telefono, nuevoCliente.getTelefono());
        assertEquals(direccion, nuevoCliente.getDireccion());
    }
    
    /**
     * Test 2: Verificar que el método getId() devuelve el ID correcto
     */
    @Test
    public void testGetId() {
        // Assert
        assertEquals(1L, cliente.getId());
    }
    
    /**
     * Test 3: Verificar que se puede actualizar el email
     */
    @Test
    public void testActualizarEmail() {
        // Arrange
        String nuevoEmail = "juan.nuevo@email.com";
        
        // Act
        Cliente clienteActualizado = cliente.conEmail(nuevoEmail);
        
        // Assert
        assertEquals(nuevoEmail, clienteActualizado.getEmail());
        // Verificar que el cliente original no cambió (inmutabilidad)
        assertEquals("juan.perez@email.com", cliente.getEmail());
    }
    
    /**
     * Test 4: Verificar que se puede actualizar el teléfono
     */
    @Test
    public void testActualizarTelefono() {
        // Arrange
        String nuevoTelefono = "555123456";
        
        // Act
        Cliente clienteActualizado = cliente.conTelefono(nuevoTelefono);
        
        // Assert
        assertEquals(nuevoTelefono, clienteActualizado.getTelefono());
        // Verificar que el cliente original no cambió
        assertEquals("123456789", cliente.getTelefono());
    }
    
    /**
     * Test 5: Verificar que se puede actualizar la dirección
     */
    @Test
    public void testActualizarDireccion() {
        // Arrange
        String nuevaDireccion = "Nueva Calle 789";
        
        // Act
        Cliente clienteActualizado = cliente.conDireccion(nuevaDireccion);
        
        // Assert
        assertEquals(nuevaDireccion, clienteActualizado.getDireccion());
        // Verificar que el cliente original no cambió
        assertEquals("Calle Mayor 123", cliente.getDireccion());
    }
    
    /**
     * Test 6: Verificar que se pueden actualizar múltiples campos a la vez
     */
    @Test
    public void testActualizarMultiplesCampos() {
        // Arrange
        String nuevoEmail = "juan.actualizado@email.com";
        String nuevoTelefono = "999888777";
        String nuevaDireccion = "Dirección Actualizada 999";
        
        // Act
        Cliente clienteActualizado = cliente.conDatosContacto(
            Optional.of(nuevoEmail), 
            Optional.of(nuevoTelefono), 
            Optional.of(nuevaDireccion)
        );
        
        // Assert
        assertEquals(nuevoEmail, clienteActualizado.getEmail());
        assertEquals(nuevoTelefono, clienteActualizado.getTelefono());
        assertEquals(nuevaDireccion, clienteActualizado.getDireccion());
        
        // Verificar que los campos no modificados siguen igual
        assertEquals(cliente.getNombre(), clienteActualizado.getNombre());
        assertEquals(cliente.getApellido(), clienteActualizado.getApellido());
        assertEquals(cliente.getDni(), clienteActualizado.getDni());
    }
    
    /**
     * Test 7: Verificar que los métodos individuales funcionan correctamente
     * (diferente al método en bloque conDatosContacto)
     */
    @Test
    public void testMetodosIndividuales() {
        // Test conEmail individual
        Cliente clienteConNuevoEmail = cliente.conEmail("nuevo@email.com");
        assertEquals("nuevo@email.com", clienteConNuevoEmail.getEmail());
        assertEquals(cliente.getTelefono(), clienteConNuevoEmail.getTelefono());
        assertEquals(cliente.getDireccion(), clienteConNuevoEmail.getDireccion());
        
        // Test conTelefono individual
        Cliente clienteConNuevoTelefono = cliente.conTelefono("999999999");
        assertEquals(cliente.getEmail(), clienteConNuevoTelefono.getEmail());
        assertEquals("999999999", clienteConNuevoTelefono.getTelefono());
        assertEquals(cliente.getDireccion(), clienteConNuevoTelefono.getDireccion());
        
        // Test conDireccion individual
        Cliente clienteConNuevaDireccion = cliente.conDireccion("Nueva Dirección 456");
        assertEquals(cliente.getEmail(), clienteConNuevaDireccion.getEmail());
        assertEquals(cliente.getTelefono(), clienteConNuevaDireccion.getTelefono());
        assertEquals("Nueva Dirección 456", clienteConNuevaDireccion.getDireccion());
    }
    
    /**
     * Test 8: Verificar que el método obtenerCamposRequeridos devuelve la lista correcta
     */
    @Test
    public void testObtenerCamposRequeridos() {
        // Act
        var camposRequeridos = Cliente.obtenerCamposRequeridos();
        
        // Assert
        assertTrue(camposRequeridos.contains("nombre"));
        assertTrue(camposRequeridos.contains("apellido"));
        assertTrue(camposRequeridos.contains("dni"));
        assertTrue(camposRequeridos.contains("email"));
        assertTrue(camposRequeridos.contains("telefono"));
        assertTrue(camposRequeridos.contains("direccion"));
        assertTrue(camposRequeridos.contains("fechaNacimiento"));
        assertEquals(7, camposRequeridos.size());
    }
} 