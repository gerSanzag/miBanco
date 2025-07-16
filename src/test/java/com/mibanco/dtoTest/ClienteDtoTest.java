package com.mibanco.dtoTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.mibanco.dto.ClienteDTO;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDate;
import java.util.Optional;

@DisplayName("Tests para la clase ClienteDTO")
class ClienteDtoTest {

    Long id = 1L;
    String nombre = "Juan";
    String apellido = "Pérez";
    String dni = "12345678A";
    String email = "juan.perez@email.com";
    String telefono = "123456789";
    LocalDate fechaNacimiento = LocalDate.of(1990, 5, 15);
    String direccion = "Calle Principal 123";
    ClienteDTO clienteDto;

    @BeforeEach
    void setUp() {
        clienteDto = ClienteDTO.builder()
                .id(id)
                .nombre(nombre)
                .apellido(apellido)
                .dni(dni)
                .email(email)
                .telefono(telefono)
                .fechaNacimiento(fechaNacimiento)
                .direccion(direccion)
                .build();
    }

    @Test
    @DisplayName("Debería crear un ClienteDTO con datos válidos usando Builder")
    void deberiaCrearClienteDtoConDatosValidosBuilder() {
        // Assert (Verificar)
        assertThat(clienteDto).isNotNull();
        assertThat(clienteDto.getId()).isEqualTo(id);
        assertThat(clienteDto.getNombre()).isEqualTo(nombre);
        assertThat(clienteDto.getApellido()).isEqualTo(apellido);
        assertThat(clienteDto.getDni()).isEqualTo(dni);
        assertThat(clienteDto.getEmail()).isEqualTo(email);
        assertThat(clienteDto.getTelefono()).isEqualTo(telefono);
        assertThat(clienteDto.getFechaNacimiento()).isEqualTo(fechaNacimiento);
        assertThat(clienteDto.getDireccion()).isEqualTo(direccion);
    }

    @Test
    @DisplayName("Debería crear un ClienteDTO usando el método factory of() con valores opcionales")
    void deberiaCrearClienteDtoUsandoFactoryOfConValoresOpcionales() {
        // Act (Actuar)
        ClienteDTO clienteConOpcionales = ClienteDTO.of(
                id, Optional.of(nombre), Optional.of(apellido), Optional.of(dni),
                Optional.of(email),
                Optional.of(telefono),
                Optional.of(fechaNacimiento),
                Optional.of(direccion)
        );

        // Assert (Verificar)
        assertThat(clienteConOpcionales).isNotNull();
        assertThat(clienteConOpcionales.getId()).isEqualTo(id);
        assertThat(clienteConOpcionales.getNombre()).isEqualTo(nombre);
        assertThat(clienteConOpcionales.getApellido()).isEqualTo(apellido);
        assertThat(clienteConOpcionales.getDni()).isEqualTo(dni);
        assertThat(clienteConOpcionales.getEmail()).isEqualTo(email);
        assertThat(clienteConOpcionales.getTelefono()).isEqualTo(telefono);
        assertThat(clienteConOpcionales.getFechaNacimiento()).isEqualTo(fechaNacimiento);
        assertThat(clienteConOpcionales.getDireccion()).isEqualTo(direccion);
    }

    @Test
    @DisplayName("Debería crear un ClienteDTO usando el método factory of() con valores nulos")
    void deberiaCrearClienteDtoUsandoFactoryOfConValoresNulos() {
        // Act (Actuar)
        ClienteDTO clienteConNulos = ClienteDTO.of(
                id, Optional.of(nombre), Optional.of(apellido), Optional.of(dni),
                Optional.empty(), // email
                Optional.empty(), // telefono
                Optional.empty(), // fechaNacimiento
                Optional.empty()  // direccion
        );

        // Assert (Verificar)
        assertThat(clienteConNulos).isNotNull();
        assertThat(clienteConNulos.getId()).isEqualTo(id);
        assertThat(clienteConNulos.getNombre()).isEqualTo(nombre);
        assertThat(clienteConNulos.getApellido()).isEqualTo(apellido);
        assertThat(clienteConNulos.getDni()).isEqualTo(dni);
        assertThat(clienteConNulos.getEmail()).isNull();
        assertThat(clienteConNulos.getTelefono()).isNull();
        assertThat(clienteConNulos.getFechaNacimiento()).isNull();
        assertThat(clienteConNulos.getDireccion()).isNull();
    }

