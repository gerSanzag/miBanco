package com.mibanco.dtoTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.mibanco.dto.TransaccionDTO;
import com.mibanco.modelo.enums.TipoTransaccion;

import static org.assertj.core.api.Assertions.assertThat;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@DisplayName("Tests para la clase TransaccionDTO")
class TransaccionDtoTest {

    Long id = 1L;
    String numeroCuenta = "ES3412345678901234567890";
    String numeroCuentaDestino = "ES3498765432109876543210";
    TipoTransaccion tipo = TipoTransaccion.TRANSFERENCIA_ENVIADA;
    BigDecimal monto = new BigDecimal("1000.50");
    LocalDateTime fecha = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
    String descripcion = "Transferencia entre cuentas";
    TransaccionDTO transaccionDto;

    @BeforeEach
    void setUp() {
        transaccionDto = TransaccionDTO.builder()
                .id(id)
                .numeroCuenta(numeroCuenta)
                .numeroCuentaDestino(numeroCuentaDestino)
                .tipo(tipo)
                .monto(monto)
                .fecha(fecha)
                .descripcion(descripcion)
                .build();
    }

    @Test
    @DisplayName("Debería crear un TransaccionDTO con datos válidos usando builder")
    void deberiaCrearTransaccionDtoConDatosValidosUsandoBuilder() {
        // Assert (Verificar)
        assertThat(transaccionDto).isNotNull();
        assertThat(transaccionDto.getId()).isEqualTo(id);
        assertThat(transaccionDto.getNumeroCuenta()).isEqualTo(numeroCuenta);
        assertThat(transaccionDto.getNumeroCuentaDestino()).isEqualTo(numeroCuentaDestino);
        assertThat(transaccionDto.getTipo()).isEqualTo(tipo);
        assertThat(transaccionDto.getMonto()).isEqualTo(monto);
        assertThat(transaccionDto.getFecha()).isEqualTo(fecha);
        assertThat(transaccionDto.getDescripcion()).isEqualTo(descripcion);
    }

    @Test
    @DisplayName("Debería crear un TransaccionDTO con datos válidos usando método factory of()")
    void deberiaCrearTransaccionDtoConDatosValidosUsandoFactoryOf() {
        // Act (Actuar)
        TransaccionDTO transaccionConOpcionales = TransaccionDTO.of(
                id, 
                numeroCuenta, 
                Optional.of(numeroCuentaDestino),
                Optional.of(tipo), 
                Optional.of(monto),
                Optional.of(fecha),
                Optional.of(descripcion)
        );

        // Assert (Verificar)
        assertThat(transaccionConOpcionales).isNotNull();
        assertThat(transaccionConOpcionales.getId()).isEqualTo(id);
        assertThat(transaccionConOpcionales.getNumeroCuenta()).isEqualTo(numeroCuenta);
        assertThat(transaccionConOpcionales.getNumeroCuentaDestino()).isEqualTo(numeroCuentaDestino);
        assertThat(transaccionConOpcionales.getTipo()).isEqualTo(tipo);
        assertThat(transaccionConOpcionales.getMonto()).isEqualTo(monto);
        assertThat(transaccionConOpcionales.getFecha()).isEqualTo(fecha);
        assertThat(transaccionConOpcionales.getDescripcion()).isEqualTo(descripcion);
    }

    @Test
    @DisplayName("Debería crear un TransaccionDTO con valores nulos usando método factory of()")
    void deberiaCrearTransaccionDtoConValoresNulosUsandoFactoryOf() {
        // Act (Actuar)
        TransaccionDTO transaccionConNulos = TransaccionDTO.of(
                id, 
                numeroCuenta, 
                Optional.empty(), // numeroCuentaDestino
                Optional.empty(), // tipo
                Optional.empty(), // monto
                Optional.empty(), // fecha
                Optional.empty()  // descripcion
        );

        // Assert (Verificar)
        assertThat(transaccionConNulos).isNotNull();
        assertThat(transaccionConNulos.getId()).isEqualTo(id);
        assertThat(transaccionConNulos.getNumeroCuenta()).isEqualTo(numeroCuenta);
        assertThat(transaccionConNulos.getNumeroCuentaDestino()).isNull();
        assertThat(transaccionConNulos.getTipo()).isNull();
        assertThat(transaccionConNulos.getMonto()).isNull();
        assertThat(transaccionConNulos.getFecha()).isNotNull(); // Valor por defecto
        assertThat(transaccionConNulos.getDescripcion()).isEqualTo(""); // Valor por defecto
    }

