package com.backend.ecommerce.service;

import com.backend.ecommerce.api.model.LoginBody;
import com.backend.ecommerce.api.model.PasswordResetBody;
import com.backend.ecommerce.api.model.RegistrationBody;
import com.backend.ecommerce.exception.EmailFailureException;
import com.backend.ecommerce.exception.EmailNotFoundException;
import com.backend.ecommerce.exception.UserAlreadyExistsException;
import com.backend.ecommerce.exception.UserNotVerifiedException;
import com.backend.ecommerce.model.LocalUser;
import com.backend.ecommerce.model.VerificationToken;
import com.backend.ecommerce.model.dao.LocalUserDAO;
import com.backend.ecommerce.model.dao.VerificationTokenDAO;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class UserServiceTest {

    @RegisterExtension
    private static GreenMailExtension greenMailExtension = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("springboot", "secret"))
            .withPerMethodLifecycle(true);

    @Autowired
    private UserService userService;

    @Autowired
    private VerificationTokenDAO verificationTokenDAO;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private LocalUserDAO localUserDAO;

    @Autowired
    private EncryptionService encryptionService;

    @Test
    @Transactional
    public void testRegisterUser() throws MessagingException {
        // Setup registration body with user details
        RegistrationBody body = new RegistrationBody();
        body.setUsername("UserA");
        body.setEmail("UserServiceTest$testRegisterUser@junit.com");
        body.setFirstName("FirstName");
        body.setLastName("LastName");
        body.setPassword("MySecretPassword123");

        // Test that username already exists
        Assertions.assertThrows(UserAlreadyExistsException.class,
                () -> userService.registerUser(body), "Username should already be in use.");

        // Modify body for testing email duplication
        body.setUsername("UserServiceTest$testRegisterUser");
        body.setEmail("UserA@junit.com");

        // Test that email already exists
        Assertions.assertThrows(UserAlreadyExistsException.class,
                () -> userService.registerUser(body), "Email should already be in use.");

        // Modify body for successful registration
        body.setEmail("UserServiceTest$testRegisterUser@junit.com");

        // Test successful user registration
        Assertions.assertDoesNotThrow(() -> userService.registerUser(body), "User should register successfully.");

        // Verify that an email was sent after registration
        Assertions.assertEquals(body.getEmail(), greenMailExtension.getReceivedMessages()[0]
                .getRecipients(Message.RecipientType.TO)[0].toString());
    }


    @Test
    @Transactional
    public void testLoginUser() throws UserNotVerifiedException, EmailFailureException {
        LoginBody body = new LoginBody();
        body.setUsername("UserA-NotExists");
        body.setPassword("PasswordA123-BadPassword");
        Assertions.assertNull(userService.loginUser(body), "The user should not exist.");
        body.setUsername("UserA");
        Assertions.assertNull(userService.loginUser(body), "The password should be incorrrect.");
        body.setPassword("PasswordA123");
        Assertions.assertNotNull(userService.loginUser(body), "The user should login successfully.");
        body.setUsername("UserB");
        body.setPassword("PasswordB123");
        try{
            userService.loginUser(body);
            Assertions.assertTrue(false, "User should not have email verified.");
        } catch (UserNotVerifiedException e) {
            Assertions.assertTrue(e.isNewEmailSent(), "Email verification mail should be sent.");
            Assertions.assertEquals(1, greenMailExtension.getReceivedMessages().length);
        }
        try{
            userService.loginUser(body);
            Assertions.assertTrue(false, "User should not have email verified.");
        } catch (UserNotVerifiedException e) {
            Assertions.assertFalse(e.isNewEmailSent(), "Email verification mail should not be resent.");
            Assertions.assertEquals(1, greenMailExtension.getReceivedMessages().length);
        }
    }

    @Test
    @Transactional
    public void testVerifyUser() throws EmailFailureException {
        Assertions.assertFalse(userService.verifyUser("Bad Token"), "Token is bad or does not exist and should return false.");
        LoginBody body = new LoginBody();
        body.setUsername("UserB");
        body.setPassword("PasswordB123");
        try{
            userService.loginUser(body);
            Assertions.assertTrue(false, "User should not have email verified.");
        } catch (UserNotVerifiedException e) {
            List<VerificationToken> tokens = verificationTokenDAO.findByUser_IdOrderByIdDesc(2L);
            String token = tokens.get(0).getToken();
            Assertions.assertTrue(userService.verifyUser(token), "Token should be valid.");
            Assertions.assertNotNull(body, "The user should now be verified.");
        }
    }

    @Test
    @Transactional
    public void testForgotPassword() throws MessagingException {
        Assertions.assertThrows(EmailNotFoundException.class, () -> userService.forgotPassword(("UserNotExist@junit.com")));
        Assertions.assertDoesNotThrow(() -> userService.forgotPassword("UserA@junit.com"), "Non existing email should be rejected.");
        Assertions.assertEquals("UserA@junit.com", greenMailExtension.getReceivedMessages()[0]
                .getRecipients(Message.RecipientType.TO)[0].toString(),
                 "Password reset email should be sent.");
    }

    @Test
    public void testResetPassword(){
        LocalUser user = localUserDAO.findByUsernameIgnoreCase("UserA").get();
        String token = jwtService.generatePasswordResetJWT(user);
        PasswordResetBody body = new PasswordResetBody();
        body.setToken(token);
        body.setPassword("Password12new");
        userService.resetPassword(body);
        user = localUserDAO.findByUsernameIgnoreCase("UserA").get();
        Assertions.assertTrue(encryptionService.verifyPassword("Password12new", user.getPassword()),
                "Password change should be written to DB.");
    }
}
