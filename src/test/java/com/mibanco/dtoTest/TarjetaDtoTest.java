package com.mibanco.dtoTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.mibanco.dto.TarjetaDTO;
import com.mibanco.dto.ClienteDTO;
import com.mibanco.modelo.enums.TipoTarjeta;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDate;
import java.util.Optional;

@DisplayName("Tests para la clase TarjetaDTO")
class TarjetaDtoTest {

    String numero = "1234567890123456";
    ClienteDTO titular;
    String numeroCuentaAsociada = "1234567890";
    TipoTarjeta tipo = TipoTarjeta.CREDITO;
    LocalDate fechaExpiracion = LocalDate.now().plusYears(3);
    boolean activa = true;
    TarjetaDTO tarjetaDto;

    @BeforeEach
    void setUp() {
        titular = ClienteDTO.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Perez")
                .dni("1234567890")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .email("juan.perez@gmail.com")
                .telefono("1234567890")
                .direccion("Calle 123, Ciudad, País")
                .build();

        tarjetaDto = TarjetaDTO.builder()
                .numero(numero)
                .titular(titular)
                .numeroCuentaAsociada(numeroCuentaAsociada)
                .tipo(tipo)
                .fechaExpiracion(fechaExpiracion)
                .activa(activa)
                .build();
    }

    @Test
    @DisplayName("Debería crear un TarjetaDTO con datos válidos usando builder")
    void deberiaCrearTarjetaDtoConDatosValidosUsandoBuilder() {
        // Assert (Verificar)
        assertThat(tarjetaDto).isNotNull();
        assertThat(tarjetaDto.getNumero()).isEqualTo(numero);
        assertThat(tarjetaDto.getTitular()).isEqualTo(titular);
        assertThat(tarjetaDto.getNumeroCuentaAsociada()).isEqualTo(numeroCuentaAsociada);
        assertThat(tarjetaDto.getTipo()).isEqualTo(tipo);
        assertThat(tarjetaDto.getFechaExpiracion()).isEqualTo(fechaExpiracion);
        assertThat(tarjetaDto.isActiva()).isEqualTo(activa);
    }

    @Test
    @DisplayName("Debería crear un TarjetaDTO con datos válidos usando método factory of()")
    void deberiaCrearTarjetaDtoConDatosValidosUsandoFactoryOf() {
        // Act (Actuar)
        TarjetaDTO tarjetaConOpcionales = TarjetaDTO.of(
                numero, 
                Optional.of(titular), 
                Optional.of(numeroCuentaAsociada),
                Optional.of(tipo), 
                Optional.of(fechaExpiracion),
                Optional.of(activa)
        );

        // Assert (Verificar)
        assertThat(tarjetaConOpcionales).isNotNull();
        assertThat(tarjetaConOpcionales.getNumero()).isEqualTo(numero);
        assertThat(tarjetaConOpcionales.getTitular()).isEqualTo(titular);
        assertThat(tarjetaConOpcionales.getNumeroCuentaAsociada()).isEqualTo(numeroCuentaAsociada);
        assertThat(tarjetaConOpcionales.getTipo()).isEqualTo(tipo);
        assertThat(tarjetaConOpcionales.getFechaExpiracion()).isEqualTo(fechaExpiracion);
        assertThat(tarjetaConOpcionales.isActiva()).isEqualTo(activa);
    }

