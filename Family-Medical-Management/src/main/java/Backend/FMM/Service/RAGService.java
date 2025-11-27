package Backend.FMM.Service;

import Backend.FMM.DTO.ChatResponseDTO;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RAGService {
    
    @Autowired
    @Qualifier("ollamaChatModel")
    private ChatModel chatModel;
    
    @Autowired
    private VectorStoreService vectorStoreService;
    
    /**
     * Process chat with RAG (Retrieval Augmented Generation)
     */
    public ChatResponseDTO chatWithRAG(String message, Integer userId) {
        // Step 1: Retrieve relevant documents
        List<Document> relevantDocs = vectorStoreService.similaritySearch(message, 5, userId);
        
        // Step 2: Build context from retrieved documents
        String context = buildContext(relevantDocs);
        
        // Step 3: Build prompt with context
        String prompt = buildPrompt(message, context);
        
        // Step 4: Get AI response
        ChatResponse response = chatModel.call(
            new Prompt(new UserMessage(prompt))
        );
        
        String aiResponse = response.getResult().getOutput().getContent();
        
        // Step 5: Extract sources
        List<String> sources = relevantDocs.stream()
            .map(doc -> doc.getMetadata().getOrDefault("fileName", "Unknown").toString())
            .distinct()
            .collect(Collectors.toList());
        
        ChatResponseDTO chatResponse = new ChatResponseDTO();
        chatResponse.setResponse(aiResponse);
        chatResponse.setUsedRAG(true);
        chatResponse.setSources(sources);
        
        return chatResponse;
    }
    
    /**
     * Direct chat without RAG
     */
    public ChatResponseDTO directChat(String message) {
        ChatResponse response = chatModel.call(
            new Prompt(new UserMessage(message))
        );
        
        String aiResponse = response.getResult().getOutput().getContent();
        
        ChatResponseDTO chatResponse = new ChatResponseDTO();
        chatResponse.setResponse(aiResponse);
        chatResponse.setUsedRAG(false);
        chatResponse.setSources(new ArrayList<>());
        
        return chatResponse;
    }
    
    /**
     * Build context from retrieved documents
     */
    private String buildContext(List<Document> documents) {
        if (documents.isEmpty()) {
            return "Không tìm thấy thông tin liên quan trong tài liệu.";
        }
        
        StringBuilder context = new StringBuilder();
        context.append("Thông tin từ tài liệu:\n\n");
        
        for (int i = 0; i < documents.size(); i++) {
            Document doc = documents.get(i);
            context.append("Tài liệu ").append(i + 1).append(":\n");
            context.append(doc.getContent()).append("\n\n");
        }
        
        return context.toString();
    }
    
    /**
     * Build prompt with context and user message
     */
    private String buildPrompt(String userMessage, String context) {
        return String.format(
            "Bạn là một trợ lý AI chuyên về quản lý hồ sơ y tế gia đình. " +
            "Hãy trả lời câu hỏi của người dùng dựa trên thông tin từ tài liệu được cung cấp. " +
            "Nếu thông tin không có trong tài liệu, hãy trả lời dựa trên kiến thức chung của bạn. " +
            "Trả lời bằng tiếng Việt.\n\n" +
            "Thông tin từ tài liệu:\n%s\n\n" +
            "Câu hỏi của người dùng: %s\n\n" +
            "Hãy trả lời:",
            context,
            userMessage
        );
    }
}

