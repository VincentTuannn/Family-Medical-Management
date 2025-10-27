import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';  // Config URL backend
import { PatientDTO } from '../../../model/patient.model';  // Model DTO từ backend

@Injectable({
  providedIn: 'root'
})
export class PatientService {
  private apiUrl = `${environment.apiUrl}/patient`;  // e.g., http://localhost:8080/api/patient

  private httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
  };

  constructor(private http: HttpClient) {}

  // GET all patients
  getAllPatients(): Observable<PatientDTO[]> {
    return this.http.get<PatientDTO[]>(this.apiUrl, this.httpOptions);
  }

  // GET by ID
  getPatientById(id: number): Observable<PatientDTO> {
    return this.http.get<PatientDTO>(`${this.apiUrl}/${id}`, this.httpOptions);
  }

  // POST create
  createPatient(patient: PatientDTO): Observable<PatientDTO> {
    return this.http.post<PatientDTO>(this.apiUrl, patient, this.httpOptions);
  }

  // PUT update
  updatePatient(id: number, patient: PatientDTO): Observable<PatientDTO> {
    return this.http.put<PatientDTO>(`${this.apiUrl}/${id}`, patient, this.httpOptions);
  }

  // DELETE by ID
  deletePatient(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, this.httpOptions);
  }
}