package org.airway.airwaybackend.boot;

import jakarta.annotation.PostConstruct;
import org.airway.airwaybackend.config.SeedProperties;
import org.airway.airwaybackend.enums.Role;
import org.airway.airwaybackend.model.User;
import org.airway.airwaybackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

    @Service
    public class AdminDataLoader {
        private UserRepository userRepository;
        private SeedProperties seedProperties;
        private PasswordEncoder passwordEncoder;

        @Autowired
        public AdminDataLoader(UserRepository userRepository, SeedProperties seedProperties, PasswordEncoder passwordEncoder) {
            this.userRepository = userRepository;
            this.seedProperties = seedProperties;
            this.passwordEncoder = passwordEncoder;
        }
        private  List<User> adminList;

        @PostConstruct
        private void seedProperties(){
            if (seedProperties.isEnabled()){
                seedAdmin();
            }
        }

        public void seedAdmin(){
            adminList = userRepository.findUserByUserRole(Role.ADMIN);
            List<User> adminData = Arrays.asList(
                    new User("Desmond", "Isama", "isamadesmond@gmail.com", "09030797493", passwordEncoder.encode("1234"), Role.ADMIN, true),
                    new User("James", "Adedini", "jamesadedini@gmail.com", "09030797493", passwordEncoder.encode("1234"), Role.ADMIN, true),
                    new User("Michael", "Sotunde", "michaelsotunde@gmail.com", "09030797493", passwordEncoder.encode("1234"), Role.ADMIN, true),
                    new User("Silas", "Bush", "silasbush@gmail.com", "09030797493", passwordEncoder.encode("1234"), Role.ADMIN, true),
                    new User("Confidence", "Obieshika", "confidenceobieshika@gmail.com", "09030797493", passwordEncoder.encode("1234"), Role.ADMIN, true));

            adminData.stream()
                    .filter(user -> !containsEmail(adminList, user.getEmail()))
                    .forEach(user -> userRepository.save(user));
        }

        private boolean containsEmail(List<User> ListOfAdmin, String email){
            return ListOfAdmin.stream()
                    .anyMatch(user -> user.getEmail().equals(email));
        }

    }
