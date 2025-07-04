package com.mibanco.modeloTest;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.mibanco.modelo.Cliente;
import com.mibanco.modelo.RegistroAuditoria;
import com.mibanco.modelo.enums.TipoOperacionCliente;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests para la clase RegistroAuditoria")
class RegistroAuditoriaTest {

    UUID id = UUID.randomUUID();
    TipoOperacionCliente tipoOperacion = TipoOperacionCliente.CREAR;
    LocalDateTime fechaHora = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
    Cliente entidad;
    String usuario = "admin";
    Double monto = 1000.50;
    String detalles = "Cliente creado exitosamente";
    RegistroAuditoria<Cliente, TipoOperacionCliente> registro;

    @BeforeEach
    void setUp() {
        entidad = Cliente.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Perez")
                .dni("1234567890")
                .build();
        
        registro = RegistroAuditoria.<Cliente, TipoOperacionCliente>builder()
                .id(id)
                .tipoOperacion(tipoOperacion)
                .fechaHora(fechaHora)
                .entidad(entidad)
                .usuario(usuario)
                .monto(monto)
                .detalles(detalles)
                .build();
    }

    @Test
    @DisplayName("Debería crear un registro de auditoría con datos válidos usando Builder")
    void deberiaCrearRegistroAuditoriaConDatosValidosBuilder() {
        // Assert (Verificar)
        assertThat(registro).isNotNull();
        assertThat(registro.getId()).isEqualTo(id);
        assertThat(registro.getTipoOperacion()).isEqualTo(tipoOperacion);
        assertThat(registro.getFechaHora()).isEqualTo(fechaHora);
        assertThat(registro.getEntidad()).isEqualTo(entidad);
        assertThat(registro.getUsuario()).isEqualTo(usuario);
        assertThat(registro.getMonto()).isEqualTo(monto);
        assertThat(registro.getDetalles()).isEqualTo(detalles);
    }

    @Test
    @DisplayName("Debería crear un registro básico usando el método factory of()")
    void deberiaCrearRegistroBasicoUsandoFactoryOf() {
        // Act (Actuar)
        RegistroAuditoria<Cliente, TipoOperacionCliente> registroBasico = 
                RegistroAuditoria.of(tipoOperacion, entidad, usuario);
        
        // Assert (Verificar)
        assertThat(registroBasico).isNotNull();
        assertThat(registroBasico.getId()).isNotNull();
        assertThat(registroBasico.getTipoOperacion()).isEqualTo(tipoOperacion);
        assertThat(registroBasico.getEntidad()).isEqualTo(entidad);
        assertThat(registroBasico.getUsuario()).isEqualTo(usuario);
        assertThat(registroBasico.getMonto()).isNull();
        assertThat(registroBasico.getDetalles()).isNull();
        
        // Verificar que la fecha se estableció automáticamente
        assertThat(registroBasico.getFechaHora()).isNotNull();
    }

    @Test
    @DisplayName("Debería crear un registro detallado usando el método factory ofDetallado()")
    void deberiaCrearRegistroDetalladoUsandoFactoryOfDetallado() {
        // Act (Actuar)
        RegistroAuditoria<Cliente, TipoOperacionCliente> registroDetallado = 
                RegistroAuditoria.ofDetallado(tipoOperacion, entidad, usuario, monto, detalles);
        
        // Assert (Verificar)
        assertThat(registroDetallado).isNotNull();
        assertThat(registroDetallado.getId()).isNotNull();
        assertThat(registroDetallado.getTipoOperacion()).isEqualTo(tipoOperacion);
        assertThat(registroDetallado.getEntidad()).isEqualTo(entidad);
        assertThat(registroDetallado.getUsuario()).isEqualTo(usuario);
        assertThat(registroDetallado.getMonto()).isEqualTo(monto);
        assertThat(registroDetallado.getDetalles()).isEqualTo(detalles);
        
        // Verificar que la fecha se estableció automáticamente
        assertThat(registroDetallado.getFechaHora()).isNotNull();
    }

