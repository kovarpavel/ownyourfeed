package com.kovarpavel.ownyourfeed.rest;

import com.kovarpavel.ownyourfeed.dto.UserRegistrationDTO;
import com.kovarpavel.ownyourfeed.service.AuthenticationService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public String registerUser(@RequestBody UserRegistrationDTO userRegistrationDTO) {
        authenticationService.registerUser(userRegistrationDTO);
        return "User Registered.";
    }

    @PostMapping("/token")
    public String getToken(Authentication authentication) {
        return authenticationService.generateToken(authentication);
    }


}