    @Test
    @DisplayName("Debería crear un TarjetaDTO con valores nulos usando método factory of()")
    void deberiaCrearTarjetaDtoConValoresNulosUsandoFactoryOf() {
        // Act (Actuar)
        TarjetaDTO tarjetaConNulos = TarjetaDTO.of(
                numero, 
                Optional.empty(), // titular
                Optional.empty(), // numeroCuentaAsociada
                Optional.empty(), // tipo
                Optional.empty(), // fechaExpiracion
                Optional.empty()  // activa
        );

        // Assert (Verificar)
        assertThat(tarjetaConNulos).isNotNull();
        assertThat(tarjetaConNulos.getNumero()).isEqualTo(numero);
        assertThat(tarjetaConNulos.getTitular()).isNull();
        assertThat(tarjetaConNulos.getNumeroCuentaAsociada()).isNull();
        assertThat(tarjetaConNulos.getTipo()).isNull();
        assertThat(tarjetaConNulos.getFechaExpiracion()).isNotNull(); // Valor por defecto
        assertThat(tarjetaConNulos.isActiva()).isTrue(); // Valor por defecto
    }

    @Test
    @DisplayName("Debería crear un TarjetaDTO con valores mixtos usando método factory of()")
    void deberiaCrearTarjetaDtoConValoresMixtosUsandoFactoryOf() {
        // Act (Actuar)
        TarjetaDTO tarjetaConMixtos = TarjetaDTO.of(
                numero, 
                Optional.of(titular),        // titular con valor
                Optional.empty(),            // numeroCuentaAsociada vacío
                Optional.of(tipo),           // tipo con valor
                Optional.empty(),            // fechaExpiracion vacío
                Optional.of(activa)          // activa con valor
        );

        // Assert (Verificar)
        assertThat(tarjetaConMixtos).isNotNull();
        assertThat(tarjetaConMixtos.getNumero()).isEqualTo(numero);
        assertThat(tarjetaConMixtos.getTitular()).isEqualTo(titular);
        assertThat(tarjetaConMixtos.getNumeroCuentaAsociada()).isNull();
        assertThat(tarjetaConMixtos.getTipo()).isEqualTo(tipo);
        assertThat(tarjetaConMixtos.getFechaExpiracion()).isNotNull(); // Valor por defecto
        assertThat(tarjetaConMixtos.isActiva()).isEqualTo(activa);
    }

    @Test
    @DisplayName("Debería actualizar la fecha de expiración de forma inmutable usando conFechaExpiracion()")
    void deberiaActualizarFechaExpiracionDeFormaInmutableUsandoConFechaExpiracion() {
        // Arrange (Preparar)
        LocalDate nuevaFecha = LocalDate.now().plusYears(5);

        // Act (Actuar)
        TarjetaDTO tarjetaActualizada = tarjetaDto.conFechaExpiracion(nuevaFecha);

        // Assert (Verificar)
        assertThat(tarjetaActualizada).isNotNull();
        assertThat(tarjetaActualizada.getFechaExpiracion()).isEqualTo(nuevaFecha);
        assertThat(tarjetaActualizada.getNumero()).isEqualTo(tarjetaDto.getNumero());
        assertThat(tarjetaActualizada.getTitular()).isEqualTo(tarjetaDto.getTitular());
        assertThat(tarjetaActualizada.getNumeroCuentaAsociada()).isEqualTo(tarjetaDto.getNumeroCuentaAsociada());
        assertThat(tarjetaActualizada.getTipo()).isEqualTo(tarjetaDto.getTipo());
        assertThat(tarjetaActualizada.isActiva()).isEqualTo(tarjetaDto.isActiva());
        
        // Verificar inmutabilidad
        assertThat(tarjetaDto.getFechaExpiracion()).isEqualTo(fechaExpiracion);
        assertThat(tarjetaActualizada).isNotSameAs(tarjetaDto);
    }

