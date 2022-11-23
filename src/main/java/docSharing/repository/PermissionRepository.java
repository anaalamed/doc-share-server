package docSharing.repository;

import docSharing.entities.permission.Authorization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PermissionRepository extends JpaRepository<Authorization, Integer> {

    @Query("SELECT a FROM Authorization a WHERE a.document.id=?1 AND a.user.id=?2")
    List<Authorization> findByDocumentAndUser(int documentId, int userId);
}