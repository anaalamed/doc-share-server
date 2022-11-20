package docSharing.controller;


import docSharing.controller.request.UserRequest;
import docSharing.controller.response.BaseResponse;

import docSharing.entities.User;
import docSharing.entities.VerificationToken;
import docSharing.service.AuthService;
import docSharing.utils.InputValidation;
import docSharing.utils.OnRegistrationCompleteEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;


import javax.servlet.http.HttpServletRequest;
import java.sql.SQLDataException;
import java.util.Calendar;
import java.util.Locale;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    private static final Logger logger = LogManager.getLogger(AuthController.class.getName());

    @RequestMapping(method = RequestMethod.POST, path = "/signup")
    public ResponseEntity<BaseResponse<User>> register(@RequestBody UserRequest userRequest, HttpServletRequest request){
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

            User createdUser = authService.createUser(userRequest);
            String appUrl = request.getContextPath();

            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(createdUser, request.getLocale(), appUrl));
            return ResponseEntity.ok(BaseResponse.success(createdUser));
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

        if (! authService.isEnabledUser(userRequest)) {
            return ResponseEntity.badRequest().body(BaseResponse.failure("You should confirm your email first!"));
        }

        Optional<String> token = authService.login(userRequest);
        if (!token.isPresent()) {
            logger.warn("User " + userRequest.getEmail() + " failed to log in");
            return ResponseEntity.badRequest().body(BaseResponse.failure("Wrong Email or Password. Login failed"));
        }

        logger.info("User with email" + userRequest.getEmail() + "logged in");
        return ResponseEntity.ok(BaseResponse.success(token.get()));
    }


    @GetMapping("/registrationConfirm")
    public String confirmRegistration(WebRequest request, Model model, @RequestParam("token") String token) {

        Locale locale = request.getLocale();

        VerificationToken verificationToken = authService.getVerificationToken(token);
        if (verificationToken == null) {
            return "redirect:/badUser.html?lang=" + locale.getLanguage();
        }

        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            return "redirect:/badUser.html?lang=" + locale.getLanguage();
        }

        user.setEnabled(true);
        authService.saveRegisteredUser(user);
        return "redirect:/login.html?lang=" + request.getLocale().getLanguage();
    }

}