    @Test
    @DisplayName("Debería actualizar el estado activo de forma inmutable usando conActiva()")
    void deberiaActualizarEstadoActivoDeFormaInmutableUsandoConActiva() {
        // Arrange (Preparar)
        boolean nuevoEstado = false;

        // Act (Actuar)
        TarjetaDTO tarjetaActualizada = tarjetaDto.conActiva(nuevoEstado);

        // Assert (Verificar)
        assertThat(tarjetaActualizada).isNotNull();
        assertThat(tarjetaActualizada.isActiva()).isEqualTo(nuevoEstado);
        assertThat(tarjetaActualizada.getNumero()).isEqualTo(tarjetaDto.getNumero());
        assertThat(tarjetaActualizada.getTitular()).isEqualTo(tarjetaDto.getTitular());
        assertThat(tarjetaActualizada.getNumeroCuentaAsociada()).isEqualTo(tarjetaDto.getNumeroCuentaAsociada());
        assertThat(tarjetaActualizada.getTipo()).isEqualTo(tarjetaDto.getTipo());
        assertThat(tarjetaActualizada.getFechaExpiracion()).isEqualTo(tarjetaDto.getFechaExpiracion());
        
        // Verificar inmutabilidad
        assertThat(tarjetaDto.isActiva()).isEqualTo(activa);
        assertThat(tarjetaActualizada).isNotSameAs(tarjetaDto);
    }

    @Test
    @DisplayName("Debería actualizar el titular de forma inmutable usando conTitular()")
    void deberiaActualizarTitularDeFormaInmutableUsandoConTitular() {
        // Arrange (Preparar)
        ClienteDTO nuevoTitular = ClienteDTO.builder()
                .id(2L)
                .nombre("Maria")
                .apellido("Garcia")
                .dni("0987654321")
                .fechaNacimiento(LocalDate.of(1985, 5, 15))
                .email("maria.garcia@gmail.com")
                .telefono("0987654321")
                .direccion("Avenida 456, Ciudad, País")
                .build();

        // Act (Actuar)
        TarjetaDTO tarjetaActualizada = tarjetaDto.conTitular(nuevoTitular);

        // Assert (Verificar)
        assertThat(tarjetaActualizada).isNotNull();
        assertThat(tarjetaActualizada.getTitular()).isEqualTo(nuevoTitular);
        assertThat(tarjetaActualizada.getNumero()).isEqualTo(tarjetaDto.getNumero());
        assertThat(tarjetaActualizada.getNumeroCuentaAsociada()).isEqualTo(tarjetaDto.getNumeroCuentaAsociada());
        assertThat(tarjetaActualizada.getTipo()).isEqualTo(tarjetaDto.getTipo());
        assertThat(tarjetaActualizada.getFechaExpiracion()).isEqualTo(tarjetaDto.getFechaExpiracion());
        assertThat(tarjetaActualizada.isActiva()).isEqualTo(tarjetaDto.isActiva());
        
        // Verificar inmutabilidad
        assertThat(tarjetaDto.getTitular()).isEqualTo(titular);
        assertThat(tarjetaActualizada).isNotSameAs(tarjetaDto);
    }

    @Test
    @DisplayName("Debería actualizar múltiples campos de forma inmutable usando conActualizaciones()")
    void deberiaActualizarMultiplesCamposDeFormaInmutableUsandoConActualizaciones() {
        // Arrange (Preparar)
        LocalDate nuevaFecha = LocalDate.now().plusYears(4);
        boolean nuevoEstado = false;

        // Act (Actuar)
        TarjetaDTO tarjetaActualizada = tarjetaDto.conActualizaciones(
                Optional.of(nuevaFecha),
                Optional.of(nuevoEstado)
        );

        // Assert (Verificar)
        assertThat(tarjetaActualizada).isNotNull();
        assertThat(tarjetaActualizada.getFechaExpiracion()).isEqualTo(nuevaFecha);
        assertThat(tarjetaActualizada.isActiva()).isEqualTo(nuevoEstado);
        assertThat(tarjetaActualizada.getNumero()).isEqualTo(tarjetaDto.getNumero());
        assertThat(tarjetaActualizada.getTitular()).isEqualTo(tarjetaDto.getTitular());
        assertThat(tarjetaActualizada.getNumeroCuentaAsociada()).isEqualTo(tarjetaDto.getNumeroCuentaAsociada());
        assertThat(tarjetaActualizada.getTipo()).isEqualTo(tarjetaDto.getTipo());
        
        // Verificar inmutabilidad
        assertThat(tarjetaDto.getFechaExpiracion()).isEqualTo(fechaExpiracion);
        assertThat(tarjetaDto.isActiva()).isEqualTo(activa);
        assertThat(tarjetaActualizada).isNotSameAs(tarjetaDto);
    }

