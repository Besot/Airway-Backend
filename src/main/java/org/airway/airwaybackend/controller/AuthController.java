package org.airway.airwaybackend.controller;


import jakarta.servlet.http.HttpServletRequest;
import org.airway.airwaybackend.dto.EmailSenderDto;
import org.airway.airwaybackend.dto.LoginDto;
import org.airway.airwaybackend.dto.ResetPasswordDto;
import org.airway.airwaybackend.exception.InvalidTokenException;
import org.airway.airwaybackend.model.User;
import org.airway.airwaybackend.serviceImpl.EmailServiceImpl;
import org.airway.airwaybackend.serviceImpl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserServiceImpl userService;
    private  final EmailServiceImpl emailService;
    @Autowired
    public AuthController(UserServiceImpl userService, EmailServiceImpl emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto){
String result = userService.logInUser(loginDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/resetPasswordMail")
    public ResponseEntity<String> resetPasswordMail(@RequestBody EmailSenderDto passwordDto, HttpServletRequest request){
        User user = userService.findUserByEmail(passwordDto.getEmail());
        String url = "";

        if (user != null){
            String token = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user, token);
            url =emailService.passwordResetTokenMail(user, emailService.applicationUrl(request), token);
        }
        return new ResponseEntity<>("Go to your mail to reset your password" + url, HttpStatus.OK);

    }

    @PostMapping("/resetPassword/{token}")
    public ResponseEntity<String> resetPassword(@PathVariable String token, @RequestBody ResetPasswordDto passwordDto) {
        String result = userService.validatePasswordResetToken(token, passwordDto);
        if (!result.equalsIgnoreCase("valid")) {
            throw new InvalidTokenException("Invalid Token");
        }
        Optional<User> user = userService.getUserByPasswordReset(token);
        if (user.isPresent()) {
            userService.changePassword(user.get(), passwordDto.getPassword(), passwordDto.getConfirmPassword());
            return new ResponseEntity<>("Password Reset Successful", HttpStatus.OK);
        } else {
            throw new InvalidTokenException("Invalid Token");
        }
    }

}
