package org.airway.airwaybackend.serviceImpl;

import jakarta.servlet.http.HttpServletRequest;
import org.airway.airwaybackend.dto.EmailSenderDto;
import org.airway.airwaybackend.dto.LoginDto;
import org.airway.airwaybackend.dto.ResetPasswordDto;
import org.airway.airwaybackend.exception.PasswordsDontMatchException;
import org.airway.airwaybackend.exception.UserNotVerifiedException;
import org.airway.airwaybackend.model.PasswordResetToken;
import org.airway.airwaybackend.model.User;
import org.airway.airwaybackend.repository.PasswordResetTokenRepository;
import org.airway.airwaybackend.repository.UserRepository;
import org.airway.airwaybackend.service.UserService;
import org.airway.airwaybackend.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailServiceImpl emailService;
    @Autowired
    public UserServiceImpl(UserRepository userRepository, JwtUtils jwtUtils, PasswordEncoder passwordEncoder, PasswordResetTokenRepository passwordResetTokenRepository, EmailServiceImpl emailService) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailService = emailService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email not Found"));
    }

    @Override
    public String logInUser(LoginDto userDto) {
        UserDetails user = loadUserByUsername(userDto.getEmail());

        if (!user.isEnabled()) {
            throw new UserNotVerifiedException("User is not verified, check email to Verify Registration");
        }

        if (!passwordEncoder.matches(userDto.getPassword(), user.getPassword())) {
            throw new UserNotVerifiedException("Username and Password is Incorrect");
        }

        return jwtUtils.createJwt.apply(user);
    }

    public User findUserByEmail(String username) {

        return userRepository.findByEmail(username).orElseThrow(() -> new RuntimeException("Username Not Found" + username));
    }
    @Override
    public void createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken newlyCreatedPasswordResetToken = new PasswordResetToken(user, token);
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByUserId(user.getId());
        if(passwordResetToken != null){
            passwordResetTokenRepository.delete(passwordResetToken);
            passwordResetTokenRepository.save(newlyCreatedPasswordResetToken);
        }
        passwordResetTokenRepository.save(newlyCreatedPasswordResetToken);
    }


    @Override
    public String resetPasswordMailSender(EmailSenderDto passwordDto, HttpServletRequest request) {
        User user = findUserByEmail(passwordDto.getEmail());
        String url = "";

        if (user != null){
            String token = UUID.randomUUID().toString();
            createPasswordResetTokenForUser(user, token);
            url =emailService.passwordResetTokenMail(user, emailService.applicationUrl(request), token);
        }
        return url;
    }
}
