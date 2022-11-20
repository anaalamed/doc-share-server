package docSharing.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import docSharing.controller.request.UserRequest;
import docSharing.entities.User;
import docSharing.repository.UserRepository;
import docSharing.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLDataException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static docSharing.utils.Utils.hashPassword;

@Service
public class AuthService {
    @Autowired
    private final UserRepository userRepository;
    static HashMap<User, String> mapUserTokens = new HashMap<>();

    private static final Logger logger = LogManager.getLogger(AuthService.class.getName());

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(UserRequest userRequest) throws SQLDataException {
        logger.info("in createUser");

        if(userRepository.findByEmail(userRequest.getEmail())!=null){
            throw new SQLDataException(String.format("Email %s exists in users table", userRequest.getEmail()));
        }

        return userRepository.save(new User(userRequest.getName(), userRequest.getEmail(), hashPassword(userRequest.getPassword())));
    }

    public Optional<String> login(UserRequest userRequest) {
        logger.info("in login");

        User userByEmail = userRepository.findByEmail(userRequest.getEmail());

        if (userByEmail == null) {
            return Optional.empty();
        }


        BCrypt.Result result = BCrypt.verifyer().verify(userByEmail.getPassword().toCharArray(),
                                                        userRequest.getPassword().toCharArray());
        if(result.verified) {
            Optional<String> token = Optional.of(Utils.generateUniqueToken()) ;
            mapUserTokens.put(userByEmail, token.get());
            return token;
        }

        return Optional.empty();
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

}
