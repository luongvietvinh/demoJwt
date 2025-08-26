package com.example.demo.service.mail;

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
    public void sendRegistrationSuccessEmail(String toEmail, String userName, String passWord) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            // Tạo context để truyền biến vào template Thymeleaf
            Context context = new Context();
            context.setVariable("userName", userName);
            context.setVariable("passWord", passWord);

            // Xử lý template
            String htmlContent = templateEngine.process("MailTemplate", context);

            helper.setText(htmlContent, true); // true = gửi dạng HTML
            helper.setTo(toEmail);
            helper.setSubject("Chào mừng bạn! Đăng ký tài khoản thành công");
            helper.setFrom("Admin@gmail.com"); // Email người gửi

            javaMailSender.send(mimeMessage);
            
        } catch (MessagingException e) {
            // Log lỗi hoặc xử lý nếu gửi mail thất bại
            // logger.error("Failed to send email", e);
            System.err.println("Gửi mail thất bại: " + e.getMessage());
        }
    }
}