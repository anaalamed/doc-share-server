package docSharing.service;

import docSharing.controller.request.UserRequest;
import docSharing.entities.User;
import docSharing.entities.VerificationToken;
import docSharing.repository.UserRepository;
import docSharing.repository.VerificationTokenRepository;
import docSharing.utils.emailActivation.OnRegistrationCompleteEvent;
import docSharing.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.sql.SQLDataException;
import java.util.*;

@Service
public class AuthService {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final VerificationTokenRepository tokenRepository;
    @Autowired
    ApplicationEventPublisher eventPublisher;
    static HashMap<User, String> mapUserTokens = new HashMap<>();

    private static final Logger logger = LogManager.getLogger(AuthService.class.getName());

    public AuthService(UserRepository userRepository, VerificationTokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }



//    public User createUser(UserRequest userRequest) throws SQLDataException {
//        logger.info("in createUser");
//
//        if(userRepository.findByEmail(userRequest.getEmail())!=null){
//            throw new SQLDataException(String.format("Email %s exists in users table", userRequest.getEmail()));
//        }
//
//        logger.debug(userRequest);
//        return userRepository.save(new User(userRequest.getName(), userRequest.getEmail(), userRequest.getPassword()));
//    }

    public User createUser(UserRequest userRequest) throws SQLDataException {
        logger.info("in createUser");

        if(userRepository.findByEmail(userRequest.getEmail())!=null){
            throw new SQLDataException(String.format("Email %s exists in users table", userRequest.getEmail()));
        }

        logger.debug(userRequest);
        User userCreated = new User(userRequest.getName(), userRequest.getEmail(), userRequest.getPassword());
        return userRepository.save(userCreated);

//        String token = UUID.randomUUID().toString();
//        createVerificationToken(userCreated, token);
//        return userCreated;
    }

    public Optional<String> login(UserRequest userRequest) {
        logger.info("in login");

        User userByEmail = userRepository.findByEmail(userRequest.getEmail());

        if (userByEmail == null) {
            return Optional.empty();
        }

        if (userByEmail.getPassword().equals(userRequest.getPassword())) {
            Optional<String> token = Optional.of(Utils.generateUniqueToken()) ;
            mapUserTokens.put(userByEmail, token.get());
            return token;
        }

        return Optional.empty();
    }

    public boolean isEnabledUser(UserRequest userRequest) {
        logger.info("in isEnabledUser");

        User userByEmail = userRepository.findByEmail(userRequest.getEmail());

        if (userByEmail == null) {
            return false;
        }

        return userByEmail.isEnabled();
    }



    // ------------------ verification token ------------------
    public User getUser(String verificationToken) {
        User user = tokenRepository.findByToken(verificationToken).getUser();
        return user;
    }

    public VerificationToken getVerificationToken(String VerificationToken) {
        return tokenRepository.findByToken(VerificationToken);
    }

    public void createVerificationToken(User user, String token) {
        VerificationToken myToken = new VerificationToken(token, user);
        logger.debug(myToken);
        tokenRepository.save(myToken);
    }

    public void saveRegisteredUser(User user) {
        userRepository.save(user);
    }


    // ----------------------- help methods ---------------------- //
    public int getTokenByUser(String email, String token)  {
        Optional<Map.Entry<User, String>> userTokenPair = mapUserTokens.entrySet().stream()
                .filter(row -> row.getKey().getEmail().equals(email))
                .findFirst();

        if (! userTokenPair.isPresent() ||
            ! userTokenPair.get().getKey().getEmail().equals(email) ||
            ! userTokenPair.get().getValue().equals(token)) {
            return 0;
        }

        return userTokenPair.get().getKey().getId();
    }

    public void updateTokensMap(String email, String token, String newEmail) {
        for (User user: mapUserTokens.keySet()) {
            if (user.getEmail().equals(email) && mapUserTokens.get(user).equals(token)) {
                user.setEmail(newEmail);
            }
        }
    }

    public void publishRegistrationEvent(User createdUser, Locale locale, String appUrl  ) {
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(createdUser, locale, appUrl));
    }

    public void deleteVerificationToken(String token) {
        tokenRepository.deleteByToken(token);
    }
}
