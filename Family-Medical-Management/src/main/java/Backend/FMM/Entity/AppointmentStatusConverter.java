package Backend.FMM.Entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AppointmentStatusConverter implements AttributeConverter<Appointment.Status, String> {

    @Override
    public String convertToDatabaseColumn(Appointment.Status status) {
        if (status == null) {
            return null;
        }
        // Lưu vào database dưới dạng lowercase
        return status.name().toLowerCase();
    }

    @Override
    public Appointment.Status convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            // Convert từ database (có thể là lowercase) sang enum (uppercase)
            return Appointment.Status.valueOf(dbData.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Nếu không tìm thấy, trả về SCHEDULED làm mặc định
            return Appointment.Status.SCHEDULED;
        }
    }
}

