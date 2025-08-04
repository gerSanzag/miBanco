package com.mibanco.repositoryEnglish;

import com.mibanco.modelEnglish.Account;
import com.mibanco.modelEnglish.enums.AccountOperationType;
import com.mibanco.repositoryEnglish.util.BaseRepository;

/**
 * Interface for Account repository
 * Extends the base interface to inherit generic CRUD operations
 */
public interface AccountRepository extends BaseRepository<Account, Long, AccountOperationType> {
    
} 