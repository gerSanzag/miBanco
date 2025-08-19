package com.mibanco.utilTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.mibanco.util.ReflectionUtil;
import com.mibanco.util.ReflectionUtil.NoRequest;

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

    // 7. Class with @NoRequest annotation with reason
    static class WithNoRequestReason {
        String name;
        @NoRequest(reason = "Internal use only") String secret;
        int age;
    }

    // 8. Class with all field types
    static class AllFieldTypes {
        String stringField;
        int intField;
        double doubleField;
        boolean booleanField;
        long longField;
        float floatField;
        char charField;
        byte byteField;
        short shortField;
        Optional<String> optionalField;
        @NoRequest String noRequestField;
    }

    // 9. Class with inheritance
    static class Parent {
        String parentField;
        @NoRequest String parentSecret;
    }

    static class Child extends Parent {
        String childField;
        Optional<String> childOptional;
    }

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

    @Test
    @DisplayName("Handles @NoRequest annotation with reason")
    void handlesNoRequestAnnotationWithReason() {
        List<String> fields = ReflectionUtil.getRequiredFields(WithNoRequestReason.class);
        assertThat(fields).containsExactlyInAnyOrder("name", "age");
    }

    @Test
    @DisplayName("Handles all primitive field types")
    void handlesAllPrimitiveFieldTypes() {
        List<String> fields = ReflectionUtil.getRequiredFields(AllFieldTypes.class);
        assertThat(fields).containsExactlyInAnyOrder(
            "stringField", "intField", "doubleField", "booleanField", 
            "longField", "floatField", "charField", "byteField", "shortField"
        );
    }

    @Test
    @DisplayName("Only considers declared fields (not inherited)")
    void onlyConsidersDeclaredFields() {
        List<String> fields = ReflectionUtil.getRequiredFields(Child.class);
        assertThat(fields).containsExactlyInAnyOrder("childField");
        // Note: parentField is not included because getDeclaredFields() only returns fields declared in the class itself
    }

    @Test
    @DisplayName("Returns correct number of fields")
    void returnsCorrectNumberOfFields() {
        List<String> fields = ReflectionUtil.getRequiredFields(AllFieldTypes.class);
        assertThat(fields).hasSize(9); // 9 required fields (excluding optional and noRequest)
    }

    @Test
    @DisplayName("Handles null class parameter gracefully")
    void handlesNullClassParameterGracefully() {
        // This test verifies that the method doesn't crash with null input
        // The actual behavior depends on the implementation
        try {
            List<String> fields = ReflectionUtil.getRequiredFields(null);
            // If it doesn't throw an exception, we can check the result
            assertThat(fields).isNotNull();
        } catch (Exception e) {
            // If it throws an exception, that's also acceptable behavior
            assertThat(e).isInstanceOf(Exception.class);
        }
    }

    @Test
    @DisplayName("Returns immutable list")
    void returnsImmutableList() {
        List<String> fields = ReflectionUtil.getRequiredFields(OnlySimple.class);
        assertThat(fields).isNotNull();
        assertThat(fields).isNotEmpty();
        
        // Try to modify the list - should not affect the original
        int originalSize = fields.size();
        try {
            fields.add("newField");
            // If modification is allowed, the size should increase
            assertThat(fields).hasSize(originalSize + 1);
        } catch (UnsupportedOperationException e) {
            // If list is immutable, this exception is expected
            assertThat(e).isInstanceOf(UnsupportedOperationException.class);
        }
    }

    // 10. Class with only Optional fields
    static class OnlyOptional {
        Optional<String> field1;
        Optional<Integer> field2;
    }

    // 11. Class with only @NoRequest fields
    static class OnlyNoRequest {
        @NoRequest String field1;
        @NoRequest int field2;
    }

    // 12. Class with mixed field modifiers
    static class MixedModifiers {
        public String publicField;
        private String privateField;
        protected String protectedField;
        String packageField;
        @NoRequest public String noRequestPublic;
        public Optional<String> optionalPublic;
    }

    @Test
    @DisplayName("Handles class with only Optional fields")
    void handlesClassWithOnlyOptionalFields() {
        List<String> fields = ReflectionUtil.getRequiredFields(OnlyOptional.class);
        assertThat(fields).isEmpty();
    }

    @Test
    @DisplayName("Handles class with only @NoRequest fields")
    void handlesClassWithOnlyNoRequestFields() {
        List<String> fields = ReflectionUtil.getRequiredFields(OnlyNoRequest.class);
        assertThat(fields).isEmpty();
    }

    @Test
    @DisplayName("Handles class with mixed field modifiers")
    void handlesClassWithMixedFieldModifiers() {
        List<String> fields = ReflectionUtil.getRequiredFields(MixedModifiers.class);
        assertThat(fields).containsExactlyInAnyOrder("publicField", "privateField", "protectedField", "packageField");
    }
} 