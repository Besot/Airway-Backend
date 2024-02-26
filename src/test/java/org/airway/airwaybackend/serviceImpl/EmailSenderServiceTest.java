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
        String toEmail = "anoruehappiness@gmail.com";
        String body = "Test email body";
        String subject = "Test subject";

        // Calling the actual method to be tested
        emailSenderService.sendSimpleEmail(toEmail, body, subject);

        // Verifying that the mock JavaMailSender is called
        verify(mockMailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}

/*import org.airway.airwaybackend.serviceImpl.EmailSenderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;


@SpringBootTest
public class EmailSenderServiceTest {

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private JavaMailSender javaMailSender;

    @Test
    public void testSendSimpleEmail() {
        String toEmail = "goodluckanorue001@gmail.com";
        String subject = "Test Subject";
        String body = "This is a test email body.";

        emailSenderService.sendSimpleEmail(toEmail, body, subject);

        // Optionally, you can add assertions or logging to verify the email was sent.
    }
}
*/
