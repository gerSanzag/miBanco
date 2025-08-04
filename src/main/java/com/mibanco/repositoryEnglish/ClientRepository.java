package com.mibanco.repositoryEnglish;

import com.mibanco.modelEnglish.Client;
import com.mibanco.modelEnglish.enums.ClientOperationType;
import com.mibanco.repositoryEnglish.util.BaseRepository;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Interface for Client repository
 * Extends the base interface to inherit generic CRUD operations
 */
public interface ClientRepository extends BaseRepository<Client, Long, ClientOperationType> {
    
  
} 