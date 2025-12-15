package com.uade.back.service.user;

import com.uade.back.dto.user.AdminUserUpdateDTO;
import com.uade.back.dto.user.UserUpdateDTO;
import com.uade.back.entity.Usuario;

import java.util.List;

/**
 * Service interface for managing users.
 */
public interface UserService {
    /**
     * Upgrades a user to admin role.
     *
     * @param userId The ID of the user.
     */
    void upgradeToAdmin(Integer userId);

    /**
     * Downgrades a user to standard user role.
     *
     * @param userId The ID of the user.
     */
    void downgradeToUser(Integer userId);

    /**
     * Updates the current user's profile.
     *
     * @param userUpdateDTO The update details.
     */
    void updateUser(UserUpdateDTO userUpdateDTO);

    /**
     * Updates a user's details by an admin.
     *
     * @param userId             The ID of the user.
     * @param adminUserUpdateDTO The admin update details.
     */
    void adminUpdateUser(Integer userId, AdminUserUpdateDTO adminUserUpdateDTO);

    /**
     * Retrieves all users.
     *
     * @return A list of all users.
     */
    List<Usuario> getAllUsers();
}
