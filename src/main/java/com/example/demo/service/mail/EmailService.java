package com.example.demo.service.mail;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;


@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    // Annotation @Async để chạy tác vụ này trên một luồng riêng
    @Async
    public void sendEmail(String toEmail, String subject,  String templateName, Map<String, Object> variables) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            // Tạo context để inject biến vào template
            Context context = new Context();
            if (variables != null) {
                variables.forEach(context::setVariable);
            }

            // Render template
            String htmlContent = templateEngine.process(templateName, context);

            helper.setText(htmlContent, true); // true = HTML
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setFrom("Admin@gmail.com"); // Có thể đọc từ config thay vì hardcode

            javaMailSender.send(mimeMessage);

        } catch (MessagingException e) {
            System.err.println("Gửi mail thất bại: " + e.getMessage());
        }
    }
}