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
    return this.http.post<AppointmentDTO>(this.apiUrl, appointment);
  }

  updateAppointment(id: number, appointment: AppointmentDTO): Observable<AppointmentDTO> {
    return this.http.put<AppointmentDTO>(`${this.apiUrl}/${id}`, appointment);
  }

  deleteAppointment(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}

