package Backend.FMM.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponseDTO {
    private String response;
    private String conversationId;
    private List<String> sources; // Document sources used for RAG
    private Boolean usedRAG;
}

