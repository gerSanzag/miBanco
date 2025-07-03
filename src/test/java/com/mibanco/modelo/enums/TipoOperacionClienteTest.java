package com.mibanco.modelo.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitarios para el enum TipoOperacionCliente
 * 
 * <p>Este test suite verifica:
 * <ul>
 *   <li>Que todos los valores del enum existan</li>
 *   <li>Que los nombres sean correctos</li>
 *   <li>Que el orden sea el esperado</li>
 *   <li>Que el enum funcione correctamente en diferentes contextos</li>
 * </ul>
 */
@DisplayName("TipoOperacionCliente Enum Tests")
class TipoOperacionClienteTest {

    @Nested
    @DisplayName("Valores del enum")
    class ValoresEnumTest {

        @Test
        @DisplayName("Debería tener exactamente 9 valores")
        void deberiaTenerExactamenteNueveValores() {
            // Arrange
            TipoOperacionCliente[] valores = TipoOperacionCliente.values();
            
            // Act & Assert
            assertThat(valores).hasSize(9);
        }

        @Test
        @DisplayName("Debería contener todos los valores esperados")
        void deberiaContenerTodosLosValoresEsperados() {
            // Arrange
            TipoOperacionCliente[] valores = TipoOperacionCliente.values();
            
            // Act & Assert
            assertThat(valores)
                .containsExactlyInAnyOrder(
                    TipoOperacionCliente.CREAR,
                    TipoOperacionCliente.ACTUALIZAR,
                    TipoOperacionCliente.MODIFICAR,
                    TipoOperacionCliente.ELIMINAR,
                    TipoOperacionCliente.CONSULTAR,
                    TipoOperacionCliente.RESTAURAR,
                    TipoOperacionCliente.BLOQUEAR,
                    TipoOperacionCliente.DESBLOQUEAR,
                    TipoOperacionCliente.CAMBIAR_DATOS_CONTACTO
                );
        }

        @Test
        @DisplayName("Debería tener el orden correcto")
        void deberiaTenerElOrdenCorrecto() {
            // Arrange
            TipoOperacionCliente[] valores = TipoOperacionCliente.values();
            
            // Act & Assert
            assertThat(valores)
                .containsExactly(
                    TipoOperacionCliente.CREAR,
                    TipoOperacionCliente.ACTUALIZAR,
                    TipoOperacionCliente.MODIFICAR,
                    TipoOperacionCliente.ELIMINAR,
                    TipoOperacionCliente.CONSULTAR,
                    TipoOperacionCliente.RESTAURAR,
                    TipoOperacionCliente.BLOQUEAR,
                    TipoOperacionCliente.DESBLOQUEAR,
                    TipoOperacionCliente.CAMBIAR_DATOS_CONTACTO
                );
        }
    }

    @Nested
    @DisplayName("Nombres del enum")
    class NombresEnumTest {

        @Test
        @DisplayName("Debería tener nombres correctos")
        void deberiaTenerNombresCorrectos() {
            // Arrange & Act & Assert
            assertThat(TipoOperacionCliente.CREAR.name()).isEqualTo("CREAR");
            assertThat(TipoOperacionCliente.ACTUALIZAR.name()).isEqualTo("ACTUALIZAR");
            assertThat(TipoOperacionCliente.MODIFICAR.name()).isEqualTo("MODIFICAR");
            assertThat(TipoOperacionCliente.ELIMINAR.name()).isEqualTo("ELIMINAR");
            assertThat(TipoOperacionCliente.CONSULTAR.name()).isEqualTo("CONSULTAR");
            assertThat(TipoOperacionCliente.RESTAURAR.name()).isEqualTo("RESTAURAR");
            assertThat(TipoOperacionCliente.BLOQUEAR.name()).isEqualTo("BLOQUEAR");
            assertThat(TipoOperacionCliente.DESBLOQUEAR.name()).isEqualTo("DESBLOQUEAR");
            assertThat(TipoOperacionCliente.CAMBIAR_DATOS_CONTACTO.name()).isEqualTo("CAMBIAR_DATOS_CONTACTO");
        }

        @Test
        @DisplayName("Debería tener toString() igual al nombre")
        void deberiaTenerToStringIgualAlNombre() {
            // Arrange & Act & Assert
            assertThat(TipoOperacionCliente.CREAR.toString()).isEqualTo("CREAR");
            assertThat(TipoOperacionCliente.ACTUALIZAR.toString()).isEqualTo("ACTUALIZAR");
            assertThat(TipoOperacionCliente.MODIFICAR.toString()).isEqualTo("MODIFICAR");
            assertThat(TipoOperacionCliente.ELIMINAR.toString()).isEqualTo("ELIMINAR");
            assertThat(TipoOperacionCliente.CONSULTAR.toString()).isEqualTo("CONSULTAR");
            assertThat(TipoOperacionCliente.RESTAURAR.toString()).isEqualTo("RESTAURAR");
            assertThat(TipoOperacionCliente.BLOQUEAR.toString()).isEqualTo("BLOQUEAR");
            assertThat(TipoOperacionCliente.DESBLOQUEAR.toString()).isEqualTo("DESBLOQUEAR");
            assertThat(TipoOperacionCliente.CAMBIAR_DATOS_CONTACTO.toString()).isEqualTo("CAMBIAR_DATOS_CONTACTO");
        }
    }

