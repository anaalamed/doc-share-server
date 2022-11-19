package docSharing.service;

import docSharing.entities.User;
import docSharing.repository.UserRepository;
import docSharing.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLDataException;
import java.util.HashMap;
import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private final UserRepository userRepository;
    static HashMap<String, User> mapUserTokens = new HashMap<>();

    private static final Logger logger = LogManager.getLogger(AuthService.class.getName());

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) throws SQLDataException {
        logger.info("in createUser");

        if(userRepository.findByEmail(user.getEmail())!=null){
            throw new SQLDataException(String.format("Email %s exists in users table", user.getEmail()));
        }

        return userRepository.save(user);
    }

    public Optional<String> login(User user) {
        logger.info("in login");

        User userByEmail = userRepository.findByEmail(user.getEmail());

        if (userByEmail == null) {
            return Optional.empty();
        }

        if (userByEmail.getPassword().equals(user.getPassword())) {
            Optional<String> token = Optional.of(Utils.generateUniqueToken()) ;
            mapUserTokens.put(token.get(), userByEmail);
            return token;
        }

        return Optional.empty();
    }

    public int getUserIdByToken(String email, String token)  {
        User user = mapUserTokens.get(token);

        if (user == null || !user.getEmail().equals(email)) {
            return 0;
        }
        return user.getId();
    }



}
