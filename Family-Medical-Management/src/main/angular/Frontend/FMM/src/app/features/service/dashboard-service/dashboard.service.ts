import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';
import { Observable, forkJoin } from 'rxjs'; 
import { map } from 'rxjs/operators'; 

export interface DashboardStats {
  patients: number;
  appointments: number;
  transfers: number;
  doctors: number;
}

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getDashboardStats(): Observable<DashboardStats> {
    // Gọi đồng thời nhiều API
    const patients$ = this.http.get<any[]>(`${this.apiUrl}/patient/my`).pipe(map(arr => arr.length));
    const appointments$ = this.http.get<any[]>(`${this.apiUrl}/appointment/my`).pipe(map(arr => arr.length));
    const transfers$ = this.http.get<any[]>(`${this.apiUrl}/transfer/my`).pipe(map(arr => arr.length));
    const doctors$ = this.http.get<any[]>(`${this.apiUrl}/doctor`).pipe(map(arr => arr.length));

    return forkJoin({
      patients: patients$,
      appointments: appointments$,
      transfers: transfers$,
      doctors: doctors$,
    });
  }
}