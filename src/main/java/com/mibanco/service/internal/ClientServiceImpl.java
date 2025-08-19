package com.mibanco.service.internal;

import com.mibanco.dto.ClientDTO;
import com.mibanco.dto.mappers.ClientMapper;
import com.mibanco.model.Client;
import com.mibanco.model.enums.ClientOperationType;
import com.mibanco.repository.ClientRepository;
import com.mibanco.repository.internal.RepositoryServiceLocator;
import com.mibanco.service.ClientService;
import com.mibanco.util.ValidationException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class ClientServiceImpl extends BaseServiceImpl<ClientDTO, Client, Long, ClientOperationType, ClientRepository> implements ClientService {
    
    private static final ClientRepository clientRepository;
    private static final ClientMapper mapper;
    private static final ClientDtoProcessorService clientDtoProcessor;
    private final ClientOperationType updateType = ClientOperationType.UPDATE;
    
    static {
        clientRepository = RepositoryServiceLocator.getService(ClientRepository.class);
        mapper = new ClientMapper();
        clientDtoProcessor = new ClientDtoProcessorService();
    }
    
    public ClientServiceImpl() {
        super(clientRepository, mapper);
    }

    @Override
    public Optional<ClientDTO> createClientDto(Map<String, String> clientData) {
        try {
            // Use specialized processor to create DTO with validations
            return clientDtoProcessor.processClientDto(clientData)
                .flatMap(clientDto -> {
                    // Validate unique DNI before saving
                    validateUniqueDni(clientDto);
                    return saveEntity(ClientOperationType.CREATE, Optional.of(clientDto));
                });
        } catch (ValidationException e) {
            System.err.println("Validation error: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<ClientDTO> saveClient(Optional<ClientDTO> clientDTO) {
        // NO validation here - DTO should already be validated
        return saveEntity(ClientOperationType.CREATE, clientDTO);
    }
    
    /**
     * Helper method to validate unique DNI
     * @param dto Client DTO to validate
     * @throws ValidationException if DNI already exists
     */
    private void validateUniqueDni(ClientDTO dto) {
        // Only validate if DNI is not null (basic validation already done in view)
        if (dto.getDni() != null) {
            Optional<Client> existingClient = clientRepository.findByPredicate(
                client -> dto.getDni().equals(client.getDni())
            );
            
            if (existingClient.isPresent()) {
                throw new ValidationException("A client with DNI already exists: " + dto.getDni());
            }
        }
    }

    @Override
    public Optional<ClientDTO> updateMultipleFields(Long id, Map<String, Object> updates) {
        return super.updateMultipleFields(
            id,
            updates,
            updateType,
            (existingClient, updateData) -> {
                // Extrae valores del Map y los convierte a Optional
                Optional<String> newEmail = Optional.ofNullable(updateData.get("email"))
                    .map(value -> value.toString());
                    
                Optional<String> newPhone = Optional.ofNullable(updateData.get("phone"))
                    .map(value -> value.toString());
                    
                Optional<String> newAddress = Optional.ofNullable(updateData.get("address"))
                    .map(value -> value.toString());
                
                // Usa withContactData con Optionals
                return existingClient.withContactData(newEmail, newPhone, newAddress);
            }
        );
    }

    @Override
    public Optional<ClientDTO> updateClientEmail(Long id, Optional<String> newEmail) {
        return newEmail.flatMap(email -> {
            Optional<ClientDTO> existingClientOpt = findById(Optional.of(id));
            
            return existingClientOpt.map(existingClient -> {
                ClientDTO updatedClient = existingClient.withEmail(email);
                return saveEntity(updateType, Optional.of(updatedClient)).orElse(updatedClient);
            });
        });
    }

    @Override
    public Optional<ClientDTO> updateClientPhone(Long id, Optional<String> newPhone) {
        return newPhone.flatMap(phone -> {
            Optional<ClientDTO> existingClientOpt = findById(Optional.of(id));
            
            return existingClientOpt.map(existingClient -> {
                ClientDTO updatedClient = existingClient.withPhone(phone);
                return saveEntity(updateType, Optional.of(updatedClient)).orElse(updatedClient);
            });
        });
    }

    @Override
    public Optional<ClientDTO> updateClientAddress(Long id, Optional<String> newAddress) {
        return newAddress.flatMap(address -> {
            Optional<ClientDTO> existingClientOpt = findById(Optional.of(id));
            
            return existingClientOpt.map(existingClient -> {
                ClientDTO updatedClient = existingClient.withAddress(address);
                return saveEntity(updateType, Optional.of(updatedClient)).orElse(updatedClient);
            });
        });
    }

    @Override
    public Optional<ClientDTO> getClientById(Optional<Long> id) {
        return findById(id);
    }

    @Override
    public Optional<ClientDTO> getClientByDni(Optional<String> dni) {
        return dni.flatMap(dniValue -> clientRepository.findById(Optional.of(Long.parseLong(dniValue)))
                .flatMap(entity -> mapper.toDto(Optional.of(entity))));
    }

    @Override
    public Optional<List<ClientDTO>> getAllClients() {
        return findAll();
    }

    @Override
    public boolean deleteClient(Optional<Long> id) {
        return deleteById(id, ClientOperationType.DELETE);
    }

    @Override
    public Optional<ClientDTO> restoreClient(Optional<Long> id) {
        return restore(id, ClientOperationType.RESTORE);
    }

    @Override
    public void setCurrentUser(String user) {
        super.setCurrentUser(user);
    }

    @Override
    public long countClients() {
        return countRecords();
    }

    @Override
    public <V> Optional<ClientDTO> updateField(
            Long id,
            Optional<V> newValue,
            java.util.function.Function<ClientDTO, V> currentValue,
            java.util.function.BiFunction<ClientDTO, V, ClientDTO> updater) {
        return super.updateField(id, newValue, currentValue, updater);
    }



    @Override
    public java.util.List<ClientDTO> getDeleted() {
        return super.getDeleted();
    }
} 