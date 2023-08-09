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
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    public void sendHtmlEmail(String[] receiverList, String subject, String htmlContentTemplate, Map<String, Object> model) throws MessagingException, IOException {

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        String htmlContent = processTemplate(htmlContentTemplate, model);

        helper.setTo(receiverList); // Pass an array of email addresses
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // The 'true' parameter indicates HTML content

        javaMailSender.send(message);
    }

    private String processTemplate(String htmlContentTemplate, Map<String, Object> model) {
        Context context = new Context();
        context.setVariables(model);
        return templateEngine.process(htmlContentTemplate, context);
    }
}
