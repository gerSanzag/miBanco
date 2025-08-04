package com.mibanco.repository;

import com.mibanco.model.Account;
import com.mibanco.model.enums.AccountOperationType;
import com.mibanco.repository.util.BaseRepository;

/**
 * Interface for Account repository
 * Extends the base interface to inherit generic CRUD operations
 */
public interface AccountRepository extends BaseRepository<Account, Long, AccountOperationType> {
    
} 