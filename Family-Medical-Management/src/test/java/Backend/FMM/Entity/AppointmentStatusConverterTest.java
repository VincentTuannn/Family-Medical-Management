package Backend.FMM.Entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentStatusConverterTest {

    private AppointmentStatusConverter converter;

    @BeforeEach
    void setUp() {
        converter = new AppointmentStatusConverter();
    }

    @Test
    void testConvertToDatabaseColumn_WithSCHEDULED_ShouldReturnLowercase() {
        // When
        String result = converter.convertToDatabaseColumn(Appointment.Status.SCHEDULED);

        // Then
        assertEquals("scheduled", result);
    }

    @Test
    void testConvertToDatabaseColumn_WithCOMPLETED_ShouldReturnLowercase() {
        // When
        String result = converter.convertToDatabaseColumn(Appointment.Status.COMPLETED);

        // Then
        assertEquals("completed", result);
    }

    @Test
    void testConvertToDatabaseColumn_WithCANCELLED_ShouldReturnLowercase() {
        // When
        String result = converter.convertToDatabaseColumn(Appointment.Status.CANCELLED);

        // Then
        assertEquals("cancelled", result);
    }

    @Test
    void testConvertToDatabaseColumn_WithNull_ShouldReturnNull() {
        // When
        String result = converter.convertToDatabaseColumn(null);

        // Then
        assertNull(result);
    }

    @Test
    void testConvertToEntityAttribute_WithScheduled_ShouldReturnSCHEDULED() {
        // When
        Appointment.Status result = converter.convertToEntityAttribute("scheduled");

        // Then
        assertEquals(Appointment.Status.SCHEDULED, result);
    }

    @Test
    void testConvertToEntityAttribute_WithSCHEDULED_ShouldReturnSCHEDULED() {
        // When
        Appointment.Status result = converter.convertToEntityAttribute("SCHEDULED");

        // Then
        assertEquals(Appointment.Status.SCHEDULED, result);
    }

    @Test
    void testConvertToEntityAttribute_WithCompleted_ShouldReturnCOMPLETED() {
        // When
        Appointment.Status result = converter.convertToEntityAttribute("completed");

        // Then
        assertEquals(Appointment.Status.COMPLETED, result);
    }

    @Test
    void testConvertToEntityAttribute_WithCancelled_ShouldReturnCANCELLED() {
        // When
        Appointment.Status result = converter.convertToEntityAttribute("cancelled");

        // Then
        assertEquals(Appointment.Status.CANCELLED, result);
    }

    @Test
    void testConvertToEntityAttribute_WithNull_ShouldReturnNull() {
        // When
        Appointment.Status result = converter.convertToEntityAttribute(null);

        // Then
        assertNull(result);
    }

    @Test
    void testConvertToEntityAttribute_WithInvalidValue_ShouldReturnSCHEDULED() {
        // When
        Appointment.Status result = converter.convertToEntityAttribute("invalid");

        // Then
        assertEquals(Appointment.Status.SCHEDULED, result);
    }
}

