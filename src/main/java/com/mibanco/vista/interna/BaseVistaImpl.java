package com.mibanco.vista.interna;

import com.mibanco.util.ReflexionUtil;
import com.mibanco.vista.util.BaseVista;
import com.mibanco.vista.util.Consola;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;


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
        consola.mostrar("Seleccione una opción: ");
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
            Optional<String> valorOpt = solicitarCampoPorTipo(campo, modelo);
            if (valorOpt.isEmpty()) {
                return new HashMap<>(); // Cancelar si algún campo falla
            }
            datos.put(campo, valorOpt.get());
        }
        
        return datos;
    }
    
    /**
     * Solicita un campo con validación específica usando enfoque funcional
     * Usa un Map de funciones para evitar múltiples if anidados
     */
    private Optional<String> solicitarCampoPorTipo(String campo, Class<?> modelo) {
        // Map funcional de validadores por tipo de campo
        Map<String, Function<String, Optional<String>>> validadores = Map.of(
            "fecha", this::solicitarFecha,
            "email", campoStr -> solicitarEmail(campoStr),
            "telefono", campoStr -> solicitarTelefono(campoStr),
            "teléfono", campoStr -> solicitarTelefono(campoStr),
            "dni", campoStr -> solicitarIdentificacion(campoStr),
            "identificacion", campoStr -> solicitarIdentificacion(campoStr),
            "saldo", campoStr -> solicitarDecimal(campoStr),
            "monto", campoStr -> solicitarDecimal(campoStr)
        );
        
        // Enfoque funcional: buscar validador y aplicarlo
        String campoLower = campo.toLowerCase();
        return validadores.entrySet().stream()
            .filter(entry -> campoLower.contains(entry.getKey()))
            .findFirst()
            .flatMap(entry -> entry.getValue().apply(campo))
            .or(() -> leerCampoConValidacion(campo));
    }
    
    /**
     * Solicita una fecha con validación mejorada usando enfoque funcional
     * @param nombreCampo Nombre del campo de fecha
     * @return Optional con la fecha en formato String o vacío si se cancela
     */
    private Optional<String> solicitarFecha(String nombreCampo) {
        while (true) {
            String fechaStr = leerCampo(nombreCampo + " (yyyy-MM-dd)");
            if (fechaStr.isEmpty()) return Optional.empty();
            
            try {
                LocalDate fecha = LocalDate.parse(fechaStr, DateTimeFormatter.ISO_LOCAL_DATE);
                
                // Validaciones funcionales con Optional
                return Optional.of(fecha)
                    .filter(f -> nombreCampo.toLowerCase().contains("nacimiento") ? !f.isAfter(LocalDate.now()) : true)
                    .filter(f -> !f.isBefore(LocalDate.of(1900, 1, 1)))
                    .map(f -> fechaStr)
                    .or(() -> {
                        if (nombreCampo.toLowerCase().contains("nacimiento") && fecha.isAfter(LocalDate.now())) {
                            mostrarMensaje("Error: La fecha de nacimiento no puede ser futura.");
                        } else if (fecha.isBefore(LocalDate.of(1900, 1, 1))) {
                            mostrarMensaje("Error: La fecha parece ser muy antigua. Verifique el formato.");
                        }
                        return Optional.empty(); // Para continuar el bucle
                    });
                    
            } catch (DateTimeParseException e) {
                mostrarMensaje("Error: Formato de fecha incorrecto. Use yyyy-MM-dd (ejemplo: 1990-05-15).");
            }
        }
    }
    
    /**
     * Solicita un email con validación
     * @param nombreCampo Nombre del campo
     * @return Optional con email validado o vacío si se cancela
     */
    private Optional<String> solicitarEmail(String nombreCampo) {
        while (true) {
            String email = leerCampo(nombreCampo);
            if (email == null || email.isEmpty()) {
                return Optional.empty(); // Cancelar
            }
            return Optional.ofNullable(email)
                .flatMap(e -> e.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$") 
                    ? Optional.of(e) 
                    : Optional.empty())
                .or(() -> {
                    mostrarMensaje("Error: El formato de email no es válido.");
                    return Optional.empty();
                });
        }
    }
    
    /**
     * Solicita un teléfono con validación
     * @param nombreCampo Nombre del campo
     * @return Optional con teléfono validado o vacío si se cancela
     */
    private Optional<String> solicitarTelefono(String nombreCampo) {
        while (true) {
            String telefono = leerCampo(nombreCampo);
            if (telefono == null || telefono.isEmpty()) {
                return Optional.empty(); // Cancelar
            }
            
            // Eliminar espacios y caracteres especiales
            String telefonoLimpio = telefono.replaceAll("[\\s\\-\\(\\)]", "");
            
            return Optional.ofNullable(telefonoLimpio)
                .flatMap(t -> t.matches("\\d{9,15}") ? Optional.of(t) : Optional.empty())
                .or(() -> {
                    mostrarMensaje("Error: El teléfono debe tener entre 9 y 15 dígitos.");
                    return Optional.empty();
                });
        }
    }
    
    /**
     * Solicita identificación con validación
     * @param nombreCampo Nombre del campo
     * @return Optional con identificación validada o vacío si se cancela
     */
    private Optional<String> solicitarIdentificacion(String nombreCampo) {
        while (true) {
            String identificacion = leerCampo(nombreCampo);
            if (identificacion == null || identificacion.isEmpty()) {
                return Optional.empty(); // Cancelar
            }
            
            return Optional.ofNullable(identificacion)
                .flatMap(id -> id.matches("^[A-Za-z0-9]{5,20}$") ? Optional.of(id.toUpperCase()) : Optional.empty())
                .or(() -> {
                    mostrarMensaje("Error: La identificación debe tener entre 5 y 20 caracteres alfanuméricos.");
                    return Optional.empty();
                });
        }
    }
    
    /**
     * Solicita un valor decimal con validación
     * @param nombreCampo Nombre del campo
     * @return Optional con decimal validado o vacío si se cancela
     */
    private Optional<String> solicitarDecimal(String nombreCampo) {
        while (true) {
            String valor = leerCampo(nombreCampo);
            if (valor == null || valor.isEmpty()) {
                return Optional.of("0.00"); // Valor por defecto
            }
            
            try {
                double decimal = Double.parseDouble(valor);
                return Optional.of(decimal)
                    .filter(d -> d >= 0)
                    .filter(d -> d <= 999999999.99)
                    .map(d -> String.format("%.2f", d))
                    .or(() -> {
                        if (decimal < 0) {
                            mostrarMensaje("Error: El valor no puede ser negativo.");
                        } else {
                            mostrarMensaje("Error: El valor es demasiado alto.");
                        }
                        return Optional.empty();
                    });
            } catch (NumberFormatException e) {
                mostrarMensaje("Error: Por favor, introduzca un número válido (ejemplo: 100.50).");
            }
        }
    }
    
    /**
     * Método protegido para leer un campo de texto con validación básica
     * @param nombreCampo Nombre del campo a leer
     * @return Optional con valor del campo o vacío si se cancela
     */
    private Optional<String> leerCampoConValidacion(String nombreCampo) {
        while (true) {
            String valor = leerCampo(nombreCampo);
            if (valor == null || valor.isEmpty()) {
                mostrarMensaje("Error: Este campo es obligatorio.");
                continue;
            }
            
            return Optional.ofNullable(valor)
                .filter(v -> v.length() >= 2)
                .filter(v -> v.length() <= 100)
                .or(() -> {
                    if (valor.length() < 2) {
                        mostrarMensaje("Error: El campo debe tener al menos 2 caracteres.");
                    } else {
                        mostrarMensaje("Error: El campo es demasiado largo (máximo 100 caracteres).");
                    }
                    return Optional.empty();
                });
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
     * Método genérico para leer un número Long con mensaje personalizado
     * @param mensaje Mensaje a mostrar
     * @param mensajeError Mensaje de error personalizado
     * @return Optional con el número o vacío si no es válido
     */
    protected Optional<Long> leerNumero(String mensaje, String mensajeError) {
        consola.mostrar(mensaje + ": ");
        try {
            String input = consola.leerLinea().trim();
            return input.isEmpty() ? Optional.empty() : Optional.of(Long.parseLong(input));
        } catch (NumberFormatException e) {
            mostrarMensaje("Error: " + mensajeError);
            return Optional.empty();
        }
    }
    
    /**
     * Método genérico para leer un número Integer con validación de rango
     * @param mensaje Mensaje a mostrar
     * @param min Valor mínimo (inclusive)
     * @param max Valor máximo (inclusive)
     * @param mensajeError Mensaje de error personalizado
     * @return Optional con el número o vacío si no es válido
     */
    protected Optional<Integer> leerNumeroConRango(String mensaje, int min, int max, String mensajeError) {
        consola.mostrar(mensaje + ": ");
        try {
            int seleccion = Integer.parseInt(consola.leerLinea().trim());
            return (seleccion >= min && seleccion <= max) 
                ? Optional.of(seleccion) 
                : Optional.empty();
        } catch (NumberFormatException e) {
            mostrarMensaje("Error: " + mensajeError);
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