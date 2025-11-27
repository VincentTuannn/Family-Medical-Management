import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { AppointmentDTO } from '../../model/appointment.model';

@Injectable({
  providedIn: 'root'
})
export class AppointmentService {
  private apiUrl = `${environment.apiUrl}/appointment`;

  constructor(private http: HttpClient) {}

  getAllAppointments(): Observable<AppointmentDTO[]> {
    return this.http.get<AppointmentDTO[]>(this.apiUrl);
  }

  getMyAppointments(): Observable<AppointmentDTO[]> {
    return this.http.get<AppointmentDTO[]>(`${this.apiUrl}/my`);
  }

  getAppointmentById(id: number): Observable<AppointmentDTO> {
    return this.http.get<AppointmentDTO>(`${this.apiUrl}/${id}`);
  }

  createAppointment(appointment: AppointmentDTO): Observable<AppointmentDTO> {
    // Format appointmentDate thành ISO string với local timezone để tránh bị convert sai
    const formattedAppointment = this.formatAppointmentForRequest(appointment);
    return this.http.post<AppointmentDTO>(this.apiUrl, formattedAppointment);
  }

  updateAppointment(id: number, appointment: AppointmentDTO): Observable<AppointmentDTO> {
    // Format appointmentDate thành ISO string với local timezone để tránh bị convert sai
    const formattedAppointment = this.formatAppointmentForRequest(appointment);
    return this.http.put<AppointmentDTO>(`${this.apiUrl}/${id}`, formattedAppointment);
  }

  /**
   * Format appointment để gửi lên server, đảm bảo date/time được format đúng
   */
  private formatAppointmentForRequest(appointment: AppointmentDTO): any {
    const formatted: any = { ...appointment };
    
    if (appointment.appointmentDate instanceof Date) {
      // Format Date thành ISO string với local timezone
      // Sử dụng toISOString() sẽ convert sang UTC, nên ta format thủ công
      const date = appointment.appointmentDate;
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      const hours = String(date.getHours()).padStart(2, '0');
      const minutes = String(date.getMinutes()).padStart(2, '0');
      const seconds = String(date.getSeconds()).padStart(2, '0');
      
      // Format: YYYY-MM-DDTHH:mm:ss (local time, không có timezone)
      formatted.appointmentDate = `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`;
    }
    
    return formatted;
  }

  deleteAppointment(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}

