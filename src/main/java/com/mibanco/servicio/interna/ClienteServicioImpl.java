package com.mibanco.servicio.interna;

import com.mibanco.modelo.Cliente;
import com.mibanco.dto.ClienteDTO;
import com.mibanco.repositorio.ClienteRepositorio;
import com.mibanco.dto.mapeador.ClienteMapeador;
import com.mibanco.modelo.enums.TipoOperacionCliente;
import com.mibanco.repositorio.interna.RepositorioFactoria;         
import com.mibanco.servicio.ClienteServicio;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class ClienteServicioImpl extends BaseServicioImpl<ClienteDTO, Cliente, Long, TipoOperacionCliente, ClienteRepositorio> implements ClienteServicio {
    
    private static final ClienteRepositorio repositorioCliente;
    private static final ClienteMapeador mapeador;
    private final TipoOperacionCliente tipoActualizar = TipoOperacionCliente.ACTUALIZAR;
    
    static {
        repositorioCliente = RepositorioFactoria.obtenerInstancia().obtenerRepositorioCliente();
        mapeador = new ClienteMapeador();
    }
    
    public ClienteServicioImpl() {
        super(repositorioCliente, mapeador);
    }

    @Override
    public Optional<ClienteDTO> crearClienteDto(Map<String, String> datosCliente) {
        // El servicio crea el DTO internamente
        ClienteDTO nuevoCliente = ClienteDTO.builder()
            .nombre(datosCliente.get("nombre"))
            .apellido(datosCliente.get("apellido"))
            .dni(datosCliente.get("dni"))
            .email(datosCliente.get("email"))
            .telefono(datosCliente.get("telefono"))
            .direccion(datosCliente.get("direccion"))
            .fechaNacimiento(LocalDate.parse(datosCliente.get("fechaNacimiento"), DateTimeFormatter.ISO_LOCAL_DATE))
            .build();
        
        // Luego lo guarda usando el método existente
        return guardar(TipoOperacionCliente.CREAR, Optional.of(nuevoCliente));
    }

    @Override
    public Optional<ClienteDTO> guardarCliente(Optional<ClienteDTO> clienteDTO) {
        return guardar(TipoOperacionCliente.CREAR,clienteDTO);
    }

    @Override
    public Optional<ClienteDTO> actualizarVariosCampos(Long id, Optional<ClienteDTO> clienteDTO) {
        Optional<ClienteDTO> actualizaVariosCampos = actualizar(
            id,
            clienteDTO,
            tipoActualizar,
            (clienteExistente, clienteNuevo) -> clienteExistente.conDatosContacto(
                Optional.ofNullable(clienteNuevo.getEmail()),
                Optional.ofNullable(clienteNuevo.getTelefono()),
                Optional.ofNullable(clienteNuevo.getDireccion())
            )
        );
        guardar(tipoActualizar, actualizaVariosCampos);
        return actualizaVariosCampos;
    }

    @Override
    public Optional<ClienteDTO> actualizarEmailCliente(Long id, Optional<String> nuevoEmail) {
        Optional<ClienteDTO> actualizadoEmail = actualizarCampo(
            id,
            nuevoEmail,
            Cliente::getEmail,
            Cliente::conEmail
        );
        guardar(tipoActualizar, actualizadoEmail);
        return actualizadoEmail;
    }

    @Override
    public Optional<ClienteDTO> actualizarTelefonoCliente(Long id, Optional<String> nuevoTelefono) {
        Optional<ClienteDTO> actualizaTelefono = actualizarCampo(
            id,
            nuevoTelefono,
            Cliente::getTelefono,
            Cliente::conTelefono
        );
        guardar(tipoActualizar, actualizaTelefono);
        return actualizaTelefono;
    }

    @Override
    public Optional<ClienteDTO> actualizarDireccionCliente(Long id, Optional<String> nuevaDireccion) {
        Optional<ClienteDTO> actualizaDireccion = actualizarCampo(
            id,
            nuevaDireccion,
            Cliente::getDireccion,
            Cliente::conDireccion
        );
        guardar(tipoActualizar, actualizaDireccion);
        return actualizaDireccion;
    }

    @Override
    public Optional<ClienteDTO> obtenerClientePorId(Optional<Long> id) {
        return obtenerPorId(id);
    }

    @Override
    public Optional<ClienteDTO> obtenerClientePorDni(Optional<String> dni) {
        return dni.flatMap(dniValue ->
            ((ClienteRepositorio)repositorio).buscarPorDni(Optional.of(dniValue))
                .flatMap(entidad -> mapeador.aDto(Optional.of(entidad)))
        );
    }

    @Override
    public Optional<List<ClienteDTO>> obtenerTodosLosClientes() {
        return obtenerTodos();
    }

    @Override
    public boolean eliminarCliente(Optional<Long> id) {
        return eliminarPorId(id, TipoOperacionCliente.ELIMINAR);
    }

    @Override
    public Optional<ClienteDTO> restaurarCliente(Optional<Long> id) {
        return restaurar(id, TipoOperacionCliente.RESTAURAR);
    }

    @Override
    public void establecerUsuarioActual(String usuario) {
        establecerUsuarioActual(usuario);
    }

    @Override
    public List<ClienteDTO> obtenerClientesEliminados() {
        return obtenerEliminados();
    }

    @Override
    public long contarClientes() {
        return contarRegistros();
    }
} 