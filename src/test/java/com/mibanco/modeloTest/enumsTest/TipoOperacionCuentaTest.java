package com.mibanco.modeloTest.enumsTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.mibanco.modelo.enums.TipoOperacionCuenta;

import org.junit.jupiter.api.Nested;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitarios para el enum TipoOperacionCuenta
 * 
 * <p>Este test suite verifica:
 * <ul>
 *   <li>Que todos los valores del enum existan</li>
 *   <li>Que los nombres sean correctos</li>
 *   <li>Que el orden sea el esperado</li>
 *   <li>Que el enum funcione correctamente en diferentes contextos</li>
 * </ul>
 */
@DisplayName("TipoOperacionCuenta Enum Tests")
class TipoOperacionCuentaTest {

    @Nested
    @DisplayName("Valores del enum")
    class ValoresEnumTest {

        @Test
        @DisplayName("Debería tener exactamente 9 valores")
        void deberiaTenerExactamenteNueveValores() {
            // Arrange
            TipoOperacionCuenta[] valores = TipoOperacionCuenta.values();
            
            // Act & Assert
            assertThat(valores).hasSize(9);
        }

        @Test
        @DisplayName("Debería contener todos los valores esperados")
        void deberiaContenerTodosLosValoresEsperados() {
            // Arrange
            TipoOperacionCuenta[] valores = TipoOperacionCuenta.values();
            
            // Act & Assert
            assertThat(valores)
                .containsExactlyInAnyOrder(
                    TipoOperacionCuenta.CREAR,
                    TipoOperacionCuenta.MODIFICAR,
                    TipoOperacionCuenta.ELIMINAR,
                    TipoOperacionCuenta.ACTIVAR,
                    TipoOperacionCuenta.DESACTIVAR,
                    TipoOperacionCuenta.BLOQUEAR,
                    TipoOperacionCuenta.DESBLOQUEAR,
                    TipoOperacionCuenta.ACTUALIZAR,
                    TipoOperacionCuenta.RESTAURAR
                );
        }

        @Test
        @DisplayName("Debería tener el orden correcto")
        void deberiaTenerElOrdenCorrecto() {
            // Arrange
            TipoOperacionCuenta[] valores = TipoOperacionCuenta.values();
            
            // Act & Assert
            assertThat(valores)
                .containsExactly(
                    TipoOperacionCuenta.CREAR,
                    TipoOperacionCuenta.MODIFICAR,
                    TipoOperacionCuenta.ELIMINAR,
                    TipoOperacionCuenta.ACTIVAR,
                    TipoOperacionCuenta.DESACTIVAR,
                    TipoOperacionCuenta.BLOQUEAR,
                    TipoOperacionCuenta.DESBLOQUEAR,
                    TipoOperacionCuenta.ACTUALIZAR,
                    TipoOperacionCuenta.RESTAURAR
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
            assertThat(TipoOperacionCuenta.CREAR.name()).isEqualTo("CREAR");
            assertThat(TipoOperacionCuenta.MODIFICAR.name()).isEqualTo("MODIFICAR");
            assertThat(TipoOperacionCuenta.ELIMINAR.name()).isEqualTo("ELIMINAR");
            assertThat(TipoOperacionCuenta.ACTIVAR.name()).isEqualTo("ACTIVAR");
            assertThat(TipoOperacionCuenta.DESACTIVAR.name()).isEqualTo("DESACTIVAR");
            assertThat(TipoOperacionCuenta.BLOQUEAR.name()).isEqualTo("BLOQUEAR");
            assertThat(TipoOperacionCuenta.DESBLOQUEAR.name()).isEqualTo("DESBLOQUEAR");
            assertThat(TipoOperacionCuenta.ACTUALIZAR.name()).isEqualTo("ACTUALIZAR");
            assertThat(TipoOperacionCuenta.RESTAURAR.name()).isEqualTo("RESTAURAR");
        }

        @Test
        @DisplayName("Debería tener toString() igual al nombre")
        void deberiaTenerToStringIgualAlNombre() {
            // Arrange & Act & Assert
            assertThat(TipoOperacionCuenta.CREAR.toString()).isEqualTo("CREAR");
            assertThat(TipoOperacionCuenta.MODIFICAR.toString()).isEqualTo("MODIFICAR");
            assertThat(TipoOperacionCuenta.ELIMINAR.toString()).isEqualTo("ELIMINAR");
            assertThat(TipoOperacionCuenta.ACTIVAR.toString()).isEqualTo("ACTIVAR");
            assertThat(TipoOperacionCuenta.DESACTIVAR.toString()).isEqualTo("DESACTIVAR");
            assertThat(TipoOperacionCuenta.BLOQUEAR.toString()).isEqualTo("BLOQUEAR");
            assertThat(TipoOperacionCuenta.DESBLOQUEAR.toString()).isEqualTo("DESBLOQUEAR");
            assertThat(TipoOperacionCuenta.ACTUALIZAR.toString()).isEqualTo("ACTUALIZAR");
            assertThat(TipoOperacionCuenta.RESTAURAR.toString()).isEqualTo("RESTAURAR");
        }
    }

