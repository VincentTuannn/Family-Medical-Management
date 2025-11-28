import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { PaymentDTO, PaymentRequest } from '../../model/payment.model';

@Injectable({
  providedIn: 'root'
})
export class PaymentService {
  private apiUrl = `${environment.apiUrl}/payment`;

  constructor(private http: HttpClient) {}

  createPayment(paymentRequest: PaymentRequest): Observable<PaymentDTO> {
    return this.http.post<PaymentDTO>(this.apiUrl, paymentRequest);
  }

  getPaymentById(id: number): Observable<PaymentDTO> {
    return this.http.get<PaymentDTO>(`${this.apiUrl}/${id}`);
  }

  getPaymentsByAppointmentId(appointmentId: number): Observable<PaymentDTO[]> {
    return this.http.get<PaymentDTO[]>(`${this.apiUrl}/appointment/${appointmentId}`);
  }

  processPayment(paymentId: number): Observable<PaymentDTO> {
    return this.http.post<PaymentDTO>(`${this.apiUrl}/${paymentId}/process`, {});
  }
}

