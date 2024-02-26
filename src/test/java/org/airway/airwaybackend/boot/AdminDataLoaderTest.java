package org.airway.airwaybackend.boot;

import org.airway.airwaybackend.config.SeedProperties;
import org.airway.airwaybackend.enums.Role;
import org.airway.airwaybackend.model.User;
import org.airway.airwaybackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


    class AdminDataLoaderTest {
        @Mock
        private SeedProperties seedProperties;
        @Mock
        private PasswordEncoder passwordEncoder;
        @Mock
        private UserRepository userRepository;
        @Mock
        private List<User> adminList;
        @InjectMocks
        private AdminDataLoader adminDataLoader;

        @BeforeEach
        public void setup() {
            MockitoAnnotations.openMocks(this);
        }

        @Test
        public void testSeedAdmin(){
            adminList = Arrays.asList(
                    new User("Desmond", "Isama", "isamadesmond@gmail.com", "09030797493", passwordEncoder.encode("1234"), Role.ADMIN, true)
            );
            when(userRepository.findUserByUserRole(Role.ADMIN)).thenReturn(adminList);

            adminDataLoader.seedAdmin();

            verify(userRepository, times(4)).save(any(User.class));
        }

    }
