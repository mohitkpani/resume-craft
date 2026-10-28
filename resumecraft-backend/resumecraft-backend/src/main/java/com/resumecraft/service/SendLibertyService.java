
package com.resumecraft.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class SendLibertyService {

    private final RestClient restClient;

    public SendLibertyService(
            @Value("${sendliberty.api.key}") String apiKey) {

        this.restClient = RestClient.builder()
                .baseUrl("https://sendliberty.com/api")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader(
                        "Content-Type",
                        MediaType.APPLICATION_JSON_VALUE
                )
                .build();
    }

    public String sendEmail(
            String from,
            String to,
            String subject,
            String html) {

        Map<String, Object> request = Map.of(
                "from", from,
                "to", to,
                "subject", subject,
                "html", html
        );

        return restClient.post()
                .uri("/send")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(String.class);
    }

    public String sendEmailWithPdf(
            String from,
            String to,
            String subject,
            String html,
            byte[] pdfBytes,
            String fileName) {

        Map<String, Object> attachment = Map.of(
                "filename", fileName,
                "content", Base64.getEncoder().encodeToString(pdfBytes),
                "type", "application/pdf"
        );

        Map<String, Object> request = Map.of(
                "from", from,
                "to", to,
                "subject", subject,
                "html", html,
                "attachments", List.of(attachment)
        );

        return restClient.post()
                .uri("/send")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(String.class);
    }
}
