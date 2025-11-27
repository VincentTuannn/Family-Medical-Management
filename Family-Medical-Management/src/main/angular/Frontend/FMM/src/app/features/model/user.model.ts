export interface UserDTO {
  userId: number;
  username: string;
  email: string;
  password?: string;
  phone: string;
  address: string;
  createdAt: Date;
  role: 'USER' | 'DOCTOR' | 'ADMIN';
  isActive: boolean;
}

