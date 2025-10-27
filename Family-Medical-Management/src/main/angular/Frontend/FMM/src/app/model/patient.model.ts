// src/app/shared/models/patient.model.ts
export interface PatientDTO {
  patientId: number;  // Integer ID
  userId: number;  // FK từ User
  fullName: string;  // Họ tên
  dateOfBirth: Date;  // java.sql.Date map sang JS Date
  gender: 'male' | 'female' | 'other';  // Enum-like string
  bloodType: string;  // e.g., 'O+'
  emergencyContact: string;  // Số điện thoại
  createdAt: Date;  // java.sql.Timestamp map sang JS Date (optional nếu không cần)
}