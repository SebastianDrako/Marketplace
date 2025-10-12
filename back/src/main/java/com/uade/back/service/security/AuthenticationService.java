package com.uade.back.service.security;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.back.dto.UserDTO;
import com.uade.back.dto.auth.AuthenticationRequest;
import com.uade.back.dto.auth.AuthenticationResponse;
import com.uade.back.dto.user.NewUserDTO;
import com.uade.back.dto.user.UpdateUserDTO;
import com.uade.back.entity.Otp;
import com.uade.back.entity.UserInfo;
import com.uade.back.entity.Usuario;
import com.uade.back.repository.OTPRepository;
import com.uade.back.repository.UserInfoRepository;
import com.uade.back.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

        private final UsuarioRepository usuarioRepository;
        private final UserInfoRepository userInfoRepository;
        private final OTPRepository otpRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final AuthenticationManager authenticationManager;


        private String otpGen(int n) {

        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder(n);

        for (int i = 0; i < n; i++) {
            int value = random.nextInt(caracteres.length());
            otp.append(caracteres.charAt(value));
        }
        return otp.toString();
        }

        @Transactional
        public AuthenticationResponse register(NewUserDTO info) {

                Optional<UserInfo> existingUser = userInfoRepository.findByMail(info.getMail());
                if (existingUser.isPresent()) {
                    throw new RuntimeException("Email ya registrado.");
                }
                
                UserInfo nuevoUsuarioInfo = UserInfo.builder()
                .confirmMail(false)
                .firstName(info.getFirstName())
                .lastName(info.getLastName()).mail(info.getMail()).build();

                UserInfo midui = userInfoRepository.save(nuevoUsuarioInfo);

                Otp nuevoOtp = Otp.builder().otp(this.otpGen(8)).timestamp(Instant.now()).build();

                Otp midotp = otpRepository.save(nuevoOtp);

                com.uade.back.entity.Role role = com.uade.back.entity.Role.USER;
                if (usuarioRepository.count() == 0) {
                    role = com.uade.back.entity.Role.ADMIN;
                }

                Usuario nuevoUsuario = Usuario.builder()
                .passkey(passwordEncoder.encode(info.getPasskey()))
                .otp(midotp)
                .authLevel(role)
                .active(true)
                .userInfo(midui)
                .build();

                usuarioRepository.save(nuevoUsuario);

                var jwtToken = jwtService.generateToken(nuevoUsuario);
                return AuthenticationResponse.builder()
                                .accessToken(jwtToken)
                                .build();
                }

        @Transactional
        public void activateAccount(Integer userId, String otpIngresado) {
            Usuario user = usuarioRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            if (user.getUserInfo().getConfirmMail()) {
                throw new RuntimeException("El correo ya ha sido confirmado.");
            }
            
            Otp otp = user.getOtp();
            if (otp == null || !otp.getOtp().equals(otpIngresado)) {
                throw new RuntimeException("OTP inválido.");
            }

            Instant now = Instant.now();
            Instant otpTimestamp = otp.getTimestamp();
            if (otpTimestamp.plus(5, ChronoUnit.MINUTES).isBefore(now)) {
                throw new RuntimeException("OTP expirado.");
            }

            UserInfo userInfo = user.getUserInfo();
            userInfo.setConfirmMail(true);
            userInfoRepository.save(userInfo);

            user.setOtp(null);
            usuarioRepository.save(user);
            otpRepository.delete(otp);
        }

        @Transactional
        public void updateUser(Integer userId, UpdateUserDTO request) {
            Usuario user = usuarioRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            UserInfo userInfo = user.getUserInfo();

            if (request.getFirstName() != null) {
                userInfo.setFirstName(request.getFirstName());
            }
            if (request.getLastName() != null) {
                userInfo.setLastName(request.getLastName());
            }

            if (request.getMail() != null && !request.getMail().equals(userInfo.getMail())) {
               
                userInfoRepository.findByMail(request.getMail()).ifPresent(u -> {
                    throw new RuntimeException("El nuevo email ya está en uso.");
                });
                
                userInfo.setMail(request.getMail());
                userInfo.setConfirmMail(false);

                
                if (user.getOtp() != null) {
                    otpRepository.delete(user.getOtp());
                }

               
                Otp newOtp = Otp.builder().otp(this.otpGen(8)).timestamp(Instant.now()).build();
                Otp savedOtp = otpRepository.save(newOtp);
                user.setOtp(savedOtp);
                usuarioRepository.save(user);
            }
            
            userInfoRepository.save(userInfo);
        }

        @Transactional
        public void deleteUser(Integer userId) {
            Usuario user = usuarioRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            user.setActive(false);
            usuarioRepository.save(user);
        }

        public AuthenticationResponse authenticate(AuthenticationRequest request) {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getEmail(),
                                                request.getPassword()));

                var userInfo = userInfoRepository.findByMail(request.getEmail())
                                .orElseThrow();

                var user = usuarioRepository.findByUserInfo_UserInfoId(userInfo.getUserInfoId())
                                .orElseThrow();

                var jwtToken = jwtService.generateToken(user);
                return AuthenticationResponse.builder()
                                .accessToken(jwtToken)
                                .build();
        }

        public UserDTO getMe() {
                var userContext = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                var user = usuarioRepository.findById(userContext.getId()).orElseThrow();
                var userInfo = userInfoRepository.findById(user.getUserInfo().getUserInfoId()).orElseThrow();

                return UserDTO.builder()
                        .id(user.getId().toString())
                        .name(userInfo.getFirstName() + " " + userInfo.getLastName())
                        .email(userInfo.getMail())
                        .roles(List.of(user.getAuthLevel().name()))
                        .build();
        }
}
