package com.uade.back.service.user;

import com.uade.back.dto.user.AdminUserUpdateDTO;
import com.uade.back.dto.user.UserUpdateDTO;
import com.uade.back.entity.UserInfo;
import com.uade.back.repository.UserInfoRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.uade.back.entity.Role;
import com.uade.back.entity.Usuario;
import com.uade.back.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of the UserService.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UsuarioRepository usuarioRepository;
    private final UserInfoRepository userInfoRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Upgrades a user to admin role.
     *
     * @param userId The ID of the user.
     * @throws RuntimeException if the user is not found.
     */
    @Override
    public void upgradeToAdmin(Integer userId) {
        Usuario user = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Update user role to ADMIN
        user.setAuthLevel(Role.ADMIN);
        usuarioRepository.save(user);
    }

    /**
     * Downgrades a user to standard user role.
     *
     * @param userId The ID of the user.
     * @throws RuntimeException if the user is not found.
     */
    @Override
    public void downgradeToUser(Integer userId) {
        Usuario user = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Update user role to USER
        user.setAuthLevel(Role.USER);
        usuarioRepository.save(user);
    }

    /**
     * Updates the current user's profile.
     *
     * @param userUpdateDTO The update details.
     */
    @Override
    public void updateUser(UserUpdateDTO userUpdateDTO) {
        // Retrieve current authenticated user
        Usuario user = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserInfo userInfo = user.getUserInfo();

        // Update fields if they are provided
        if (userUpdateDTO.getFirstName() != null) {
            userInfo.setFirstName(userUpdateDTO.getFirstName());
        }
        if (userUpdateDTO.getLastName() != null) {
            userInfo.setLastName(userUpdateDTO.getLastName());
        }
        if (userUpdateDTO.getMail() != null) {
            userInfo.setMail(userUpdateDTO.getMail());
        }

        userInfoRepository.save(userInfo);
    }

    /**
     * Updates a user's details by an admin.
     *
     * @param userId             The ID of the user.
     * @param adminUserUpdateDTO The admin update details.
     * @throws RuntimeException if the user is not found.
     */
    @Override
    public void adminUpdateUser(Integer userId, AdminUserUpdateDTO adminUserUpdateDTO) {
        Usuario user = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserInfo userInfo = user.getUserInfo();

        // Update UserInfo fields if provided
        if (adminUserUpdateDTO.getFirstName() != null) {
            userInfo.setFirstName(adminUserUpdateDTO.getFirstName());
        }
        if (adminUserUpdateDTO.getLastName() != null) {
            userInfo.setLastName(adminUserUpdateDTO.getLastName());
        }
        if (adminUserUpdateDTO.getMail() != null) {
            userInfo.setMail(adminUserUpdateDTO.getMail());
        }

        // Update User fields if provided
        if (adminUserUpdateDTO.getPassword() != null) {
            user.setPasskey(passwordEncoder.encode(adminUserUpdateDTO.getPassword()));
        }
        if (adminUserUpdateDTO.getIsEnabled() != null) {
            user.setActive(adminUserUpdateDTO.getIsEnabled());
        }
        if (adminUserUpdateDTO.getIsEmailConfirmed() != null) {
            userInfo.setConfirmMail(adminUserUpdateDTO.getIsEmailConfirmed());
        }

        userInfoRepository.save(userInfo);
        usuarioRepository.save(user);
    }

    /**
     * Retrieves all users.
     *
     * @return A list of all users.
     */
    @Override
    public java.util.List<com.uade.back.entity.Usuario> getAllUsers() {
        return usuarioRepository.findAll();
    }
}
