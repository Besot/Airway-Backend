package org.airway.airwaybackend.controller;


import org.airway.airwaybackend.dto.LoginDto;
import org.airway.airwaybackend.serviceImpl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserServiceImpl userService;
    @Autowired
    public AuthController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto){
String result = userService.logInUser(loginDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


}