    @Test
    @DisplayName("Debería crear un ClienteDTO usando el método factory of() con valores mixtos")
    void deberiaCrearClienteDtoUsandoFactoryOfConValoresMixtos() {
        // Act (Actuar)
        ClienteDTO clienteConMixtos = ClienteDTO.of(
                id, Optional.of(nombre), Optional.of(apellido), Optional.of(dni),
                Optional.of(email),        // email con valor
                Optional.empty(),          // telefono vacío
                Optional.of(fechaNacimiento), // fechaNacimiento con valor
                Optional.empty()           // direccion vacía
        );

        // Assert (Verificar)
        assertThat(clienteConMixtos).isNotNull();
        assertThat(clienteConMixtos.getId()).isEqualTo(id);
        assertThat(clienteConMixtos.getNombre()).isEqualTo(nombre);
        assertThat(clienteConMixtos.getApellido()).isEqualTo(apellido);
        assertThat(clienteConMixtos.getDni()).isEqualTo(dni);
        assertThat(clienteConMixtos.getEmail()).isEqualTo(email);
        assertThat(clienteConMixtos.getTelefono()).isNull();
        assertThat(clienteConMixtos.getFechaNacimiento()).isEqualTo(fechaNacimiento);
        assertThat(clienteConMixtos.getDireccion()).isNull();
    }

    @Test
    @DisplayName("Debería actualizar el email de forma inmutable usando conEmail()")
    void deberiaActualizarEmailDeFormaInmutableUsandoConEmail() {
        // Arrange (Preparar)
        String nuevoEmail = "nuevo.email@test.com";

        // Act (Actuar)
        ClienteDTO clienteActualizado = clienteDto.conEmail(nuevoEmail);

        // Assert (Verificar)
        assertThat(clienteActualizado).isNotNull();
        assertThat(clienteActualizado.getEmail()).isEqualTo(nuevoEmail);
        assertThat(clienteActualizado.getId()).isEqualTo(clienteDto.getId());
        assertThat(clienteActualizado.getNombre()).isEqualTo(clienteDto.getNombre());
        assertThat(clienteActualizado.getApellido()).isEqualTo(clienteDto.getApellido());
        assertThat(clienteActualizado.getDni()).isEqualTo(clienteDto.getDni());
        assertThat(clienteActualizado.getTelefono()).isEqualTo(clienteDto.getTelefono());
        assertThat(clienteActualizado.getFechaNacimiento()).isEqualTo(clienteDto.getFechaNacimiento());
        assertThat(clienteActualizado.getDireccion()).isEqualTo(clienteDto.getDireccion());

        // Verificar que el original no cambió
        assertThat(clienteDto.getEmail()).isEqualTo(email);
        assertThat(clienteActualizado).isNotSameAs(clienteDto);
    }

    @Test
    @DisplayName("Debería actualizar el teléfono de forma inmutable usando conTelefono()")
    void deberiaActualizarTelefonoDeFormaInmutableUsandoConTelefono() {
        // Arrange (Preparar)
        String nuevoTelefono = "987654321";

        // Act (Actuar)
        ClienteDTO clienteActualizado = clienteDto.conTelefono(nuevoTelefono);

        // Assert (Verificar)
        assertThat(clienteActualizado).isNotNull();
        assertThat(clienteActualizado.getTelefono()).isEqualTo(nuevoTelefono);
        assertThat(clienteActualizado.getId()).isEqualTo(clienteDto.getId());
        assertThat(clienteActualizado.getNombre()).isEqualTo(clienteDto.getNombre());
        assertThat(clienteActualizado.getApellido()).isEqualTo(clienteDto.getApellido());
        assertThat(clienteActualizado.getDni()).isEqualTo(clienteDto.getDni());
        assertThat(clienteActualizado.getEmail()).isEqualTo(clienteDto.getEmail());
        assertThat(clienteActualizado.getFechaNacimiento()).isEqualTo(clienteDto.getFechaNacimiento());
        assertThat(clienteActualizado.getDireccion()).isEqualTo(clienteDto.getDireccion());

        // Verificar que el original no cambió
        assertThat(clienteDto.getTelefono()).isEqualTo(telefono);
        assertThat(clienteActualizado).isNotSameAs(clienteDto);
    }

    @Test
    @DisplayName("Debería actualizar la dirección de forma inmutable usando conDireccion()")
    void deberiaActualizarDireccionDeFormaInmutableUsandoConDireccion() {
        // Arrange (Preparar)
        String nuevaDireccion = "Nueva Calle 456";

        // Act (Actuar)
        ClienteDTO clienteActualizado = clienteDto.conDireccion(nuevaDireccion);

        // Assert (Verificar)
        assertThat(clienteActualizado).isNotNull();
        assertThat(clienteActualizado.getDireccion()).isEqualTo(nuevaDireccion);
        assertThat(clienteActualizado.getId()).isEqualTo(clienteDto.getId());
        assertThat(clienteActualizado.getNombre()).isEqualTo(clienteDto.getNombre());
        assertThat(clienteActualizado.getApellido()).isEqualTo(clienteDto.getApellido());
        assertThat(clienteActualizado.getDni()).isEqualTo(clienteDto.getDni());
        assertThat(clienteActualizado.getEmail()).isEqualTo(clienteDto.getEmail());
        assertThat(clienteActualizado.getTelefono()).isEqualTo(clienteDto.getTelefono());
        assertThat(clienteActualizado.getFechaNacimiento()).isEqualTo(clienteDto.getFechaNacimiento());

        // Verificar que el original no cambió
        assertThat(clienteDto.getDireccion()).isEqualTo(direccion);
        assertThat(clienteActualizado).isNotSameAs(clienteDto);
    }

