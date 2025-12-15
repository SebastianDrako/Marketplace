package com.uade.back.controller;

import com.uade.back.dto.user.UserUpdateDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.back.service.user.UserService;

import lombok.RequiredArgsConstructor;

/**
 * Controller for managing users.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Upgrades a user to admin role.
     *
     * @param userId The ID of the user.
     * @return A response entity with no content.
     */
    @PostMapping("/{userId}/upgrade")
    public ResponseEntity<Void> upgradeToAdmin(@PathVariable Integer userId) {
        userService.upgradeToAdmin(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Downgrades a user to standard user role.
     *
     * @param userId The ID of the user.
     * @return A response entity with no content.
     */
    @PostMapping("/{userId}/downgrade")
    public ResponseEntity<Void> downgradeToUser(@PathVariable Integer userId) {
        userService.downgradeToUser(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Updates the current user's profile.
     *
     * @param userUpdateDTO The new user profile details.
     * @return A response entity with no content.
     */
    @PutMapping("/me")
    public ResponseEntity<Void> updateUser(@RequestBody UserUpdateDTO userUpdateDTO) {
        userService.updateUser(userUpdateDTO);
        return ResponseEntity.noContent().build();
    }
}
