import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ChatRequest, ChatResponse, DocumentDTO } from '../../model/chat.model';

@Injectable({
  providedIn: 'root'
})
export class AIService {
  private apiUrl = `${environment.apiUrl}/ai`;

  constructor(private http: HttpClient) {}

  /**
   * Chat with AI (with or without RAG)
   */
  chat(request: ChatRequest): Observable<ChatResponse> {
    console.log('📤 Sending chat request:', {
      url: `${this.apiUrl}/chat`,
      request: request
    });
    return this.http.post<ChatResponse>(`${this.apiUrl}/chat`, request);
  }

  /**
   * Upload document for RAG
   */
  uploadDocument(file: File): Observable<DocumentDTO> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<DocumentDTO>(`${this.apiUrl}/documents/upload`, formData);
  }

  /**
   * Get user's documents
   */
  getDocuments(): Observable<DocumentDTO[]> {
    return this.http.get<DocumentDTO[]>(`${this.apiUrl}/documents`);
  }

  /**
   * Delete document
   */
  deleteDocument(documentId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/documents/${documentId}`);
  }
}

