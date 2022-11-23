package docSharing.repository;

import docSharing.entities.permission.Authorization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PermissionRepository extends JpaRepository<Authorization, Integer> {

    @Query("SELECT a FROM Authorization a WHERE a.documentId=?1 AND a.userId=?2")
    List<Authorization> findByDocIdAndUserId(int documentId, int userId);

    @Query(value = "DELETE a FROM Authorization a WHERE a.documentId=?1", nativeQuery = true)
    void deleteByDocumentId(int documentId);

    @Query(value = "DELETE a FROM Authorization a WHERE a.userId=?1", nativeQuery = true)
    void deleteByUserId(int userId);
}