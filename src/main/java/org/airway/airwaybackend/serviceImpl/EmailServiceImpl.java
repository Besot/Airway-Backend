package org.airway.airwaybackend.serviceImpl;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.airway.airwaybackend.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl  {
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Autowired
    private JavaMailSender javaMailSender;

    public String sendMail(String to, String subject, String body) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(fromEmail);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(body);

            javaMailSender.send(mimeMessage);
            return "Mail sent successfully";

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String applicationUrl(HttpServletRequest request) {
        return "http://" +
                request.getServerName() +
                ":" +
                request.getServerPort() +
                "/api/v1/auth" +
                request.getContextPath();
    }

    public String passwordResetTokenMail(User user, String applicationUrl, String token) {
        String url = applicationUrl +"/resetPassword/" + token;
        this.sendMail(
                user.getEmail(),
                "Click on your Password link to reset your Password: " + url,
                "Password Reset Code Sent");
        return url;
    }
}
