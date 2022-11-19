package docSharing.controller;

import docSharing.controller.request.UserRequest;
import docSharing.controller.response.BaseResponse;
import docSharing.entities.User;
import docSharing.service.AuthService;
import docSharing.utils.InputValidation;
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
    public ResponseEntity<BaseResponse<User>> register(@RequestBody UserRequest userRequest){
        try {
            logger.info("in register");

            if (userRequest.getEmail() == null | !InputValidation.isValidEmail(userRequest.getEmail())) {
                return ResponseEntity.badRequest().body(BaseResponse.failure("Invalid email address!"));
            }
            if (userRequest.getName() == null | !InputValidation.isValidName(userRequest.getName())) {
                return ResponseEntity.badRequest().body(BaseResponse.failure("Invalid name!"));
            }
            if (userRequest.getPassword() == null | !InputValidation.isValidPassword(userRequest.getPassword())) {
                return ResponseEntity.badRequest().body(BaseResponse.failure("Invalid password!"));
            }

            logger.info("New user created" + userRequest);
            return ResponseEntity.ok(BaseResponse.success(authService.createUser(userRequest)));
        } catch (SQLDataException e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("Email already exists"));
        }
    }

    @RequestMapping(method = RequestMethod.POST, path = "/login")
    public  ResponseEntity<BaseResponse<String>> login(@RequestBody UserRequest userRequest){
        logger.info("in login");

        if (userRequest.getEmail() == null | !InputValidation.isValidEmail(userRequest.getEmail())) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("Invalid email!"));
        }
        if (userRequest.getPassword() == null | !InputValidation.isValidPassword(userRequest.getPassword())) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("Invalid password!"));
        }

        Optional<String> token = authService.login(userRequest);
        if (!token.isPresent()) {
            logger.warn("User " + userRequest.getEmail() + " failed to log in");
            return ResponseEntity.badRequest().body(BaseResponse.failure("Wrong Email or Password. Login failed"));
        }

        logger.info("User with email" + userRequest.getEmail() + "logged in");
        return ResponseEntity.ok(BaseResponse.success(token.get()));
    }


}
