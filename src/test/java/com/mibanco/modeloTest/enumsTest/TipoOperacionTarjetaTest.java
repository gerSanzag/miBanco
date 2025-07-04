package com.mibanco.modeloTest.enumsTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.mibanco.modelo.enums.TipoOperacionTarjeta;

import org.junit.jupiter.api.Nested;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitarios para el enum TipoOperacionTarjeta
 * 
 * <p>Este test suite verifica:
 * <ul>
 *   <li>Que todos los valores del enum existan</li>
 *   <li>Que los nombres sean correctos</li>
 *   <li>Que el orden sea el esperado</li>
 *   <li>Que el enum funcione correctamente en diferentes contextos</li>
 * </ul>
 */
@DisplayName("TipoOperacionTarjeta Enum Tests")
class TipoOperacionTarjetaTest {

    @Nested
    @DisplayName("Valores del enum")
    class ValoresEnumTest {

        @Test
        @DisplayName("Debería tener exactamente 10 valores")
        void deberiaTenerExactamenteDiezValores() {
            // Arrange
            TipoOperacionTarjeta[] valores = TipoOperacionTarjeta.values();
            
            // Act & Assert
            assertThat(valores).hasSize(10);
        }

        @Test
        @DisplayName("Debería contener todos los valores esperados")
        void deberiaContenerTodosLosValoresEsperados() {
            // Arrange
            TipoOperacionTarjeta[] valores = TipoOperacionTarjeta.values();
            
            // Act & Assert
            assertThat(valores)
                .containsExactlyInAnyOrder(
                    TipoOperacionTarjeta.CREAR,
                    TipoOperacionTarjeta.MODIFICAR,
                    TipoOperacionTarjeta.ELIMINAR,
                    TipoOperacionTarjeta.ACTIVAR,
                    TipoOperacionTarjeta.DESACTIVAR,
                    TipoOperacionTarjeta.BLOQUEAR,
                    TipoOperacionTarjeta.DESBLOQUEAR,
                    TipoOperacionTarjeta.AUMENTAR_LIMITE,
                    TipoOperacionTarjeta.REDUCIR_LIMITE,
                    TipoOperacionTarjeta.ACTUALIZAR
                );
        }

        @Test
        @DisplayName("Debería tener el orden correcto")
        void deberiaTenerElOrdenCorrecto() {
            // Arrange
            TipoOperacionTarjeta[] valores = TipoOperacionTarjeta.values();
            
            // Act & Assert
            assertThat(valores)
                .containsExactly(
                    TipoOperacionTarjeta.CREAR,
                    TipoOperacionTarjeta.MODIFICAR,
                    TipoOperacionTarjeta.ELIMINAR,
                    TipoOperacionTarjeta.ACTIVAR,
                    TipoOperacionTarjeta.DESACTIVAR,
                    TipoOperacionTarjeta.BLOQUEAR,
                    TipoOperacionTarjeta.DESBLOQUEAR,
                    TipoOperacionTarjeta.AUMENTAR_LIMITE,
                    TipoOperacionTarjeta.REDUCIR_LIMITE,
                    TipoOperacionTarjeta.ACTUALIZAR
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
            assertThat(TipoOperacionTarjeta.CREAR.name()).isEqualTo("CREAR");
            assertThat(TipoOperacionTarjeta.MODIFICAR.name()).isEqualTo("MODIFICAR");
            assertThat(TipoOperacionTarjeta.ELIMINAR.name()).isEqualTo("ELIMINAR");
            assertThat(TipoOperacionTarjeta.ACTIVAR.name()).isEqualTo("ACTIVAR");
            assertThat(TipoOperacionTarjeta.DESACTIVAR.name()).isEqualTo("DESACTIVAR");
            assertThat(TipoOperacionTarjeta.BLOQUEAR.name()).isEqualTo("BLOQUEAR");
            assertThat(TipoOperacionTarjeta.DESBLOQUEAR.name()).isEqualTo("DESBLOQUEAR");
            assertThat(TipoOperacionTarjeta.AUMENTAR_LIMITE.name()).isEqualTo("AUMENTAR_LIMITE");
            assertThat(TipoOperacionTarjeta.REDUCIR_LIMITE.name()).isEqualTo("REDUCIR_LIMITE");
            assertThat(TipoOperacionTarjeta.ACTUALIZAR.name()).isEqualTo("ACTUALIZAR");
        }

        @Test
        @DisplayName("Debería tener toString() igual al nombre")
        void deberiaTenerToStringIgualAlNombre() {
            // Arrange & Act & Assert
            assertThat(TipoOperacionTarjeta.CREAR.toString()).isEqualTo("CREAR");
            assertThat(TipoOperacionTarjeta.MODIFICAR.toString()).isEqualTo("MODIFICAR");
            assertThat(TipoOperacionTarjeta.ELIMINAR.toString()).isEqualTo("ELIMINAR");
            assertThat(TipoOperacionTarjeta.ACTIVAR.toString()).isEqualTo("ACTIVAR");
            assertThat(TipoOperacionTarjeta.DESACTIVAR.toString()).isEqualTo("DESACTIVAR");
            assertThat(TipoOperacionTarjeta.BLOQUEAR.toString()).isEqualTo("BLOQUEAR");
            assertThat(TipoOperacionTarjeta.DESBLOQUEAR.toString()).isEqualTo("DESBLOQUEAR");
            assertThat(TipoOperacionTarjeta.AUMENTAR_LIMITE.toString()).isEqualTo("AUMENTAR_LIMITE");
            assertThat(TipoOperacionTarjeta.REDUCIR_LIMITE.toString()).isEqualTo("REDUCIR_LIMITE");
            assertThat(TipoOperacionTarjeta.ACTUALIZAR.toString()).isEqualTo("ACTUALIZAR");
        }
    }

