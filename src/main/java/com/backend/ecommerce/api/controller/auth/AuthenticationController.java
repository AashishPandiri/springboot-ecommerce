package com.backend.ecommerce.api.controller.auth;

import com.backend.ecommerce.api.model.LoginBody;
import com.backend.ecommerce.api.model.LoginResponse;
import com.backend.ecommerce.api.model.PasswordResetBody;
import com.backend.ecommerce.api.model.RegistrationBody;
import com.backend.ecommerce.exception.EmailFailureException;
import com.backend.ecommerce.exception.EmailNotFoundException;
import com.backend.ecommerce.exception.UserAlreadyExistsException;
import com.backend.ecommerce.exception.UserNotVerifiedException;
import com.backend.ecommerce.model.LocalUser;
import com.backend.ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private UserService userService;

    public AuthenticationController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity registerUser(@Valid @RequestBody RegistrationBody registrationBody){
        try{
            userService.registerUser(registrationBody);
            return ResponseEntity.ok().build();
        }catch (UserAlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }catch (EmailFailureException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity loginUser(@Valid @RequestBody LoginBody loginBody) {
        String jwt = null;
        try {
            jwt = userService.loginUser(loginBody);
        } catch (UserNotVerifiedException e) {
            LoginResponse response = new LoginResponse();
            response.setSuccess(false);
            String reason = "USER_NOT_VERIFIED";
            if(e.isNewEmailSent()){
                reason += "_EMAIL_RESENT";
            }
            response.setFailureReason(reason);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(reason);
        } catch (EmailFailureException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        if(jwt == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else {
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setJwt(jwt);
            loginResponse.setSuccess(true);
            return ResponseEntity.ok(loginResponse);
        }
    }

    @PostMapping("/verify")
    public ResponseEntity verifyEmail(@RequestParam String token){
        if(userService.verifyUser(token)){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @GetMapping("/me")
    public LocalUser getLoggedInUserProfile(@AuthenticationPrincipal LocalUser user){
        return user;
    }

    @PostMapping("/forgot")
    public ResponseEntity forgotPassword(@RequestParam String email){
        try{
            userService.forgotPassword(email);
            return ResponseEntity.status(HttpStatus.OK).build();
        }catch (EmailNotFoundException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }catch (EmailFailureException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/reset")
    public ResponseEntity resetPassword(@Valid @RequestBody PasswordResetBody body){
        userService.resetPassword(body);
        return ResponseEntity.ok().build();
    }
}
