export interface TransferDTO {
  transferId: number;
  userId: number;
  patientId: number;
  doctorId: number;
  recordIds?: number[];
  accessType: 'VIEW' | 'EDIT';
  expiresAt?: Date;
  status: 'PENDING' | 'APPROVED' | 'REVOKED';
  transferredAt: Date;
}

