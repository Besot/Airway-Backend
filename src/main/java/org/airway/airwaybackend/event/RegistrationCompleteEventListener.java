package org.airway.airwaybackend.event;
import lombok.extern.slf4j.Slf4j;
import org.airway.airwaybackend.model.User;
import org.airway.airwaybackend.serviceImpl.EmailServiceImpl;
import org.airway.airwaybackend.serviceImpl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {
    private final UserServiceImpl userService;

    private final EmailServiceImpl emailService;
    @Autowired
    public RegistrationCompleteEventListener(UserServiceImpl userService,
                                             EmailServiceImpl emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {

        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.saveVerificationTokenForUser(user, token);

        String url = event.getApplicationUrl() + "/verifyRegistration?token=" + token;

        log.info("Click the link to verify your account: {}", url);
        if (user.getUsername()!=null){
            emailService.sendMail(user.getEmail(), "Verification Token Sent" ,"Click on the verification link to verify your account:" + url);
        }
        else {
            log.error("User's email address is null. Unable to send verification email.");
        }
    }
}
