package com.mibanco.vista.interna;

import com.mibanco.util.ReflexionUtil;
import com.mibanco.vista.util.BaseVista;
import com.mibanco.vista.util.Consola;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Implementación base para todas las vistas
 * Proporciona la funcionalidad común que comparten todas las vistas
 * @param <T> Tipo de DTO que maneja la vista
 */
abstract class BaseVistaImpl<T> implements BaseVista<T> {
    
    protected final Consola consola;
    
    /**
     * Constructor protegido que inicializa la consola
     */
    protected BaseVistaImpl() {
        this.consola = new ConsolaImpl(new Scanner(System.in));
    }
    
    @Override
    public Optional<Integer> obtenerOpcion() {
        try {
            return Optional.of(Integer.parseInt(consola.leerLinea().trim()));
        } catch (NumberFormatException e) {
            mostrarMensaje("Error: Por favor, introduzca un número válido.");
            return Optional.empty();
        }
    }
    
    @Override
    public void mostrarMensaje(String mensaje) {
        consola.mostrar(">> " + mensaje + "\n");
    }
    
    @Override
    public boolean confirmarAccion(T entidad, String titulo, String mensaje) {
        consola.mostrar("\n--- " + titulo + " ---\n");
        mostrarEntidad(Optional.of(entidad));
        consola.mostrar("\n" + mensaje + " (s/n): ");
        String respuesta = consola.leerLinea().toLowerCase();
        return "s".equals(respuesta);
    }
    
    /**
     * Método genérico para solicitar datos de cualquier modelo
     * Usa reflexión para obtener los campos requeridos automáticamente
     * @param modelo Clase del modelo (ej: Cliente.class)
     * @return Map con los datos solicitados o Map vacío si se cancela
     */
    protected Map<String, String> solicitarDatosGenerico(Class<?> modelo) {
        List<String> campos = ReflexionUtil.obtenerCamposRequeridos(modelo);
        Map<String, String> datos = new HashMap<>();
        
        for (String campo : campos) {
            String valor = solicitarCampoPorTipo(campo, modelo);
            if (valor == null) {
                return new HashMap<>(); // Cancelar si algún campo falla
            }
            datos.put(campo, valor);
        }
        
        return datos;
    }
    
    /**
     * Solicita un campo según su tipo usando reflexión
     * @param campo Nombre del campo
     * @param modelo Clase del modelo
     * @return Valor del campo o null si se cancela
     */
    private String solicitarCampoPorTipo(String campo, Class<?> modelo) {
        // Validaciones específicas por tipo de campo
        if (campo.toLowerCase().contains("fecha")) {
            return solicitarFecha(campo);
        }
        
        if (campo.toLowerCase().contains("email")) {
            return solicitarEmail(campo);
        }
        
        if (campo.toLowerCase().contains("telefono") || campo.toLowerCase().contains("teléfono")) {
            return solicitarTelefono(campo);
        }
        
        if (campo.toLowerCase().contains("dni") || campo.toLowerCase().contains("identificacion")) {
            return solicitarIdentificacion(campo);
        }
        
        if (campo.toLowerCase().contains("saldo") || campo.toLowerCase().contains("monto")) {
            return solicitarDecimal(campo);
        }
        
        // Para el resto de campos, lectura simple con validación básica
        return leerCampoConValidacion(campo);
    }
    
    /**
     * Solicita una fecha con validación mejorada
     * @param nombreCampo Nombre del campo de fecha
     * @return Fecha en formato String o null si se cancela
     */
    private String solicitarFecha(String nombreCampo) {
        while (true) {
            String fechaStr = leerCampo(nombreCampo + " (yyyy-MM-dd)");
            if (fechaStr.isEmpty()) {
                return null; // Cancelar
            }
            
            try {
                LocalDate fecha = LocalDate.parse(fechaStr, DateTimeFormatter.ISO_LOCAL_DATE);
                
                // Validar que la fecha no sea futura para fechas de nacimiento
                if (nombreCampo.toLowerCase().contains("nacimiento")) {
                    if (fecha.isAfter(LocalDate.now())) {
                        mostrarMensaje("Error: La fecha de nacimiento no puede ser futura.");
                        continue;
                    }
                }
                
                // Validar que la fecha no sea muy antigua
                if (fecha.isBefore(LocalDate.of(1900, 1, 1))) {
                    mostrarMensaje("Error: La fecha parece ser muy antigua. Verifique el formato.");
                    continue;
                }
                
                return fechaStr;
            } catch (DateTimeParseException e) {
                mostrarMensaje("Error: Formato de fecha incorrecto. Use yyyy-MM-dd (ejemplo: 1990-05-15).");
            }
        }
    }
    
