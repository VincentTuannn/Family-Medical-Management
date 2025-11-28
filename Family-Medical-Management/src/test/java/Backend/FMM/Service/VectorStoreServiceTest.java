package Backend.FMM.Service;

import Backend.FMM.Entity.Document;
import Backend.FMM.Entity.User;
import Backend.FMM.Repository.DocumentRepository;
import Backend.FMM.Repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.embedding.EmbeddingModel;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VectorStoreServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmbeddingModel embeddingModel;

    @InjectMocks
    private VectorStoreService vectorStoreService;

    private User mockUser;
    private float[] mockEmbedding;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setUserId(1);
        mockUser.setUsername("testuser");
        mockUser.setEmail("test@example.com");

        // Mock embedding array
        mockEmbedding = new float[]{0.1f, 0.2f, 0.3f, 0.4f, 0.5f};
    }

    @Test
    void testAddDocument_Success() {
        // Arrange
        String content = "Test document content";
        String fileName = "test.pdf";
        String fileType = "application/pdf";
        Integer userId = 1;
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("key", "value");

        when(embeddingModel.embed(content)).thenReturn(mockEmbedding);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(documentRepository.save(any(Document.class))).thenAnswer(invocation -> {
            Document doc = invocation.getArgument(0);
            doc.setDocumentId(1);
            return doc;
        });

        // Act
        vectorStoreService.addDocument(content, fileName, fileType, userId, metadata);

        // Assert
        verify(embeddingModel, times(1)).embed(content);
        verify(userRepository, times(1)).findById(userId);
        verify(documentRepository, times(1)).save(any(Document.class));
    }

    @Test
    void testAddDocument_UserNotFound() {
        // Arrange
        String content = "Test document content";
        String fileName = "test.pdf";
        String fileType = "application/pdf";
        Integer userId = 999;
        Map<String, Object> metadata = new HashMap<>();

        when(embeddingModel.embed(content)).thenReturn(mockEmbedding);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        when(documentRepository.save(any(Document.class))).thenAnswer(invocation -> {
            Document doc = invocation.getArgument(0);
            doc.setDocumentId(1);
            return doc;
        });

        // Act
        vectorStoreService.addDocument(content, fileName, fileType, userId, metadata);

        // Assert
        verify(embeddingModel, times(1)).embed(content);
        verify(userRepository, times(1)).findById(userId);
        verify(documentRepository, times(1)).save(any(Document.class));
    }

    @Test
    void testAddDocument_NullUserId() {
        // Arrange
        String content = "Test document content";
        String fileName = "test.pdf";
        String fileType = "application/pdf";
        Integer userId = null;
        Map<String, Object> metadata = new HashMap<>();

        when(embeddingModel.embed(content)).thenReturn(mockEmbedding);
        when(documentRepository.save(any(Document.class))).thenAnswer(invocation -> {
            Document doc = invocation.getArgument(0);
            doc.setDocumentId(1);
            return doc;
        });

        // Act
        vectorStoreService.addDocument(content, fileName, fileType, userId, metadata);

        // Assert
        verify(embeddingModel, times(1)).embed(content);
        verify(userRepository, never()).findById(any());
        verify(documentRepository, times(1)).save(any(Document.class));
    }

    @Test
    void testAddDocuments_Success() {
        // Arrange
        List<String> chunks = new ArrayList<>();
        chunks.add("Chunk 1 content");
        chunks.add("Chunk 2 content");
        chunks.add("Chunk 3 content");
        String fileName = "test.pdf";
        String fileType = "application/pdf";
        Integer userId = 1;
        Map<String, Object> metadata = new HashMap<>();

        when(embeddingModel.embed(anyString())).thenReturn(mockEmbedding);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(documentRepository.saveAll(anyList())).thenAnswer(invocation -> {
            List<Document> docs = invocation.getArgument(0);
            for (int i = 0; i < docs.size(); i++) {
                docs.get(i).setDocumentId(i + 1);
            }
            return docs;
        });

        // Act
        vectorStoreService.addDocuments(chunks, fileName, fileType, userId, metadata);

        // Assert
        verify(embeddingModel, times(chunks.size())).embed(anyString());
        verify(userRepository, times(1)).findById(userId);
        verify(documentRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testAddDocuments_UserNotFound() {
        // Arrange
        List<String> chunks = new ArrayList<>();
        chunks.add("Chunk 1 content");
        Integer userId = 999;
        Map<String, Object> metadata = new HashMap<>();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        vectorStoreService.addDocuments(chunks, "test.pdf", "application/pdf", userId, metadata);

        // Assert
        verify(userRepository, times(1)).findById(userId);
        verify(embeddingModel, never()).embed(anyString());
        verify(documentRepository, never()).saveAll(anyList());
    }

    @Test
    void testAddDocuments_EmptyChunks() {
        // Arrange
        List<String> chunks = new ArrayList<>();
        Integer userId = 1;
        Map<String, Object> metadata = new HashMap<>();

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // Act
        vectorStoreService.addDocuments(chunks, "test.pdf", "application/pdf", userId, metadata);

        // Assert
        verify(userRepository, times(1)).findById(userId);
        verify(embeddingModel, never()).embed(anyString());
        // Nếu chunks rỗng, saveAll không được gọi vì documentsToSave sẽ rỗng
        verify(documentRepository, never()).saveAll(anyList());
    }

    @Test
    void testSimilaritySearch_WithUserId() {
        // Arrange
        String query = "test query";
        int topK = 3;
        Integer userId = 1;

        List<Document> mockDocuments = createMockDocuments();
        when(embeddingModel.embed(query)).thenReturn(mockEmbedding);
        when(documentRepository.findByUserId(userId)).thenReturn(mockDocuments);

        // Act
        List<org.springframework.ai.document.Document> result = vectorStoreService.similaritySearch(query, topK, userId);

        // Assert
        assertNotNull(result);
        assertTrue(result.size() <= topK);
        verify(embeddingModel, times(1)).embed(query);
        verify(documentRepository, times(1)).findByUserId(userId);
    }

    @Test
    void testSimilaritySearch_WithoutUserId() {
        // Arrange
        String query = "test query";
        int topK = 3;
        Integer userId = null;

        List<Document> mockDocuments = createMockDocuments();
        when(embeddingModel.embed(query)).thenReturn(mockEmbedding);
        when(documentRepository.findAll()).thenReturn(mockDocuments);

        // Act
        List<org.springframework.ai.document.Document> result = vectorStoreService.similaritySearch(query, topK, userId);

        // Assert
        assertNotNull(result);
        assertTrue(result.size() <= topK);
        verify(embeddingModel, times(1)).embed(query);
        verify(documentRepository, times(1)).findAll();
        verify(documentRepository, never()).findByUserId(any());
    }

    @Test
    void testSimilaritySearch_NoDocuments() {
        // Arrange
        String query = "test query";
        int topK = 3;
        Integer userId = 1;

        when(embeddingModel.embed(query)).thenReturn(mockEmbedding);
        when(documentRepository.findByUserId(userId)).thenReturn(new ArrayList<>());

        // Act
        List<org.springframework.ai.document.Document> result = vectorStoreService.similaritySearch(query, topK, userId);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(embeddingModel, times(1)).embed(query);
        verify(documentRepository, times(1)).findByUserId(userId);
    }

    @Test
    void testAddDocuments_MaxChunksLimit() {
        // Arrange
        List<String> chunks = new ArrayList<>();
        // Create more than 1000 chunks
        for (int i = 0; i < 1500; i++) {
            chunks.add("Chunk " + i);
        }
        Integer userId = 1;
        Map<String, Object> metadata = new HashMap<>();

        when(embeddingModel.embed(anyString())).thenReturn(mockEmbedding);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(documentRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        vectorStoreService.addDocuments(chunks, "test.pdf", "application/pdf", userId, metadata);

        // Assert
        // Should only process 1000 chunks (max limit)
        verify(embeddingModel, times(1000)).embed(anyString());
        verify(documentRepository, times(1)).saveAll(anyList());
    }

    private List<Document> createMockDocuments() {
        List<Document> documents = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Document doc = new Document();
            doc.setDocumentId(i + 1);
            doc.setFileName("test" + i + ".pdf");
            doc.setContent("Content " + i);
            doc.setChunkIndex(i);
            doc.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            
            // Create embedding JSON
            try {
                ObjectMapper mapper = new ObjectMapper();
                List<Double> embedding = new ArrayList<>();
                for (float f : mockEmbedding) {
                    embedding.add((double) f);
                }
                doc.setEmbedding(mapper.writeValueAsString(embedding));
                doc.setMetadata("{\"fileName\":\"test" + i + ".pdf\"}");
            } catch (Exception e) {
                doc.setEmbedding("[]");
                doc.setMetadata("{}");
            }
            
            documents.add(doc);
        }
        return documents;
    }
}

