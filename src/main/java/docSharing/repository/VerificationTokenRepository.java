package docSharing.repository;

import docSharing.entities.User;
import docSharing.entities.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    VerificationToken findByToken(String token);

    VerificationToken findByUser(User user);

    @Transactional
    @Modifying
    @Query("delete from VerificationToken v where v.token = ?1")
    void deleteByToken(String token);
}
