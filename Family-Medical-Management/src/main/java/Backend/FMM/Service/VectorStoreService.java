package Backend.FMM.Service;

import Backend.FMM.Entity.User;
import Backend.FMM.Repository.DocumentRepository;
import Backend.FMM.Repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VectorStoreService {
    
    @Autowired
    private DocumentRepository documentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    @Qualifier("ollamaEmbeddingModel")
    private EmbeddingModel embeddingModel;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Add document to vector store
     */
    public void addDocument(String content, String fileName, String fileType, Integer userId, Map<String, Object> metadata) {
        // Generate embedding
        float[] embeddingArray = embeddingModel.embed(content);
        
        // Convert float[] to List<Double>
        List<Double> embedding = new ArrayList<>();
        for (float f : embeddingArray) {
            embedding.add((double) f);
        }
        
        // Convert embedding to JSON string
        String embeddingJson;
        try {
            embeddingJson = objectMapper.writeValueAsString(embedding);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize embedding", e);
        }
        
        // Save to database
        Backend.FMM.Entity.Document document = new Backend.FMM.Entity.Document();
        document.setFileName(fileName);
        document.setFileType(fileType);
        document.setContent(content);
        document.setEmbedding(embeddingJson);
        document.setChunkIndex(0);
        
        try {
            String metadataJson = objectMapper.writeValueAsString(metadata);
            document.setMetadata(metadataJson);
        } catch (Exception e) {
            document.setMetadata("{}");
        }
        
        if (userId != null) {
            // Load User from database (not create new)
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                document.setUser(user);
            } else {
                System.err.println("⚠️ User không tìm thấy với userId: " + userId);
            }
        }
        
        documentRepository.save(document);
    }
    
    /**
     * Add chunked documents
     */
    @Transactional
    public void addDocuments(List<String> chunks, String fileName, String fileType, Integer userId, Map<String, Object> metadata) {
        // Load User entity once from database (not create new)
        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                System.err.println("⚠️ User không tìm thấy với userId: " + userId);
                return;
            }
        }
        
        // Limit chunks to prevent too many inserts (max 1000 chunks per file)
        int maxChunks = Math.min(chunks.size(), 1000);
        System.out.println("📄 Processing " + maxChunks + " chunks for file: " + fileName);
        
        // Batch save documents
        List<Backend.FMM.Entity.Document> documentsToSave = new ArrayList<>();
        
        for (int i = 0; i < maxChunks; i++) {
            String chunk = chunks.get(i);
            
            try {
                // Generate embedding
                float[] embeddingArray = embeddingModel.embed(chunk);
                
                // Convert float[] to List<Double>
                List<Double> embedding = new ArrayList<>();
                for (float f : embeddingArray) {
                    embedding.add((double) f);
                }
                
                String embeddingJson;
                try {
                    embeddingJson = objectMapper.writeValueAsString(embedding);
                } catch (Exception e) {
                    System.err.println("⚠️ Failed to serialize embedding for chunk " + i + ": " + e.getMessage());
                    continue; // Skip this chunk if embedding fails
                }
                
                Backend.FMM.Entity.Document document = new Backend.FMM.Entity.Document();
                document.setFileName(fileName);
                document.setFileType(fileType);
                document.setContent(chunk);
                document.setEmbedding(embeddingJson);
                document.setChunkIndex(i);
                
                try {
                    Map<String, Object> chunkMetadata = new java.util.HashMap<>(metadata);
                    chunkMetadata.put("chunkIndex", i);
                    chunkMetadata.put("totalChunks", chunks.size());
                    String metadataJson = objectMapper.writeValueAsString(chunkMetadata);
                    document.setMetadata(metadataJson);
                } catch (Exception e) {
                    document.setMetadata("{}");
                }
                
                // Use loaded User entity (not create new)
                if (user != null) {
                    document.setUser(user);
                }
                
                documentsToSave.add(document);
                
                // Log progress every 100 chunks
                if ((i + 1) % 100 == 0) {
                    System.out.println("  ✓ Processed " + (i + 1) + "/" + maxChunks + " chunks");
                }
            } catch (Exception e) {
                System.err.println("⚠️ Error processing chunk " + i + ": " + e.getMessage());
                // Continue with next chunk
            }
        }
        
        // Batch save all documents at once
        if (!documentsToSave.isEmpty()) {
            System.out.println("💾 Saving " + documentsToSave.size() + " documents to database...");
            documentRepository.saveAll(documentsToSave);
            System.out.println("✅ Successfully saved " + documentsToSave.size() + " document chunks");
        }
    }
    
    /**
     * Similarity search - find most similar documents
     */
    public List<Document> similaritySearch(String query, int topK, Integer userId) {
        // Generate query embedding
        float[] embeddingArray = embeddingModel.embed(query);
        
        // Convert float[] to List<Double>
        List<Double> queryEmbedding = new ArrayList<>();
        for (float f : embeddingArray) {
            queryEmbedding.add((double) f);
        }
        
        // Get all documents (or user's documents if userId provided)
        List<Backend.FMM.Entity.Document> allDocuments;
        if (userId != null) {
            allDocuments = documentRepository.findByUserId(userId);
            System.out.println("🔍 Similarity search - userId: " + userId + ", found " + allDocuments.size() + " documents");
        } else {
            allDocuments = documentRepository.findAll();
            System.out.println("⚠️ Similarity search - userId is NULL, searching in ALL " + allDocuments.size() + " documents");
        }
        
        // Calculate cosine similarity and sort
        List<DocumentWithSimilarity> scoredDocs = allDocuments.stream()
            .map((Backend.FMM.Entity.Document doc) -> {
                try {
                    List<Double> docEmbedding = objectMapper.readValue(
                        doc.getEmbedding(), 
                        new TypeReference<List<Double>>() {}
                    );
                    double similarity = cosineSimilarity(queryEmbedding, docEmbedding);
                    return new DocumentWithSimilarity(doc, similarity);
                } catch (Exception e) {
                    return new DocumentWithSimilarity(doc, 0.0);
                }
            })
            .sorted((a, b) -> Double.compare(b.similarity, a.similarity))
            .limit(topK)
            .collect(Collectors.toList());
        
        // Convert to Document
        return scoredDocs.stream()
            .map(item -> {
                Document aiDoc = new Document(item.document.getContent());
                try {
                    if (item.document.getMetadata() != null) {
                        Map<String, Object> metadata = objectMapper.readValue(
                            item.document.getMetadata(),
                            new TypeReference<Map<String, Object>>() {}
                        );
                        metadata.put("fileName", item.document.getFileName());
                        metadata.put("similarity", item.similarity);
                        aiDoc.getMetadata().putAll(metadata);
                    }
                } catch (Exception e) {
                    aiDoc.getMetadata().put("fileName", item.document.getFileName());
                    aiDoc.getMetadata().put("similarity", item.similarity);
                }
                return aiDoc;
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Calculate cosine similarity between two vectors
     */
    private double cosineSimilarity(List<Double> vec1, List<Double> vec2) {
        if (vec1.size() != vec2.size()) {
            return 0.0;
        }
        
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        
        for (int i = 0; i < vec1.size(); i++) {
            dotProduct += vec1.get(i) * vec2.get(i);
            norm1 += vec1.get(i) * vec1.get(i);
            norm2 += vec2.get(i) * vec2.get(i);
        }
        
        if (norm1 == 0.0 || norm2 == 0.0) {
            return 0.0;
        }
        
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
    
    private static class DocumentWithSimilarity {
        Backend.FMM.Entity.Document document;
        double similarity;
        
        DocumentWithSimilarity(Backend.FMM.Entity.Document document, double similarity) {
            this.document = document;
            this.similarity = similarity;
        }
    }
}

