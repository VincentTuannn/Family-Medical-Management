package Backend.FMM.Entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GenderConverterTest {

    private GenderConverter converter;

    @BeforeEach
    void setUp() {
        converter = new GenderConverter();
    }

    @Test
    void testConvertToDatabaseColumn_WithMALE_ShouldReturnLowercase() {
        // When
        String result = converter.convertToDatabaseColumn(Patient.Gender.MALE);

        // Then
        assertEquals("male", result);
    }

    @Test
    void testConvertToDatabaseColumn_WithFEMALE_ShouldReturnLowercase() {
        // When
        String result = converter.convertToDatabaseColumn(Patient.Gender.FEMALE);

        // Then
        assertEquals("female", result);
    }

    @Test
    void testConvertToDatabaseColumn_WithOTHER_ShouldReturnLowercase() {
        // When
        String result = converter.convertToDatabaseColumn(Patient.Gender.OTHER);

        // Then
        assertEquals("other", result);
    }

    @Test
    void testConvertToDatabaseColumn_WithNull_ShouldReturnNull() {
        // When
        String result = converter.convertToDatabaseColumn(null);

        // Then
        assertNull(result);
    }

    @Test
    void testConvertToEntityAttribute_WithMale_ShouldReturnMALE() {
        // When
        Patient.Gender result = converter.convertToEntityAttribute("male");

        // Then
        assertEquals(Patient.Gender.MALE, result);
    }

    @Test
    void testConvertToEntityAttribute_WithMALE_ShouldReturnMALE() {
        // When
        Patient.Gender result = converter.convertToEntityAttribute("MALE");

        // Then
        assertEquals(Patient.Gender.MALE, result);
    }

    @Test
    void testConvertToEntityAttribute_WithFemale_ShouldReturnFEMALE() {
        // When
        Patient.Gender result = converter.convertToEntityAttribute("female");

        // Then
        assertEquals(Patient.Gender.FEMALE, result);
    }

    @Test
    void testConvertToEntityAttribute_WithOther_ShouldReturnOTHER() {
        // When
        Patient.Gender result = converter.convertToEntityAttribute("other");

        // Then
        assertEquals(Patient.Gender.OTHER, result);
    }

    @Test
    void testConvertToEntityAttribute_WithNull_ShouldReturnNull() {
        // When
        Patient.Gender result = converter.convertToEntityAttribute(null);

        // Then
        assertNull(result);
    }

    @Test
    void testConvertToEntityAttribute_WithInvalidValue_ShouldReturnMALE() {
        // When
        Patient.Gender result = converter.convertToEntityAttribute("invalid");

        // Then
        assertEquals(Patient.Gender.MALE, result);
    }
}