    /**
     * Solicita un email con validación
     * @param nombreCampo Nombre del campo
     * @return Email validado o null si se cancela
     */
    private String solicitarEmail(String nombreCampo) {
        while (true) {
            String email = leerCampo(nombreCampo);
            if (email.isEmpty()) {
                return null; // Cancelar
            }
            
            // Validación básica de email
            if (email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                return email;
            } else {
                mostrarMensaje("Error: Formato de email incorrecto. Use ejemplo@dominio.com");
            }
        }
    }
    
    /**
     * Solicita un teléfono con validación
     * @param nombreCampo Nombre del campo
     * @return Teléfono validado o null si se cancela
     */
    private String solicitarTelefono(String nombreCampo) {
        while (true) {
            String telefono = leerCampo(nombreCampo);
            if (telefono.isEmpty()) {
                return null; // Cancelar
            }
            
            // Eliminar espacios y caracteres especiales
            String telefonoLimpio = telefono.replaceAll("[\\s\\-\\(\\)]", "");
            
            // Validar que sea numérico y tenga longitud razonable
            if (telefonoLimpio.matches("\\d{9,15}")) {
                return telefonoLimpio;
            } else {
                mostrarMensaje("Error: El teléfono debe tener entre 9 y 15 dígitos.");
            }
        }
    }
    
    /**
     * Solicita identificación con validación
     * @param nombreCampo Nombre del campo
     * @return Identificación validada o null si se cancela
     */
    private String solicitarIdentificacion(String nombreCampo) {
        while (true) {
            String identificacion = leerCampo(nombreCampo);
            if (identificacion.isEmpty()) {
                return null; // Cancelar
            }
            
            // Validar que tenga longitud razonable y formato alfanumérico
            if (identificacion.matches("^[A-Za-z0-9]{5,20}$")) {
                return identificacion.toUpperCase();
            } else {
                mostrarMensaje("Error: La identificación debe tener entre 5 y 20 caracteres alfanuméricos.");
            }
        }
    }
    
    /**
     * Solicita un valor decimal con validación
     * @param nombreCampo Nombre del campo
     * @return Decimal validado o null si se cancela
     */
    private String solicitarDecimal(String nombreCampo) {
        while (true) {
            String valor = leerCampo(nombreCampo);
            if (valor.isEmpty()) {
                return "0.00"; // Valor por defecto
            }
            
            try {
                double decimal = Double.parseDouble(valor);
                if (decimal < 0) {
                    mostrarMensaje("Error: El valor no puede ser negativo.");
                    continue;
                }
                if (decimal > 999999999.99) {
                    mostrarMensaje("Error: El valor es demasiado alto.");
                    continue;
                }
                return String.format("%.2f", decimal);
            } catch (NumberFormatException e) {
                mostrarMensaje("Error: Por favor, introduzca un número válido (ejemplo: 100.50).");
            }
        }
    }
    
    /**
     * Método protegido para leer un campo de texto con validación básica
     * @param nombreCampo Nombre del campo a leer
     * @return Valor del campo
     */
    private String leerCampoConValidacion(String nombreCampo) {
        while (true) {
            String valor = leerCampo(nombreCampo);
            if (valor.isEmpty()) {
                mostrarMensaje("Error: Este campo es obligatorio.");
                continue;
            }
            
            // Validar longitud mínima
            if (valor.length() < 2) {
                mostrarMensaje("Error: El campo debe tener al menos 2 caracteres.");
                continue;
            }
            
            // Validar longitud máxima
            if (valor.length() > 100) {
                mostrarMensaje("Error: El campo es demasiado largo (máximo 100 caracteres).");
                continue;
            }
            
            return valor;
        }
    }
    
    /**
     * Método protegido para leer un campo de texto
     * @param nombreCampo Nombre del campo a leer
     * @return Valor del campo
     */
    protected String leerCampo(String nombreCampo) {
        consola.mostrar(nombreCampo + ": ");
        return consola.leerLinea().trim();
    }
    
    /**
     * Método protegido para leer un número
     * @param mensaje Mensaje a mostrar
     * @return Optional con el número o vacío si no es válido
     */
    protected Optional<Long> leerNumero(String mensaje) {
        consola.mostrar(mensaje + ": ");
        try {
            String input = consola.leerLinea().trim();
            return input.isEmpty() ? Optional.empty() : Optional.of(Long.parseLong(input));
        } catch (NumberFormatException e) {
            mostrarMensaje("Error: Por favor, introduzca un número válido.");
            return Optional.empty();
        }
    }
    
    /**
     * Método abstracto para mostrar una entidad
     * Cada vista debe implementar cómo mostrar su tipo específico de entidad
     * @param entidad Optional con la entidad a mostrar
     */
    public abstract void mostrarEntidad(Optional<T> entidad);
} 