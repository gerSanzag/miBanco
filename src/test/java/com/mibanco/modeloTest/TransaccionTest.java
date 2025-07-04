package com.mibanco.modeloTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.mibanco.modelo.Transaccion;
import com.mibanco.modelo.enums.TipoTransaccion;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests para la clase Transaccion")
class TransaccionTest {

    Long id = 1L;
    Long numeroCuenta = 1234567890L;
    Long numeroCuentaDestino = 9876543210L;
    TipoTransaccion tipo = TipoTransaccion.TRANSFERENCIA_ENVIADA;
    BigDecimal monto = new BigDecimal("1000.50");
    LocalDateTime fecha = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
    String descripcion = "Transferencia entre cuentas";
    Transaccion transaccion;

    @BeforeEach
    void setUp() {
        transaccion = Transaccion.of(id, numeroCuenta, numeroCuentaDestino, tipo, monto, fecha, descripcion);
    }

    @Test
    @DisplayName("Debería crear una transacción con datos válidos usando Builder")
    void deberiaCrearTransaccionConDatosValidosBuilder() {
        // Act (Actuar)
        Transaccion transaccionBuilder = Transaccion.builder()
                .id(id)
                .numeroCuenta(numeroCuenta)
                .numeroCuentaDestino(numeroCuentaDestino)
                .tipo(tipo)
                .monto(monto)
                .fecha(fecha)
                .descripcion(descripcion)
                .build();

        // Assert (Verificar)
        assertThat(transaccionBuilder).isNotNull();
        assertThat(transaccionBuilder.getId()).isEqualTo(id);
        assertThat(transaccionBuilder.getNumeroCuenta()).isEqualTo(numeroCuenta);
        assertThat(transaccionBuilder.getNumeroCuentaDestino()).isEqualTo(numeroCuentaDestino);
        assertThat(transaccionBuilder.getTipo()).isEqualTo(tipo);
        assertThat(transaccionBuilder.getMonto()).isEqualTo(monto);
        assertThat(transaccionBuilder.getFecha()).isEqualTo(fecha);
        assertThat(transaccionBuilder.getDescripcion()).isEqualTo(descripcion);
    }

    @Test
    @DisplayName("Debería crear una transacción con datos válidos usando el método factory of()")
    void deberiaCrearTransaccionConDatosValidosFactoryOf() {
        // Assert (Verificar)
        assertThat(transaccion).isNotNull();
        assertThat(transaccion.getId()).isEqualTo(id);
        assertThat(transaccion.getNumeroCuenta()).isEqualTo(numeroCuenta);
        assertThat(transaccion.getNumeroCuentaDestino()).isEqualTo(numeroCuentaDestino);
        assertThat(transaccion.getTipo()).isEqualTo(tipo);
        assertThat(transaccion.getMonto()).isEqualTo(monto);
        assertThat(transaccion.getFecha()).isEqualTo(fecha);
        assertThat(transaccion.getDescripcion()).isEqualTo(descripcion);
    }

    @Test
    @DisplayName("Debería usar fecha actual cuando fecha es null en factory method")
    void deberiaUsarFechaActualCuandoFechaEsNull() {
        // Arrange (Preparar)
        LocalDateTime antes = LocalDateTime.now();
        
        // Act (Actuar)
        Transaccion transaccionSinFecha = Transaccion.of(id, numeroCuenta, numeroCuentaDestino, tipo, monto, null, descripcion);
        LocalDateTime despues = LocalDateTime.now();
        
        // Assert (Verificar)
        assertThat(transaccionSinFecha.getFecha()).isNotNull();
        assertThat(transaccionSinFecha.getFecha()).isBetween(antes, despues);
    }

    @Test
    @DisplayName("Debería usar descripción vacía cuando descripción es null en factory method")
    void deberiaUsarDescripcionVaciaCuandoDescripcionEsNull() {
        // Act (Actuar)
        Transaccion transaccionSinDescripcion = Transaccion.of(id, numeroCuenta, numeroCuentaDestino, tipo, monto, fecha, null);
        
        // Assert (Verificar)
        assertThat(transaccionSinDescripcion.getDescripcion()).isEqualTo("");
    }

    @Test
    @DisplayName("Debería devolver el identificador de la transacción")
    void deberiaDevolverElIdentificadorDeLaTransaccion() {
        // Assert (Verificar)
        assertThat(transaccion.getId()).isEqualTo(id);
        assertThat(transaccion.getId()).isNotNull();
    }

