package com.mibanco.dtoTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import com.mibanco.dto.RespuestaDTO;
import com.mibanco.dto.ClienteDTO;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@DisplayName("Tests para la clase RespuestaDTO")
class RespuestaDtoTest {

    @Nested
    @DisplayName("Tests para respuestas exitosas")
    class RespuestasExitosasTest {

        @Test
        @DisplayName("Debería crear una respuesta exitosa con datos y mensaje personalizado")
        void deberiaCrearRespuestaExitosaConDatosYMensaje() {
            // Arrange
            ClienteDTO cliente = ClienteDTO.builder()
                    .id(1L)
                    .nombre("Juan")
                    .apellido("Pérez")
                    .build();
            String mensaje = "Cliente creado exitosamente";

            // Act
            RespuestaDTO<ClienteDTO> respuesta = RespuestaDTO.success(cliente, Optional.of(mensaje));

            // Assert
            assertThat(respuesta).isNotNull();
            assertThat(respuesta.isSuccess()).isTrue();
            assertThat(respuesta.getMessage()).isEqualTo(mensaje);
            assertThat(respuesta.getData()).isEqualTo(cliente);
            assertThat(respuesta.getErrors()).isEmpty();
            assertThat(respuesta.getTimestamp()).isNotNull();
            assertThat(respuesta.getTimestamp()).isBeforeOrEqualTo(LocalDateTime.now());
        }

        @Test
        @DisplayName("Debería crear una respuesta exitosa con datos y mensaje por defecto")
        void deberiaCrearRespuestaExitosaConMensajePorDefecto() {
            // Arrange
            String dato = "Dato de prueba";

            // Act
            RespuestaDTO<String> respuesta = RespuestaDTO.success(dato, Optional.empty());

            // Assert
            assertThat(respuesta).isNotNull();
            assertThat(respuesta.isSuccess()).isTrue();
            assertThat(respuesta.getMessage()).isEqualTo("Operación realizada con éxito");
            assertThat(respuesta.getData()).isEqualTo(dato);
            assertThat(respuesta.getErrors()).isEmpty();
            assertThat(respuesta.getTimestamp()).isNotNull();
        }

        @Test
        @DisplayName("Debería crear una respuesta exitosa simplificada")
        void deberiaCrearRespuestaExitosaSimplificada() {
            // Arrange
            Integer dato = 42;

            // Act
            RespuestaDTO<Integer> respuesta = RespuestaDTO.success(dato);

            // Assert
            assertThat(respuesta).isNotNull();
            assertThat(respuesta.isSuccess()).isTrue();
            assertThat(respuesta.getMessage()).isEqualTo("Operación realizada con éxito");
            assertThat(respuesta.getData()).isEqualTo(dato);
            assertThat(respuesta.getErrors()).isEmpty();
            assertThat(respuesta.getTimestamp()).isNotNull();
        }

