package com.mibanco;

import com.mibanco.dto.ClientDTO;
import com.mibanco.service.ClientService;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Helper class for creating test clients
 * Provides methods to create clients for testing purposes without validation
 */
public class TestClientHelper {
    
    private final ClientService clientService;
    private static final AtomicLong clientCounter = new AtomicLong(0);
    
    public TestClientHelper(ClientService clientService) {
        this.clientService = clientService;
    }
    
    /**
     * Creates a client for testing purposes without validation
     * This method bypasses the normal validation process by using unique DNI
     * 
     * @param clientDto The client DTO to save
     * @return Optional with the saved client DTO
     */
    public Optional<ClientDTO> createTestClient(ClientDTO clientDto) {
        // Generate a unique DNI to avoid conflicts using atomic counter
        String uniqueDni = clientDto.getDni() + "_" + clientCounter.incrementAndGet();
        
        // Create a new DTO with unique DNI
        ClientDTO uniqueClientDto = clientDto.toBuilder()
            .dni(uniqueDni)
            .build();
        
        // Convert to map and use the service method
        java.util.Map<String, String> clientData = java.util.Map.of(
            "firstName", uniqueClientDto.getFirstName() != null ? uniqueClientDto.getFirstName() : "",
            "lastName", uniqueClientDto.getLastName() != null ? uniqueClientDto.getLastName() : "",
            "dni", uniqueClientDto.getDni(),
            "birthDate", uniqueClientDto.getBirthDate() != null ? uniqueClientDto.getBirthDate().toString() : "",
            "email", uniqueClientDto.getEmail() != null ? uniqueClientDto.getEmail() : "",
            "phone", uniqueClientDto.getPhone() != null ? uniqueClientDto.getPhone() : "",
            "address", uniqueClientDto.getAddress() != null ? uniqueClientDto.getAddress() : ""
        );
        
        return clientService.createClientDto(clientData);
    }
}
