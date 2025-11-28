package Backend.FMM.Entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleAttributeConverterTest {

    private RoleAttributeConverter converter;

    @BeforeEach
    void setUp() {
        converter = new RoleAttributeConverter();
    }

    @Test
    void testConvertToDatabaseColumn_WithUSER_ShouldReturnUSER() {
        // When
        String result = converter.convertToDatabaseColumn(User.Role.USER);

        // Then
        assertEquals("USER", result);
    }

    @Test
    void testConvertToDatabaseColumn_WithADMIN_ShouldReturnADMIN() {
        // When
        String result = converter.convertToDatabaseColumn(User.Role.ADMIN);

        // Then
        assertEquals("ADMIN", result);
    }

    @Test
    void testConvertToDatabaseColumn_WithNull_ShouldReturnUSER() {
        // When
        String result = converter.convertToDatabaseColumn(null);

        // Then
        assertEquals("USER", result);
    }

    @Test
    void testConvertToEntityAttribute_WithUSER_ShouldReturnUSER() {
        // When
        User.Role result = converter.convertToEntityAttribute("USER");

        // Then
        assertEquals(User.Role.USER, result);
    }

    @Test
    void testConvertToEntityAttribute_WithUser_ShouldReturnUSER() {
        // When
        User.Role result = converter.convertToEntityAttribute("user");

        // Then
        assertEquals(User.Role.USER, result);
    }

    @Test
    void testConvertToEntityAttribute_WithADMIN_ShouldReturnADMIN() {
        // When
        User.Role result = converter.convertToEntityAttribute("ADMIN");

        // Then
        assertEquals(User.Role.ADMIN, result);
    }

    @Test
    void testConvertToEntityAttribute_WithAdmin_ShouldReturnADMIN() {
        // When
        User.Role result = converter.convertToEntityAttribute("admin");

        // Then
        assertEquals(User.Role.ADMIN, result);
    }

    @Test
    void testConvertToEntityAttribute_WithNull_ShouldReturnUSER() {
        // When
        User.Role result = converter.convertToEntityAttribute(null);

        // Then
        assertEquals(User.Role.USER, result);
    }

    @Test
    void testConvertToEntityAttribute_WithEmptyString_ShouldReturnUSER() {
        // When
        User.Role result = converter.convertToEntityAttribute("");

        // Then
        assertEquals(User.Role.USER, result);
    }

    @Test
    void testConvertToEntityAttribute_WithInvalidValue_ShouldReturnUSER() {
        // When
        User.Role result = converter.convertToEntityAttribute("invalid");

        // Then
        assertEquals(User.Role.USER, result);
    }
}