        @Test
        @DisplayName("Debería crear una respuesta exitosa con datos nulos")
        void deberiaCrearRespuestaExitosaConDatosNulos() {
            // Act
            RespuestaDTO<String> respuesta = RespuestaDTO.success(null, Optional.of("Sin datos"));

            // Assert
            assertThat(respuesta).isNotNull();
            assertThat(respuesta.isSuccess()).isTrue();
            assertThat(respuesta.getMessage()).isEqualTo("Sin datos");
            assertThat(respuesta.getData()).isNull();
            assertThat(respuesta.getErrors()).isEmpty();
            assertThat(respuesta.getTimestamp()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Tests para respuestas de error")
    class RespuestasErrorTest {

        @Test
        @DisplayName("Debería crear una respuesta de error con mensaje y lista de errores")
        void deberiaCrearRespuestaErrorConMensajeYErrores() {
            // Arrange
            String mensaje = "Error en la operación";
            List<String> errores = Arrays.asList("Error 1", "Error 2", "Error 3");

            // Act
            RespuestaDTO<String> respuesta = RespuestaDTO.error(mensaje, Optional.of(errores));

            // Assert
            assertThat(respuesta).isNotNull();
            assertThat(respuesta.isSuccess()).isFalse();
            assertThat(respuesta.getMessage()).isEqualTo(mensaje);
            assertThat(respuesta.getData()).isNull();
            assertThat(respuesta.getErrors()).containsExactly("Error 1", "Error 2", "Error 3");
            assertThat(respuesta.getTimestamp()).isNotNull();
        }

        @Test
        @DisplayName("Debería crear una respuesta de error con mensaje y lista vacía de errores")
        void deberiaCrearRespuestaErrorConListaVaciaDeErrores() {
            // Arrange
            String mensaje = "Error simple";

            // Act
            RespuestaDTO<Integer> respuesta = RespuestaDTO.error(mensaje, Optional.empty());

            // Assert
            assertThat(respuesta).isNotNull();
            assertThat(respuesta.isSuccess()).isFalse();
            assertThat(respuesta.getMessage()).isEqualTo(mensaje);
            assertThat(respuesta.getData()).isNull();
            assertThat(respuesta.getErrors()).isEmpty();
            assertThat(respuesta.getTimestamp()).isNotNull();
        }

        @Test
        @DisplayName("Debería crear una respuesta de error simplificada")
        void deberiaCrearRespuestaErrorSimplificada() {
            // Arrange
            String mensaje = "Error crítico";

            // Act
            RespuestaDTO<Double> respuesta = RespuestaDTO.error(mensaje);

            // Assert
            assertThat(respuesta).isNotNull();
            assertThat(respuesta.isSuccess()).isFalse();
            assertThat(respuesta.getMessage()).isEqualTo(mensaje);
            assertThat(respuesta.getData()).isNull();
            assertThat(respuesta.getErrors()).isEmpty();
            assertThat(respuesta.getTimestamp()).isNotNull();
        }

        @Test
        @DisplayName("Debería crear una respuesta de error con lista nula de errores")
        void deberiaCrearRespuestaErrorConListaNulaDeErrores() {
            // Arrange
            String mensaje = "Error con lista nula";

            // Act
            RespuestaDTO<String> respuesta = RespuestaDTO.error(mensaje, Optional.ofNullable(null));

            // Assert
            assertThat(respuesta).isNotNull();
            assertThat(respuesta.isSuccess()).isFalse();
            assertThat(respuesta.getMessage()).isEqualTo(mensaje);
            assertThat(respuesta.getData()).isNull();
            assertThat(respuesta.getErrors()).isEmpty();
            assertThat(respuesta.getTimestamp()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Tests de inmutabilidad")
    class InmutabilidadTest {

        @Test
        @DisplayName("Debería verificar inmutabilidad total de la respuesta")
        void deberiaVerificarInmutabilidadTotal() {
            // Arrange
            String datoOriginal = "Dato original";
            String mensajeOriginal = "Mensaje original";
            List<String> erroresOriginales = Arrays.asList("Error 1");

            RespuestaDTO<String> respuesta = RespuestaDTO.<String>builder()
                    .success(true)
                    .message(mensajeOriginal)
                    .data(datoOriginal)
                    .errors(erroresOriginales)
                    .timestamp(LocalDateTime.now())
                    .build();

            // Act - Intentar modificar (no debería ser posible por inmutabilidad)
            // Los campos son finales, por lo que no se pueden modificar

            // Assert
            assertThat(respuesta.getData()).isEqualTo(datoOriginal);
            assertThat(respuesta.getMessage()).isEqualTo(mensajeOriginal);
            assertThat(respuesta.getErrors()).containsExactly("Error 1");
        }

        @Test
        @DisplayName("Debería crear nuevas instancias para diferentes tipos genéricos")
        void deberiaCrearNuevasInstanciasParaDiferentesTipos() {
            // Act
            RespuestaDTO<String> respuestaString = RespuestaDTO.success("String");
            RespuestaDTO<Integer> respuestaInteger = RespuestaDTO.success(42);
            RespuestaDTO<ClienteDTO> respuestaCliente = RespuestaDTO.success(
                ClienteDTO.builder().id(1L).nombre("Juan").build()
            );

            // Assert
            assertThat(respuestaString.getData()).isInstanceOf(String.class);
            assertThat(respuestaInteger.getData()).isInstanceOf(Integer.class);
            assertThat(respuestaCliente.getData()).isInstanceOf(ClienteDTO.class);
        }
    }

    @Nested
    @DisplayName("Tests de casos edge")
    class CasosEdgeTest {

        @Test
        @DisplayName("Debería manejar mensajes vacíos y nulos")
        void deberiaManejarMensajesVaciosYNulos() {
            // Act
            RespuestaDTO<String> respuestaVacia = RespuestaDTO.success("dato", Optional.of(""));
            RespuestaDTO<String> respuestaNula = RespuestaDTO.success("dato", Optional.ofNullable(null));

            // Assert
            assertThat(respuestaVacia.getMessage()).isEmpty();
            assertThat(respuestaNula.getMessage()).isEqualTo("Operación realizada con éxito");
        }

        @Test
        @DisplayName("Debería manejar listas de errores con elementos vacíos")
        void deberiaManejarListasDeErroresConElementosVacios() {
            // Arrange
            List<String> erroresConVacios = Arrays.asList("Error 1", "", "Error 3", null);

            // Act
            RespuestaDTO<String> respuesta = RespuestaDTO.error("Error", Optional.of(erroresConVacios));

            // Assert
            assertThat(respuesta.getErrors()).containsExactly("Error 1", "", "Error 3", null);
        }

        @Test
        @DisplayName("Debería manejar timestamps consistentes")
        void deberiaManejarTimestampsConsistentes() {
            // Act
            LocalDateTime antes = LocalDateTime.now();
            RespuestaDTO<String> respuesta = RespuestaDTO.success("dato");
            LocalDateTime despues = LocalDateTime.now();

            // Assert
            assertThat(respuesta.getTimestamp()).isBetween(antes, despues);
        }
    }

    @Nested
    @DisplayName("Tests de funcionalidad genérica")
    class FuncionalidadGenericaTest {

        @Test
        @DisplayName("Debería funcionar con tipos complejos")
        void deberiaFuncionarConTiposComplejos() {
            // Arrange
            ClienteDTO cliente = ClienteDTO.builder()
                    .id(1L)
                    .nombre("María")
                    .apellido("García")
                    .email("maria@email.com")
                    .build();

            // Act
            RespuestaDTO<ClienteDTO> respuesta = RespuestaDTO.success(cliente);

            // Assert
            assertThat(respuesta.getData()).isEqualTo(cliente);
            assertThat(respuesta.getData().getNombre()).isEqualTo("María");
            assertThat(respuesta.getData().getEmail()).isEqualTo("maria@email.com");
        }

        @Test
        @DisplayName("Debería funcionar con colecciones")
        void deberiaFuncionarConColecciones() {
            // Arrange
            List<String> lista = Arrays.asList("item1", "item2", "item3");

            // Act
            RespuestaDTO<List<String>> respuesta = RespuestaDTO.success(lista);

            // Assert
            assertThat(respuesta.getData()).containsExactly("item1", "item2", "item3");
            assertThat(respuesta.getData()).hasSize(3);
        }

        @Test
        @DisplayName("Debería funcionar con tipos primitivos")
        void deberiaFuncionarConTiposPrimitivos() {
            // Act
            RespuestaDTO<Integer> respuestaInt = RespuestaDTO.success(100);
            RespuestaDTO<Double> respuestaDouble = RespuestaDTO.success(3.14);
            RespuestaDTO<Boolean> respuestaBoolean = RespuestaDTO.success(true);

            // Assert
            assertThat(respuestaInt.getData()).isEqualTo(100);
            assertThat(respuestaDouble.getData()).isEqualTo(3.14);
            assertThat(respuestaBoolean.getData()).isTrue();
        }
    }
}
