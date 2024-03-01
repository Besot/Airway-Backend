package org.airway.airwaybackend.serviceImpl;

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

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Email Not Found"));
    }

    public boolean checkIfValidOldPassword(User user, String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }

    public void changePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
