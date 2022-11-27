package docSharing.service;

import docSharing.entities.DTO.UserDTO;
import docSharing.entities.User;
import docSharing.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static docSharing.utils.Utils.hashPassword;

@Service
public class UserService {

    private final UserRepository userRepository;

    private static final Logger logger = LogManager.getLogger(UserService.class.getName());

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public Optional<UserDTO> getByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return Optional.empty();
        }

        return Optional.of(new UserDTO(user));
    }

    public boolean deleteUser(int id) {
        int lines = userRepository.deleteById(id);
        logger.debug("lines deleted: " + lines);

        if (lines == 1) {
            logger.debug("User #" + id + " deleted: ");
            return true;
        }
        return false;
    }

    public Optional<UserDTO> updateName(int id, String name) {
        int lines = userRepository.updateUserNameById(id, name);
        logger.debug("lines updated: " + lines);

        return getUpdatedUser(id, lines);
    }

    public Optional<UserDTO> updateEmail(int id, String email) {
        int lines = userRepository.updateUserEmailById(id, email);
        logger.debug("lines updated: " + lines);

        return getUpdatedUser(id, lines);
    }

    public Optional<UserDTO> updatePassword(int id, String password) {
        int lines = userRepository.updateUserPasswordById(id,hashPassword(password));
        logger.debug("lines updated: " + lines);

        return getUpdatedUser(id, lines);
    }

    public Optional<UserDTO> updateEnabled(int id, Boolean enabled) {
        int lines = userRepository.updateUserEnabledById(id, enabled);
        logger.debug("lines updated: " + lines);

        return getUpdatedUser(id, lines);
    }


    // -------------------- help methods --------------------------- //
    private Optional<UserDTO> getUpdatedUser(int id, int lines) {
        if (lines == 1) {
            Optional<User> user = userRepository.findById(id);
            logger.debug("User #" + id + " updated: " + user.get());
            UserDTO userDTO = new UserDTO(user.get());
            return Optional.of(userDTO);
        }

        return Optional.empty();
    }
}