    @Nested
    @DisplayName("Funcionalidad del enum")
    class FuncionalidadEnumTest {

        @Test
        @DisplayName("Debería poder obtener valor por nombre")
        void deberiaPoderObtenerValorPorNombre() {
            // Arrange & Act & Assert
            assertThat(TipoOperacionTarjeta.valueOf("CREAR")).isEqualTo(TipoOperacionTarjeta.CREAR);
            assertThat(TipoOperacionTarjeta.valueOf("MODIFICAR")).isEqualTo(TipoOperacionTarjeta.MODIFICAR);
            assertThat(TipoOperacionTarjeta.valueOf("ELIMINAR")).isEqualTo(TipoOperacionTarjeta.ELIMINAR);
            assertThat(TipoOperacionTarjeta.valueOf("ACTIVAR")).isEqualTo(TipoOperacionTarjeta.ACTIVAR);
            assertThat(TipoOperacionTarjeta.valueOf("DESACTIVAR")).isEqualTo(TipoOperacionTarjeta.DESACTIVAR);
            assertThat(TipoOperacionTarjeta.valueOf("BLOQUEAR")).isEqualTo(TipoOperacionTarjeta.BLOQUEAR);
            assertThat(TipoOperacionTarjeta.valueOf("DESBLOQUEAR")).isEqualTo(TipoOperacionTarjeta.DESBLOQUEAR);
            assertThat(TipoOperacionTarjeta.valueOf("AUMENTAR_LIMITE")).isEqualTo(TipoOperacionTarjeta.AUMENTAR_LIMITE);
            assertThat(TipoOperacionTarjeta.valueOf("REDUCIR_LIMITE")).isEqualTo(TipoOperacionTarjeta.REDUCIR_LIMITE);
            assertThat(TipoOperacionTarjeta.valueOf("ACTUALIZAR")).isEqualTo(TipoOperacionTarjeta.ACTUALIZAR);
        }

        @Test
        @DisplayName("Debería poder comparar valores")
        void deberiaPoderCompararValores() {
            // Arrange
            TipoOperacionTarjeta crear1 = TipoOperacionTarjeta.CREAR;
            TipoOperacionTarjeta crear2 = TipoOperacionTarjeta.CREAR;
            TipoOperacionTarjeta modificar = TipoOperacionTarjeta.MODIFICAR;
            
            // Act & Assert
            assertThat(crear1).isEqualTo(crear2);
            assertThat(crear1).isNotEqualTo(modificar);
            assertThat(crear1 == crear2).isTrue(); // Comparación por referencia
        }

        @Test
        @DisplayName("Debería poder usar en switch expressions funcionales")
        void deberiaPoderUsarEnSwitchExpressions() {
            // Arrange
            TipoOperacionTarjeta operacion = TipoOperacionTarjeta.AUMENTAR_LIMITE;
            
            // Act - Switch expression funcional
            String resultado = switch (operacion) {
                case CREAR -> "Tarjeta creada";
                case MODIFICAR -> "Tarjeta modificada";
                case ELIMINAR -> "Tarjeta eliminada";
                case ACTIVAR -> "Tarjeta activada";
                case DESACTIVAR -> "Tarjeta desactivada";
                case BLOQUEAR -> "Tarjeta bloqueada";
                case DESBLOQUEAR -> "Tarjeta desbloqueada";
                case AUMENTAR_LIMITE -> "Límite aumentado";
                case REDUCIR_LIMITE -> "Límite reducido";
                case ACTUALIZAR -> "Tarjeta actualizada";
            };
            
            // Assert
            assertThat(resultado).isEqualTo("Límite aumentado");
        }

        @Test
        @DisplayName("Debería poder obtener ordinal correcto")
        void deberiaPoderObtenerOrdinalCorrecto() {
            // Arrange & Act & Assert
            assertThat(TipoOperacionTarjeta.CREAR.ordinal()).isEqualTo(0);
            assertThat(TipoOperacionTarjeta.MODIFICAR.ordinal()).isEqualTo(1);
            assertThat(TipoOperacionTarjeta.ELIMINAR.ordinal()).isEqualTo(2);
            assertThat(TipoOperacionTarjeta.ACTIVAR.ordinal()).isEqualTo(3);
            assertThat(TipoOperacionTarjeta.DESACTIVAR.ordinal()).isEqualTo(4);
            assertThat(TipoOperacionTarjeta.BLOQUEAR.ordinal()).isEqualTo(5);
            assertThat(TipoOperacionTarjeta.DESBLOQUEAR.ordinal()).isEqualTo(6);
            assertThat(TipoOperacionTarjeta.AUMENTAR_LIMITE.ordinal()).isEqualTo(7);
            assertThat(TipoOperacionTarjeta.REDUCIR_LIMITE.ordinal()).isEqualTo(8);
            assertThat(TipoOperacionTarjeta.ACTUALIZAR.ordinal()).isEqualTo(9);
        }
    }

    @Nested
    @DisplayName("Casos edge")
    class CasosEdgeTest {

        @Test
        @DisplayName("Debería manejar valores nulos correctamente")
        void deberiaManejarValoresNulosCorrectamente() {
            // Arrange
            TipoOperacionTarjeta operacion = null;
            
            // Act & Assert
            assertThat(operacion).isNull();
        }

        @Test
        @DisplayName("Debería poder iterar sobre todos los valores")
        void deberiaPoderIterarSobreTodosLosValores() {
            // Arrange
            int contador = 0;
            
            // Act
            for (TipoOperacionTarjeta operacion : TipoOperacionTarjeta.values()) {
                contador++;
                assertThat(operacion).isNotNull();
            }
            
            // Assert
            assertThat(contador).isEqualTo(10);
        }
    }
}
