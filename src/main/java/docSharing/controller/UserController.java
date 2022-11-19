package docSharing.controller;

import docSharing.controller.response.BaseResponse;
import docSharing.entities.User;
import docSharing.service.AuthService;
import docSharing.service.UserService;
import docSharing.utils.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLDataException;
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
    public ResponseEntity<BaseResponse<User>> getUserById(@RequestParam int id){
        logger.info("in getUserById");

        Optional<User> user = userService.getById(id);
        return user.map(value -> ResponseEntity.ok(BaseResponse.success(value))).orElseGet(() -> ResponseEntity.badRequest().body(BaseResponse.failure("User not found!")));
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "delete")
    public ResponseEntity<BaseResponse<String>> deleteUser(@RequestParam String email, @RequestHeader String token){
        logger.debug("in deleteUser");

        Optional<Integer> id = authService.getUserIdByToken(email, token);
        if (!id.isPresent()) {
            return ResponseEntity.ok(BaseResponse.failure("User not authorized"));
        }

        if (userService.deleteById(id.get())) {
            return ResponseEntity.ok(BaseResponse.noContent(true, "user " + email + " deleted"));
        }

        return ResponseEntity.ok(BaseResponse.failure("Delete user #" + id.get() + "failed"));
    }

    @RequestMapping(method = RequestMethod.PATCH, value="/update/{email}", params = "name")
    public ResponseEntity<BaseResponse<User>> updateName(@PathVariable("email") String email, @RequestHeader String token,
                                                         @RequestParam String name) {
        logger.debug("in updateName");

        if (!Validate.isValidName(name)) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("Invalid name!"));
        }

        Optional<Integer> id = authService.getUserIdByToken(email, token);
        if (!id.isPresent()) {
            return ResponseEntity.ok(BaseResponse.failure("User not authorized"));
        }

        Optional<User> updatedUser = userService.updateName(id.get(), name);
        return updatedUser.map(value -> ResponseEntity.ok(BaseResponse.success(value))).
                orElseGet(() -> ResponseEntity.ok(BaseResponse.failure("User not found")));
    }

    @RequestMapping(method = RequestMethod.PATCH, value="/update/{email}", params = "newEmail")
    public ResponseEntity<BaseResponse<User>> updateEmail(@PathVariable("email") String email, @RequestHeader String token,
                                                          @RequestParam String newEmail){
        logger.debug("in updateName");

        if (!Validate.isValidEmail(newEmail)) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("Invalid email!"));
        }

        Optional<Integer> id = authService.getUserIdByToken(email, token);
        if (!id.isPresent()) {
            return ResponseEntity.ok(BaseResponse.failure("User not authorized"));
        }

        Optional<User> updatedUser = userService.updateEmail(id.get(), newEmail);
        return updatedUser.map(user -> ResponseEntity.ok(BaseResponse.success(user)))
                .orElseGet(() -> ResponseEntity.ok(BaseResponse.failure("User not found")));
    }

    @RequestMapping(method = RequestMethod.PATCH, value="/update/{email}", params = "password")
    public ResponseEntity<BaseResponse<User>> updatePassword(@PathVariable("email") String email, @RequestHeader String token,
                                                          @RequestParam String password){
        logger.debug("in updatePassword");

        if (!Validate.isValidPassword(password)) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("Invalid password!"));
        }

        Optional<Integer> id = authService.getUserIdByToken(email, token);
        if (!id.isPresent()) {
            return ResponseEntity.ok(BaseResponse.failure("User not authorized"));
        }

        Optional<User> updatedUser = userService.updatePassword(id.get(), password);
        return updatedUser.map(user -> ResponseEntity.ok(BaseResponse.success(user)))
                .orElseGet(() -> ResponseEntity.ok(BaseResponse.failure("User not found")));
    }


}
