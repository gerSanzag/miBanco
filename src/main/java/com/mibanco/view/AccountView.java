package com.mibanco.view;

import java.util.Optional;
import java.util.Map;
import java.util.List;
import com.mibanco.dto.AccountDTO;
import com.mibanco.model.Account;

/**
 * Interface for account management view.
 * Handles user interaction for account operations through console input/output.
 * Uses functional programming approach with Optionals and lambdas.
 */
public interface AccountView {
    
    /**
     * Shows the main account management menu.
     * Returns the selected option for the controller to handle.
     * 
     * @return Optional with the selected menu option, or empty if user wants to exit
     */
    Optional<String> showAccountMenu();
    
    /**
     * Shows the account update submenu.
     * Returns the selected option for the controller to handle.
     * 
     * @return Optional with the selected menu option, or empty if user wants to exit
     */
    Optional<String> showUpdateAccountMenu();
    
    /**
     * Captures account data for creating a new account.
     *
     * @return Optional with Map containing account data ready for the service, or empty if creation was cancelled
     */
    Optional<Map<String, String>> captureDataAccount();

    /**
     * Searches for an account using criteria from the user.
     *
     * @return Optional with Map containing search criteria, or empty if search was cancelled
     */
    Optional<Map<String, String>> searchAccount();
    
    /**
     * Updates account balance.
     * 
     * @return Optional with Map containing update data (id and newBalance), or empty if update was cancelled
     */
    Optional<Map<String, String>> updateAccountBalance();
    
    /**
     * Updates account status (active/inactive).
     * 
     * @return Optional with Map containing update data (id and newStatus), or empty if update was cancelled
     */
    Optional<Map<String, String>> updateAccountStatus();
    
    /**
     * Updates account holder.
     * 
     * @return Optional with Map containing update data (id and newHolderId), or empty if update was cancelled
     */
    Optional<Map<String, String>> updateAccountHolder();
    
    /**
     * Updates multiple account fields simultaneously.
     * 
     * @return Optional with Map containing update data for multiple fields, or empty if update was cancelled
     */
    Optional<Map<String, Object>> updateAccountMultipleFields();
    
    /**
     * Deletes an account.
     * 
     * @return Optional with account ID as string, or empty if deletion was cancelled
     */
    Optional<String> deleteAccount();
    
    /**
     * Restores a previously deleted account.
     * 
     * @return Optional with account ID as string, or empty if restoration was cancelled
     */
    Optional<String> restoreAccount();
    
    /**
     * Lists all accounts.
     * 
     * @param accounts list of account DTOs to display
     */
    void listAllAccounts(List<AccountDTO> accounts);
    
    /**
     * Displays account information and asks for confirmation.
     * 
     * @param account the account entity to display
     * @return true if user confirms the information is correct, false otherwise
     */
    boolean displayAccount(Account account);

    /**
     * Shows a message to the user
     * @param message the message to display
     */
    void showMessage(String message);
}
