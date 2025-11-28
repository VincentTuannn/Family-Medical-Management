export interface ChatRequest {
  message: string;
  conversationId?: string;
  useRAG?: boolean;
  useDatabase?: boolean; // Use database context
}

export interface ChatResponse {
  response: string;
  conversationId?: string;
  sources?: string[];
  usedRAG?: boolean;
}

export interface DocumentDTO {
  documentId?: number;
  fileName: string;
  fileType?: string;
  content?: string;
  chunkIndex?: number;
  metadata?: any;
  createdAt?: Date;
  userId?: number;
}

export interface ChatMessage {
  id: string;
  content: string;
  isUser: boolean;
  timestamp: Date;
  sources?: string[];
  usedRAG?: boolean;
  loading?: boolean;
}

