package Backend.FMM.Repository;

import Backend.FMM.Entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Integer> {
    @Query("SELECT d FROM Document d WHERE d.user.userId = :userId")
    List<Document> findByUserId(@Param("userId") Integer userId);
    
    List<Document> findByFileName(String fileName);
    
    @Query("SELECT d FROM Document d WHERE d.user.userId = :userId ORDER BY d.createdAt DESC")
    List<Document> findRecentDocumentsByUserId(@Param("userId") Integer userId);
}