    @Test
    @DisplayName("Debería manejar Optional vacío en actualizaciones múltiples")
    void deberiaManejarOptionalVacioEnActualizacionesMultiples() {
        // Arrange (Preparar)
        LocalDate nuevaFecha = LocalDate.now().plusYears(4);

        // Act (Actuar) - Solo actualizar fecha, mantener activa original
        TarjetaDTO tarjetaActualizada = tarjetaDto.conActualizaciones(
                Optional.of(nuevaFecha),
                Optional.empty() // No cambiar activa
        );

        // Assert (Verificar)
        assertThat(tarjetaActualizada.getFechaExpiracion()).isEqualTo(nuevaFecha);
        assertThat(tarjetaActualizada.isActiva()).isEqualTo(activa); // Mantiene valor original
        
        // Verificar que el original no cambió
        assertThat(tarjetaDto.getFechaExpiracion()).isEqualTo(fechaExpiracion);
        assertThat(tarjetaDto.isActiva()).isEqualTo(activa);
    }

    @Test
    @DisplayName("Debería mantener valores por defecto cuando se usan Optional.empty()")
    void deberiaMantenerValoresPorDefectoCuandoSeUsanOptionalEmpty() {
        // Act (Actuar) - Usar Optional.empty() para todos los campos opcionales
        TarjetaDTO tarjetaConDefaults = tarjetaDto.conActualizaciones(
                Optional.empty(), // Mantener fecha original
                Optional.empty()  // Mantener activa original
        );

        // Assert (Verificar)
        assertThat(tarjetaConDefaults.getFechaExpiracion()).isEqualTo(fechaExpiracion);
        assertThat(tarjetaConDefaults.isActiva()).isEqualTo(activa);
        
        // Verificar que el original no cambió
        assertThat(tarjetaDto.getFechaExpiracion()).isEqualTo(fechaExpiracion);
        assertThat(tarjetaDto.isActiva()).isEqualTo(activa);
    }

    @Test
    @DisplayName("Debería verificar inmutabilidad total del DTO")
    void deberiaVerificarInmutabilidadTotalDelDto() {
        // Arrange (Preparar)
        LocalDate fechaOriginal = tarjetaDto.getFechaExpiracion();
        boolean activaOriginal = tarjetaDto.isActiva();
        ClienteDTO titularOriginal = tarjetaDto.getTitular();

        // Act (Actuar) - Realizar múltiples actualizaciones
        TarjetaDTO tarjeta1 = tarjetaDto.conFechaExpiracion(LocalDate.now().plusYears(5));
        TarjetaDTO tarjeta2 = tarjeta1.conActiva(false);
        TarjetaDTO tarjeta3 = tarjeta2.conTitular(ClienteDTO.builder()
                .id(3L)
                .nombre("Pedro")
                .apellido("Lopez")
                .dni("1122334455")
                .build());

        // Assert (Verificar)
        // Verificar que cada actualización crea una nueva instancia
        assertThat(tarjeta1).isNotSameAs(tarjetaDto);
        assertThat(tarjeta2).isNotSameAs(tarjeta1);
        assertThat(tarjeta3).isNotSameAs(tarjeta2);

        // Verificar que el original no cambió
        assertThat(tarjetaDto.getFechaExpiracion()).isEqualTo(fechaOriginal);
        assertThat(tarjetaDto.isActiva()).isEqualTo(activaOriginal);
        assertThat(tarjetaDto.getTitular()).isEqualTo(titularOriginal);
    }
}
