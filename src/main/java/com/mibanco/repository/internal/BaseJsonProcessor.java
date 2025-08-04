package com.mibanco.repository.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Utility class for generic JSON file processing
 * Applies the Single Responsibility Principle (SRP)
 * Handles only JSON read and write operations
 * For internal use of repositories only
 * @param <T> Entity type to process
 */
class BaseJsonProcessor<T> {
    
    private final ObjectMapper mapper;
    
    public BaseJsonProcessor() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
    
    /**
     * Loads data from JSON if file exists, or returns empty list
     * Functional approach using Optional
     * @param path Path to the JSON file
     * @param classType Class of objects (e.g., Client.class)
     * @return List of objects loaded from JSON or empty list
     */
    public List<T> loadDataConditionally(String path, Class<T> classType) {
        return Optional.ofNullable(path)
            .filter(this::fileExists)
            .map(file -> readJson(file, classType))
            .orElse(new ArrayList<>());
    }
    
    /**
     * Calculates the maximum ID from a list of entities
     * Functional approach using streams
     * @param entities List of entities
     * @param idExtractor Function to extract ID from each entity
     * @return The maximum ID found, or 0 if list is empty
     */
    public Long calculateMaxId(List<T> entities, java.util.function.Function<T, Long> idExtractor) {
        return entities.stream()
            .mapToLong(idExtractor::apply)
            .max()
            .orElse(0);
    }
    
    /**
     * Verifies if a file exists in the system
     * @param path File path
     * @return true if exists and is a file, false otherwise
     */
    private boolean fileExists(String path) {
        File file = new File(path);
        return file.exists() && file.isFile();
    }
    
    /**
     * Reads and converts data from JSON file
     * @param path Path to the JSON file
     * @param classType Class of objects (e.g., Client.class)
     * @return List of objects loaded from JSON
     */
    private List<T> readJson(String path, Class<T> classType) {
        try {
            File file = new File(path);
            
            return mapper.readValue(file, 
                mapper.getTypeFactory().constructCollectionType(List.class, classType));
                
        } catch (Exception e) {
            // If there's an error, return empty list
            System.err.println("Error reading JSON: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Saves a list of objects to JSON file
     * @param path Path where to save the JSON file
     * @param data List of objects to save
     */
    public void saveData(List<T> data, String path) {
        try {
            File file = new File(path);
            
            // Create directory if it doesn't exist
            file.getParentFile().mkdirs();
            
            // Write JSON with readable format
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, data);
            
        } catch (Exception e) {
            // Log the error (for now just print)
            System.err.println("Error saving JSON: " + e.getMessage());
        }
    }
} 