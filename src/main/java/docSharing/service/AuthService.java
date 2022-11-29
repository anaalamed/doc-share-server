package docSharing.service;

import docSharing.controller.request.UserRequest;
import docSharing.entities.DTO.UserDTO;
import docSharing.entities.User;
import docSharing.entities.VerificationToken;
import docSharing.repository.UserRepository;
import docSharing.repository.VerificationTokenRepository;
import docSharing.events.emailActivation.OnRegistrationCompleteEvent;
import docSharing.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.SQLDataException;
import java.sql.Timestamp;
import java.util.*;

import static docSharing.utils.Utils.hashPassword;
import static docSharing.utils.Utils.verifyPassword;

@Service
public class AuthService {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final VerificationTokenRepository tokenRepository;
    @Autowired
    ApplicationEventPublisher eventPublisher;

    private static final int SCHEDULE = 1000 * 60 * 60;

    static HashMap<Integer, String> usersTokensMap = new HashMap<>();

    private static final Logger logger = LogManager.getLogger(AuthService.class.getName());

    public AuthService(UserRepository userRepository, VerificationTokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    public UserDTO createUser(UserRequest userRequest) throws SQLDataException {
        logger.info("in createUser()");

        if(userRepository.findByEmail(userRequest.getEmail()).isPresent()){
            throw new SQLDataException(String.format("Email %s already exists!", userRequest.getEmail()));
        }

        logger.debug(userRequest);
        User user = userRepository.save(new User(userRequest.getName(), userRequest.getEmail(),
                hashPassword(userRequest.getPassword())));

        return new UserDTO(user);
    }

    public Optional<String> login(UserRequest userRequest) {
        logger.info("in login()");

        Optional<User> user = userRepository.findByEmail(userRequest.getEmail());
        Optional<String> token = Optional.empty();

        if (user.isPresent() && verifyPassword(userRequest.getPassword(), user.get().getPassword())) {
            token = Optional.of(Utils.generateUniqueToken()) ;
            usersTokensMap.put(user.get().getId(), token.get());
        }

        return token;
    }

    public boolean isEnabledUser(UserRequest userRequest) {
        logger.info("in isEnabledUser()");

        Optional<User> user = userRepository.findByEmail(userRequest.getEmail());

        return user.isPresent() && user.get().isEnabled();
    }

    public boolean isAuthenticated(int userId, String token) {
        return usersTokensMap.containsKey(userId) && usersTokensMap.get(userId).equals(token);
    }


    // ------------------ verification token ------------------ //

    public void publishRegistrationEvent(UserDTO createdUser, Locale locale, String appUrl) {
        User user = userRepository.getReferenceById(createdUser.getId());
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, locale, appUrl));
    }

    public void createVerificationToken(User user, String token) {
        VerificationToken myToken = new VerificationToken(token, user);
        tokenRepository.save(myToken);
    }

    public VerificationToken getVerificationToken(String VerificationToken) {
        return tokenRepository.findByToken(VerificationToken);
    }

    public void deleteVerificationToken(String token) {
        tokenRepository.deleteByToken(token);
    }

    @Scheduled(fixedRate = SCHEDULE)
    public void scheduleDeleteNotActivatedUsers() {
        logger.info("---------- in scheduleDeleteNotActivatedUsers-------------");
        Calendar cal = Calendar.getInstance();
        List<VerificationToken> expiredTokens = tokenRepository.findAllExpired(new Timestamp(cal.getTime().getTime()));
        logger.debug(expiredTokens);

        for (VerificationToken token: expiredTokens) {
            deleteVerificationToken(token.getToken());
            userRepository.deleteById(token.getUser().getId());
            logger.debug("Verification token for user_id#" + token.getUser().getId() + " and non activated user was deleted");
        }
    }
}
