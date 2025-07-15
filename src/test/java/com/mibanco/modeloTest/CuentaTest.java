package com.mibanco.modeloTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.mibanco.modelo.Cliente;
import com.mibanco.modelo.Cuenta;
import com.mibanco.modelo.enums.TipoCuenta;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests para la clase Cuenta")
class CuentaTest {

    Cliente cliente;
    String numeroCuenta = "ES3412345678901234567890";
    Cliente titular ;
    TipoCuenta tipo = TipoCuenta.AHORRO;
    LocalDateTime fechaCreacion = LocalDateTime.now();
    BigDecimal saldoInicial = BigDecimal.valueOf(1000.0);
    BigDecimal saldo = saldoInicial;
    boolean activa = true;
    Cuenta cuenta;

    @BeforeEach
    void setUp() {
        cliente = Cliente.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Perez")
                .dni("1234567890")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .email("juan.perez@gmail.com")
                .telefono("1234567890")
                .direccion("Calle 123, Ciudad, País")
                .build();

        titular = cliente;
        cuenta = Cuenta.of(numeroCuenta, titular, tipo, saldoInicial, fechaCreacion, activa);
    }

    @Test
    @DisplayName("Debería crear una cuenta con datos válidos")
    void deberiaCrearCuentaConDatosValidos() {
        
         // Act (Actuar)
         Cuenta cuentaBuilder = Cuenta.builder()
                
         .numeroCuenta(numeroCuenta)
         .titular(titular)
         .tipo(tipo)
         .fechaCreacion(fechaCreacion)
         .saldoInicial(saldoInicial)
         .saldo(saldo)
         .activa(activa)
         .build();

        // Assert (Verificar)
        assertThat(cuentaBuilder).isNotNull();
        assertThat(cuentaBuilder.getNumeroCuenta()).isEqualTo(numeroCuenta);
        assertThat(cuentaBuilder.getTitular()).isEqualTo(titular);
        assertThat(cuentaBuilder.getTipo()).isEqualTo(tipo);
        assertThat(cuentaBuilder.getFechaCreacion()).isEqualTo(fechaCreacion);
        assertThat(cuentaBuilder.getSaldoInicial()).isEqualTo(saldoInicial);
        assertThat(cuentaBuilder.getSaldo()).isEqualTo(saldo);
        assertThat(cuentaBuilder.isActiva()).isEqualTo(activa);
       
    }


@Test
@DisplayName("Debería crear una cuenta con datos válidos usando el método factory of()")
void deberiaCrearCuentaConDatosValidosFactoryOf() {
    // Arrange (Preparar)

    // Act (Actuar)
    

    // Assert (Verificar)
    assertThat(cuenta).isNotNull();
    assertThat(cuenta.getNumeroCuenta()).isEqualTo(numeroCuenta);
    assertThat(cuenta.getTitular()).isEqualTo(titular);
    assertThat(cuenta.getTipo()).isEqualTo(tipo);
    assertThat(cuenta.getFechaCreacion()).isEqualTo(fechaCreacion);
    assertThat(cuenta.getSaldoInicial()).isEqualTo(saldoInicial);
    assertThat(cuenta.isActiva()).isEqualTo(activa);
    
}

@Test
@DisplayName("Debería validar el formato del número de cuenta IBAN")
void deberiaValidarFormatoNumeroCuentaIBAN() {
    // Assert (Verificar)
    assertThat(cuenta.getNumeroCuenta()).isNotNull();
    assertThat(cuenta.getNumeroCuenta()).startsWith("ES34");
    assertThat(cuenta.getNumeroCuenta()).hasSize(24);
    assertThat(cuenta.getNumeroCuenta()).matches("ES34\\d{20}");
}

@Test
@DisplayName("Debería manejar número de cuenta nulo")
void deberiaManejarNumeroCuentaNulo() {
    // Arrange (Preparar)
    Cuenta cuentaBuilder = Cuenta.builder()
            .numeroCuenta(null)
            .titular(titular)
            .tipo(tipo)
            .fechaCreacion(fechaCreacion)
            .saldoInicial(saldoInicial)
            .saldo(saldo)
            .activa(activa)
            .build();
    
    // Assert (Verificar)
    assertThat(cuentaBuilder.getNumeroCuenta()).isNull();
}

@Test
@DisplayName("Debería mantener inmutabilidad total")
void deberiaMantenerInmutabilidadTotal() {
    // Arrange (Preparar)
    BigDecimal saldoOriginal = cuenta.getSaldo();
    boolean activaOriginal = cuenta.isActiva();
    
    // Act (Actuar) - Crear una nueva cuenta con diferentes valores
    Cuenta cuentaNueva = Cuenta.of(
        cuenta.getNumeroCuenta() + "1",
        cuenta.getTitular(),
        cuenta.getTipo(),
        BigDecimal.valueOf(3000.0),
        cuenta.getFechaCreacion(),
        false
    );
    
    // Assert (Verificar)
    assertThat(cuentaNueva.getSaldo()).isEqualTo(BigDecimal.valueOf(3000.0));
    assertThat(cuentaNueva.isActiva()).isFalse();
    
    // Verificar que el original no cambió
    assertThat(cuenta.getSaldo()).isEqualTo(saldoOriginal);
    assertThat(cuenta.isActiva()).isEqualTo(activaOriginal);
    assertThat(cuentaNueva).isNotSameAs(cuenta);
}



@Test
@DisplayName("Debería crear cuenta con saldo diferente usando factory method")
void deberiaCrearCuentaConSaldoDiferenteUsandoFactoryMethod() {
    // Arrange
    BigDecimal saldoOriginal = cuenta.getSaldo();
    BigDecimal nuevoSaldo = BigDecimal.valueOf(2000.0);
    
    // Act
    Cuenta cuentaConNuevoSaldo = Cuenta.of(
        cuenta.getNumeroCuenta(),
        cuenta.getTitular(),
        cuenta.getTipo(),
        nuevoSaldo,
        cuenta.getFechaCreacion(),
        cuenta.isActiva()
    );
    
    // Assert
    assertThat(cuentaConNuevoSaldo.getSaldo()).isEqualTo(nuevoSaldo);
    assertThat(cuenta.getSaldo()).isEqualTo(saldoOriginal); // ← Original no cambia
    assertThat(cuentaConNuevoSaldo).isNotSameAs(cuenta); // ← Son objetos diferentes
}   
@Test
@DisplayName("Debería crear cuenta con estado de activación diferente usando factory method")
void deberiaCrearCuentaConEstadoDeActivacionDiferenteUsandoFactoryMethod() {
    // Arrange (Preparar)
    boolean estadoOriginal = cuenta.isActiva();
    
    // Act (Actuar)
    Cuenta cuentaConNuevoEstado = Cuenta.of(
        cuenta.getNumeroCuenta(),
        cuenta.getTitular(),
        cuenta.getTipo(),
        cuenta.getSaldoInicial(),
        cuenta.getFechaCreacion(),
        false
    );
    
    // Assert (Verificar)
    assertThat(cuentaConNuevoEstado.isActiva()).isFalse();
    assertThat(cuenta.isActiva()).isEqualTo(estadoOriginal); // Original no cambia
    assertThat(cuentaConNuevoEstado).isNotSameAs(cuenta); // Son objetos diferentes
}
@Test
@DisplayName("Debería crear cuenta con múltiples cambios usando factory method")
void deberiaCrearCuentaConMultiplesCambiosUsandoFactoryMethod() {
    // Arrange (Preparar)
    BigDecimal saldoOriginal = cuenta.getSaldo();
    boolean activaOriginal = cuenta.isActiva();
    
    // Act (Actuar)
    Cuenta cuentaConNuevoSaldoYEstado = Cuenta.of(
        cuenta.getNumeroCuenta(),
        cuenta.getTitular(),
        cuenta.getTipo(),
        BigDecimal.valueOf(2000.0),
        cuenta.getFechaCreacion(),
        false
    );
    
    // Assert (Verificar)
    assertThat(cuentaConNuevoSaldoYEstado.getSaldo()).isEqualTo(BigDecimal.valueOf(2000.0));
    assertThat(cuentaConNuevoSaldoYEstado.isActiva()).isFalse();
    assertThat(cuenta.getSaldo()).isEqualTo(saldoOriginal); // Original no cambia
    assertThat(cuenta.isActiva()).isEqualTo(activaOriginal); // Original no cambia
    assertThat(cuentaConNuevoSaldoYEstado).isNotSameAs(cuenta); // Son objetos diferentes
}
}