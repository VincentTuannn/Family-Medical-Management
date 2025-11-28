package Backend.FMM.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequestDTO {
    private String message;
    private String conversationId; // Optional: for conversation history
    private Boolean useRAG = true; // Use RAG (documents) or direct chat
    private Boolean useDatabase = false; // Use database context
}

