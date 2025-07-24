package com.mibanco.utilEnglish;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for working with reflection in models
 * Gets required fields generically
 */
public class ReflectionUtil {
    
    /**
     * Annotation to mark fields that should not be requested from the user
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface NoRequest {
        String reason() default "";
    }
    
    /**
     * Gets the names of required fields of a class
     * (fields that are NOT of Optional type and do NOT have the @NoRequest annotation)
     * @param clazz The class to examine
     * @return List with the names of required fields
     */
    public static List<String> getRequiredFields(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> !field.getType().equals(java.util.Optional.class))
                .filter(field -> !field.isAnnotationPresent(NoRequest.class))
                .map(Field::getName)
                .collect(Collectors.toList());
    }
} 