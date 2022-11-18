package docSharing.controller;

import docSharing.controller.response.BaseResponse;
import docSharing.controller.response.TokenResponse;
import docSharing.entities.User;
import docSharing.service.AuthService;
import docSharing.utils.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLDataException;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    private static final Logger logger = LogManager.getLogger(AuthController.class.getName());

    @RequestMapping(method = RequestMethod.POST, path = "/signup")
    public BaseResponse<User> register(@RequestBody User user){
        try {
            logger.info("in register");

            if (user.getEmail() == null | !Validate.isValidEmail(user.getEmail())) {
                return BaseResponse.failure("Invalid email address!");
            }
            if (user.getName() == null | !Validate.isValidName(user.getName())) {
                return BaseResponse.failure("Invalid name!");
            }
            if (user.getPassword() == null | !Validate.isValidPassword(user.getPassword())) {
                return BaseResponse.failure("Invalid password!");
            }

            logger.info("New user created" + user);
            return BaseResponse.success(authService.createUser(user));
        } catch (SQLDataException e) {
            return  BaseResponse.failure("Email already exists");
        }
    }

    @RequestMapping(method = RequestMethod.POST, path = "/login")
    public  BaseResponse<TokenResponse> login(@RequestBody User user){
        logger.info("in login");

        if (user.getEmail() == null | !Validate.isValidEmail(user.getEmail())) {
            return BaseResponse.failure("Invalid name!");
        }
        if (user.getPassword() == null | !Validate.isValidPassword(user.getPassword())) {
            return BaseResponse.failure("Invalid password!");
        }

        String token = authService.login(user);
        if (token == null) {
            return BaseResponse.failure("Email or password doesn't match. Login failed");
        }

        logger.info("User with email" + user.getEmail() + "loged in");
        return BaseResponse.success(new TokenResponse(token));
    }


}
