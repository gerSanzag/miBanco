package com.mibanco.dtoEnglish;

import java.time.LocalDate;
import java.util.Optional;

import com.mibanco.utilEnglish.ReflectionUtil.NoRequest;

import lombok.Builder;
import lombok.Value;

/**
 * DTO to transfer Client information between layers
 * We use @Value to create an immutable class (functional approach)
 * and Optional to handle possible null values
 */
@Value
@Builder(toBuilder = true) // We enable toBuilder to facilitate the creation of new instances
public class ClientDTO {
    @NoRequest(reason = "Generated automatically") // Se genera automáticamente
    Long id;
    String firstName;        // nombre
    String lastName;         // apellido
    String dni;
    LocalDate birthDate;     // fechaNacimiento
    String email;
    String phone;            // telefono
    String address;          // direccion

    /**
     * Static method that builds a ClientDTO with optional values
     * Example of functional approach and Optionals usage
     */
    public static ClientDTO of(Long id, Optional<String> firstName, Optional<String> lastName, Optional<String> dni, 
                                Optional<String> email, Optional<String> phone,
                                Optional<LocalDate> birthDate, Optional<String> address) {
        return ClientDTO.builder()
                .id(id)
                .firstName(firstName.orElse(null))
                .lastName(lastName.orElse(null))
                .dni(dni.orElse(null))
                .email(email.orElse(null))
                .phone(phone.orElse(null))
                .birthDate(birthDate.orElse(null))
                .address(address.orElse(null))
                .build();
    }
    
    /**
     * Immutable version to update email
     * @return A new instance with updated email
     */
    public ClientDTO withEmail(String newEmail) {
        return this.toBuilder()
                .email(newEmail)
                .build();
    }
    
    /**
     * Immutable version to update phone
     * @return A new instance with updated phone
     */
    public ClientDTO withPhone(String newPhone) {
        return this.toBuilder()
                .phone(newPhone)
                .build();
    }
    
    /**
     * Immutable version to update address
     * @return A new instance with updated address
     */
    public ClientDTO withAddress(String newAddress) {
        return this.toBuilder()
                .address(newAddress)
                .build();
    }
    
    /**
     * Immutable version to update multiple fields at once
     * @return A new instance with updated fields
     */
    public ClientDTO withContactData(Optional<String> newEmail, 
                                      Optional<String> newPhone, 
                                      Optional<String> newAddress) {
        return this.toBuilder()
                .email(newEmail.orElse(this.email))
                .phone(newPhone.orElse(this.phone))
                .address(newAddress.orElse(this.address))
                .build();
    }
} 