    @Test
    @DisplayName("Debería devolver el identificador de la transacción cuando es nulo")
    void deberiaDevolverElIdentificadorDeLaTransaccionCuandoEsNulo() {
        // Arrange (Preparar)
        Transaccion transaccionSinId = Transaccion.builder()
                .id(null)
                .numeroCuenta(numeroCuenta)
                .numeroCuentaDestino(numeroCuentaDestino)
                .tipo(tipo)
                .monto(monto)
                .fecha(fecha)
                .descripcion(descripcion)
                .build();

        // Act (Actuar)
        Long id = transaccionSinId.getId();

        // Assert (Verificar)
        assertThat(id).isNull();
    }



    @Test
    @DisplayName("Debería manejar diferentes tipos de transacción")
    void deberiaManejarDiferentesTiposDeTransaccion() {
        // Arrange (Preparar)
        Transaccion transaccionDeposito = Transaccion.of(id, numeroCuenta, null, TipoTransaccion.DEPOSITO, monto, fecha, "Depósito");
        Transaccion transaccionRetiro = Transaccion.of(id, numeroCuenta, null, TipoTransaccion.RETIRO, monto, fecha, "Retiro");
        Transaccion transaccionPago = Transaccion.of(id, numeroCuenta, numeroCuentaDestino, TipoTransaccion.PAGO_SERVICIO, monto, fecha, "Pago");
        
        // Assert (Verificar)
        assertThat(transaccionDeposito.getTipo()).isEqualTo(TipoTransaccion.DEPOSITO);
        assertThat(transaccionRetiro.getTipo()).isEqualTo(TipoTransaccion.RETIRO);
        assertThat(transaccionPago.getTipo()).isEqualTo(TipoTransaccion.PAGO_SERVICIO);
    }

    @Test
    @DisplayName("Debería manejar montos con diferentes precisiones")
    void deberiaManejarMontosConDiferentesPrecisiones() {
        // Arrange (Preparar)
        BigDecimal montoConDecimales = new BigDecimal("1234.567");
        BigDecimal montoEntero = new BigDecimal("1000");
        BigDecimal montoCero = BigDecimal.ZERO;
        
        // Act (Actuar)
        Transaccion transaccionDecimales = Transaccion.of(id, numeroCuenta, numeroCuentaDestino, tipo, montoConDecimales, fecha, descripcion);
        Transaccion transaccionEntero = Transaccion.of(id, numeroCuenta, numeroCuentaDestino, tipo, montoEntero, fecha, descripcion);
        Transaccion transaccionCero = Transaccion.of(id, numeroCuenta, numeroCuentaDestino, tipo, montoCero, fecha, descripcion);
        
        // Assert (Verificar)
        assertThat(transaccionDecimales.getMonto()).isEqualTo(montoConDecimales);
        assertThat(transaccionEntero.getMonto()).isEqualTo(montoEntero);
        assertThat(transaccionCero.getMonto()).isEqualTo(montoCero);
    }

    @Test
    @DisplayName("Debería manejar transacciones sin cuenta destino")
    void deberiaManejarTransaccionesSinCuentaDestino() {
        // Act (Actuar)
        Transaccion transaccionSinDestino = Transaccion.of(id, numeroCuenta, null, TipoTransaccion.DEPOSITO, monto, fecha, descripcion);
        
        // Assert (Verificar)
        assertThat(transaccionSinDestino.getNumeroCuentaDestino()).isNull();
        assertThat(transaccionSinDestino.getNumeroCuenta()).isEqualTo(numeroCuenta);
    }

    @Test
    @DisplayName("Debería mantener inmutabilidad en todos los campos")
    void deberiaMantenerInmutabilidadEnTodosLosCampos() {
        // Arrange (Preparar)
        Transaccion transaccionOriginal = transaccion;
        
        // Act (Actuar) - Crear una nueva transacción con datos diferentes
        Transaccion transaccionNueva = Transaccion.of(999L, 9999999999L, 8888888888L, 
                TipoTransaccion.DEPOSITO, new BigDecimal("9999.99"), LocalDateTime.now(), "Nueva descripción");
        
        // Assert (Verificar) - Original no debe cambiar
        assertThat(transaccionOriginal.getId()).isEqualTo(id);
        assertThat(transaccionOriginal.getNumeroCuenta()).isEqualTo(numeroCuenta);
        assertThat(transaccionOriginal.getNumeroCuentaDestino()).isEqualTo(numeroCuentaDestino);
        assertThat(transaccionOriginal.getTipo()).isEqualTo(tipo);
        assertThat(transaccionOriginal.getMonto()).isEqualTo(monto);
        assertThat(transaccionOriginal.getFecha()).isEqualTo(fecha);
        assertThat(transaccionOriginal.getDescripcion()).isEqualTo(descripcion);
        
        // Verificar que son objetos diferentes
        assertThat(transaccionNueva).isNotSameAs(transaccionOriginal);
        assertThat(transaccionNueva.getId()).isNotEqualTo(transaccionOriginal.getId());
    }
}
