package docSharing.repository;

import docSharing.entities.User;
import docSharing.entities.VerificationToken;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {


//    @Query("select from VerificationToken v where v.expiry_date < ?1")
//    List<VerificationToken> findAll(Date date);

    List<VerificationToken> findAll();

    VerificationToken findByToken(String token);

    VerificationToken findByUser(User user);

    @Transactional
    @Modifying
    @Query("delete from VerificationToken v where v.token = ?1")
    void deleteByToken(String token);
}
