import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { DoctorDTO } from '../../model/doctor.model';

@Injectable({
  providedIn: 'root'
})
export class DoctorService {
  private apiUrl = `${environment.apiUrl}/doctor`;

  constructor(private http: HttpClient) {}

  getAllDoctors(): Observable<DoctorDTO[]> {
    return this.http.get<DoctorDTO[]>(this.apiUrl);
  }

  hireDoctor(doctorId: number, request: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/${doctorId}/hire`, request);
  }
}
