import { Component, OnInit, OnDestroy, ViewChild, ElementRef, AfterViewChecked } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AIService } from '../../../features/service/ai-service/ai.service';
import { ChatMessage, ChatRequest, DocumentDTO } from '../../../features/model/chat.model';
import { Nl2BrPipe } from '../../../features/pipe/nl2br.pipe';
import { Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-chat-container',
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatInputModule,
    MatFormFieldModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
    MatSnackBarModule,
    Nl2BrPipe
  ],
  templateUrl: './container.html',
  styleUrl: './container.scss'
})
export class ChatContainer implements OnInit, OnDestroy, AfterViewChecked {
  @ViewChild('messagesContainer') private messagesContainer!: ElementRef;
  @ViewChild('fileInput') private fileInput!: ElementRef<HTMLInputElement>;

  messages: ChatMessage[] = [];
  currentMessage: string = '';
  useRAG: boolean = true;
  isLoading: boolean = false;
  isUploading: boolean = false;
  documents: DocumentDTO[] = [];
  showDocuments: boolean = false;

  private destroy$ = new Subject<void>();
  private shouldScroll = false;

  constructor(
    private aiService: AIService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadDocuments();
    // Welcome message
    this.addMessage('Xin chào! Tôi là trợ lý AI chuyên về quản lý hồ sơ y tế gia đình. Tôi có thể giúp bạn tìm hiểu thông tin từ các tài liệu y tế của bạn. Hãy hỏi tôi bất cứ điều gì!', false);
  }

  ngAfterViewChecked(): void {
    if (this.shouldScroll) {
      this.scrollToBottom();
      this.shouldScroll = false;
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  sendMessage(): void {
    if (!this.currentMessage.trim() || this.isLoading) {
      return;
    }

    const userMessage = this.currentMessage.trim();
    this.currentMessage = '';

    // Add user message
    this.addMessage(userMessage, true);

    // Add loading message
    const loadingMessage = this.addMessage('Đang suy nghĩ...', false, true);

    this.isLoading = true;

    const request: ChatRequest = {
      message: userMessage,
      useRAG: this.useRAG
    };

    this.aiService.chat(request)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          // Remove loading message
          this.messages = this.messages.filter(m => m.id !== loadingMessage.id);

          // Add AI response
          this.addMessage(
            response.response,
            false,
            false,
            response.sources,
            response.usedRAG
          );
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Chat error:', error);
          // Remove loading message
          this.messages = this.messages.filter(m => m.id !== loadingMessage.id);

          // Add error message
          this.addMessage(
            'Xin lỗi, đã có lỗi xảy ra. Vui lòng thử lại sau.',
            false
          );
          this.isLoading = false;
          this.snackBar.open('Lỗi khi gửi tin nhắn', 'Đóng', { duration: 3000 });
        }
      });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      this.uploadFile(file);
    }
  }

  uploadFile(file: File): void {
    // Check file size (50MB)
    if (file.size > 50 * 1024 * 1024) {
      this.snackBar.open('File quá lớn. Kích thước tối đa là 50MB', 'Đóng', { duration: 5000 });
      return;
    }

    this.isUploading = true;
    this.snackBar.open('Đang upload file...', 'Đóng', { duration: 2000 });

    this.aiService.uploadDocument(file)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (document) => {
          this.snackBar.open(`Upload thành công: ${document.fileName}`, 'Đóng', { duration: 3000 });
          this.loadDocuments();
          this.isUploading = false;
          // Reset file input
          if (this.fileInput) {
            this.fileInput.nativeElement.value = '';
          }
        },
        error: (error) => {
          console.error('Upload error:', error);
          const errorMsg = error.error?.error || 'Lỗi khi upload file';
          this.snackBar.open(errorMsg, 'Đóng', { duration: 5000 });
          this.isUploading = false;
        }
      });
  }

  loadDocuments(): void {
    this.aiService.getDocuments()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (documents) => {
          this.documents = documents;
        },
        error: (error) => {
          console.error('Load documents error:', error);
        }
      });
  }

  deleteDocument(documentId: number): void {
    if (confirm('Bạn có chắc muốn xóa tài liệu này?')) {
      this.aiService.deleteDocument(documentId)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            this.snackBar.open('Đã xóa tài liệu', 'Đóng', { duration: 2000 });
            this.loadDocuments();
          },
          error: (error) => {
            console.error('Delete error:', error);
            this.snackBar.open('Lỗi khi xóa tài liệu', 'Đóng', { duration: 3000 });
          }
        });
    }
  }

  toggleRAG(): void {
    this.useRAG = !this.useRAG;
    const mode = this.useRAG ? 'RAG (với tài liệu)' : 'Chat thường';
    this.snackBar.open(`Đã chuyển sang chế độ: ${mode}`, 'Đóng', { duration: 2000 });
  }

  toggleDocuments(): void {
    this.showDocuments = !this.showDocuments;
  }

  private addMessage(
    content: string,
    isUser: boolean,
    loading: boolean = false,
    sources?: string[],
    usedRAG?: boolean
  ): ChatMessage {
    const message: ChatMessage = {
      id: Date.now().toString() + Math.random(),
      content,
      isUser,
      timestamp: new Date(),
      sources,
      usedRAG,
      loading
    };
    this.messages.push(message);
    this.shouldScroll = true;
    return message;
  }

  private scrollToBottom(): void {
    try {
      if (this.messagesContainer) {
        this.messagesContainer.nativeElement.scrollTop = 
          this.messagesContainer.nativeElement.scrollHeight;
      }
    } catch (err) {
      console.error('Scroll error:', err);
    }
  }

  formatTime(date: Date): string {
    return new Date(date).toLocaleTimeString('vi-VN', { 
      hour: '2-digit', 
      minute: '2-digit' 
    });
  }
}

