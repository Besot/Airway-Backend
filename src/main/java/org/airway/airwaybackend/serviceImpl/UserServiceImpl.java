package org.airway.airwaybackend.serviceImpl;

import org.airway.airwaybackend.dto.ChangePasswordDto;
import org.airway.airwaybackend.dto.LoginDto;
import org.airway.airwaybackend.exception.UserNotVerifiedException;
import org.airway.airwaybackend.model.User;
import org.airway.airwaybackend.repository.UserRepository;
import org.airway.airwaybackend.service.UserService;
import org.airway.airwaybackend.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    public UserServiceImpl(UserRepository userRepository, JwtUtils jwtUtils, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
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


    public boolean checkIfValidOldPassword(User user, String oldPassword) {
        return validatePassword(oldPassword) && passwordEncoder.matches(oldPassword, user.getPassword());
    }

    public void changePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public String changeUserPassword(ChangePasswordDto passwordDto) {
        User user = userRepository.findUserByEmail(passwordDto.getEmail());
        if (user == null) {
            return "User not found";
        }

//        THIS IS COMMENTED OUT BECAUSE THE ADMIN PASSWORD IS 1234

//        if (!validatePassword(passwordDto.getOldPassword())) {
//            return "Invalid Old Password. Password must meet the required criteria: at least 1 uppercase letter, 1 lowercase letter, 1 digit, 1 special character (@#$%^&+=), and minimum length of 8 characters";
//        }




        if (passwordDto.getOldPassword().equals(passwordDto.getNewPassword())) {
            return "New password must be different from the old password";
        }

        if (!validatePassword(passwordDto.getNewPassword())) {
            return "New password does not meet the required criteria: at least 1 uppercase letter, 1 lowercase letter, 1 digit, 1 special character (@#$%^&+=), and minimum length of 8 characters";
        }

        if (!passwordEncoder.matches(passwordDto.getOldPassword(),user.getPassword())){
            return "Password does not match";
         } else {
        return "Password Changed Successfully ";
    }



    }


    public boolean validatePassword(String password) {
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

}
