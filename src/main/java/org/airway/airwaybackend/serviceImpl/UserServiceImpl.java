package org.airway.airwaybackend.serviceImpl;

import jakarta.servlet.http.HttpServletRequest;
import org.airway.airwaybackend.dto.EmailSenderDto;
import org.airway.airwaybackend.dto.LoginDto;
import org.airway.airwaybackend.dto.ResetPasswordDto;
import org.airway.airwaybackend.dto.SignupDto;
import org.airway.airwaybackend.enums.Role;
import org.airway.airwaybackend.exception.PasswordsDontMatchException;
import org.airway.airwaybackend.exception.UserNotVerifiedException;
import org.airway.airwaybackend.model.PasswordResetToken;
import org.airway.airwaybackend.model.User;
import org.airway.airwaybackend.model.VerificationToken;
import org.airway.airwaybackend.repository.PasswordResetTokenRepository;
import org.airway.airwaybackend.repository.UserRepository;
import org.airway.airwaybackend.repository.VerificationTokenRepository;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailServiceImpl emailService;
    private final VerificationTokenRepository verificationTokenRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, JwtUtils jwtUtils, PasswordEncoder passwordEncoder, PasswordResetTokenRepository passwordResetTokenRepository, EmailServiceImpl emailService, VerificationTokenRepository verificationTokenRepository) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailService = emailService;
        this.verificationTokenRepository = verificationTokenRepository;

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
        }
        passwordResetTokenRepository.save(newlyCreatedPasswordResetToken);
    }


    @Override
    public void forgotPassword(EmailSenderDto passwordDto, HttpServletRequest request) {
        User user = findUserByEmail(passwordDto.getEmail());
        if (user == null) {
            throw new UsernameNotFoundException("User with email " + passwordDto.getEmail() + " not found");
        }
            String token = UUID.randomUUID().toString();
            createPasswordResetTokenForUser(user, token);
            emailService.passwordResetTokenMail(user, emailService.applicationUrl(request), token);
    }

    @Override
    public User saveUser(SignupDto signupDto) {
        if (userRepository.existsByEmail(signupDto.getEmail())) {
            throw new RuntimeException("Email is already taken, try Logging In or Signup with another email" );
        }
        User user = new User();

        if (!signupDto.getPassword().equals (signupDto.getConfirmPassword())){
            throw new RuntimeException("Passwords are not the same");
        }
        if (!validatePassword(signupDto.getPassword())) {
            throw new RuntimeException("Password does not meet the required criteria");
        }

        user.setPassword(passwordEncoder.encode(signupDto.getPassword()));
        user.setConfirmPassword(passwordEncoder.encode(signupDto.getConfirmPassword()));
        user.setFirstName(signupDto.getFirstName());
        user.setLastName(signupDto.getLastName());
        user.setPassportNumber(signupDto.getPhoneNumber());
        user.setEmail(signupDto.getEmail());
        user.setUserRole(Role.PASSENGER);
        return userRepository.save(user);
    }
    public boolean validatePassword(String password){
        String capitalLetterPattern = "(?=.*[A-Z])";
        String lowercaseLetterPattern = "(?=.*[a-z])";
        String digitPattern = "(?=.*\\d)";
        String symbolPattern = "(?=.*[@#$%^&+=])";
        String lengthPattern = ".{8,}";

        String regex = capitalLetterPattern + lowercaseLetterPattern + digitPattern + symbolPattern + lengthPattern;

        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(password);

        return matcher.matches();

    }
    public void saveVerificationTokenForUser(User user, String token) {
        VerificationToken verificationToken = new VerificationToken(user, token);
        verificationTokenRepository.save(verificationToken);

    }
    public String validateVerificationToken(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        if (verificationToken == null){
            return "invalid";
        }
        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpirationTime().getTime()
                - cal.getTime().getTime()) <=0) {
            verificationTokenRepository.delete(verificationToken);
            return "expired";
        }
        user.setIsEnabled(true);
        userRepository.save(user);
        return "valid";
    }

    public VerificationToken generateNewVerificationToken(String oldToken) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(oldToken);
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationTokenRepository.save(verificationToken);
        return verificationToken;
    }
}
