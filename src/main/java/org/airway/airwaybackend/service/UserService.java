package org.airway.airwaybackend.service;

import org.airway.airwaybackend.dto.ChangePasswordDto;
import org.airway.airwaybackend.dto.LoginDto;
import org.airway.airwaybackend.model.User;

public interface UserService {
    String logInUser(LoginDto userDto);


    String changeUserPassword(ChangePasswordDto passwordDto);
}
