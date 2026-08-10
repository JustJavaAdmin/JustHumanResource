package com.justjava.humanresource.utils;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.Attachment;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;

@Service
public class ResendService {
    private final String apiKey;
    private final String defaultFrom;

    public ResendService(
            @Value("${resend.api-key:}") String apiKey,
            @Value("${resend.default-from:}") String defaultFrom
    ) {
        this.apiKey = apiKey;
        this.defaultFrom = defaultFrom;
    }

    public String sendPdfAttachment(
            String to,
            String subject,
            String html,
            String text,
            String filename,
            byte[] pdfBytes
    ) {
        validateConfigured();

        String encodedPdf = Base64.getEncoder().encodeToString(pdfBytes);
        Attachment attachment = Attachment.builder()
                .fileName(filename)
                .content(encodedPdf)
                .contentType("application/pdf")
                .build();

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from(defaultFrom)
                .to(to)
                .subject(subject)
                .html(html)
                .text(text)
                .attachments(List.of(attachment))
                .build();

        try {
            Resend resend = new Resend(apiKey);
            CreateEmailResponse response = resend.emails().send(params);
            return response != null ? response.getId() : null;
        } catch (ResendException e) {
            throw new IllegalStateException("Resend failed to send email: " + e.getMessage(), e);
        }
    }

    public String sendEmail(
            String to,
            String subject,
            String html,
            String text
    ) {
        validateConfigured();
        if (to == null || to.isBlank()) {
            throw new IllegalStateException("Recipient email address is not provided.");
        }

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from(defaultFrom)
                .to(to)
                .subject(subject)
                .html(html)
                .text(text)
                .build();

        try {
            Resend resend = new Resend(apiKey);
            CreateEmailResponse response = resend.emails().send(params);
            return response != null ? response.getId() : null;
        } catch (ResendException e) {
            throw new IllegalStateException("Resend failed to send email: " + e.getMessage(), e);
        }
    }

    private void validateConfigured() {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Resend API key is not configured.");
        }
        if (defaultFrom == null || defaultFrom.isBlank()) {
            throw new IllegalStateException("Resend sender address is not configured.");
        }
    }
}