package docSharing.controller;

import docSharing.controller.response.BaseResponse;
import docSharing.entities.User;
import docSharing.service.AuthService;
import docSharing.service.UserService;
import docSharing.utils.InputValidation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private AuthService authService;

    private static final Logger logger = LogManager.getLogger(UserController.class.getName());


    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<BaseResponse<User>> getUserByEmail(@RequestParam String email){
        logger.info("in getUserByEmail");

        Optional<User> user = userService.getByEmail(email);
        return user.map(value -> ResponseEntity.ok(BaseResponse.success(value)))
                .orElseGet(() -> ResponseEntity.badRequest().body(BaseResponse.failure("User not found!")));
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "delete")
    public ResponseEntity<BaseResponse<String>> deleteUser(@RequestParam String email, @RequestHeader String token){
        logger.debug("in deleteUser");

        int id = authService.getTokenByUser(email, token);
        if (id == 0) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("User not authorized"));
        }

        if (userService.deleteUser(id)) {
            return ResponseEntity.ok(BaseResponse.noContent(true, "user " + email + " deleted"));
        }

        return ResponseEntity.badRequest().body(BaseResponse.failure("Delete user #" + id + "failed"));
    }

    @RequestMapping(method = RequestMethod.PATCH, value="/update/{email}", params = "name")
    public ResponseEntity<BaseResponse<User>> updateName(@PathVariable("email") String email, @RequestHeader String token,
                                                         @RequestParam String name) {
        logger.debug("in updateName");

        if (!InputValidation.isValidName(name)) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("Invalid name!"));
        }

        int id = authService.getTokenByUser(email, token);
        logger.debug(id);
        if (id == 0) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("User not authorized"));
        }

        Optional<User> updatedUser = userService.updateName(id, name);
        return updatedUser.map(value -> ResponseEntity.ok(BaseResponse.success(value))).
                orElseGet(() -> ResponseEntity.badRequest().body(BaseResponse.failure("User not found")));
    }

    @RequestMapping(method = RequestMethod.PATCH, value="/update/{email}", params = "newEmail")
    public ResponseEntity<BaseResponse<User>> updateEmail(@PathVariable("email") String email, @RequestHeader String token,
                                                          @RequestParam String newEmail){
        logger.debug("in updateEmail");

        if (!InputValidation.isValidEmail(newEmail)) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("Invalid email!"));
        }

        int id = authService.getTokenByUser(email, token);
        if (id == 0) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("User not authorized"));
        }

        Optional<User> updatedUser = userService.updateEmail(id, newEmail);
        authService.updateTokensMap(email, token, newEmail);
        return updatedUser.map(user -> ResponseEntity.ok(BaseResponse.success(user)))
                .orElseGet(() -> ResponseEntity.badRequest().body(BaseResponse.failure("User not found")));
    }

    @RequestMapping(method = RequestMethod.PATCH, value="/update/{email}", params = "password")
    public ResponseEntity<BaseResponse<User>> updatePassword(@PathVariable("email") String email, @RequestHeader String token,
                                                          @RequestParam String password){
        logger.debug("in updatePassword");

        if (!InputValidation.isValidPassword(password)) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("Invalid password!"));
        }

        int id = authService.getTokenByUser(email, token);
        if (id == 0) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("User not authorized"));
        }

        Optional<User> updatedUser = userService.updatePassword(id, password);
        return updatedUser.map(user -> ResponseEntity.ok(BaseResponse.success(user)))
                .orElseGet(() -> ResponseEntity.badRequest().body(BaseResponse.failure("User not found")));
    }


}
