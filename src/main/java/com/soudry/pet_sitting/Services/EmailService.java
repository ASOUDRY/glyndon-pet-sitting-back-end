package com.soudry.pet_sitting.Services;

import com.soudry.pet_sitting.dto.ContactRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.contact-email}")
    private String contactEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendContactRequest(ContactRequest request) {

        SimpleMailMessage email = new SimpleMailMessage();

        email.setTo(contactEmail);

        email.setSubject(
                "New Pet Care Request from " + request.getName()
        );

        email.setReplyTo(request.getEmail());

        email.setText(
                "Name: " + request.getName() + "\n" +
                "Email: " + request.getEmail() + "\n" +
                "Phone: " + request.getPhone() + "\n\n" +
                "Message:\n" + request.getMessage()
        );

        mailSender.send(email);
    }
}