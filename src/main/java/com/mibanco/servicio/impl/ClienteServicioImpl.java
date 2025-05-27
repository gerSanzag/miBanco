package com.mibanco.servicio.impl;

import com.mibanco.modelo.Cliente;
import com.mibanco.dto.ClienteDTO;
import com.mibanco.repositorio.ClienteRepositorio;
import com.mibanco.dto.mapeador.ClienteMapeador;
import com.mibanco.modelo.enums.TipoOperacionCliente;
import com.mibanco.servicio.util.BaseServicioImpl;
import com.mibanco.servicio.ClienteServicio;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteServicioImpl extends BaseServicioImpl<ClienteDTO, Cliente, Long, TipoOperacionCliente> implements ClienteServicio {
    
    private final ClienteRepositorio clienteRepositorio;
    private final ClienteMapeador clienteMapeador;
    
    public ClienteServicioImpl(ClienteRepositorio repositorio, ClienteMapeador mapeador) {
        super(repositorio, mapeador);
        this.clienteRepositorio = repositorio;
        this.clienteMapeador = mapeador;
    }

    @Override
    public Optional<ClienteDTO> crearCliente(Optional<ClienteDTO> clienteDTO) {
        return guardar(clienteDTO);
    }

    @Override
    public Optional<ClienteDTO> actualizarCliente(Long id, Optional<ClienteDTO> clienteDTO) {
        return Optional.ofNullable(id)
            .flatMap(idValue -> clienteRepositorio.buscarPorId(Optional.of(idValue)))
            .map(clienteExistente -> 
                clienteDTO.flatMap(dto -> clienteMapeador.aEntidad(Optional.of(dto)))
                    .map(clienteNuevo -> clienteExistente.conDatosContacto(
                        Optional.ofNullable(clienteNuevo.getEmail()),
                        Optional.ofNullable(clienteNuevo.getTelefono()),
                        Optional.ofNullable(clienteNuevo.getDireccion())
                    ))
                    .orElse(clienteExistente)
            )
            .flatMap(entidad -> clienteMapeador.aDto(Optional.of(entidad)))
            .flatMap(dto -> guardar(Optional.of(dto)));
    }

    @Override
    public Optional<ClienteDTO> actualizarEmailCliente(Long id, Optional<String> nuevoEmail) {
        return actualizarCampo(
            id,
            nuevoEmail,
            Cliente::getEmail,
            Cliente::conEmail
        );
    }

    @Override
    public Optional<ClienteDTO> actualizarTelefonoCliente(Long id, Optional<String> nuevoTelefono) {
        return actualizarCampo(
            id,
            nuevoTelefono,
            Cliente::getTelefono,
            Cliente::conTelefono
        );
    }

    @Override
    public Optional<ClienteDTO> actualizarDireccionCliente(Long id, Optional<String> nuevaDireccion) {
        return actualizarCampo(
            id,
            nuevaDireccion,
            Cliente::getDireccion,
            Cliente::conDireccion
        );
    }

    @Override
    public Optional<ClienteDTO> obtenerClientePorId(Optional<Long> id) {
        return id.flatMap(idValue -> 
            clienteRepositorio.buscarPorId(Optional.of(idValue))
                .flatMap(entidad -> clienteMapeador.aDto(Optional.of(entidad)))
        );
    }

    @Override
    public Optional<ClienteDTO> obtenerClientePorDni(Optional<String> dni) {
        return dni.flatMap(dniValue ->
            clienteRepositorio.buscarPorDni(Optional.of(dniValue))
                .flatMap(entidad -> clienteMapeador.aDto(Optional.of(entidad)))
        );
    }

    @Override
    public Optional<List<ClienteDTO>> obtenerTodosLosClientes() {
        return clienteRepositorio.buscarTodos()
            .map(clientes -> clientes.stream()
                .map(entidad -> clienteMapeador.aDto(Optional.of(entidad)).orElse(null))
                .filter(java.util.Objects::nonNull)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll)
            );
    }

    @Override
    public boolean eliminarCliente(Optional<Long> id) {
        return id.flatMap(idValue -> 
            clienteRepositorio.eliminarPorId(Optional.of(idValue), obtenerTipoOperacionEliminar())
        ).isPresent();
    }

    @Override
    public Optional<ClienteDTO> restaurarCliente(Optional<Long> id) {
        return id.flatMap(idValue -> 
            clienteRepositorio.restaurar(Optional.of(idValue), TipoOperacionCliente.RESTAURAR)
                .flatMap(entidad -> clienteMapeador.aDto(Optional.of(entidad)))
        );
    }

    @Override
    public void establecerUsuarioActual(String usuario) {
        clienteRepositorio.setUsuarioActual(usuario);
    }

    @Override
    public List<ClienteDTO> obtenerClientesEliminados() {
        return clienteRepositorio.obtenerClientesEliminados().stream()
            .map(entidad -> clienteMapeador.aDto(Optional.of(entidad)).orElse(null))
            .filter(java.util.Objects::nonNull)
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    @Override
    public long contarClientes() {
        return clienteRepositorio.contarRegistros();
    }

    @Override
    protected TipoOperacionCliente obtenerTipoOperacionCrear() {
        return TipoOperacionCliente.CREAR;
    }

    @Override
    protected TipoOperacionCliente obtenerTipoOperacionActualizar() {
        return TipoOperacionCliente.MODIFICAR;
    }

    @Override
    protected TipoOperacionCliente obtenerTipoOperacionEliminar() {
        return TipoOperacionCliente.ELIMINAR;
    }
} 