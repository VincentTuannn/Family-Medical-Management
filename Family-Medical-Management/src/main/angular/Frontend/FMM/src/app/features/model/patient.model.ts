// src/app/shared/models/patient.model.ts
export interface PatientDTO {
  patientId: number;  
  userId: number;  
  fullName: string;  
  dateOfBirth: Date;  
  gender: 'male' | 'female' | 'other';  
  bloodType: string;  
  emergencyContact: string;  
  createdAt: Date;  
}