    @Test
    @DisplayName("Debería actualizar múltiples campos de contacto usando conDatosContacto()")
    void deberiaActualizarMultiplesCamposDeContactoUsandoConDatosContacto() {
        // Arrange (Preparar)
        String nuevoEmail = "nuevo.email@test.com";
        String nuevoTelefono = "987654321";
        String nuevaDireccion = "Nueva Calle 456";

        // Act (Actuar)
        ClienteDTO clienteActualizado = clienteDto.conDatosContacto(
                Optional.of(nuevoEmail),     // nuevo email
                Optional.of(nuevoTelefono),  // nuevo teléfono
                Optional.of(nuevaDireccion)  // nueva dirección
        );

        // Assert (Verificar)
        assertThat(clienteActualizado).isNotNull();
        assertThat(clienteActualizado.getEmail()).isEqualTo(nuevoEmail);
        assertThat(clienteActualizado.getTelefono()).isEqualTo(nuevoTelefono);
        assertThat(clienteActualizado.getDireccion()).isEqualTo(nuevaDireccion);
        assertThat(clienteActualizado.getId()).isEqualTo(clienteDto.getId());
        assertThat(clienteActualizado.getNombre()).isEqualTo(clienteDto.getNombre());
        assertThat(clienteActualizado.getApellido()).isEqualTo(clienteDto.getApellido());
        assertThat(clienteActualizado.getDni()).isEqualTo(clienteDto.getDni());
        assertThat(clienteActualizado.getFechaNacimiento()).isEqualTo(clienteDto.getFechaNacimiento());

        // Verificar que el original no cambió
        assertThat(clienteDto.getEmail()).isEqualTo(email);
        assertThat(clienteDto.getTelefono()).isEqualTo(telefono);
        assertThat(clienteDto.getDireccion()).isEqualTo(direccion);
        assertThat(clienteActualizado).isNotSameAs(clienteDto);
    }

    @Test
    @DisplayName("Debería mantener valores originales cuando se pasan Optionals vacíos en conDatosContacto()")
    void deberiaMantenerValoresOriginalesCuandoSePasanOptionalsVaciosEnConDatosContacto() {
        // Act (Actuar)
        ClienteDTO clienteActualizado = clienteDto.conDatosContacto(
                Optional.empty(), // mantener email original
                Optional.empty(), // mantener teléfono original
                Optional.empty()  // mantener dirección original
        );

        // Assert (Verificar)
        assertThat(clienteActualizado).isNotNull();
        assertThat(clienteActualizado.getEmail()).isEqualTo(clienteDto.getEmail());
        assertThat(clienteActualizado.getTelefono()).isEqualTo(clienteDto.getTelefono());
        assertThat(clienteActualizado.getDireccion()).isEqualTo(clienteDto.getDireccion());
        assertThat(clienteActualizado.getId()).isEqualTo(clienteDto.getId());
        assertThat(clienteActualizado.getNombre()).isEqualTo(clienteDto.getNombre());
        assertThat(clienteActualizado.getApellido()).isEqualTo(clienteDto.getApellido());
        assertThat(clienteActualizado.getDni()).isEqualTo(clienteDto.getDni());
        assertThat(clienteActualizado.getFechaNacimiento()).isEqualTo(clienteDto.getFechaNacimiento());
    }

    @Test
    @DisplayName("Debería actualizar solo algunos campos cuando se pasan Optionals mixtos en conDatosContacto()")
    void deberiaActualizarSoloAlgunosCamposCuandoSePasanOptionalsMixtosEnConDatosContacto() {
        // Arrange (Preparar)
        String nuevoEmail = "nuevo.email@test.com";
        String nuevaDireccion = "Nueva Calle 456";

        // Act (Actuar)
        ClienteDTO clienteActualizado = clienteDto.conDatosContacto(
                Optional.of(nuevoEmail),     // actualizar email
                Optional.empty(),            // mantener teléfono original
                Optional.of(nuevaDireccion)  // actualizar dirección
        );

        // Assert (Verificar)
        assertThat(clienteActualizado).isNotNull();
        assertThat(clienteActualizado.getEmail()).isEqualTo(nuevoEmail);
        assertThat(clienteActualizado.getTelefono()).isEqualTo(clienteDto.getTelefono()); // Mantiene original
        assertThat(clienteActualizado.getDireccion()).isEqualTo(nuevaDireccion);
        assertThat(clienteActualizado.getId()).isEqualTo(clienteDto.getId());
        assertThat(clienteActualizado.getNombre()).isEqualTo(clienteDto.getNombre());
        assertThat(clienteActualizado.getApellido()).isEqualTo(clienteDto.getApellido());
        assertThat(clienteActualizado.getDni()).isEqualTo(clienteDto.getDni());
        assertThat(clienteActualizado.getFechaNacimiento()).isEqualTo(clienteDto.getFechaNacimiento());
    }

