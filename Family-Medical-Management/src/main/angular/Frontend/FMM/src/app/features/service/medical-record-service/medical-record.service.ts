import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { MedicalRecordDTO } from '../../model/medical-record.model';

@Injectable({
  providedIn: 'root'
})
export class MedicalRecordService {
  private apiUrl = `${environment.apiUrl}/medical-record`;

  constructor(private http: HttpClient) {}

  getAllMedicalRecords(): Observable<MedicalRecordDTO[]> {
    return this.http.get<MedicalRecordDTO[]>(this.apiUrl);
  }

  getMedicalRecordById(id: number): Observable<MedicalRecordDTO> {
    return this.http.get<MedicalRecordDTO>(`${this.apiUrl}/${id}`);
  }

  createMedicalRecord(record: MedicalRecordDTO): Observable<MedicalRecordDTO> {
    return this.http.post<MedicalRecordDTO>(this.apiUrl, record);
  }

  updateMedicalRecord(id: number, record: MedicalRecordDTO): Observable<MedicalRecordDTO> {
    return this.http.put<MedicalRecordDTO>(`${this.apiUrl}/${id}`, record);
  }

  deleteMedicalRecord(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}

