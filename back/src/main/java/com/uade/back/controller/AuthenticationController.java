package com.uade.back.controller;

import com.uade.back.dto.auth.AccountActivationDTO;
import com.uade.back.dto.auth.PasswordChangeDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.back.dto.UserDTO;
import com.uade.back.dto.auth.AuthenticationRequest;
import com.uade.back.dto.auth.AuthenticationResponse;
import com.uade.back.dto.user.NewUserDTO;
import com.uade.back.service.security.AuthenticationService;

import lombok.RequiredArgsConstructor;

/**
 * Controller for user authentication and account management.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    /**
     * Registers a new user.
     *
     * @param request The registration details.
     * @return The authentication response containing the token.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody NewUserDTO request) {
        return ResponseEntity.ok(service.register(request));
    }

    /**
     * Authenticates a user.
     *
     * @param request The authentication credentials.
     * @return The authentication response containing the token.
     */
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    /**
     * Retrieves the current authenticated user's details.
     *
     * @return The user details.
     */
    @GetMapping("/me")
    public ResponseEntity<UserDTO> me() {
        return ResponseEntity.ok(service.getMe());
    }

    /**
     * Changes the password for the current user.
     *
     * @param request The password change request containing old and new passwords.
     * @return A response entity with no content.
     */
    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody PasswordChangeDTO request) {
        service.changePassword(request);
        return ResponseEntity.noContent().build();
    }

    /**
     * Activates a user account by email.
     *
     * @param request The account activation details.
     * @return A response entity with no content.
     */
    @PostMapping("/activate")
    public ResponseEntity<Void> activateAccount(@RequestBody AccountActivationDTO request) {
        service.activateAccountByEmail(request);
        return ResponseEntity.noContent().build();
    }
}