    @Nested
    @DisplayName("Funcionalidad del enum")
    class FuncionalidadEnumTest {

        @Test
        @DisplayName("Debería poder obtener valor por nombre")
        void deberiaPoderObtenerValorPorNombre() {
            // Arrange & Act & Assert
            assertThat(TipoOperacionCliente.valueOf("CREAR")).isEqualTo(TipoOperacionCliente.CREAR);
            assertThat(TipoOperacionCliente.valueOf("ACTUALIZAR")).isEqualTo(TipoOperacionCliente.ACTUALIZAR);
            assertThat(TipoOperacionCliente.valueOf("MODIFICAR")).isEqualTo(TipoOperacionCliente.MODIFICAR);
            assertThat(TipoOperacionCliente.valueOf("ELIMINAR")).isEqualTo(TipoOperacionCliente.ELIMINAR);
            assertThat(TipoOperacionCliente.valueOf("CONSULTAR")).isEqualTo(TipoOperacionCliente.CONSULTAR);
            assertThat(TipoOperacionCliente.valueOf("RESTAURAR")).isEqualTo(TipoOperacionCliente.RESTAURAR);
            assertThat(TipoOperacionCliente.valueOf("BLOQUEAR")).isEqualTo(TipoOperacionCliente.BLOQUEAR);
            assertThat(TipoOperacionCliente.valueOf("DESBLOQUEAR")).isEqualTo(TipoOperacionCliente.DESBLOQUEAR);
            assertThat(TipoOperacionCliente.valueOf("CAMBIAR_DATOS_CONTACTO")).isEqualTo(TipoOperacionCliente.CAMBIAR_DATOS_CONTACTO);
        }

        @Test
        @DisplayName("Debería poder comparar valores")
        void deberiaPoderCompararValores() {
            // Arrange
            TipoOperacionCliente crear1 = TipoOperacionCliente.CREAR;
            TipoOperacionCliente crear2 = TipoOperacionCliente.CREAR;
            TipoOperacionCliente actualizar = TipoOperacionCliente.ACTUALIZAR;
            
            // Act & Assert
            assertThat(crear1).isEqualTo(crear2);
            assertThat(crear1).isNotEqualTo(actualizar);
            assertThat(crear1 == crear2).isTrue(); // Comparación por referencia
        }

        @Test
        @DisplayName("Debería poder usar en switch statements")
        void deberiaPoderUsarEnSwitchStatements() {
            // Arrange
            TipoOperacionCliente operacion = TipoOperacionCliente.BLOQUEAR;
            String resultado = "";
            
            // Act
            switch (operacion) {
                case CREAR:
                    resultado = "Cliente creado";
                    break;
                case ACTUALIZAR:
                    resultado = "Cliente actualizado";
                    break;
                case MODIFICAR:
                    resultado = "Cliente modificado";
                    break;
                case ELIMINAR:
                    resultado = "Cliente eliminado";
                    break;
                case CONSULTAR:
                    resultado = "Cliente consultado";
                    break;
                case RESTAURAR:
                    resultado = "Cliente restaurado";
                    break;
                case BLOQUEAR:
                    resultado = "Cliente bloqueado";
                    break;
                case DESBLOQUEAR:
                    resultado = "Cliente desbloqueado";
                    break;
                case CAMBIAR_DATOS_CONTACTO:
                    resultado = "Datos de contacto cambiados";
                    break;
            }
            
            // Assert
            assertThat(resultado).isEqualTo("Cliente bloqueado");
        }

        @Test
        @DisplayName("Debería poder obtener ordinal correcto")
        void deberiaPoderObtenerOrdinalCorrecto() {
            // Arrange & Act & Assert
            assertThat(TipoOperacionCliente.CREAR.ordinal()).isEqualTo(0);
            assertThat(TipoOperacionCliente.ACTUALIZAR.ordinal()).isEqualTo(1);
            assertThat(TipoOperacionCliente.MODIFICAR.ordinal()).isEqualTo(2);
            assertThat(TipoOperacionCliente.ELIMINAR.ordinal()).isEqualTo(3);
            assertThat(TipoOperacionCliente.CONSULTAR.ordinal()).isEqualTo(4);
            assertThat(TipoOperacionCliente.RESTAURAR.ordinal()).isEqualTo(5);
            assertThat(TipoOperacionCliente.BLOQUEAR.ordinal()).isEqualTo(6);
            assertThat(TipoOperacionCliente.DESBLOQUEAR.ordinal()).isEqualTo(7);
            assertThat(TipoOperacionCliente.CAMBIAR_DATOS_CONTACTO.ordinal()).isEqualTo(8);
        }
    }

    @Nested
    @DisplayName("Casos edge")
    class CasosEdgeTest {

        @Test
        @DisplayName("Debería manejar valores nulos correctamente")
        void deberiaManejarValoresNulosCorrectamente() {
            // Arrange
            TipoOperacionCliente operacion = null;
            
            // Act & Assert
            assertThat(operacion).isNull();
        }

        @Test
        @DisplayName("Debería poder iterar sobre todos los valores")
        void deberiaPoderIterarSobreTodosLosValores() {
            // Arrange
            int contador = 0;
            
            // Act
            for (TipoOperacionCliente operacion : TipoOperacionCliente.values()) {
                contador++;
                assertThat(operacion).isNotNull();
            }
            
            // Assert
            assertThat(contador).isEqualTo(9);
        }
    }
}
