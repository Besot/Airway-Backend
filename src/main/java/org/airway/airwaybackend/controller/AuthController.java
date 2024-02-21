package org.airway.airwaybackend.controller;


import org.airway.airwaybackend.utils.GoogleJwtUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/auth")
public class AuthController {
    private final GoogleJwtUtils googleJwtUtils;

    public AuthController(GoogleJwtUtils googleJwtUtils) {
        this.googleJwtUtils = googleJwtUtils;
    }

    @GetMapping("/google/{tkn}")
    public ResponseEntity<String> authorizeOauthUser(@PathVariable("tkn") String token){
        return ResponseEntity.ok(googleJwtUtils.googleOauthUserJWT(token));
    }

}
