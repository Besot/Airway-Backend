package org.airway.airwaybackend.serviceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EmailSenderServiceTest {

    @Mock
    private JavaMailSender mockMailSender;

    @InjectMocks
    private EmailSenderService emailSenderService;

    @Test
    public void testSendSimpleEmail() {
        // Test data
        String toEmail = "goodluckanorue001@gmail.com";
        String body = "Test email body";
        String subject = "Test subject";

        // Calling the actual method to be tested
        emailSenderService.sendSimpleEmail(toEmail, body, subject);

        // Verifying that the mock JavaMailSender is called
        verify(mockMailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}
