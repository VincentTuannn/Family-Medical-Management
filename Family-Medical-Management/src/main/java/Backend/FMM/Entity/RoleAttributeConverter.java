package Backend.FMM.Entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RoleAttributeConverter implements AttributeConverter<User.Role, String> {

    @Override
    public String convertToDatabaseColumn(User.Role role) {
        if (role == null) {
            return "USER"; // Default value
        }
        // Lưu enum dạng chữ hoa vào DB để nhất quán
        return role.name();
    }

    @Override
    public User.Role convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return User.Role.USER; // Default role
        }
        try {
            // Chuyển dữ liệu từ DB (có thể là chữ thường hoặc chữ hoa) thành enum
            return User.Role.valueOf(dbData.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Nếu không tìm thấy, trả về USER làm mặc định
            return User.Role.USER;
        }
    }
}

