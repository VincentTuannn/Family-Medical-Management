export interface PaymentDTO {
  paymentId?: number;
  appointmentId?: number;
  amount: number;
  paymentMethod: 'CASH' | 'BANK_TRANSFER' | 'CREDIT_CARD' | 'E_WALLET';
  status: 'PENDING' | 'COMPLETED' | 'FAILED' | 'REFUNDED';
  transactionId?: string;
  paidAt?: Date;
  notes?: string;
}

export interface PaymentRequest {
  appointmentId?: number;
  doctorId: number;
  amount: number;
  paymentMethod: 'CASH' | 'BANK_TRANSFER' | 'CREDIT_CARD' | 'E_WALLET';
  appointmentDate: Date;
  notes?: string;
  reason?: string;
  transactionId?: string;
}