    @Test
    @DisplayName("Debería crear un TransaccionDTO con valores mixtos usando método factory of()")
    void deberiaCrearTransaccionDtoConValoresMixtosUsandoFactoryOf() {
        // Act (Actuar)
        TransaccionDTO transaccionConMixtos = TransaccionDTO.of(
                id, 
                numeroCuenta, 
                Optional.of(numeroCuentaDestino), // numeroCuentaDestino con valor
                Optional.empty(),                 // tipo vacío
                Optional.of(monto),               // monto con valor
                Optional.empty(),                 // fecha vacío
                Optional.of(descripcion)          // descripcion con valor
        );

        // Assert (Verificar)
        assertThat(transaccionConMixtos).isNotNull();
        assertThat(transaccionConMixtos.getId()).isEqualTo(id);
        assertThat(transaccionConMixtos.getNumeroCuenta()).isEqualTo(numeroCuenta);
        assertThat(transaccionConMixtos.getNumeroCuentaDestino()).isEqualTo(numeroCuentaDestino);
        assertThat(transaccionConMixtos.getTipo()).isNull();
        assertThat(transaccionConMixtos.getMonto()).isEqualTo(monto);
        assertThat(transaccionConMixtos.getFecha()).isNotNull(); // Valor por defecto
        assertThat(transaccionConMixtos.getDescripcion()).isEqualTo(descripcion);
    }

    @Test
    @DisplayName("Debería verificar inmutabilidad total del DTO")
    void deberiaVerificarInmutabilidadTotalDelDto() {
        // Arrange (Preparar)
        BigDecimal montoOriginal = transaccionDto.getMonto();
        String descripcionOriginal = transaccionDto.getDescripcion();
        TipoTransaccion tipoOriginal = transaccionDto.getTipo();
        String numeroCuentaDestinoOriginal = transaccionDto.getNumeroCuentaDestino();

        // Act (Actuar) - Crear nuevas instancias usando builder estático
        TransaccionDTO transaccion1 = TransaccionDTO.builder()
                .id(transaccionDto.getId())
                .numeroCuenta(transaccionDto.getNumeroCuenta())
                .numeroCuentaDestino(transaccionDto.getNumeroCuentaDestino())
                .tipo(transaccionDto.getTipo())
                .monto(new BigDecimal("5000.00"))
                .fecha(transaccionDto.getFecha())
                .descripcion(transaccionDto.getDescripcion())
                .build();
        
        TransaccionDTO transaccion2 = TransaccionDTO.builder()
                .id(transaccion1.getId())
                .numeroCuenta(transaccion1.getNumeroCuenta())
                .numeroCuentaDestino(transaccion1.getNumeroCuentaDestino())
                .tipo(transaccion1.getTipo())
                .monto(transaccion1.getMonto())
                .fecha(transaccion1.getFecha())
                .descripcion("Descripción actualizada")
                .build();
        
        TransaccionDTO transaccion3 = TransaccionDTO.builder()
                .id(transaccion2.getId())
                .numeroCuenta(transaccion2.getNumeroCuenta())
                .numeroCuentaDestino(transaccion2.getNumeroCuentaDestino())
                .tipo(TipoTransaccion.DEPOSITO)
                .monto(transaccion2.getMonto())
                .fecha(transaccion2.getFecha())
                .descripcion(transaccion2.getDescripcion())
                .build();
        
        TransaccionDTO transaccion4 = TransaccionDTO.builder()
                .id(transaccion3.getId())
                .numeroCuenta(transaccion3.getNumeroCuenta())
                .numeroCuentaDestino("ES3411111111111111111111")
                .tipo(transaccion3.getTipo())
                .monto(transaccion3.getMonto())
                .fecha(transaccion3.getFecha())
                .descripcion(transaccion3.getDescripcion())
                .build();

        // Assert (Verificar)
        // Verificar que cada actualización crea una nueva instancia
        assertThat(transaccion1).isNotSameAs(transaccionDto);
        assertThat(transaccion2).isNotSameAs(transaccion1);
        assertThat(transaccion3).isNotSameAs(transaccion2);
        assertThat(transaccion4).isNotSameAs(transaccion3);

        // Verificar que el original no cambió
        assertThat(transaccionDto.getMonto()).isEqualTo(montoOriginal);
        assertThat(transaccionDto.getDescripcion()).isEqualTo(descripcionOriginal);
        assertThat(transaccionDto.getTipo()).isEqualTo(tipoOriginal);
        assertThat(transaccionDto.getNumeroCuentaDestino()).isEqualTo(numeroCuentaDestinoOriginal);
    }
}
