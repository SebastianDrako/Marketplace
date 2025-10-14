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

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UsuarioRepository usuarioRepository;
    private final UserInfoRepository userInfoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void upgradeToAdmin(Integer userId) {
        Usuario user = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setAuthLevel(Role.ADMIN);
        usuarioRepository.save(user);
    }

    @Override
    public void downgradeToUser(Integer userId) {
        Usuario user = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setAuthLevel(Role.USER);
        usuarioRepository.save(user);
    }

    @Override
    public void updateUser(UserUpdateDTO userUpdateDTO) {
        Usuario user = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserInfo userInfo = user.getUserInfo();

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

    @Override
    public void adminUpdateUser(Integer userId, AdminUserUpdateDTO adminUserUpdateDTO) {
        Usuario user = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserInfo userInfo = user.getUserInfo();

        if (adminUserUpdateDTO.getFirstName() != null) {
            userInfo.setFirstName(adminUserUpdateDTO.getFirstName());
        }
        if (adminUserUpdateDTO.getLastName() != null) {
            userInfo.setLastName(adminUserUpdateDTO.getLastName());
        }
        if (adminUserUpdateDTO.getMail() != null) {
            userInfo.setMail(adminUserUpdateDTO.getMail());
        }
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

    @Override
    public java.util.List<com.uade.back.entity.Usuario> getAllUsers() {
        return usuarioRepository.findAll();
    }
}
