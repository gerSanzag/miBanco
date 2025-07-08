package com.mibanco.utilTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mibanco.modelo.Cliente;
import com.mibanco.modelo.RegistroAuditoria;
import com.mibanco.modelo.enums.TipoOperacionCliente;
import com.mibanco.repositorio.AuditoriaRepositorio;
import com.mibanco.util.AuditoriaUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para la clase AuditoriaUtil")
class AuditoriaUtilTest {

    @Mock
    private AuditoriaRepositorio mockAuditoriaRepositorio;
    
    private Cliente cliente;
    private TipoOperacionCliente tipoOperacion;
    private String usuario;
    private RegistroAuditoria<Cliente, TipoOperacionCliente> registroEsperado;

    @BeforeEach
    void setUp() {
        // Datos de prueba comunes
        cliente = Cliente.of(
            1L, "Juan", "Pérez", "12345678A", 
            LocalDate.of(1990, 5, 15), 
            "juan.perez@email.com", "123456789", "Calle Principal 123"
        );
        
        tipoOperacion = TipoOperacionCliente.CREAR;
        usuario = "usuario_test";
        
        // Crear registro esperado
        registroEsperado = RegistroAuditoria.of(
            Optional.of(tipoOperacion), 
            Optional.of(cliente), 
            Optional.of(usuario)
        );
    }

    @Test
    @DisplayName("Debería registrar operación exitosamente")
    void deberiaRegistrarOperacionExitosamente() {
        // Arrange (Preparar)
        // Configurar el mock: "cuando se llame registrar con cualquier parámetro, retorna el registro esperado"
        when(mockAuditoriaRepositorio.registrar(any(Optional.class))).thenReturn(Optional.of(registroEsperado));
        
        // Act (Actuar)
        RegistroAuditoria<Cliente, TipoOperacionCliente> resultado = AuditoriaUtil.registrarOperacion(
            mockAuditoriaRepositorio,
            Optional.of(tipoOperacion),
            Optional.of(cliente),
            Optional.of(usuario)
        );
        
        // Assert (Verificar)
        assertThat(resultado).isNotNull();
        assertThat(resultado).isEqualTo(registroEsperado);
        
        // Verificar que se llamó el método registrar del repositorio
        verify(mockAuditoriaRepositorio).registrar(any());
    }

    @Test
    @DisplayName("Debería retornar registro original cuando el repositorio falla")
    void deberiaRetornarRegistroOriginalCuandoRepositorioFalla() {
        // Arrange (Preparar)
        // Configurar el mock: "cuando se llame registrar, retorna vacío (simula fallo)"
        when(mockAuditoriaRepositorio.registrar(any(Optional.class))).thenReturn(Optional.empty());
        
        // Act (Actuar)
        RegistroAuditoria<Cliente, TipoOperacionCliente> resultado = AuditoriaUtil.registrarOperacion(
            mockAuditoriaRepositorio,
            Optional.of(tipoOperacion),
            Optional.of(cliente),
            Optional.of(usuario)
        );
        
        // Assert (Verificar)
        assertThat(resultado).isNotNull();
        // Los getters retornan valores directos, no Optional
        assertThat(resultado.getTipoOperacion()).isEqualTo(tipoOperacion);
        assertThat(resultado.getEntidad()).isEqualTo(cliente);
        assertThat(resultado.getUsuario()).isEqualTo(usuario);
        
        // Verificar que se llamó el método registrar del repositorio
        verify(mockAuditoriaRepositorio).registrar(any());
    }

    @Test
    @DisplayName("Debería manejar Optionals vacíos correctamente")
    void deberiaManejarOptionalsVaciosCorrectamente() {
        // Arrange (Preparar)
        // Configurar el mock para retornar un registro con Optionals vacíos
        RegistroAuditoria<Cliente, TipoOperacionCliente> registroConVacios = RegistroAuditoria.of(
            Optional.<TipoOperacionCliente>empty(), 
            Optional.<Cliente>empty(), 
            Optional.<String>empty()
        );
        when(mockAuditoriaRepositorio.registrar(any(Optional.class))).thenReturn(Optional.of(registroConVacios));
        
        // Act (Actuar)
        RegistroAuditoria<Cliente, TipoOperacionCliente> resultado = AuditoriaUtil.registrarOperacion(
            mockAuditoriaRepositorio,
            Optional.<TipoOperacionCliente>empty(), // tipoOperacion vacío
            Optional.<Cliente>empty(), // entidad vacía
            Optional.<String>empty()  // usuario vacío
        );
        
        // Assert (Verificar)
        assertThat(resultado).isNotNull();
        // Los getters retornan null cuando los Optional están vacíos
        assertThat(resultado.getTipoOperacion()).isNull();
        assertThat(resultado.getEntidad()).isNull();
        assertThat(resultado.getUsuario()).isNull();
        
        // Verificar que se llamó el método registrar del repositorio
        verify(mockAuditoriaRepositorio).registrar(any());
    }

    @Test
    @DisplayName("Debería mantener inmutabilidad de los parámetros de entrada")
    void deberiaMantenerInmutabilidadDeLosParametrosDeEntrada() {
        // Arrange (Preparar)
        // Guardar referencias originales para verificar inmutabilidad
        Optional<TipoOperacionCliente> tipoOperacionOriginal = Optional.of(tipoOperacion);
        Optional<Cliente> clienteOriginal = Optional.of(cliente);
        Optional<String> usuarioOriginal = Optional.of(usuario);
        
        when(mockAuditoriaRepositorio.registrar(any(Optional.class))).thenReturn(Optional.of(registroEsperado));
        
        // Act (Actuar)
        RegistroAuditoria<Cliente, TipoOperacionCliente> resultado = AuditoriaUtil.registrarOperacion(
            mockAuditoriaRepositorio,
            tipoOperacionOriginal,
            clienteOriginal,
            usuarioOriginal
        );
        
        // Assert (Verificar)
        assertThat(resultado).isNotNull();
        
        // Verificar que los parámetros originales no cambiaron
        assertThat(tipoOperacionOriginal).isEqualTo(Optional.of(tipoOperacion));
        assertThat(clienteOriginal).isEqualTo(Optional.of(cliente));
        assertThat(usuarioOriginal).isEqualTo(Optional.of(usuario));
        
        // Verificar que se llamó el método registrar del repositorio
        verify(mockAuditoriaRepositorio).registrar(any());
    }
}
