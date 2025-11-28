import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { AuditLogDTO } from '../../model/audit-log.model';

@Injectable({
  providedIn: 'root'
})
export class AuditLogService {
  private apiUrl = `${environment.apiUrl}/audit-log`;

  constructor(private http: HttpClient) {}

  getAllAuditLogs(): Observable<AuditLogDTO[]> {
    return this.http.get<AuditLogDTO[]>(this.apiUrl);
  }

  getAuditLogById(id: number): Observable<AuditLogDTO> {
    return this.http.get<AuditLogDTO>(`${this.apiUrl}/${id}`);
  }

  getAuditLogsByUserId(userId: number): Observable<AuditLogDTO[]> {
    return this.http.get<AuditLogDTO[]>(`${this.apiUrl}/user/${userId}`);
  }

  createAuditLog(log: AuditLogDTO): Observable<AuditLogDTO> {
    return this.http.post<AuditLogDTO>(this.apiUrl, log);
  }

  deleteAuditLog(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}

