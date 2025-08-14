package com.mibanco.repository.internal;

import com.mibanco.dto.mappers.ClientMapper;
import com.mibanco.model.Client;
import com.mibanco.model.enums.ClientOperationType;
import com.mibanco.repository.ClientRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Implementation of Client repository with restricted access
 * Only accessible through Factory and public interfaces
 */
class ClientRepositoryImpl extends BaseRepositoryImpl<Client, Long, ClientOperationType> implements ClientRepository {
    
    /**
     * Package-private constructor
     */
    ClientRepositoryImpl() {
        super();
    }
    
    /**
     * Specific implementation to assign new ID to Client
     * Uses DTOs to maintain entity immutability
     * Pure functional approach with Optional
     * @param client Client without assigned ID
     * @return Client with new assigned ID
     */
    @Override
    protected Client createWithNewId(Client client) {
        ClientMapper mapper = new ClientMapper();
        
        return mapper.toDtoDirect(client)
            .map(dto -> dto.toBuilder()
                .id(idCounter.incrementAndGet())
                .build())
            .flatMap(mapper::toEntityDirect)
            .orElseThrow(() -> new IllegalStateException("Could not process Client entity"));
    }
    
    /**
     * Specific implementation to get repository configuration
     * @return Map with necessary configuration
     */
    @Override
    protected Map<String, Object> getConfiguration() {
        Map<String, Object> config = new HashMap<>();
        config.put("filePath", "src/main/resources/data/clientes.json");
        config.put("classType", Client.class);
        config.put("idExtractor", (Function<Client, Long>) Client::getId);
        return config;
    }
} 