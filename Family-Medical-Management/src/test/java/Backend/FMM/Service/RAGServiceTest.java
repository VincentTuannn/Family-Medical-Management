package Backend.FMM.Service;

import Backend.FMM.DTO.ChatResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RAGServiceTest {

    @Mock
    private ChatModel chatModel;

    @Mock
    private VectorStoreService vectorStoreService;

    @Mock
    private DatabaseContextService databaseContextService;

    @InjectMocks
    private RAGService ragService;

    private ChatResponse mockChatResponse;
    private List<Document> mockDocuments;

    @BeforeEach
    void setUp() {
        // Setup mock ChatResponse
        AssistantMessage assistantMessage = new AssistantMessage("Test response");
        Generation generation = new Generation(assistantMessage);
        mockChatResponse = new ChatResponse(List.of(generation));

        // Setup mock documents
        mockDocuments = new ArrayList<>();
        Document doc1 = new Document("Test content 1");
        Map<String, Object> metadata1 = new HashMap<>();
        metadata1.put("fileName", "test1.pdf");
        doc1.getMetadata().putAll(metadata1);
        mockDocuments.add(doc1);

        Document doc2 = new Document("Test content 2");
        Map<String, Object> metadata2 = new HashMap<>();
        metadata2.put("fileName", "test2.pdf");
        doc2.getMetadata().putAll(metadata2);
        mockDocuments.add(doc2);
    }

    @Test
    void testChatWithRAG_Success() {
        // Arrange
        String message = "What is diabetes?";
        Integer userId = 1;

        when(vectorStoreService.similaritySearch(message, 5, userId))
                .thenReturn(mockDocuments);
        when(chatModel.call(any(Prompt.class))).thenReturn(mockChatResponse);

        // Act
        ChatResponseDTO result = ragService.chatWithRAG(message, userId);

        // Assert
        assertNotNull(result);
        assertEquals("Test response", result.getResponse());
        assertTrue(result.getUsedRAG());
        assertNotNull(result.getSources());
        assertEquals(2, result.getSources().size());
        assertTrue(result.getSources().contains("test1.pdf"));
        assertTrue(result.getSources().contains("test2.pdf"));

        verify(vectorStoreService, times(1)).similaritySearch(message, 5, userId);
        verify(chatModel, times(1)).call(any(Prompt.class));
    }

    @Test
    void testChatWithRAG_NoDocuments() {
        // Arrange
        String message = "What is diabetes?";
        Integer userId = 1;

        when(vectorStoreService.similaritySearch(message, 5, userId))
                .thenReturn(new ArrayList<>());
        when(chatModel.call(any(Prompt.class))).thenReturn(mockChatResponse);

        // Act
        ChatResponseDTO result = ragService.chatWithRAG(message, userId);

        // Assert
        assertNotNull(result);
        assertEquals("Test response", result.getResponse());
        assertTrue(result.getUsedRAG());
        assertNotNull(result.getSources());
        assertEquals(0, result.getSources().size());

        verify(vectorStoreService, times(1)).similaritySearch(message, 5, userId);
        verify(chatModel, times(1)).call(any(Prompt.class));
    }

    @Test
    void testDirectChat_Success() {
        // Arrange
        String message = "Hello";

        when(chatModel.call(any(Prompt.class))).thenReturn(mockChatResponse);

        // Act
        ChatResponseDTO result = ragService.directChat(message);

        // Assert
        assertNotNull(result);
        assertEquals("Test response", result.getResponse());
        assertFalse(result.getUsedRAG());
        assertNotNull(result.getSources());
        assertEquals(0, result.getSources().size());

        verify(chatModel, times(1)).call(any(Prompt.class));
        verify(vectorStoreService, never()).similaritySearch(any(), anyInt(), any());
    }

    @Test
    void testDirectChat_EmptyMessage() {
        // Arrange
        String message = "";

        when(chatModel.call(any(Prompt.class))).thenReturn(mockChatResponse);

        // Act
        ChatResponseDTO result = ragService.directChat(message);

        // Assert
        assertNotNull(result);
        assertEquals("Test response", result.getResponse());
        assertFalse(result.getUsedRAG());

        verify(chatModel, times(1)).call(any(Prompt.class));
    }

    @Test
    void testChatWithRAG_NullUserId() {
        // Arrange
        String message = "What is diabetes?";
        Integer userId = null;

        when(vectorStoreService.similaritySearch(message, 5, userId))
                .thenReturn(mockDocuments);
        when(chatModel.call(any(Prompt.class))).thenReturn(mockChatResponse);

        // Act
        ChatResponseDTO result = ragService.chatWithRAG(message, userId);

        // Assert
        assertNotNull(result);
        assertTrue(result.getUsedRAG());
        verify(vectorStoreService, times(1)).similaritySearch(message, 5, userId);
    }

    @Test
    void testChatWithRAGAndDatabase_WithDatabase_ShouldIncludeDatabaseContext() {
        // Arrange
        String message = "thông tin bệnh nhân";
        Integer userId = 1;
        String databaseContext = "Thông tin từ cơ sở dữ liệu:\n\n=== THÔNG TIN BỆNH NHÂN ===\n";

        when(vectorStoreService.similaritySearch(message, 5, userId))
                .thenReturn(mockDocuments);
        when(databaseContextService.smartQueryDatabase(message, userId))
                .thenReturn(databaseContext);
        when(chatModel.call(any(Prompt.class))).thenReturn(mockChatResponse);

        // Act
        ChatResponseDTO result = ragService.chatWithRAGAndDatabase(message, userId, true);

        // Assert
        assertNotNull(result);
        assertTrue(result.getUsedRAG());
        assertTrue(result.getSources().contains("Cơ sở dữ liệu"));
        verify(databaseContextService, times(1)).smartQueryDatabase(message, userId);
        verify(vectorStoreService, times(1)).similaritySearch(message, 5, userId);
    }

    @Test
    void testChatWithRAGAndDatabase_WithoutDatabase_ShouldNotIncludeDatabaseContext() {
        // Arrange
        String message = "What is diabetes?";
        Integer userId = 1;

        when(vectorStoreService.similaritySearch(message, 5, userId))
                .thenReturn(mockDocuments);
        when(chatModel.call(any(Prompt.class))).thenReturn(mockChatResponse);

        // Act
        ChatResponseDTO result = ragService.chatWithRAGAndDatabase(message, userId, false);

        // Assert
        assertNotNull(result);
        assertTrue(result.getUsedRAG());
        assertFalse(result.getSources().contains("Cơ sở dữ liệu"));
        verify(databaseContextService, never()).smartQueryDatabase(anyString(), anyInt());
        verify(vectorStoreService, times(1)).similaritySearch(message, 5, userId);
    }

    @Test
    void testChatWithRAGAndDatabase_WithEmptyDocuments_ShouldHandleEmptyContext() {
        // Arrange
        String message = "test";
        Integer userId = 1;

        when(vectorStoreService.similaritySearch(message, 5, userId))
                .thenReturn(new ArrayList<>());
        when(databaseContextService.smartQueryDatabase(message, userId))
                .thenReturn("");
        when(chatModel.call(any(Prompt.class))).thenReturn(mockChatResponse);

        // Act
        ChatResponseDTO result = ragService.chatWithRAGAndDatabase(message, userId, true);

        // Assert
        assertNotNull(result);
        assertTrue(result.getUsedRAG());
        verify(chatModel, times(1)).call(any(Prompt.class));
    }

    @Test
    void testChatWithRAG_DocumentWithNoFileName_ShouldUseUnknown() {
        // Arrange
        String message = "test";
        Integer userId = 1;
        List<Document> docsWithoutFileName = new ArrayList<>();
        Document doc = new Document("content");
        docsWithoutFileName.add(doc);

        when(vectorStoreService.similaritySearch(message, 5, userId))
                .thenReturn(docsWithoutFileName);
        when(chatModel.call(any(Prompt.class))).thenReturn(mockChatResponse);

        // Act
        ChatResponseDTO result = ragService.chatWithRAG(message, userId);

        // Assert
        assertNotNull(result);
        assertTrue(result.getUsedRAG());
        assertTrue(result.getSources().contains("Unknown"));
    }

    @Test
    void testChatWithRAG_DuplicateFileNames_ShouldReturnDistinct() {
        // Arrange
        String message = "test";
        Integer userId = 1;
        List<Document> docsWithSameFileName = new ArrayList<>();
        Document doc1 = new Document("content1");
        Map<String, Object> metadata1 = new HashMap<>();
        metadata1.put("fileName", "test.pdf");
        doc1.getMetadata().putAll(metadata1);
        docsWithSameFileName.add(doc1);

        Document doc2 = new Document("content2");
        Map<String, Object> metadata2 = new HashMap<>();
        metadata2.put("fileName", "test.pdf");
        doc2.getMetadata().putAll(metadata2);
        docsWithSameFileName.add(doc2);

        when(vectorStoreService.similaritySearch(message, 5, userId))
                .thenReturn(docsWithSameFileName);
        when(chatModel.call(any(Prompt.class))).thenReturn(mockChatResponse);

        // Act
        ChatResponseDTO result = ragService.chatWithRAG(message, userId);

        // Assert
        assertNotNull(result);
        assertTrue(result.getUsedRAG());
        // Should only have one "test.pdf" in sources (distinct)
        long count = result.getSources().stream().filter(s -> s.equals("test.pdf")).count();
        assertEquals(1, count);
    }
}

