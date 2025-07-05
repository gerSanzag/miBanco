package com.mibanco.modeloTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.mibanco.modelo.Cliente;
import com.mibanco.modelo.Tarjeta;
import com.mibanco.modelo.enums.TipoTarjeta;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests para la clase Tarjeta")
class TarjetaTest {

    Cliente cliente;
    String numero = "1234567890123456";
    Cliente titular;
    String numeroCuentaAsociada = "1234567890";
    TipoTarjeta tipo = TipoTarjeta.CREDITO;
    LocalDate fechaExpiracion = LocalDate.now().plusYears(3);
    String cvv = "123";
    boolean activa = true;
    Tarjeta tarjeta;

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
        tarjeta = Tarjeta.of(numero, titular, numeroCuentaAsociada, tipo, fechaExpiracion, cvv, activa);
    }

    @Test
    @DisplayName("Debería crear una tarjeta con datos válidos")
    void deberiaCrearTarjetaConDatosValidos() {
        // Act (Actuar)
        Tarjeta tarjetaBuilder = Tarjeta.builder()
                .numero(numero)
                .titular(titular)
                .numeroCuentaAsociada(numeroCuentaAsociada)
                .tipo(tipo)
                .fechaExpiracion(fechaExpiracion)
                .cvv(cvv)
                .activa(activa)
                .build();

        // Assert (Verificar)
        assertThat(tarjetaBuilder).isNotNull();
        assertThat(tarjetaBuilder.getNumero()).isEqualTo(numero);
        assertThat(tarjetaBuilder.getTitular()).isEqualTo(titular);
        assertThat(tarjetaBuilder.getNumeroCuentaAsociada()).isEqualTo(numeroCuentaAsociada);
        assertThat(tarjetaBuilder.getTipo()).isEqualTo(tipo);
        assertThat(tarjetaBuilder.getFechaExpiracion()).isEqualTo(fechaExpiracion);
        assertThat(tarjetaBuilder.getCvv()).isEqualTo(cvv);
        assertThat(tarjetaBuilder.isActiva()).isEqualTo(activa);
    }

    @Test
    @DisplayName("Debería crear una tarjeta con datos válidos usando el método factory of()")
    void deberiaCrearTarjetaConDatosValidosFactoryOf() {
        // Assert (Verificar)
        assertThat(tarjeta).isNotNull();
        assertThat(tarjeta.getNumero()).isEqualTo(numero);
        assertThat(tarjeta.getTitular()).isEqualTo(titular);
        assertThat(tarjeta.getNumeroCuentaAsociada()).isEqualTo(numeroCuentaAsociada);
        assertThat(tarjeta.getTipo()).isEqualTo(tipo);
        assertThat(tarjeta.getFechaExpiracion()).isEqualTo(fechaExpiracion);
        assertThat(tarjeta.getCvv()).isEqualTo(cvv);
        assertThat(tarjeta.isActiva()).isEqualTo(activa);
    }

    @Test
    @DisplayName("Debería devolver el identificador de la tarjeta")
    void deberiaDevolverElIdentificadorDeLaTarjeta() {
        // Assert (Verificar)
        assertThat(tarjeta.getId()).isEqualTo((long) numero.hashCode());
        assertThat(tarjeta.getId()).isNotNull();
    }
    @Test
    @DisplayName("Debería devolver el identificador de la tarjeta cuando es nulo")
    void deberiaDevolverElIdentificadorDeLaTarjetaCuandoEsNulo() {
        // Arrange (Preparar)
        Tarjeta tarjetaBuilder = Tarjeta.builder()
        .numero(null)
        .titular(titular)
        .numeroCuentaAsociada(numeroCuentaAsociada)
        .tipo(tipo)
        .fechaExpiracion(fechaExpiracion)
        .cvv(cvv)
        .activa(activa)
        .build();
        // Act (Actuar)
        Long id = tarjetaBuilder.getId();
        // Assert (Verificar)
        assertThat(id).isNull();;
    }

    @Test
    @DisplayName("Debería crear tarjeta con fecha de expiración diferente usando factory method")
    void deberiaCrearTarjetaConFechaExpiracionDiferenteUsandoFactoryMethod() {
        // Arrange (Preparar)
        LocalDate nuevaFecha = LocalDate.now().plusYears(5);
        
        // Act (Actuar)
        Tarjeta tarjetaActualizada = Tarjeta.of(
            tarjeta.getNumero(),
            tarjeta.getTitular(),
            tarjeta.getNumeroCuentaAsociada(),
            tarjeta.getTipo(),
            nuevaFecha,
            tarjeta.getCvv(),
            tarjeta.isActiva()
        );
        
        // Assert (Verificar)
        assertThat(tarjetaActualizada.getFechaExpiracion()).isEqualTo(nuevaFecha);
        assertThat(tarjeta.getFechaExpiracion()).isEqualTo(fechaExpiracion); // Original no cambia
        assertThat(tarjetaActualizada).isNotSameAs(tarjeta); // Son objetos diferentes
    }

    @Test
    @DisplayName("Debería crear tarjeta con estado activo diferente usando factory method")
    void deberiaCrearTarjetaConEstadoActivoDiferenteUsandoFactoryMethod() {
        // Arrange (Preparar)
        boolean nuevoEstado = false;
        
        // Act (Actuar)
        Tarjeta tarjetaActualizada = Tarjeta.of(
            tarjeta.getNumero(),
            tarjeta.getTitular(),
            tarjeta.getNumeroCuentaAsociada(),
            tarjeta.getTipo(),
            tarjeta.getFechaExpiracion(),
            tarjeta.getCvv(),
            nuevoEstado
        );
        
        // Assert (Verificar)
        assertThat(tarjetaActualizada.isActiva()).isEqualTo(nuevoEstado);
        assertThat(tarjeta.isActiva()).isEqualTo(activa); // Original no cambia
        assertThat(tarjetaActualizada).isNotSameAs(tarjeta); // Son objetos diferentes
    }

    @Test
    @DisplayName("Debería crear tarjeta con múltiples cambios usando factory method")
    void deberiaCrearTarjetaConMultiplesCambiosUsandoFactoryMethod() {
        // Arrange (Preparar)
        LocalDate nuevaFecha = LocalDate.now().plusYears(4);
        boolean nuevoEstado = false;
        
        // Act (Actuar)
        Tarjeta tarjetaActualizada = Tarjeta.of(
            tarjeta.getNumero(),
            tarjeta.getTitular(),
            tarjeta.getNumeroCuentaAsociada(),
            tarjeta.getTipo(),
            nuevaFecha,
            tarjeta.getCvv(),
            nuevoEstado
        );
        
        // Assert (Verificar)
        assertThat(tarjetaActualizada.getFechaExpiracion()).isEqualTo(nuevaFecha);
        assertThat(tarjetaActualizada.isActiva()).isEqualTo(nuevoEstado);
        
        // Verificar que el original no cambió
        assertThat(tarjeta.getFechaExpiracion()).isEqualTo(fechaExpiracion);
        assertThat(tarjeta.isActiva()).isEqualTo(activa);
    }

    @Test
    @DisplayName("Debería mantener inmutabilidad total")
    void deberiaMantenerInmutabilidadTotal() {
        // Arrange (Preparar)
        LocalDate fechaOriginal = tarjeta.getFechaExpiracion();
        boolean activaOriginal = tarjeta.isActiva();
        
        // Act (Actuar) - Crear nueva instancia con algunos cambios
        Tarjeta tarjetaNueva = Tarjeta.of(
            tarjeta.getNumero(),
            tarjeta.getTitular(),
            tarjeta.getNumeroCuentaAsociada(),
            tarjeta.getTipo(),
            LocalDate.now().plusYears(4),
            tarjeta.getCvv(),
            tarjeta.isActiva()
        );
        
        // Assert (Verificar)
        assertThat(tarjetaNueva.getFechaExpiracion()).isNotEqualTo(fechaOriginal);
        assertThat(tarjetaNueva.isActiva()).isEqualTo(activaOriginal); // Mantiene valor original
        
        // Verificar que el original no cambió
        assertThat(tarjeta.getFechaExpiracion()).isEqualTo(fechaOriginal);
        assertThat(tarjeta.isActiva()).isEqualTo(activaOriginal);
    }
}