    @Test
    @DisplayName("Debería generar ID único automáticamente en factory methods")
    void deberiaGenerarIdUnicoAutomaticamenteEnFactoryMethods() {
        // Act (Actuar)
        RegistroAuditoria<Cliente, TipoOperacionCliente> registro1 = 
                RegistroAuditoria.of(tipoOperacion, entidad, usuario);
        RegistroAuditoria<Cliente, TipoOperacionCliente> registro2 = 
                RegistroAuditoria.of(tipoOperacion, entidad, usuario);
        
        // Assert (Verificar)
        assertThat(registro1.getId()).isNotNull();
        assertThat(registro2.getId()).isNotNull();
        assertThat(registro1.getId()).isNotEqualTo(registro2.getId()); // IDs diferentes
    }

    @Test
    @DisplayName("Debería establecer fecha actual automáticamente en factory methods")
    void deberiaEstablecerFechaActualAutomaticamenteEnFactoryMethods() {
        // Arrange (Preparar)
        LocalDateTime antes = LocalDateTime.now();
        
        // Act (Actuar)
        RegistroAuditoria<Cliente, TipoOperacionCliente> registroBasico = 
                RegistroAuditoria.of(tipoOperacion, entidad, usuario);
        LocalDateTime despues = LocalDateTime.now();
        
        // Assert (Verificar)
        assertThat(registroBasico.getFechaHora()).isNotNull();
        assertThat(registroBasico.getFechaHora()).isBetween(antes, despues);
    }

    @Test
    @DisplayName("Debería manejar diferentes tipos de operación")
    void deberiaManejarDiferentesTiposDeOperacion() {
        // Arrange (Preparar)
        RegistroAuditoria<Cliente, TipoOperacionCliente> registroCrear = 
                RegistroAuditoria.of(TipoOperacionCliente.CREAR, entidad, usuario);
        RegistroAuditoria<Cliente, TipoOperacionCliente> registroActualizar = 
                RegistroAuditoria.of(TipoOperacionCliente.ACTUALIZAR, entidad, usuario);
        RegistroAuditoria<Cliente, TipoOperacionCliente> registroEliminar = 
                RegistroAuditoria.of(TipoOperacionCliente.ELIMINAR, entidad, usuario);
        
        // Assert (Verificar)
        assertThat(registroCrear.getTipoOperacion()).isEqualTo(TipoOperacionCliente.CREAR);
        assertThat(registroActualizar.getTipoOperacion()).isEqualTo(TipoOperacionCliente.ACTUALIZAR);
        assertThat(registroEliminar.getTipoOperacion()).isEqualTo(TipoOperacionCliente.ELIMINAR);
    }

    @Test
    @DisplayName("Debería manejar montos nulos y con valores")
    void deberiaManejarMontosNulosYConValores() {
        // Arrange (Preparar)
        Double montoCero = 0.0;
        Double montoNegativo = -100.0;
        
        // Act (Actuar)
        RegistroAuditoria<Cliente, TipoOperacionCliente> registroSinMonto = 
                RegistroAuditoria.of(tipoOperacion, entidad, usuario);
        RegistroAuditoria<Cliente, TipoOperacionCliente> registroConMonto = 
                RegistroAuditoria.ofDetallado(tipoOperacion, entidad, usuario, monto, detalles);
        RegistroAuditoria<Cliente, TipoOperacionCliente> registroMontoCero = 
                RegistroAuditoria.ofDetallado(tipoOperacion, entidad, usuario, montoCero, detalles);
        RegistroAuditoria<Cliente, TipoOperacionCliente> registroMontoNegativo = 
                RegistroAuditoria.ofDetallado(tipoOperacion, entidad, usuario, montoNegativo, detalles);
        
        // Assert (Verificar)
        assertThat(registroSinMonto.getMonto()).isNull();
        assertThat(registroConMonto.getMonto()).isEqualTo(monto);
        assertThat(registroMontoCero.getMonto()).isEqualTo(montoCero);
        assertThat(registroMontoNegativo.getMonto()).isEqualTo(montoNegativo);
    }

