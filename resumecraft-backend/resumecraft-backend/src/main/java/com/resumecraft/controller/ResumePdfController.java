
package com.resumecraft.controller;

import com.resumecraft.model.ResumeRequest;
import com.resumecraft.service.ResumeEmailTemplate;
import com.resumecraft.service.ResumePdfService;
import com.resumecraft.service.SendLibertyService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resume")
public class ResumePdfController {

    private final ResumePdfService resumePdfService;
    private final SendLibertyService sendLibertyService;
    private final ResumeEmailTemplate resumeEmailTemplate;

    public ResumePdfController(
            ResumePdfService resumePdfService,
            SendLibertyService sendLibertyService,
            ResumeEmailTemplate resumeEmailTemplate) {

        this.resumePdfService = resumePdfService;
        this.sendLibertyService = sendLibertyService;
        this.resumeEmailTemplate = resumeEmailTemplate;
    }

    @PostMapping(
            value = "/generate",
            produces = MediaType.APPLICATION_PDF_VALUE
    )
    public ResponseEntity<byte[]> generateResume(
            @RequestBody ResumeRequest request
    ) throws Exception {

        byte[] pdf = resumePdfService.generatePdf(request);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=resume.pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @PostMapping("/generate-and-email")
    public ResponseEntity<String> generateAndEmailResume(
            @RequestBody EmailResumeRequest emailRequest
    ) throws Exception {

        byte[] pdf = resumePdfService.generatePdf(
                emailRequest.resume()
        );

        String html = resumeEmailTemplate.buildEmailHtml("there");

        String response = sendLibertyService.sendEmailWithPdf(
                "ResumeCraft <mytechlabs.tech@gmail.com>",
                emailRequest.email(),
                "Your ResumeCraft Resume",
                html,
                pdf,
                "resume.pdf"
        );

        return ResponseEntity.ok(response);
    }

    public record EmailResumeRequest(
            String email,
            ResumeRequest resume
    ) {}
}
