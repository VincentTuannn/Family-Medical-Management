export interface MedicalRecordDTO {
  recordId: number;
  patientId: number;
  diagnosis: string;
  treatment: string;
  medications?: string;
  allergies?: string;
  notes?: string;
  recordDate: Date;
  doctorName: string;
}