    @Nested
    @DisplayName("Funcionalidad del enum")
    class FuncionalidadEnumTest {

        @Test
        @DisplayName("Debería poder obtener valor por nombre")
        void deberiaPoderObtenerValorPorNombre() {
            // Arrange & Act & Assert
            assertThat(TipoOperacionCuenta.valueOf("CREAR")).isEqualTo(TipoOperacionCuenta.CREAR);
            assertThat(TipoOperacionCuenta.valueOf("MODIFICAR")).isEqualTo(TipoOperacionCuenta.MODIFICAR);
            assertThat(TipoOperacionCuenta.valueOf("ELIMINAR")).isEqualTo(TipoOperacionCuenta.ELIMINAR);
            assertThat(TipoOperacionCuenta.valueOf("ACTIVAR")).isEqualTo(TipoOperacionCuenta.ACTIVAR);
            assertThat(TipoOperacionCuenta.valueOf("DESACTIVAR")).isEqualTo(TipoOperacionCuenta.DESACTIVAR);
            assertThat(TipoOperacionCuenta.valueOf("BLOQUEAR")).isEqualTo(TipoOperacionCuenta.BLOQUEAR);
            assertThat(TipoOperacionCuenta.valueOf("DESBLOQUEAR")).isEqualTo(TipoOperacionCuenta.DESBLOQUEAR);
            assertThat(TipoOperacionCuenta.valueOf("ACTUALIZAR")).isEqualTo(TipoOperacionCuenta.ACTUALIZAR);
            assertThat(TipoOperacionCuenta.valueOf("RESTAURAR")).isEqualTo(TipoOperacionCuenta.RESTAURAR);
        }

        @Test
        @DisplayName("Debería poder comparar valores")
        void deberiaPoderCompararValores() {
            // Arrange
            TipoOperacionCuenta crear1 = TipoOperacionCuenta.CREAR;
            TipoOperacionCuenta crear2 = TipoOperacionCuenta.CREAR;
            TipoOperacionCuenta modificar = TipoOperacionCuenta.MODIFICAR;
            
            // Act & Assert
            assertThat(crear1).isEqualTo(crear2);
            assertThat(crear1).isNotEqualTo(modificar);
            assertThat(crear1 == crear2).isTrue(); // Comparación por referencia
        }

        @Test
        @DisplayName("Debería poder usar en switch expressions funcionales")
        void deberiaPoderUsarEnSwitchExpressions() {
            // Arrange
            TipoOperacionCuenta operacion = TipoOperacionCuenta.ACTIVAR;
            
            // Act - Switch expression funcional
            String resultado = switch (operacion) {
                case CREAR -> "Cuenta creada";
                case MODIFICAR -> "Cuenta modificada";
                case ELIMINAR -> "Cuenta eliminada";
                case ACTIVAR -> "Cuenta activada";
                case DESACTIVAR -> "Cuenta desactivada";
                case BLOQUEAR -> "Cuenta bloqueada";
                case DESBLOQUEAR -> "Cuenta desbloqueada";
                case ACTUALIZAR -> "Cuenta actualizada";
                case RESTAURAR -> "Cuenta restaurada";
            };
            
            // Assert
            assertThat(resultado).isEqualTo("Cuenta activada");
        }

        @Test
        @DisplayName("Debería poder obtener ordinal correcto")
        void deberiaPoderObtenerOrdinalCorrecto() {
            // Arrange & Act & Assert
            assertThat(TipoOperacionCuenta.CREAR.ordinal()).isEqualTo(0);
            assertThat(TipoOperacionCuenta.MODIFICAR.ordinal()).isEqualTo(1);
            assertThat(TipoOperacionCuenta.ELIMINAR.ordinal()).isEqualTo(2);
            assertThat(TipoOperacionCuenta.ACTIVAR.ordinal()).isEqualTo(3);
            assertThat(TipoOperacionCuenta.DESACTIVAR.ordinal()).isEqualTo(4);
            assertThat(TipoOperacionCuenta.BLOQUEAR.ordinal()).isEqualTo(5);
            assertThat(TipoOperacionCuenta.DESBLOQUEAR.ordinal()).isEqualTo(6);
            assertThat(TipoOperacionCuenta.ACTUALIZAR.ordinal()).isEqualTo(7);
            assertThat(TipoOperacionCuenta.RESTAURAR.ordinal()).isEqualTo(8);
        }
    }

    @Nested
    @DisplayName("Casos edge")
    class CasosEdgeTest {

        @Test
        @DisplayName("Debería manejar valores nulos correctamente")
        void deberiaManejarValoresNulosCorrectamente() {
            // Arrange
            TipoOperacionCuenta operacion = null;
            
            // Act & Assert
            assertThat(operacion).isNull();
        }

        @Test
        @DisplayName("Debería poder iterar sobre todos los valores")
        void deberiaPoderIterarSobreTodosLosValores() {
            // Arrange
            int contador = 0;
            
            // Act
            for (TipoOperacionCuenta operacion : TipoOperacionCuenta.values()) {
                contador++;
                assertThat(operacion).isNotNull();
            }
            
            // Assert
            assertThat(contador).isEqualTo(9);
        }
    }
}
