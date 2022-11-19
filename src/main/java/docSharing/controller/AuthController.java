package docSharing.controller;

import docSharing.controller.response.BaseResponse;
import docSharing.controller.response.TokenResponse;
import docSharing.entities.User;
import docSharing.service.AuthService;
import docSharing.utils.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLDataException;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    private static final Logger logger = LogManager.getLogger(AuthController.class.getName());

    @RequestMapping(method = RequestMethod.POST, path = "/signup")
    public ResponseEntity<BaseResponse<User>> register(@RequestBody User user){
        try {
            logger.info("in register");

            if (user.getEmail() == null | !Validate.isValidEmail(user.getEmail())) {
                return ResponseEntity.badRequest().body(BaseResponse.failure("Invalid email address!"));
            }
            if (user.getName() == null | !Validate.isValidName(user.getName())) {
                return ResponseEntity.badRequest().body(BaseResponse.failure("Invalid name!"));
            }
            if (user.getPassword() == null | !Validate.isValidPassword(user.getPassword())) {
                return ResponseEntity.badRequest().body(BaseResponse.failure("Invalid password!"));
            }

            logger.info("New user created" + user);
            return ResponseEntity.ok(BaseResponse.success(authService.createUser(user)));
        } catch (SQLDataException e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("Email already exists"));
        }
    }

    @RequestMapping(method = RequestMethod.POST, path = "/login")
    public  ResponseEntity<BaseResponse<TokenResponse>> login(@RequestBody User user){
        logger.info("in login");

        if (user.getEmail() == null | !Validate.isValidEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("Invalid name!"));
        }
        if (user.getPassword() == null | !Validate.isValidPassword(user.getPassword())) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("Invalid password!"));
        }

        Optional<String> token = authService.login(user);
        if (!token.isPresent()) {
            logger.warn("User " + user.getEmail() + " failed to log in");
            return ResponseEntity.badRequest().body(BaseResponse.failure("Wrong Email or Password. Login failed"));
        }

        logger.info("User with email" + user.getEmail() + "logged in");
        return ResponseEntity.ok(BaseResponse.success(new TokenResponse(token.get())));
    }


}
