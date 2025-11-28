package Backend.FMM.Controller;

import Backend.FMM.DTO.ChatRequestDTO;
import Backend.FMM.DTO.ChatResponseDTO;
import Backend.FMM.DTO.DocumentDTO;
import Backend.FMM.Security.JwtTokenProvider;
import Backend.FMM.Service.DocumentService;
import Backend.FMM.Service.RAGService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class ChatControllerTest {

    @Mock
    private RAGService ragService;

    @Mock
    private DocumentService documentService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private HttpServletRequest httpRequest;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private ChatController chatController;

    private ChatResponseDTO mockChatResponse;
    private DocumentDTO mockDocumentDTO;

    @BeforeEach
    void setUp() {
        mockChatResponse = new ChatResponseDTO();
        mockChatResponse.setResponse("Test response");
        mockChatResponse.setUsedRAG(true);
        mockChatResponse.setSources(new ArrayList<>());

        mockDocumentDTO = new DocumentDTO();
        mockDocumentDTO.setDocumentId(1);
        mockDocumentDTO.setFileName("test.pdf");
        mockDocumentDTO.setFileType("application/pdf");
        mockDocumentDTO.setUserId(1);
    }

    @Test
    void testChat_WithRAG() {
        // Arrange
        ChatRequestDTO request = new ChatRequestDTO();
        request.setMessage("What is diabetes?");
        request.setUseRAG(true);
        request.setUseDatabase(false); // Explicitly set useDatabase
        Integer userId = 1;

        when(httpRequest.getHeader("Authorization")).thenReturn("Bearer test-token");
        when(jwtTokenProvider.getUserIdFromJWT("test-token")).thenReturn(userId);
        when(ragService.chatWithRAGAndDatabase(request.getMessage(), userId, false)).thenReturn(mockChatResponse);

        // Act
        ResponseEntity<ChatResponseDTO> response = chatController.chat(request, httpRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ChatResponseDTO body = response.getBody();
        assertNotNull(body);
        assertEquals("Test response", body.getResponse());
        assertTrue(body.getUsedRAG());

        verify(ragService, times(1)).chatWithRAGAndDatabase(request.getMessage(), userId, false);
        verify(ragService, never()).directChat(anyString());
    }

    @Test
    void testChat_WithoutRAG() {
        // Arrange
        ChatRequestDTO request = new ChatRequestDTO();
        request.setMessage("Hello");
        request.setUseRAG(false);

        mockChatResponse.setUsedRAG(false);
        when(ragService.directChat(request.getMessage())).thenReturn(mockChatResponse);

        // Act
        ResponseEntity<ChatResponseDTO> response = chatController.chat(request, httpRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ChatResponseDTO body = response.getBody();
        assertNotNull(body);
        assertEquals("Test response", body.getResponse());
        assertFalse(body.getUsedRAG());

        verify(ragService, times(1)).directChat(request.getMessage());
        verify(ragService, never()).chatWithRAGAndDatabase(anyString(), anyInt(), anyBoolean());
    }

    @Test
    void testChat_NullUseRAG() {
        // Arrange
        ChatRequestDTO request = new ChatRequestDTO();
        request.setMessage("Hello");
        request.setUseRAG(null);

        mockChatResponse.setUsedRAG(false);
        when(ragService.directChat(request.getMessage())).thenReturn(mockChatResponse);

        // Act
        ResponseEntity<ChatResponseDTO> response = chatController.chat(request, httpRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(ragService, times(1)).directChat(request.getMessage());
    }

    @Test
    void testChat_WithRAGAndDatabase() {
        // Arrange
        ChatRequestDTO request = new ChatRequestDTO();
        request.setMessage("What is diabetes?");
        request.setUseRAG(true);
        request.setUseDatabase(true);
        Integer userId = 1;

        when(httpRequest.getHeader("Authorization")).thenReturn("Bearer test-token");
        when(jwtTokenProvider.getUserIdFromJWT("test-token")).thenReturn(userId);
        when(ragService.chatWithRAGAndDatabase(request.getMessage(), userId, true)).thenReturn(mockChatResponse);

        // Act
        ResponseEntity<ChatResponseDTO> response = chatController.chat(request, httpRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ChatResponseDTO body = response.getBody();
        assertNotNull(body);
        assertEquals("Test response", body.getResponse());
        assertTrue(body.getUsedRAG());

        verify(ragService, times(1)).chatWithRAGAndDatabase(request.getMessage(), userId, true);
        verify(ragService, never()).directChat(anyString());
    }

    @Test
    void testUploadDocument_Success() throws Exception {
        // Arrange
        Integer userId = 1;

        when(httpRequest.getHeader("Authorization")).thenReturn("Bearer test-token");
        when(jwtTokenProvider.getUserIdFromJWT("test-token")).thenReturn(userId);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getSize()).thenReturn(1024L); // 1KB
        when(documentService.uploadDocument(multipartFile, userId)).thenReturn(mockDocumentDTO);

        // Act
        ResponseEntity<?> response = chatController.uploadDocument(multipartFile, httpRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof DocumentDTO);

        verify(documentService, times(1)).uploadDocument(multipartFile, userId);
    }

    @Test
    void testUploadDocument_FileIsNull() throws Exception {
        // Arrange
        MultipartFile nullFile = null;

        // Act
        ResponseEntity<?> response = chatController.uploadDocument(nullFile, httpRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, String> error = (Map<String, String>) response.getBody();
        assertNotNull(error);
        assertTrue(error.containsKey("error"));
        assertEquals("File không được tìm thấy trong request", error.get("error"));

        verify(documentService, never()).uploadDocument(any(), anyInt());
    }

    @Test
    void testUploadDocument_FileIsEmpty() throws Exception {
        // Arrange
        when(multipartFile.isEmpty()).thenReturn(true);

        // Act
        ResponseEntity<?> response = chatController.uploadDocument(multipartFile, httpRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);

        verify(documentService, never()).uploadDocument(any(), anyInt());
    }

    @Test
    void testUploadDocument_FileTooLarge() throws Exception {
        // Arrange
        Integer userId = 1;
        long fileSize = 60 * 1024 * 1024L; // 60MB

        // Lenient vì test throw exception trước khi dùng JWT stubbing
        lenient().when(httpRequest.getHeader("Authorization")).thenReturn("Bearer test-token");
        lenient().when(jwtTokenProvider.getUserIdFromJWT("test-token")).thenReturn(userId);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getSize()).thenReturn(fileSize);

        // Act
        ResponseEntity<?> response = chatController.uploadDocument(multipartFile, httpRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, String> error = (Map<String, String>) response.getBody();
        assertNotNull(error);
        assertTrue(error.containsKey("error"));
        assertTrue(error.get("error").contains("quá lớn"));

        verify(documentService, never()).uploadDocument(any(), anyInt());
    }

    @Test
    void testUploadDocument_MaxUploadSizeExceededException() throws Exception {
        // Arrange
        Integer userId = 1;

        when(httpRequest.getHeader("Authorization")).thenReturn("Bearer test-token");
        when(jwtTokenProvider.getUserIdFromJWT("test-token")).thenReturn(userId);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getSize()).thenReturn(1024L);
        when(documentService.uploadDocument(multipartFile, userId))
                .thenThrow(new MaxUploadSizeExceededException(50 * 1024 * 1024L));

        // Act
        ResponseEntity<?> response = chatController.uploadDocument(multipartFile, httpRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
    }

    @Test
    void testUploadDocument_Unauthorized() throws Exception {
        // Arrange
        when(httpRequest.getHeader("Authorization")).thenReturn("Bearer test-token");
        when(jwtTokenProvider.getUserIdFromJWT("test-token")).thenReturn(null);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getSize()).thenReturn(1024L);

        // Act
        ResponseEntity<?> response = chatController.uploadDocument(multipartFile, httpRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);

        verify(documentService, never()).uploadDocument(any(), anyInt());
    }

    @Test
    void testUploadDocument_NoAuthHeader() throws Exception {
        // Arrange
        when(httpRequest.getHeader("Authorization")).thenReturn(null);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getSize()).thenReturn(1024L);

        // Act
        ResponseEntity<?> response = chatController.uploadDocument(multipartFile, httpRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        verify(documentService, never()).uploadDocument(any(), anyInt());
    }

    @Test
    void testGetUserDocuments_Success() {
        // Arrange
        Integer userId = 1;
        List<DocumentDTO> documents = new ArrayList<>();
        documents.add(mockDocumentDTO);

        when(httpRequest.getHeader("Authorization")).thenReturn("Bearer test-token");
        when(jwtTokenProvider.getUserIdFromJWT("test-token")).thenReturn(userId);
        when(documentService.getUserDocuments(userId)).thenReturn(documents);

        // Act
        ResponseEntity<List<DocumentDTO>> response = chatController.getUserDocuments(httpRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<DocumentDTO> body = response.getBody();
        if (body != null) {
            assertEquals(1, body.size());
        }

        verify(documentService, times(1)).getUserDocuments(userId);
    }

    @Test
    void testGetUserDocuments_Empty() {
        // Arrange
        Integer userId = 1;
        List<DocumentDTO> emptyList = new ArrayList<>();

        when(httpRequest.getHeader("Authorization")).thenReturn("Bearer test-token");
        when(jwtTokenProvider.getUserIdFromJWT("test-token")).thenReturn(userId);
        when(documentService.getUserDocuments(userId)).thenReturn(emptyList);

        // Act
        ResponseEntity<List<DocumentDTO>> response = chatController.getUserDocuments(httpRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<DocumentDTO> body = response.getBody();
        assertNotNull(body);
        assertEquals(0, body.size());
    }

    @Test
    void testDeleteDocument_Success() {
        // Arrange
        Integer documentId = 1;
        Integer userId = 1;

        when(httpRequest.getHeader("Authorization")).thenReturn("Bearer test-token");
        when(jwtTokenProvider.getUserIdFromJWT("test-token")).thenReturn(userId);
        doNothing().when(documentService).deleteDocument(documentId, userId);

        // Act
        ResponseEntity<Void> response = chatController.deleteDocument(documentId, httpRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(documentService, times(1)).deleteDocument(documentId, userId);
    }

    @Test
    void testDeleteDocument_Exception() {
        // Arrange
        Integer documentId = 1;
        Integer userId = 1;

        when(httpRequest.getHeader("Authorization")).thenReturn("Bearer test-token");
        when(jwtTokenProvider.getUserIdFromJWT("test-token")).thenReturn(userId);
        doThrow(new RuntimeException("Document not found"))
                .when(documentService).deleteDocument(documentId, userId);

        // Act
        ResponseEntity<Void> response = chatController.deleteDocument(documentId, httpRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        verify(documentService, times(1)).deleteDocument(documentId, userId);
    }
}

