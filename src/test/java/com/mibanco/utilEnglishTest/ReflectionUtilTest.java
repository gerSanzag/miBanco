package com.mibanco.utilEnglishTest;

import com.mibanco.utilEnglish.ReflectionUtil;
import com.mibanco.utilEnglish.ReflectionUtil.NoRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests for ReflectionUtil class")
class ReflectionUtilTest {

    // 1. Class with only simple fields
    static class OnlySimple {
        String name;
        int age;
        double balance;
    }

    // 2. Class with Optional fields
    static class WithOptional {
        String name;
        Optional<String> email;
        int age;
        Optional<Double> balance;
    }

    // 3. Class with @NoRequest fields
    static class WithNoRequest {
        String name;
        @NoRequest String secret;
        int age;
        @NoRequest String token;
    }

    // 4. Class with both filters
    static class Mixed {
        String name;
        @NoRequest String secret;
        Optional<String> email;
        int age;
        @NoRequest Optional<String> token;
        Optional<Double> balance;
    }

    // 5. Class without required fields
    static class WithoutRequired {
        @NoRequest Optional<String> secret;
        @NoRequest String token;
        Optional<Double> balance;
    }

    // 6. Empty class
    static class Empty {}

    @Test
    @DisplayName("Returns all simple fields")
    void returnsAllSimpleFields() {
        List<String> fields = ReflectionUtil.getRequiredFields(OnlySimple.class);
        assertThat(fields).containsExactlyInAnyOrder("name", "age", "balance");
    }

    @Test
    @DisplayName("Correctly filters Optional fields")
    void filtersOptionalFields() {
        List<String> fields = ReflectionUtil.getRequiredFields(WithOptional.class);
        assertThat(fields).containsExactlyInAnyOrder("name", "age");
    }

    @Test
    @DisplayName("Correctly filters fields with @NoRequest")
    void filtersNoRequestFields() {
        List<String> fields = ReflectionUtil.getRequiredFields(WithNoRequest.class);
        assertThat(fields).containsExactlyInAnyOrder("name", "age");
    }

    @Test
    @DisplayName("Correctly filters both: Optional and @NoRequest")
    void filtersBothFilters() {
        List<String> fields = ReflectionUtil.getRequiredFields(Mixed.class);
        assertThat(fields).containsExactlyInAnyOrder("name", "age");
    }

    @Test
    @DisplayName("Returns empty list if no required fields")
    void returnsEmptyListIfNoRequiredFields() {
        List<String> fields = ReflectionUtil.getRequiredFields(WithoutRequired.class);
        assertThat(fields).isEmpty();
    }

    @Test
    @DisplayName("Returns empty list for class without fields")
    void returnsEmptyListForEmptyClass() {
        List<String> fields = ReflectionUtil.getRequiredFields(Empty.class);
        assertThat(fields).isEmpty();
    }
} 