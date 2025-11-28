package Backend.FMM.Service;

import Backend.FMM.DTO.DocumentDTO;
import Backend.FMM.Entity.Document;
import Backend.FMM.Entity.User;
import Backend.FMM.Repository.DocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private VectorStoreService vectorStoreService;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private DocumentService documentService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setUserId(1);
        mockUser.setUsername("testuser");
    }

    @Test
    void testUploadDocument_PlainText() throws IOException {
        // Arrange
        String content = "This is a plain text document.";
        byte[] contentBytes = content.getBytes();
        
        when(multipartFile.getOriginalFilename()).thenReturn("test.txt");
        when(multipartFile.getContentType()).thenReturn("text/plain");
        when(multipartFile.getBytes()).thenReturn(contentBytes);
        when(multipartFile.getSize()).thenReturn((long) contentBytes.length);
        // Lenient vì getInputStream() có thể không được dùng trong một số trường hợp
        lenient().when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(contentBytes));
        
        doNothing().when(vectorStoreService).addDocuments(anyList(), anyString(), anyString(), anyInt(), any());

        // Act
        DocumentDTO result = documentService.uploadDocument(multipartFile, 1);

        // Assert
        assertNotNull(result);
        assertEquals("test.txt", result.getFileName());
        assertEquals("text/plain", result.getFileType());
        assertEquals(1, result.getUserId());
        assertNotNull(result.getContent());
        
        verify(vectorStoreService, times(1)).addDocuments(anyList(), anyString(), anyString(), anyInt(), any());
    }

    @Test
    void testUploadDocument_PDF() throws IOException {
        // Arrange
        String content = "This is a PDF document content.";
        byte[] contentBytes = content.getBytes();
        
        when(multipartFile.getOriginalFilename()).thenReturn("test.pdf");
        when(multipartFile.getContentType()).thenReturn("application/pdf");
        when(multipartFile.getSize()).thenReturn((long) contentBytes.length);
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(contentBytes));
        
        doNothing().when(vectorStoreService).addDocuments(anyList(), anyString(), anyString(), anyInt(), any());

        // Act
        DocumentDTO result = documentService.uploadDocument(multipartFile, 1);

        // Assert
        assertNotNull(result);
        assertEquals("test.pdf", result.getFileName());
        assertEquals("application/pdf", result.getFileType());
        assertEquals(1, result.getUserId());
        
        verify(vectorStoreService, times(1)).addDocuments(anyList(), anyString(), anyString(), anyInt(), any());
    }

    @Test
    void testUploadDocument_WordDocument() throws IOException {
        // Arrange
        String content = "This is a Word document content.";
        byte[] contentBytes = content.getBytes();
        
        when(multipartFile.getOriginalFilename()).thenReturn("test.docx");
        when(multipartFile.getContentType()).thenReturn("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        when(multipartFile.getSize()).thenReturn((long) contentBytes.length);
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(contentBytes));
        
        doNothing().when(vectorStoreService).addDocuments(anyList(), anyString(), anyString(), anyInt(), any());

        // Act
        DocumentDTO result = documentService.uploadDocument(multipartFile, 1);

        // Assert
        assertNotNull(result);
        assertEquals("test.docx", result.getFileName());
        assertEquals(1, result.getUserId());
        
        verify(vectorStoreService, times(1)).addDocuments(anyList(), anyString(), anyString(), anyInt(), any());
    }

    @Test
    void testGetUserDocuments_Success() {
        // Arrange
        List<Document> documents = createMockDocuments();
        when(documentRepository.findByUserId(1)).thenReturn(documents);

        // Act
        List<DocumentDTO> result = documentService.getUserDocuments(1);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("test1.pdf", result.get(0).getFileName());
        assertEquals("test2.pdf", result.get(1).getFileName());
        
        verify(documentRepository, times(1)).findByUserId(1);
    }

    @Test
    void testGetUserDocuments_Empty() {
        // Arrange
        when(documentRepository.findByUserId(1)).thenReturn(new ArrayList<>());

        // Act
        List<DocumentDTO> result = documentService.getUserDocuments(1);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        
        verify(documentRepository, times(1)).findByUserId(1);
    }

    @Test
    void testDeleteDocument_Success() {
        // Arrange
        Integer documentId = 1;
        Integer userId = 1;
        
        Document document = new Document();
        document.setDocumentId(documentId);
        document.setFileName("test.pdf");
        document.setUser(mockUser);
        
        when(documentRepository.findById(documentId)).thenReturn(Optional.of(document));
        doNothing().when(documentRepository).delete(document);

        // Act
        assertDoesNotThrow(() -> documentService.deleteDocument(documentId, userId));

        // Assert
        verify(documentRepository, times(1)).findById(documentId);
        verify(documentRepository, times(1)).delete(document);
    }

    @Test
    void testDeleteDocument_NotFound() {
        // Arrange
        Integer documentId = 999;
        Integer userId = 1;
        
        when(documentRepository.findById(documentId)).thenReturn(Optional.empty());

        // Act
        assertDoesNotThrow(() -> documentService.deleteDocument(documentId, userId));

        // Assert
        verify(documentRepository, times(1)).findById(documentId);
        verify(documentRepository, never()).delete(any());
    }

    @Test
    void testDeleteDocument_Unauthorized() {
        // Arrange
        Integer documentId = 1;
        Integer userId = 2; // Different user
        
        Document document = new Document();
        document.setDocumentId(documentId);
        document.setFileName("test.pdf");
        document.setUser(mockUser); // User 1
        
        when(documentRepository.findById(documentId)).thenReturn(Optional.of(document));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            documentService.deleteDocument(documentId, userId),
            "Unauthorized to delete this document"
        );

        verify(documentRepository, times(1)).findById(documentId);
        verify(documentRepository, never()).delete(any());
    }

    @Test
    void testDeleteDocument_NullIds() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            documentService.deleteDocument(null, 1)
        );
        
        assertThrows(IllegalArgumentException.class, () -> 
            documentService.deleteDocument(1, null)
        );
        
        assertThrows(IllegalArgumentException.class, () -> 
            documentService.deleteDocument(null, null)
        );
        
        verify(documentRepository, never()).findById(any());
        verify(documentRepository, never()).delete(any());
    }

    @Test
    void testChunkText_EmptyText() throws IOException {
        // This tests the private chunkText method indirectly through uploadDocument
        // Arrange
        String content = "";
        byte[] contentBytes = content.getBytes();
        
        when(multipartFile.getOriginalFilename()).thenReturn("empty.txt");
        when(multipartFile.getContentType()).thenReturn("text/plain");
        when(multipartFile.getBytes()).thenReturn(contentBytes);
        when(multipartFile.getSize()).thenReturn(0L);
        // Lenient vì getInputStream() có thể không được dùng khi content rỗng
        lenient().when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(contentBytes));
        
        doNothing().when(vectorStoreService).addDocuments(anyList(), anyString(), anyString(), anyInt(), any());

        // Act
        DocumentDTO result = documentService.uploadDocument(multipartFile, 1);

        // Assert
        assertNotNull(result);
        verify(vectorStoreService, times(1)).addDocuments(anyList(), anyString(), anyString(), anyInt(), any());
    }

    @Test
    void testChunkText_LongText() throws IOException {
        // Arrange - Create a long text that should be chunked
        StringBuilder longText = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            longText.append("This is sentence ").append(i).append(". ");
        }
        byte[] contentBytes = longText.toString().getBytes();
        
        when(multipartFile.getOriginalFilename()).thenReturn("long.txt");
        when(multipartFile.getContentType()).thenReturn("text/plain");
        when(multipartFile.getBytes()).thenReturn(contentBytes);
        when(multipartFile.getSize()).thenReturn((long) contentBytes.length);
        // Lenient vì getInputStream() có thể không được dùng trong một số trường hợp
        lenient().when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(contentBytes));
        
        doNothing().when(vectorStoreService).addDocuments(anyList(), anyString(), anyString(), anyInt(), any());

        // Act
        DocumentDTO result = documentService.uploadDocument(multipartFile, 1);

        // Assert
        assertNotNull(result);
        // Should have multiple chunks
        verify(vectorStoreService, times(1)).addDocuments(anyList(), anyString(), anyString(), anyInt(), any());
    }

    private List<Document> createMockDocuments() {
        List<Document> documents = new ArrayList<>();
        
        Document doc1 = new Document();
        doc1.setDocumentId(1);
        doc1.setFileName("test1.pdf");
        doc1.setContent("Content 1");
        doc1.setChunkIndex(0);
        doc1.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        doc1.setUser(mockUser);
        documents.add(doc1);
        
        Document doc2 = new Document();
        doc2.setDocumentId(2);
        doc2.setFileName("test2.pdf");
        doc2.setContent("Content 2");
        doc2.setChunkIndex(0);
        doc2.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        doc2.setUser(mockUser);
        documents.add(doc2);
        
        return documents;
    }
}

