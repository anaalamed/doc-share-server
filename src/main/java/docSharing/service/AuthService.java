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

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    static HashMap<String, String> mapUserTokens = new HashMap<>();

    private static final Logger logger = LogManager.getLogger(AuthService.class.getName());


    public User createUser(User user) throws SQLDataException {
        logger.info("in createUser");

        if(userRepository.getByEmail(user.getEmail())!=null){
            throw new SQLDataException(String.format("Email %s exists in users table", user.getEmail()));
        }

        return userRepository.save(user);
    }

    public String login(User user) {

        User userByEmail = userRepository.getByEmail(user.getEmail());
        logger.debug(userByEmail);

        if (userByEmail == null) {
            logger.debug("User not found");
            throw new NullPointerException("User not found");
        }

        if (userByEmail.getPassword().equals(user.getPassword())) {
            String token = Utils.generateUniqueToken();
            logger.debug(token);
            mapUserTokens.put(token, String.valueOf(userByEmail.getId()));
            return token;
        }

        return null;
    }

//    public Integer getUserIdByToken(String token)  {
//        String id = mapUserTokens.get(token);
//
//        if (id == null) {
//            throw new NullPointerException("user not authorized");
//        }
//        return Integer.valueOf(id);
//    }

}
