package com.mibanco.controller;

/**
 * Controller interface for Account operations
 * Acts as a bridge between AccountView and AccountService
 */
public interface AccountController {
    
    /**
     * Creates a new account using data from the view
     * @return true if account was created successfully, false otherwise
     */
    boolean createAccount();

    /**
     * Searches for an account using criteria from the view
     * @return true if account was found and displayed, false otherwise
     */
    boolean searchAccount();
    
    /**
     * Searches for an account by ID or account number
     * @return true if account was found and displayed, false otherwise
     */
    boolean searchAccount();
    
    /**
     * Updates account's balance
     * @return true if balance was updated successfully, false otherwise
     */
    boolean updateAccountBalance();
    
    /**
     * Updates account's status (active/inactive)
     * @return true if status was updated successfully, false otherwise
     */
    boolean updateAccountStatus();
    
    /**
     * Updates account's holder
     * @return true if holder was updated successfully, false otherwise
     */
    boolean updateAccountHolder();
    
    /**
     * Updates multiple account fields at once
     * @return true if fields were updated successfully, false otherwise
     */
    boolean updateAccountMultipleFields();
    
    /**
     * Deletes an account by ID
     * @return true if account was deleted successfully, false otherwise
     */
    boolean deleteAccount();
    
    /**
     * Restores a previously deleted account
     * @return true if account was restored successfully, false otherwise
     */
    boolean restoreAccount();
    
    /**
     * Lists all accounts
     * @return true if accounts were displayed successfully, false otherwise
     */
    boolean listAllAccounts();
    
    /**
     * Shows the main account menu
     */
    void showAccountMenu();
    
    /**
     * Shows the update account submenu
     */
    void showUpdateAccountMenu();
}
