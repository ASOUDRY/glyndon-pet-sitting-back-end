package com.soudry.pet_sitting.controller;

import com.soudry.pet_sitting.dto.ContactRequest;
import com.soudry.pet_sitting.Services.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin(origins = "http://localhost:5173")
public class ContactController {

    private final EmailService emailService;

    public ContactController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping
    public ResponseEntity<String> sendContactRequest(
            @RequestBody ContactRequest request
    ) {

        emailService.sendContactRequest(request);

        return ResponseEntity.ok(
                "Contact request sent successfully."
        );
    }
}