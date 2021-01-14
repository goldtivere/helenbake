package com.helenbake.helenbake.controller;

import com.helenbake.helenbake.domain.User;
import com.helenbake.helenbake.security.ProfileDetails;
import com.helenbake.helenbake.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping(value = "/me")
    public ResponseEntity<User> getUserInSession(@AuthenticationPrincipal ProfileDetails profileDetails) {
        return ResponseEntity.ok(profileDetails.toUser());
    }
    
}
