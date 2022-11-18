package docSharing.service;

import docSharing.entities.User;
import docSharing.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLDataException;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
