package Backend.FMM.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDTO {
    private Integer documentId;
    private String fileName;
    private String fileType;
    private String content;
    private Integer chunkIndex;
    private String metadata;
    private Date createdAt;
    private Integer userId;
}