    @Test
    @DisplayName("Debería manejar detalles nulos y con valores")
    void deberiaManejarDetallesNulosYConValores() {
        // Arrange (Preparar)
        String detallesVacio = "";
        String detallesLargo = "Este es un detalle muy largo que contiene mucha información sobre la operación realizada";
        
        // Act (Actuar)
        RegistroAuditoria<Cliente, TipoOperacionCliente> registroSinDetalles = 
                RegistroAuditoria.of(tipoOperacion, entidad, usuario);
        RegistroAuditoria<Cliente, TipoOperacionCliente> registroConDetalles = 
                RegistroAuditoria.ofDetallado(tipoOperacion, entidad, usuario, monto, detalles);
        RegistroAuditoria<Cliente, TipoOperacionCliente> registroDetallesVacio = 
                RegistroAuditoria.ofDetallado(tipoOperacion, entidad, usuario, monto, detallesVacio);
        RegistroAuditoria<Cliente, TipoOperacionCliente> registroDetallesLargo = 
                RegistroAuditoria.ofDetallado(tipoOperacion, entidad, usuario, monto, detallesLargo);
        
        // Assert (Verificar)
        assertThat(registroSinDetalles.getDetalles()).isNull();
        assertThat(registroConDetalles.getDetalles()).isEqualTo(detalles);
        assertThat(registroDetallesVacio.getDetalles()).isEqualTo(detallesVacio);
        assertThat(registroDetallesLargo.getDetalles()).isEqualTo(detallesLargo);
    }



    @Test
    @DisplayName("Debería mantener inmutabilidad en todos los campos")
    void deberiaMantenerInmutabilidadEnTodosLosCampos() {
        // Arrange (Preparar)
        RegistroAuditoria<Cliente, TipoOperacionCliente> registroOriginal = registro;
        
        // Act (Actuar) - Crear un nuevo registro con datos diferentes
        RegistroAuditoria<Cliente, TipoOperacionCliente> registroNuevo = 
                RegistroAuditoria.ofDetallado(TipoOperacionCliente.ELIMINAR, entidad, "nuevo_usuario", 9999.99, "Nuevos detalles");
        
        // Assert (Verificar) - Original no debe cambiar
        assertThat(registroOriginal.getId()).isEqualTo(id);
        assertThat(registroOriginal.getTipoOperacion()).isEqualTo(tipoOperacion);
        assertThat(registroOriginal.getFechaHora()).isEqualTo(fechaHora);
        assertThat(registroOriginal.getEntidad()).isEqualTo(entidad);
        assertThat(registroOriginal.getUsuario()).isEqualTo(usuario);
        assertThat(registroOriginal.getMonto()).isEqualTo(monto);
        assertThat(registroOriginal.getDetalles()).isEqualTo(detalles);
        
        // Verificar que son objetos diferentes
        assertThat(registroNuevo).isNotSameAs(registroOriginal);
        assertThat(registroNuevo.getId()).isNotEqualTo(registroOriginal.getId());
    }

    @Test
    @DisplayName("Debería manejar diferentes tipos de entidades")
    void deberiaManejarDiferentesTiposDeEntidades() {
        // Arrange (Preparar)
        Cliente cliente = Cliente.builder()
                .id(1L)
                .nombre("Ana")
                .apellido("Garcia")
                .dni("9876543210")
                .build();
        
        // Act (Actuar)
        RegistroAuditoria<Cliente, TipoOperacionCliente> registroCliente = 
                RegistroAuditoria.of(TipoOperacionCliente.CREAR, cliente, usuario);
        
        // Assert (Verificar)
        assertThat(registroCliente.getEntidad()).isEqualTo(cliente);
        assertThat(registroCliente.getEntidad().getId()).isEqualTo(1L);
        assertThat(registroCliente.getEntidad().getNombre()).isEqualTo("Ana");
    }
}
