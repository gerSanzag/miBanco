package com.mibanco.repository;

import com.mibanco.model.Client;
import com.mibanco.model.enums.ClientOperationType;
import com.mibanco.repository.util.BaseRepository;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Interface for Client repository
 * Extends the base interface to inherit generic CRUD operations
 */
public interface ClientRepository extends BaseRepository<Client, Long, ClientOperationType> {
    
  
} 