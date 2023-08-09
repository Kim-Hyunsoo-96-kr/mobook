package com.mb.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;

    public void sendHtmlEmail(String[] receiverList, String subject, String htmlContentTemplate) throws MessagingException, IOException {
        String htmlContent = loadHtmlContentFromFile(htmlContentTemplate);

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(receiverList); // Pass an array of email addresses
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // The 'true' parameter indicates HTML content

        javaMailSender.send(message);
    }

    public String loadHtmlContentFromFile(String filePath) throws IOException {
        Resource resource = new ClassPathResource(filePath);
        byte[] contentBytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
        return new String(contentBytes, "UTF-8");
    }
}
