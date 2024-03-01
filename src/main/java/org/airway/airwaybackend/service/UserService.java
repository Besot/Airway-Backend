package org.airway.airwaybackend.service;

import org.airway.airwaybackend.dto.LoginDto;
import org.airway.airwaybackend.model.User;

public interface UserService {
    String logInUser(LoginDto userDto);


}
