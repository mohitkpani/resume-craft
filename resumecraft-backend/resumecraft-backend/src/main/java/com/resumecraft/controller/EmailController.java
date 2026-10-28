
package com.resumecraft.controller;

import com.resumecraft.service.ResumeEmailTemplate;
import com.resumecraft.service.SendLibertyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    private final SendLibertyService sendLibertyService;
    private final ResumeEmailTemplate resumeEmailTemplate;

    public EmailController(
            SendLibertyService sendLibertyService,
            ResumeEmailTemplate resumeEmailTemplate) {
        this.sendLibertyService = sendLibertyService;
        this.resumeEmailTemplate = resumeEmailTemplate;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(
            @RequestBody EmailRequest request) {

        String html = request.html();

        if (html == null || html.isBlank()) {
            html = resumeEmailTemplate.buildEmailHtml("there");
        }

        String subject = request.subject() == null
                || request.subject().isBlank()
                ? "Your ResumeCraft Resume"
                : request.subject();

        String response = sendLibertyService.sendEmail(
                request.from(),
                request.to(),
                subject,
                html
        );

        return ResponseEntity.ok(response);
    }

    public record EmailRequest(
            String from,
            String to,
            String subject,
            String html
    ) {}
}
