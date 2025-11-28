package Backend.FMM.Controller;

import Backend.FMM.DTO.ChatRequestDTO;
import Backend.FMM.DTO.ChatResponseDTO;
import Backend.FMM.DTO.DocumentDTO;
import Backend.FMM.Service.DocumentService;
import Backend.FMM.Service.RAGService;
import Backend.FMM.Security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class ChatController {
    
    @Autowired
    private RAGService ragService;
    
    @Autowired
    private DocumentService documentService;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    /**
     * Chat endpoint with RAG
     */
    @PostMapping("/chat")
    public ResponseEntity<ChatResponseDTO> chat(
            @RequestBody ChatRequestDTO request,
            HttpServletRequest httpRequest) {
        
        Integer userId = getUserIdFromRequest(httpRequest);
        
        // Debug logging
        System.out.println("🔍 Chat request - useRAG: " + request.getUseRAG() + ", useDatabase: " + request.getUseDatabase() + ", userId: " + userId);
        System.out.println("🔍 Message: " + request.getMessage());
        
        ChatResponseDTO response;
        if (request.getUseRAG() != null && request.getUseRAG()) {
            if (userId == null) {
                System.out.println("⚠️ WARNING: userId is null, RAG will search in ALL documents");
            }
            // Use RAG with or without database
            boolean useDatabase = request.getUseDatabase() != null && request.getUseDatabase();
            response = ragService.chatWithRAGAndDatabase(request.getMessage(), userId, useDatabase);
        } else {
            response = ragService.directChat(request.getMessage());
        }
        
        System.out.println("✅ Chat response - usedRAG: " + response.getUsedRAG() + ", sources: " + response.getSources());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Upload document
     */
    @PostMapping("/documents/upload")
    public ResponseEntity<?> uploadDocument(
            @RequestParam(value = "file", required = false) MultipartFile file,
            HttpServletRequest httpRequest) {
        
        try {
            // Check if file is present
            if (file == null || file.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "File không được tìm thấy trong request");
                error.put("message", "Vui lòng đảm bảo gửi file với key 'file' trong form-data");
                error.put("hint", "Trong Postman, chọn Body -> form-data -> Key: 'file' (type: File)");
                return ResponseEntity.badRequest().body(error);
            }
            
            // Check file size (50MB limit)
            if (file.getSize() > 50 * 1024 * 1024) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "File quá lớn. Kích thước tối đa là 50MB");
                error.put("fileSize", String.valueOf(file.getSize()));
                error.put("maxSize", "52428800"); // 50MB in bytes
                return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(error);
            }
            
            Integer userId = getUserIdFromRequest(httpRequest);
            if (userId == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Không thể xác định user. Vui lòng đăng nhập lại.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            
            DocumentDTO document = documentService.uploadDocument(file, userId);
            return ResponseEntity.ok(document);
        } catch (MaxUploadSizeExceededException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "File quá lớn. Kích thước tối đa là 50MB");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Lỗi khi upload file: " + e.getMessage());
            error.put("type", e.getClass().getSimpleName());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Get user's documents
     */
    @GetMapping("/documents")
    public ResponseEntity<List<DocumentDTO>> getUserDocuments(
            HttpServletRequest httpRequest) {
        
        Integer userId = getUserIdFromRequest(httpRequest);
        List<DocumentDTO> documents = documentService.getUserDocuments(userId);
        return ResponseEntity.ok(documents);
    }
    
    /**
     * Delete document
     */
    @DeleteMapping("/documents/{id}")
    public ResponseEntity<Void> deleteDocument(
            @PathVariable Integer id,
            HttpServletRequest httpRequest) {
        
        try {
            Integer userId = getUserIdFromRequest(httpRequest);
            documentService.deleteDocument(id, userId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Extract user ID from JWT token
     */
    private Integer getUserIdFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            Integer userId = jwtTokenProvider.getUserIdFromJWT(token);
            System.out.println("🔍 Auth header found, token length: " + token.length() + ", userId: " + userId);
            return userId;
        } else {
            System.out.println("⚠️ No Authorization header found or invalid format");
        }
        return null;
    }
}

