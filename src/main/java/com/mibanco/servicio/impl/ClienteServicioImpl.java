package com.mibanco.servicio.impl;

import com.mibanco.modelo.Cliente;
import com.mibanco.dto.ClienteDTO;
import com.mibanco.repositorio.ClienteRepositorio;
import com.mibanco.dto.mapeador.ClienteMapeador;
import com.mibanco.modelo.enums.TipoOperacionCliente;
import com.mibanco.servicio.util.BaseServicioImpl;
import com.mibanco.repositorio.interna.RepositorioFactoria;
import com.mibanco.repositorio.util.BaseRepositorio;
import com.mibanco.servicio.ClienteServicio;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteServicioImpl extends BaseServicioImpl<ClienteDTO, Cliente, Long, TipoOperacionCliente> implements ClienteServicio {
    
    private static final BaseRepositorio<Cliente, Long, TipoOperacionCliente> repositorioCliente;
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
    public Optional<ClienteDTO> crearCliente(Optional<ClienteDTO> clienteDTO) {
        return guardar(TipoOperacionCliente.CREAR,clienteDTO);
    }

    @Override
    public Optional<ClienteDTO> actualizarVariosCampos(Long id, Optional<ClienteDTO> clienteDTO) {
        return actualizar(
            id,
            clienteDTO,
            tipoActualizar,
            (clienteExistente, clienteNuevo) -> clienteExistente.conDatosContacto(
                Optional.ofNullable(clienteNuevo.getEmail()),
                Optional.ofNullable(clienteNuevo.getTelefono()),
                Optional.ofNullable(clienteNuevo.getDireccion())
            )
        );
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
        Optional<ClienteDTO> actualizadoDireccion = actualizarCampo(
            id,
            nuevaDireccion,
            Cliente::getDireccion,
            Cliente::conDireccion
        );
        guardar(tipoActualizar, actualizadoDireccion);
        return actualizadoDireccion;
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
        return id.flatMap(idValue -> 
            ((ClienteRepositorio)repositorio).restaurarClienteEliminado(Optional.of(idValue))
                .flatMap(entidad -> mapeador.aDto(Optional.of(entidad)))
        );
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