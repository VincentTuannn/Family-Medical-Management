export interface AppointmentDTO {
  appointmentId: number;
  patientId: number;
  doctorId: number;
  transferId?: number;
  appointmentDate: Date;
  status: 'SCHEDULED' | 'COMPLETED' | 'CANCELLED';
  notes?: string;
}

