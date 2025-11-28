export interface AuditLogDTO {
  logId: number;
  userId: number;
  action: string;
  details: string;
  ipAddress?: string;
  createdAt: Date;
}

