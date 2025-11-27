package Backend.FMM.Service;

import Backend.FMM.DTO.DocumentDTO;
import Backend.FMM.Entity.Document;
import Backend.FMM.Repository.DocumentRepository;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DocumentService {
    
    @Autowired
    private DocumentRepository documentRepository;
    
    @Autowired
    private VectorStoreService vectorStoreService;
    
    /**
     * Upload and process document
     */
    public DocumentDTO uploadDocument(MultipartFile file, Integer userId) throws IOException {
        String fileName = file.getOriginalFilename();
        String fileType = file.getContentType();
        String content;
        
        // Read document content based on file type
        if (fileType != null && (fileType.contains("pdf") || 
                                 fileType.contains("word") || 
                                 fileType.contains("msword") ||
                                 fileType.contains("application/vnd.openxmlformats-officedocument"))) {
            // Use Tika for PDF, Word, etc.
            try {
                Resource resource = new InputStreamResource(file.getInputStream(), fileName);
                DocumentReader reader = new TikaDocumentReader(resource);
                List<org.springframework.ai.document.Document> documents = reader.get();
                content = documents.stream()
                    .map(org.springframework.ai.document.Document::getContent)
                    .reduce("", (a, b) -> a + "\n" + b);
            } catch (Exception e) {
                // Fallback to plain text if Tika fails
                content = new String(file.getBytes(), StandardCharsets.UTF_8);
            }
        } else {
            // Plain text
            content = new String(file.getBytes(), StandardCharsets.UTF_8);
        }
        
        // Chunk the content
        List<String> chunks = chunkText(content, 500, 100); // 500 tokens per chunk, 100 overlap
        
        // Prepare metadata
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("fileName", fileName);
        metadata.put("fileType", fileType);
        metadata.put("fileSize", file.getSize());
        
        // Add to vector store
        vectorStoreService.addDocuments(chunks, fileName, fileType, userId, metadata);
        
        // Return DTO
        DocumentDTO dto = new DocumentDTO();
        dto.setFileName(fileName);
        dto.setFileType(fileType);
        dto.setContent(content.substring(0, Math.min(500, content.length()))); // First 500 chars
        dto.setUserId(userId);
        
        return dto;
    }
    
    /**
     * Simple text chunking
     */
    private List<String> chunkText(String text, int chunkSize, int overlap) {
        List<String> chunks = new ArrayList<>();
        
        if (text == null || text.isEmpty()) {
            return chunks;
        }
        
        // Split by sentences first
        String[] sentences = text.split("[.!?]\\s+");
        
        StringBuilder currentChunk = new StringBuilder();
        int currentLength = 0;
        
        for (String sentence : sentences) {
            int sentenceLength = sentence.length();
            
            if (currentLength + sentenceLength > chunkSize && currentChunk.length() > 0) {
                // Save current chunk
                chunks.add(currentChunk.toString().trim());
                
                // Start new chunk with overlap
                String lastPart = currentChunk.toString();
                int overlapStart = Math.max(0, lastPart.length() - overlap);
                currentChunk = new StringBuilder(lastPart.substring(overlapStart));
                currentLength = currentChunk.length();
            }
            
            currentChunk.append(sentence).append(". ");
            currentLength += sentenceLength;
        }
        
        // Add last chunk
        if (currentChunk.length() > 0) {
            chunks.add(currentChunk.toString().trim());
        }
        
        return chunks;
    }
    
    /**
     * Get user's documents
     */
    public List<DocumentDTO> getUserDocuments(Integer userId) {
        List<Document> documents = documentRepository.findByUserId(userId);
        return documents.stream()
            .map(this::toDTO)
            .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Delete document
     */
    public void deleteDocument(Integer documentId, Integer userId) {
        if (documentId == null || userId == null) {
            throw new IllegalArgumentException("Document ID and User ID cannot be null");
        }
        Optional<Document> docOpt = documentRepository.findById(documentId);
        if (docOpt.isPresent()) {
            Document doc = docOpt.get();
            // Check ownership
            if (doc.getUser() != null && doc.getUser().getUserId().equals(userId)) {
                documentRepository.delete(doc);
            } else {
                throw new RuntimeException("Unauthorized to delete this document");
            }
        }
    }
    
    private DocumentDTO toDTO(Document document) {
        DocumentDTO dto = new DocumentDTO();
        dto.setDocumentId(document.getDocumentId());
        dto.setFileName(document.getFileName());
        dto.setFileType(document.getFileType());
        dto.setContent(document.getContent());
        dto.setChunkIndex(document.getChunkIndex());
        dto.setMetadata(document.getMetadata());
        dto.setCreatedAt(new java.util.Date(document.getCreatedAt().getTime()));
        if (document.getUser() != null) {
            dto.setUserId(document.getUser().getUserId());
        }
        return dto;
    }
}

