import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';  // Config URL backend
import { PatientDTO } from '../../model/patient.model';  // Model DTO từ backend

@Injectable({
  providedIn: 'root'
})
export class PatientService {
  private apiUrl = `${environment.apiUrl}/patient`;  // e.g., http://localhost:8081/api/patient

  // Đã loại bỏ httpOptions tĩnh
  // Angular tự động set Content-Type: application/json khi có body
  // Interceptor tự động thêm Authorization header
  // Khi cần custom headers (cho Spring AI, Kafka, etc.), có thể thêm trực tiếp trong method

  constructor(private http: HttpClient) {}

  // GET all patients
  getAllPatients(): Observable<PatientDTO[]> {
    return this.http.get<PatientDTO[]>(this.apiUrl);
  }

  // GET by ID
  getPatientById(id: number): Observable<PatientDTO> {
    return this.http.get<PatientDTO>(`${this.apiUrl}/${id}`);
  }

  // POST create
  createPatient(patient: PatientDTO): Observable<PatientDTO> {
    return this.http.post<PatientDTO>(this.apiUrl, patient);
  }

  // PUT update
  updatePatient(id: number, patient: PatientDTO): Observable<PatientDTO> {
    return this.http.put<PatientDTO>(`${this.apiUrl}/${id}`, patient);
  }

  // DELETE by ID
  deletePatient(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getPatientsByUserId(userId: number): Observable<PatientDTO[]> {
    return this.http.get<PatientDTO[]>(`${this.apiUrl}/user/${userId}`);  // Gọi API /api/patient/user/{userId}
  }

  // Ví dụ: Khi cần thêm custom headers cho Spring AI (tương lai)
  // getAIPrediction(data: any): Observable<any> {
  //   return this.http.post(`${this.apiUrl}/ai/predict`, data, {
  //     headers: { 'X-AI-Model': 'gpt-4', 'X-Stream': 'true' }
  //   });
  //   // Interceptor vẫn tự động thêm Authorization header
  // }
}