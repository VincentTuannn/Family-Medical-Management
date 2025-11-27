import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { TransferDTO } from '../../model/transfer.model';

@Injectable({
  providedIn: 'root'
})
export class TransferService {
  private apiUrl = `${environment.apiUrl}/transfer`;

  constructor(private http: HttpClient) {}

  getAllTransfers(): Observable<TransferDTO[]> {
    return this.http.get<TransferDTO[]>(this.apiUrl);
  }

  getTransferById(id: number): Observable<TransferDTO> {
    return this.http.get<TransferDTO>(`${this.apiUrl}/${id}`);
  }

  createTransfer(transfer: TransferDTO): Observable<TransferDTO> {
    return this.http.post<TransferDTO>(this.apiUrl, transfer);
  }

  updateTransfer(id: number, transfer: TransferDTO): Observable<TransferDTO> {
    return this.http.put<TransferDTO>(`${this.apiUrl}/${id}`, transfer);
  }

  deleteTransfer(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}

