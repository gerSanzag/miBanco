package com.mibanco.servicio.interna;

import com.mibanco.dto.ClienteDTO;
import com.mibanco.dto.CuentaDTO;
import com.mibanco.servicio.ClienteServicio;
import com.mibanco.servicio.TransaccionOperacionesServicio;
import com.mibanco.servicio.interna.ServicioFactoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class CuentaServicioImplTest {
    private CuentaServicioImpl cuentaServicio;
    private ClienteServicio clienteServicio;
    private TransaccionOperacionesServicio transaccionServicio;

    @BeforeEach
    public void setUp() {
        cuentaServicio = (CuentaServicioImpl) ServicioFactoria.obtenerInstancia().obtenerServicioCuenta();
        clienteServicio = ServicioFactoria.obtenerInstancia().obtenerServicioCliente();
        transaccionServicio = ServicioFactoria.obtenerInstancia().obtenerServicioTransaccionOperaciones();
    }

    @Test
    public void testCrearCuentaConIngresoInicial_Exito() {
        // 1. Crear un cliente de prueba usando datos crudos
        Map<String, String> datosCliente = new HashMap<>();
        datosCliente.put("id", "1");
        datosCliente.put("nombre", "Juan");
        datosCliente.put("apellido", "Pérez");
        datosCliente.put("dni", "12345678A");
        datosCliente.put("fechaNacimiento", "1990-05-15");
        datosCliente.put("email", "juan@email.com");
        datosCliente.put("telefono", "123456789");
        datosCliente.put("direccion", "Calle 1");
        clienteServicio.crearClienteDto(datosCliente);

        // 2. Preparar los datos crudos para la cuenta
        Map<String, String> datosCrudos = new HashMap<>();
        datosCrudos.put("idTitular", "1");
        datosCrudos.put("tipo", "AHORRO");
        // Puedes agregar más campos si tu builder los requiere

        // 3. Monto inicial válido
        BigDecimal montoInicial = new BigDecimal("100.00");

        // 4. Llamar al método de integración
        Optional<CuentaDTO> resultado = cuentaServicio.crearCuentaConIngresoInicial(
            datosCrudos, montoInicial, transaccionServicio
        );

        // 5. Verificar que la cuenta se creó correctamente
        assertTrue(resultado.isPresent(), "La cuenta debería haberse creado correctamente");
        CuentaDTO cuenta = resultado.get();
        assertEquals(montoInicial, cuenta.getSaldoInicial(), "El saldo inicial debe coincidir con el monto ingresado");
        assertEquals(montoInicial, cuenta.getSaldo(), "El saldo debe coincidir con el monto ingresado");
        assertEquals("Juan", cuenta.getTitular().getNombre(), "El titular debe coincidir");
        // (Opcional) Aquí podrías verificar que la transacción de apertura existe
    }
} 