package com.mibanco.utilTest;

import com.mibanco.util.ReflexionUtil;
import com.mibanco.util.ReflexionUtil.NoSolicitar;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests para la clase ReflexionUtil")
class ReflexionUtilTest {

    // 1. Clase con solo campos simples
    static class SoloSimples {
        String nombre;
        int edad;
        double saldo;
    }

    // 2. Clase con campos Optional
    static class ConOptional {
        String nombre;
        Optional<String> email;
        int edad;
        Optional<Double> saldo;
    }

    // 3. Clase con campos @NoSolicitar
    static class ConNoSolicitar {
        String nombre;
        @NoSolicitar String secreto;
        int edad;
        @NoSolicitar String token;
    }

    // 4. Clase con ambos filtros
    static class Mixta {
        String nombre;
        @NoSolicitar String secreto;
        Optional<String> email;
        int edad;
        @NoSolicitar Optional<String> token;
        Optional<Double> saldo;
    }

    // 5. Clase sin campos requeridos
    static class SinRequeridos {
        @NoSolicitar Optional<String> secreto;
        @NoSolicitar String token;
        Optional<Double> saldo;
    }

    // 6. Clase vacía
    static class Vacia {}

    @Test
    @DisplayName("Devuelve todos los campos simples")
    void devuelveTodosLosCamposSimples() {
        List<String> campos = ReflexionUtil.obtenerCamposRequeridos(SoloSimples.class);
        assertThat(campos).containsExactlyInAnyOrder("nombre", "edad", "saldo");
    }

    @Test
    @DisplayName("Filtra correctamente los campos Optional")
    void filtraCamposOptional() {
        List<String> campos = ReflexionUtil.obtenerCamposRequeridos(ConOptional.class);
        assertThat(campos).containsExactlyInAnyOrder("nombre", "edad");
    }

    @Test
    @DisplayName("Filtra correctamente los campos con @NoSolicitar")
    void filtraCamposNoSolicitar() {
        List<String> campos = ReflexionUtil.obtenerCamposRequeridos(ConNoSolicitar.class);
        assertThat(campos).containsExactlyInAnyOrder("nombre", "edad");
    }

    @Test
    @DisplayName("Filtra correctamente ambos: Optional y @NoSolicitar")
    void filtraAmbosFiltros() {
        List<String> campos = ReflexionUtil.obtenerCamposRequeridos(Mixta.class);
        assertThat(campos).containsExactlyInAnyOrder("nombre", "edad");
    }

    @Test
    @DisplayName("Devuelve lista vacía si no hay campos requeridos")
    void devuelveListaVaciaSiNoHayCamposRequeridos() {
        List<String> campos = ReflexionUtil.obtenerCamposRequeridos(SinRequeridos.class);
        assertThat(campos).isEmpty();
    }

    @Test
    @DisplayName("Devuelve lista vacía para clase sin campos")
    void devuelveListaVaciaParaClaseVacia() {
        List<String> campos = ReflexionUtil.obtenerCamposRequeridos(Vacia.class);
        assertThat(campos).isEmpty();
    }
}
