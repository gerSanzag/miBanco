package com.mibanco.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.mibanco.modelo.enums.TipoCuenta;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests para la clase Cuenta")
class CuentaTest {

    Cliente cliente;
    Long numeroCuenta = 167384367890L;
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
                
         .titular(titular)
         .tipo(tipo)
         .fechaCreacion(fechaCreacion)
         .saldoInicial(saldoInicial)
         .saldo(saldo)
         .activa(activa)
         .build();

        // Assert (Verificar)
        assertThat(cuentaBuilder).isNotNull();

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
@DisplayName("Debería asignar saldoInicial automáticamente cuando es null")
void deberiaAsignarSaldoInicialCuandoEsNull() {
    
    // Arrange (Preparar)
    
    // Act (Actuar)
    Cuenta cuenta = Cuenta.of(numeroCuenta, titular, tipo, null, fechaCreacion, activa);

    // Assert (Verificar)
    assertThat(cuenta).isNotNull();
    assertThat(cuenta.getNumeroCuenta()).isEqualTo(numeroCuenta);
    assertThat(cuenta.getTitular()).isEqualTo(titular);
    assertThat(cuenta.getTipo()).isEqualTo(tipo);
    assertThat(cuenta.isActiva()).isEqualTo(activa);


    assertThat(cuenta.getSaldoInicial()).isEqualTo(BigDecimal.ZERO);
    assertThat(cuenta.getSaldo()).isEqualTo(BigDecimal.ZERO);
}

@Test
@DisplayName("Debería asignar fechaCreacion automática cuando es null")
void deberiaAsignarFechaCreacionCuandoEsNull() {
    // Arrange (Preparar)
    
    // Act (Actuar)
    Cuenta cuenta = Cuenta.of(numeroCuenta, titular, tipo, saldoInicial, null, activa);

    // Assert (Verificar)
    assertThat(cuenta).isNotNull();
    assertThat(cuenta.getNumeroCuenta()).isEqualTo(numeroCuenta);
    assertThat(cuenta.getTitular()).isEqualTo(titular);
    assertThat(cuenta.getTipo()).isEqualTo(tipo);
    assertThat(cuenta.getSaldoInicial()).isEqualTo(saldoInicial);
    assertThat(cuenta.getSaldo()).isEqualTo(saldo);
    assertThat(cuenta.isActiva()).isEqualTo(activa);

    assertThat(cuenta.getFechaCreacion()).isAfter(LocalDateTime.now().minusDays(1));
    assertThat(cuenta.getFechaCreacion()).isBefore(LocalDateTime.now().plusDays(1));
    
}

@Test
@DisplayName("Debería devolver el identificador de la cuenta")
void deberiaDevolverElIdentificadorDeLaCuenta() {
    
    // Arrange (Preparar)

    // Act (Actuar)
    
    // Assert (Verificar)
    assertThat(cuenta.getId()).isEqualTo(numeroCuenta);
    
}

@Test
void deberiaModificarElSaldoDeFormaInmutable() {
    // Arrange
    BigDecimal saldoOriginal = cuenta.getSaldo();
    BigDecimal nuevoSaldo = BigDecimal.valueOf(2000.0);
    
    // Act
    Cuenta cuentaConNuevoSaldo = cuenta.conSaldo(nuevoSaldo);
    
    // Assert
    assertThat(cuentaConNuevoSaldo.getSaldo()).isEqualTo(nuevoSaldo);
    assertThat(cuenta.getSaldo()).isEqualTo(saldoOriginal); // ← Original no cambia
    assertThat(cuentaConNuevoSaldo).isNotSameAs(cuenta); // ← Son objetos diferentes
}   
@Test
@DisplayName("Deberia modificar el estado de activacion de la cuenta")
void deberiaModificarElEstadoDeActivacionDeLaCuenta() {
    // Arrange (Preparar)
    
    // Act (Actuar)
    Cuenta cuentaConNuevoEstado = cuenta.conActiva(false);
    
    assertThat(cuentaConNuevoEstado.isActiva()).isFalse();
    
}
@Test
@DisplayName("Deberia modificar el saldo y el estado de activacion en bloque")
void deberiaModificarElSaldoYElEstadoDeActivacionEnBloque() {
    // Arrange (Preparar)
    
    // Act (Actuar)
    Cuenta cuentaConNuevoSaldoYTitular = cuenta.conActualizaciones(Optional.of(BigDecimal.valueOf(2000.0)), Optional.of(false));
    // Assert (Verificar)
    assertThat(cuentaConNuevoSaldoYTitular.getSaldo()).isNotEqualTo(cuenta.getSaldo());
    assertThat(cuentaConNuevoSaldoYTitular.isActiva()).isNotEqualTo(cuenta.isActiva());
    
}
}