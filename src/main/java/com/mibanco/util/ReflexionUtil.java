package com.mibanco.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Clase utilitaria para trabajar con reflexión en los modelos
 * Obtiene los campos requeridos de forma genérica
 */
public class ReflexionUtil {
    
    /**
     * Anotación para marcar campos que no se deben solicitar al usuario
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface NoSolicitar {
        String razon() default "";
    }
    
    /**
     * Obtiene los nombres de los campos requeridos de una clase
     * (campos que NO son de tipo Optional y NO tienen la anotación @NoSolicitar)
     * @param clase La clase a examinar
     * @return Lista con los nombres de los campos requeridos
     */
    public static List<String> obtenerCamposRequeridos(Class<?> clase) {
        return Arrays.stream(clase.getDeclaredFields())
                .filter(field -> !field.getType().equals(java.util.Optional.class))
                .filter(field -> !field.isAnnotationPresent(NoSolicitar.class))
                .map(Field::getName)
                .collect(Collectors.toList());
    }
} 