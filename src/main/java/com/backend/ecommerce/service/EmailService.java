package com.backend.ecommerce.service;

import com.backend.ecommerce.exception.EmailFailureException;
import com.backend.ecommerce.model.LocalUser;
import com.backend.ecommerce.model.VerificationToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${email.from}")
    private String fromAdress;

    @Value("${app.frontend.url}")
    private String url;

    private JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    private SimpleMailMessage makeMailMessage(){
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(fromAdress);
        return simpleMailMessage;
    }

    public void sendVerificationEmail(VerificationToken verificationToken) throws EmailFailureException{
        SimpleMailMessage message = makeMailMessage();
        message.setTo(verificationToken.getUser().getEmail());
        message.setSubject("Verify your email to activate your account.");
        message.setText("Please follow the link below to verify your email to activate your account.\n" +
                url + "/auth/verify?token=" + verificationToken.getToken());
        try {
            javaMailSender.send(message);
        }catch (MailException e){
            throw new EmailFailureException();
        }
    }

    public void sendPasswordResetEmail(LocalUser user, String token) throws EmailFailureException{
        SimpleMailMessage message = makeMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Your password reset request link.");
        message.setText("You requested a password reset on our website. Please " +
                "find the link below to be able to reset your password.\n" + url +
                "/auth/reset?token=" + token);
        try {
            javaMailSender.send(message);
        }catch (MailException e){
            throw new EmailFailureException();
        }
    }
}
