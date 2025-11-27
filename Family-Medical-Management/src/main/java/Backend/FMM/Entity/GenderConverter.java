package Backend.FMM.Entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class GenderConverter implements AttributeConverter<Patient.Gender, String> {

    @Override
    public String convertToDatabaseColumn(Patient.Gender gender) {
        if (gender == null) {
            return null;
        }
        // Lưu vào database dưới dạng lowercase
        return gender.name().toLowerCase();
    }

    @Override
    public Patient.Gender convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            // Convert từ database (có thể là lowercase) sang enum (uppercase)
            return Patient.Gender.valueOf(dbData.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Nếu không tìm thấy, trả về MALE làm mặc định
            return Patient.Gender.MALE;
        }
    }
}