    @Test
    @DisplayName("Debería manejar valores nulos en campos opcionales")
    void deberiaManejarValoresNulosEnCamposOpcionales() {
        // Act (Actuar)
        ClienteDTO clienteConNulos = ClienteDTO.builder()
                .id(id)
                .nombre(nombre)
                .apellido(apellido)
                .dni(dni)
                .email(null)
                .telefono(null)
                .fechaNacimiento(null)
                .direccion(null)
                .build();

        // Assert (Verificar)
        assertThat(clienteConNulos).isNotNull();
        assertThat(clienteConNulos.getId()).isEqualTo(id);
        assertThat(clienteConNulos.getNombre()).isEqualTo(nombre);
        assertThat(clienteConNulos.getApellido()).isEqualTo(apellido);
        assertThat(clienteConNulos.getDni()).isEqualTo(dni);
        assertThat(clienteConNulos.getEmail()).isNull();
        assertThat(clienteConNulos.getTelefono()).isNull();
        assertThat(clienteConNulos.getFechaNacimiento()).isNull();
        assertThat(clienteConNulos.getDireccion()).isNull();
    }

    @Test
    @DisplayName("Debería manejar diferentes tipos de fechas de nacimiento")
    void deberiaManejarDiferentesTiposDeFechasDeNacimiento() {
        // Arrange (Preparar)
        LocalDate fechaPasada = LocalDate.of(1980, 1, 1);
        LocalDate fechaFutura = LocalDate.of(2030, 12, 31);
        LocalDate fechaActual = LocalDate.now();

        // Act (Actuar)
        ClienteDTO clientePasado = ClienteDTO.builder()
                .id(1L)
                .nombre("Cliente")
                .apellido("Pasado")
                .dni("11111111A")
                .fechaNacimiento(fechaPasada)
                .build();

        ClienteDTO clienteFuturo = ClienteDTO.builder()
                .id(2L)
                .nombre("Cliente")
                .apellido("Futuro")
                .dni("22222222B")
                .fechaNacimiento(fechaFutura)
                .build();

        ClienteDTO clienteActual = ClienteDTO.builder()
                .id(3L)
                .nombre("Cliente")
                .apellido("Actual")
                .dni("33333333C")
                .fechaNacimiento(fechaActual)
                .build();

        // Assert (Verificar)
        assertThat(clientePasado.getFechaNacimiento()).isEqualTo(fechaPasada);
        assertThat(clienteFuturo.getFechaNacimiento()).isEqualTo(fechaFutura);
        assertThat(clienteActual.getFechaNacimiento()).isEqualTo(fechaActual);
    }

    @Test
    @DisplayName("Debería mantener inmutabilidad en todos los campos")
    void deberiaMantenerInmutabilidadEnTodosLosCampos() {
        // Arrange (Preparar)
        ClienteDTO clienteOriginal = clienteDto;

        // Act (Actuar) - Crear un nuevo cliente con datos diferentes
        ClienteDTO clienteNuevo = ClienteDTO.builder()
                .id(999L)
                .nombre("Nuevo")
                .apellido("Cliente")
                .dni("99999999Z")
                .email("nuevo@test.com")
                .telefono("999999999")
                .fechaNacimiento(LocalDate.of(2000, 1, 1))
                .direccion("Nueva Dirección")
                .build();

        // Assert (Verificar) - Original no debe cambiar
        assertThat(clienteOriginal.getId()).isEqualTo(id);
        assertThat(clienteOriginal.getNombre()).isEqualTo(nombre);
        assertThat(clienteOriginal.getApellido()).isEqualTo(apellido);
        assertThat(clienteOriginal.getDni()).isEqualTo(dni);
        assertThat(clienteOriginal.getEmail()).isEqualTo(email);
        assertThat(clienteOriginal.getTelefono()).isEqualTo(telefono);
        assertThat(clienteOriginal.getFechaNacimiento()).isEqualTo(fechaNacimiento);
        assertThat(clienteOriginal.getDireccion()).isEqualTo(direccion);

        // Verificar que son objetos diferentes
        assertThat(clienteNuevo).isNotSameAs(clienteOriginal);
        assertThat(clienteNuevo.getId()).isNotEqualTo(clienteOriginal.getId());
    }
}
