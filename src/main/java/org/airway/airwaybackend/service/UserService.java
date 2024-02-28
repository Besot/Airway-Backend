package org.airway.airwaybackend.service;

import org.airway.airwaybackend.dto.LoginDto;

public interface UserService {
    String logInUser(LoginDto userDto);
}
