package com.mibanco.dtoTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.mibanco.dto.CuentaDTO;
import com.mibanco.dto.ClienteDTO;
import com.mibanco.modelo.enums.TipoCuenta;

import static org.assertj.core.api.Assertions.assertThat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@DisplayName("Tests para la clase CuentaDTO")
class CuentaDtoTest {

    String numeroCuenta = "ES3412345678901234567890";
    ClienteDTO titular;
    TipoCuenta tipo = TipoCuenta.AHORRO;
    LocalDateTime fechaCreacion = LocalDateTime.now();
    BigDecimal saldoInicial = BigDecimal.valueOf(1000.0);
    BigDecimal saldo = saldoInicial;
    boolean activa = true;
    CuentaDTO cuentaDto;

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

        cuentaDto = CuentaDTO.builder()
                .numeroCuenta(numeroCuenta)
                .titular(titular)
                .tipo(tipo)
                .fechaCreacion(fechaCreacion)
                .saldoInicial(saldoInicial)
                .saldo(saldo)
                .activa(activa)
                .build();
    }

    @Test
    @DisplayName("Debería crear un CuentaDTO con datos válidos usando Builder")
    void deberiaCrearCuentaDtoConDatosValidosBuilder() {
        // Assert (Verificar)
        assertThat(cuentaDto).isNotNull();
        assertThat(cuentaDto.getNumeroCuenta()).isEqualTo(numeroCuenta);
        assertThat(cuentaDto.getTitular()).isEqualTo(titular);
        assertThat(cuentaDto.getTipo()).isEqualTo(tipo);
        assertThat(cuentaDto.getFechaCreacion()).isEqualTo(fechaCreacion);
        assertThat(cuentaDto.getSaldoInicial()).isEqualTo(saldoInicial);
        assertThat(cuentaDto.getSaldo()).isEqualTo(saldo);
        assertThat(cuentaDto.isActiva()).isEqualTo(activa);
    }

    @Test
    @DisplayName("Debería crear un CuentaDTO usando el método factory of() con valores opcionales")
    void deberiaCrearCuentaDtoUsandoFactoryOfConValoresOpcionales() {
        // Act (Actuar)
        CuentaDTO cuentaConOpcionales = CuentaDTO.of(
                numeroCuenta,
                Optional.of(titular),
                Optional.of(tipo),
                saldoInicial,
                Optional.of(fechaCreacion),
                Optional.of(activa)
        );

        // Assert (Verificar)
        assertThat(cuentaConOpcionales).isNotNull();
        assertThat(cuentaConOpcionales.getNumeroCuenta()).isEqualTo(numeroCuenta);
        assertThat(cuentaConOpcionales.getTitular()).isEqualTo(titular);
        assertThat(cuentaConOpcionales.getTipo()).isEqualTo(tipo);
        assertThat(cuentaConOpcionales.getSaldoInicial()).isEqualTo(saldoInicial);
        assertThat(cuentaConOpcionales.getSaldo()).isEqualTo(saldoInicial);
        assertThat(cuentaConOpcionales.getFechaCreacion()).isEqualTo(fechaCreacion);
        assertThat(cuentaConOpcionales.isActiva()).isEqualTo(activa);
    }

    @Test
    @DisplayName("Debería crear un CuentaDTO usando el método factory of() con valores nulos")
    void deberiaCrearCuentaDtoUsandoFactoryOfConValoresNulos() {
        // Act (Actuar)
        CuentaDTO cuentaConNulos = CuentaDTO.of(
                numeroCuenta,
                Optional.empty(), // titular
                Optional.empty(), // tipo
                null, // saldoInicial
                Optional.empty(), // fechaCreacion
                Optional.empty()  // activa
        );

        // Assert (Verificar)
        assertThat(cuentaConNulos).isNotNull();
        assertThat(cuentaConNulos.getNumeroCuenta()).isEqualTo(numeroCuenta);
        assertThat(cuentaConNulos.getTitular()).isNull();
        assertThat(cuentaConNulos.getTipo()).isNull();
        assertThat(cuentaConNulos.getSaldoInicial()).isEqualTo(BigDecimal.ZERO);
        assertThat(cuentaConNulos.getSaldo()).isEqualTo(BigDecimal.ZERO);
        assertThat(cuentaConNulos.getFechaCreacion()).isNotNull(); // Se establece automáticamente
        assertThat(cuentaConNulos.isActiva()).isTrue(); // Valor por defecto
    }

    @Test
    @DisplayName("Debería actualizar el saldo de forma inmutable usando conSaldo()")
    void deberiaActualizarSaldoDeFormaInmutableUsandoConSaldo() {
        // Arrange (Preparar)
        BigDecimal nuevoSaldo = BigDecimal.valueOf(2000.0);

        // Act (Actuar)
        CuentaDTO cuentaActualizada = cuentaDto.conSaldo(nuevoSaldo);

        // Assert (Verificar)
        assertThat(cuentaActualizada).isNotNull();
        assertThat(cuentaActualizada.getSaldo()).isEqualTo(nuevoSaldo);
        assertThat(cuentaActualizada.getNumeroCuenta()).isEqualTo(cuentaDto.getNumeroCuenta());
        assertThat(cuentaActualizada.getTitular()).isEqualTo(cuentaDto.getTitular());
        assertThat(cuentaActualizada.getTipo()).isEqualTo(cuentaDto.getTipo());
        assertThat(cuentaActualizada.getFechaCreacion()).isEqualTo(cuentaDto.getFechaCreacion());
        assertThat(cuentaActualizada.getSaldoInicial()).isEqualTo(cuentaDto.getSaldoInicial());
        assertThat(cuentaActualizada.isActiva()).isEqualTo(cuentaDto.isActiva());

        // Verificar que el original no cambió
        assertThat(cuentaDto.getSaldo()).isEqualTo(saldo);
        assertThat(cuentaActualizada).isNotSameAs(cuentaDto);
    }

    @Test
    @DisplayName("Debería actualizar el estado activo de forma inmutable usando conActiva()")
    void deberiaActualizarEstadoActivoDeFormaInmutableUsandoConActiva() {
        // Arrange (Preparar)
        boolean nuevoEstado = false;

        // Act (Actuar)
        CuentaDTO cuentaActualizada = cuentaDto.conActiva(nuevoEstado);

        // Assert (Verificar)
        assertThat(cuentaActualizada).isNotNull();
        assertThat(cuentaActualizada.isActiva()).isEqualTo(nuevoEstado);
        assertThat(cuentaActualizada.getNumeroCuenta()).isEqualTo(cuentaDto.getNumeroCuenta());
        assertThat(cuentaActualizada.getTitular()).isEqualTo(cuentaDto.getTitular());
        assertThat(cuentaActualizada.getTipo()).isEqualTo(cuentaDto.getTipo());
        assertThat(cuentaActualizada.getFechaCreacion()).isEqualTo(cuentaDto.getFechaCreacion());
        assertThat(cuentaActualizada.getSaldoInicial()).isEqualTo(cuentaDto.getSaldoInicial());
        assertThat(cuentaActualizada.getSaldo()).isEqualTo(cuentaDto.getSaldo());

        // Verificar que el original no cambió
        assertThat(cuentaDto.isActiva()).isEqualTo(activa);
        assertThat(cuentaActualizada).isNotSameAs(cuentaDto);
    }

    @Test
    @DisplayName("Debería actualizar el titular de forma inmutable usando conTitular()")
    void deberiaActualizarTitularDeFormaInmutableUsandoConTitular() {
        // Arrange (Preparar)
        ClienteDTO nuevoTitular = ClienteDTO.builder()
                .id(2L)
                .nombre("María")
                .apellido("García")
                .dni("9876543210")
                .fechaNacimiento(LocalDate.of(1985, 6, 20))
                .email("maria.garcia@gmail.com")
                .telefono("9876543210")
                .direccion("Avenida Principal 456, Ciudad, País")
                .build();

        // Act (Actuar)
        CuentaDTO cuentaActualizada = cuentaDto.conTitular(nuevoTitular);

        // Assert (Verificar)
        assertThat(cuentaActualizada).isNotNull();
        assertThat(cuentaActualizada.getTitular()).isEqualTo(nuevoTitular);
        assertThat(cuentaActualizada.getTitular().getId()).isEqualTo(2L);
        assertThat(cuentaActualizada.getTitular().getNombre()).isEqualTo("María");
        assertThat(cuentaActualizada.getTitular().getApellido()).isEqualTo("García");
        assertThat(cuentaActualizada.getNumeroCuenta()).isEqualTo(cuentaDto.getNumeroCuenta());
        assertThat(cuentaActualizada.getTipo()).isEqualTo(cuentaDto.getTipo());
        assertThat(cuentaActualizada.getFechaCreacion()).isEqualTo(cuentaDto.getFechaCreacion());
        assertThat(cuentaActualizada.getSaldoInicial()).isEqualTo(cuentaDto.getSaldoInicial());
        assertThat(cuentaActualizada.getSaldo()).isEqualTo(cuentaDto.getSaldo());
        assertThat(cuentaActualizada.isActiva()).isEqualTo(cuentaDto.isActiva());

        // Verificar que el original no cambió
        assertThat(cuentaDto.getTitular()).isEqualTo(titular);
        assertThat(cuentaDto.getTitular().getId()).isEqualTo(1L);
        assertThat(cuentaDto.getTitular().getNombre()).isEqualTo("Juan");
        assertThat(cuentaActualizada).isNotSameAs(cuentaDto);
    }

    @Test
    @DisplayName("Debería actualizar múltiples campos usando conActualizaciones()")
    void deberiaActualizarMultiplesCamposUsandoConActualizaciones() {
        // Arrange (Preparar)
        BigDecimal nuevoSaldo = BigDecimal.valueOf(3000.0);
        boolean nuevoEstado = false;

        // Act (Actuar)
        CuentaDTO cuentaActualizada = cuentaDto.conActualizaciones(
                Optional.of(nuevoSaldo),
                Optional.of(nuevoEstado)
        );

        // Assert (Verificar)
        assertThat(cuentaActualizada).isNotNull();
        assertThat(cuentaActualizada.getSaldo()).isEqualTo(nuevoSaldo);
        assertThat(cuentaActualizada.isActiva()).isEqualTo(nuevoEstado);
        assertThat(cuentaActualizada.getNumeroCuenta()).isEqualTo(cuentaDto.getNumeroCuenta());
        assertThat(cuentaActualizada.getTitular()).isEqualTo(cuentaDto.getTitular());
        assertThat(cuentaActualizada.getTipo()).isEqualTo(cuentaDto.getTipo());
        assertThat(cuentaActualizada.getFechaCreacion()).isEqualTo(cuentaDto.getFechaCreacion());
        assertThat(cuentaActualizada.getSaldoInicial()).isEqualTo(cuentaDto.getSaldoInicial());

        // Verificar que el original no cambió
        assertThat(cuentaDto.getSaldo()).isEqualTo(saldo);
        assertThat(cuentaDto.isActiva()).isEqualTo(activa);
        assertThat(cuentaActualizada).isNotSameAs(cuentaDto);
    }

    @Test
    @DisplayName("Debería mantener valores originales cuando se pasan Optionals vacíos en conActualizaciones()")
    void deberiaMantenerValoresOriginalesCuandoSePasanOptionalsVaciosEnConActualizaciones() {
        // Act (Actuar)
        CuentaDTO cuentaActualizada = cuentaDto.conActualizaciones(
                Optional.empty(), // mantener saldo original
                Optional.empty()  // mantener estado original
        );

        // Assert (Verificar)
        assertThat(cuentaActualizada).isNotNull();
        assertThat(cuentaActualizada.getSaldo()).isEqualTo(cuentaDto.getSaldo());
        assertThat(cuentaActualizada.isActiva()).isEqualTo(cuentaDto.isActiva());
        assertThat(cuentaActualizada.getNumeroCuenta()).isEqualTo(cuentaDto.getNumeroCuenta());
        assertThat(cuentaActualizada.getTitular()).isEqualTo(cuentaDto.getTitular());
        assertThat(cuentaActualizada.getTipo()).isEqualTo(cuentaDto.getTipo());
        assertThat(cuentaActualizada.getFechaCreacion()).isEqualTo(cuentaDto.getFechaCreacion());
        assertThat(cuentaActualizada.getSaldoInicial()).isEqualTo(cuentaDto.getSaldoInicial());
    }

    @Test
    @DisplayName("Debería actualizar solo algunos campos cuando se pasan Optionals mixtos en conActualizaciones()")
    void deberiaActualizarSoloAlgunosCamposCuandoSePasanOptionalsMixtosEnConActualizaciones() {
        // Arrange (Preparar)
        BigDecimal nuevoSaldo = BigDecimal.valueOf(5000.0);

        // Act (Actuar)
        CuentaDTO cuentaActualizada = cuentaDto.conActualizaciones(
                Optional.of(nuevoSaldo), // actualizar saldo
                Optional.empty()         // mantener estado original
        );

        // Assert (Verificar)
        assertThat(cuentaActualizada).isNotNull();
        assertThat(cuentaActualizada.getSaldo()).isEqualTo(nuevoSaldo);
        assertThat(cuentaActualizada.isActiva()).isEqualTo(cuentaDto.isActiva()); // Mantiene original
        assertThat(cuentaActualizada.getNumeroCuenta()).isEqualTo(cuentaDto.getNumeroCuenta());
        assertThat(cuentaActualizada.getTitular()).isEqualTo(cuentaDto.getTitular());
        assertThat(cuentaActualizada.getTipo()).isEqualTo(cuentaDto.getTipo());
        assertThat(cuentaActualizada.getFechaCreacion()).isEqualTo(cuentaDto.getFechaCreacion());
        assertThat(cuentaActualizada.getSaldoInicial()).isEqualTo(cuentaDto.getSaldoInicial());

        // Verificar que el original no cambió
        assertThat(cuentaDto.getSaldo()).isEqualTo(saldo);
        assertThat(cuentaDto.isActiva()).isEqualTo(activa);
    }

    @Test
    @DisplayName("Debería manejar diferentes tipos de cuenta")
    void deberiaManejarDiferentesTiposDeCuenta() {
        // Arrange (Preparar)
        CuentaDTO cuentaCorriente = CuentaDTO.of(
                numeroCuenta + 1L,
                Optional.of(titular),
                Optional.of(TipoCuenta.CORRIENTE),
                saldoInicial,
                Optional.of(fechaCreacion),
                Optional.of(activa)
        );

        CuentaDTO cuentaPlazoFijo = CuentaDTO.of(
                numeroCuenta + 2L,
                Optional.of(titular),
                Optional.of(TipoCuenta.PLAZO_FIJO),
                saldoInicial,
                Optional.of(fechaCreacion),
                Optional.of(activa)
        );

        // Assert (Verificar)
        assertThat(cuentaCorriente.getTipo()).isEqualTo(TipoCuenta.CORRIENTE);
        assertThat(cuentaPlazoFijo.getTipo()).isEqualTo(TipoCuenta.PLAZO_FIJO);
        assertThat(cuentaDto.getTipo()).isEqualTo(TipoCuenta.AHORRO);
    }

    @Test
    @DisplayName("Debería manejar saldos nulos y con valores")
    void deberiaManejarSaldosNulosYConValores() {
        // Arrange (Preparar)
        BigDecimal saldoCero = BigDecimal.ZERO;
        BigDecimal saldoNegativo = BigDecimal.valueOf(-100.0);

        // Act (Actuar)
        CuentaDTO cuentaSinSaldo = CuentaDTO.of(
                numeroCuenta + 1L,
                Optional.of(titular),
                Optional.of(tipo),
                null, // saldoInicial nulo
                Optional.of(fechaCreacion),
                Optional.of(activa)
        );

        CuentaDTO cuentaConSaldoCero = CuentaDTO.of(
                numeroCuenta + 2L,
                Optional.of(titular),
                Optional.of(tipo),
                saldoCero,
                Optional.of(fechaCreacion),
                Optional.of(activa)
        );

        CuentaDTO cuentaConSaldoNegativo = CuentaDTO.of(
                numeroCuenta + 3L,
                Optional.of(titular),
                Optional.of(tipo),
                saldoNegativo,
                Optional.of(fechaCreacion),
                Optional.of(activa)
        );

        // Assert (Verificar)
        assertThat(cuentaSinSaldo.getSaldoInicial()).isEqualTo(BigDecimal.ZERO);
        assertThat(cuentaSinSaldo.getSaldo()).isEqualTo(BigDecimal.ZERO);
        assertThat(cuentaConSaldoCero.getSaldoInicial()).isEqualTo(saldoCero);
        assertThat(cuentaConSaldoCero.getSaldo()).isEqualTo(saldoCero);
        assertThat(cuentaConSaldoNegativo.getSaldoInicial()).isEqualTo(saldoNegativo);
        assertThat(cuentaConSaldoNegativo.getSaldo()).isEqualTo(saldoNegativo);
    }

    @Test
    @DisplayName("Debería mantener inmutabilidad en todos los campos")
    void deberiaMantenerInmutabilidadEnTodosLosCampos() {
        // Arrange (Preparar)
        CuentaDTO cuentaOriginal = cuentaDto;

        // Act (Actuar) - Crear una nueva cuenta con datos diferentes
        CuentaDTO cuentaNueva = CuentaDTO.of(
                numeroCuenta + 1L,
                Optional.of(titular),
                Optional.of(TipoCuenta.CORRIENTE),
                BigDecimal.valueOf(9999.99),
                Optional.of(fechaCreacion.plusDays(1)),
                Optional.of(false)
        );

        // Assert (Verificar) - Original no debe cambiar
        assertThat(cuentaOriginal.getNumeroCuenta()).isEqualTo(numeroCuenta);
        assertThat(cuentaOriginal.getTitular()).isEqualTo(titular);
        assertThat(cuentaOriginal.getTipo()).isEqualTo(tipo);
        assertThat(cuentaOriginal.getFechaCreacion()).isEqualTo(fechaCreacion);
        assertThat(cuentaOriginal.getSaldoInicial()).isEqualTo(saldoInicial);
        assertThat(cuentaOriginal.getSaldo()).isEqualTo(saldo);
        assertThat(cuentaOriginal.isActiva()).isEqualTo(activa);

        // Verificar que son objetos diferentes
        assertThat(cuentaNueva).isNotSameAs(cuentaOriginal);
        assertThat(cuentaNueva.getNumeroCuenta()).isNotEqualTo(cuentaOriginal.getNumeroCuenta());
        assertThat(cuentaNueva.getTipo()).isNotEqualTo(cuentaOriginal.getTipo());
        assertThat(cuentaNueva.getSaldoInicial()).isNotEqualTo(cuentaOriginal.getSaldoInicial());
        assertThat(cuentaNueva.isActiva()).isNotEqualTo(cuentaOriginal.isActiva());
    }
}
