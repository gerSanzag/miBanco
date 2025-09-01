package com.mibanco.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mibanco.util.ReflectionUtil;
import lombok.Value;
import lombok.Builder;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * Class that represents a bank client
 * Implements a completely functional approach with total immutability
 */
@Value
@Builder
public class Client implements Identifiable {
    
    @ReflectionUtil.NoRequest(reason = "Generated automatically")
    Long id;
    String firstName;        // nombre
    String lastName;         // apellido
    String dni;
    LocalDate birthDate;     // fechaNacimiento
    String email;
    String phone;            // telefono
    String address;          // direccion
    
    @JsonCreator
    public Client(
        @JsonProperty("id") Long id,
        @JsonProperty("firstName") String firstName,
        @JsonProperty("lastName") String lastName,
        @JsonProperty("dni") String dni,
        @JsonProperty("birthDate") LocalDate birthDate,
        @JsonProperty("email") String email,
        @JsonProperty("phone") String phone,
        @JsonProperty("address") String address
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dni = dni;
        this.birthDate = birthDate;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }
    
    /**
     * Defines the required fields to create a new client
     * @return List of field names that are mandatory
     */
    public static List<String> getRequiredFields() {
        return Arrays.asList(
            "firstName",      // nombre
            "lastName",       // apellido
            "dni",
            "email",
            "phone",          // telefono
            "address",        // direccion
            "birthDate"       // fechaNacimiento
        );
    }
    
    /**
     * Factory method that facilitates instance creation
     */
    public static Client of(Long id, String firstName, String lastName, String dni,
                            LocalDate birthDate, String email, String phone, String address) {
        return Client.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .dni(dni)
                .birthDate(birthDate)
                .email(email)
                .phone(phone)
                .address(address)
                .build();
    }

} 