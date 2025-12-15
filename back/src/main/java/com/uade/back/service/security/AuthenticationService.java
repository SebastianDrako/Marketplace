package com.uade.back.service.security;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import com.uade.back.dto.OtpDTO;
import com.uade.back.dto.auth.AccountActivationDTO;
import com.uade.back.dto.auth.PasswordChangeDTO;
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

/**
 * Service for handling user authentication, registration, and account management.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {

        private final UsuarioRepository usuarioRepository;
        private final UserInfoRepository userInfoRepository;
        private final OTPRepository otpRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final AuthenticationManager authenticationManager;


        /**
         * Generates a random OTP string.
         *
         * @param n The length of the OTP.
         * @return The generated OTP string.
         */
        private String otpGen(int n) {

        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder(n);

        // Generate a random string of length n from the characters defined above
        for (int i = 0; i < n; i++) {
            int value = random.nextInt(caracteres.length());
            otp.append(caracteres.charAt(value));
        }
        return otp.toString();
        }

        /**
         * Registers a new user.
         *
         * @param info The new user information.
         * @return The authentication response containing the access token.
         * @throws RuntimeException if the email is already registered.
         */
        @Transactional
        public AuthenticationResponse register(NewUserDTO info) {

                // Check if the email is already in use
                Optional<UserInfo> existingUser = userInfoRepository.findByMail(info.getMail());
                if (existingUser.isPresent()) {
                    throw new RuntimeException("Email ya registrado.");
                }
                
                // Create UserInfo entity
                UserInfo nuevoUsuarioInfo = UserInfo.builder()
                .confirmMail(false)
                .firstName(info.getFirstName())
                .lastName(info.getLastName()).mail(info.getMail()).build();

                UserInfo midui = userInfoRepository.save(nuevoUsuarioInfo);

                // Generate and save OTP
                Otp nuevoOtp = Otp.builder().otp(this.otpGen(8)).timestamp(Instant.now()).build();

                Otp midotp = otpRepository.save(nuevoOtp);

                // Determine role: First user is ADMIN, others are USER
                com.uade.back.entity.Role role = com.uade.back.entity.Role.USER;
                if (usuarioRepository.count() == 0) {
                    role = com.uade.back.entity.Role.ADMIN;
                }

                // Create User entity with encoded password
                Usuario nuevoUsuario = Usuario.builder()
                .passkey(passwordEncoder.encode(info.getPasskey()))
                .otp(midotp)
                .authLevel(role)
                .active(true)
                .userInfo(midui)
                .build();

                usuarioRepository.save(nuevoUsuario);

                // Generate JWT token
                var jwtToken = jwtService.generateToken(nuevoUsuario);
                return AuthenticationResponse.builder()
                                .accessToken(jwtToken)
                                .build();
                }

        /**
         * Activates a user account using an OTP.
         *
         * @param userId       The ID of the user.
         * @param otpIngresado The OTP provided by the user.
         * @throws RuntimeException if the user is not found, email is already confirmed, or OTP is invalid/expired.
         */
        @Transactional
        public void activateAccount(Integer userId, String otpIngresado) {
            Usuario user = usuarioRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Check if email is already confirmed
            if (user.getUserInfo().getConfirmMail()) {
                throw new RuntimeException("El correo ya ha sido confirmado.");
            }
            
            Otp otp = user.getOtp();
            // Validate OTP existence and match
            if (otp == null || !otp.getOtp().equals(otpIngresado)) {
                throw new RuntimeException("OTP inválido.");
            }

            // Validate OTP expiration (5 minutes)
            Instant now = Instant.now();
            Instant otpTimestamp = otp.getTimestamp();
            if (otpTimestamp.plus(5, ChronoUnit.MINUTES).isBefore(now)) {
                throw new RuntimeException("OTP expirado.");
            }

            // Mark email as confirmed
            UserInfo userInfo = user.getUserInfo();
            userInfo.setConfirmMail(true);
            userInfoRepository.save(userInfo);

            // Clear OTP from user and delete from repository
            user.setOtp(null);
            usuarioRepository.save(user);
            otpRepository.delete(otp);
        }

        /**
         * Updates user information.
         *
         * @param userId  The ID of the user.
         * @param request The update details.
         * @throws RuntimeException if the user is not found or the new email is already in use.
         */
        @Transactional
        public void updateUser(Integer userId, UpdateUserDTO request) {
            Usuario user = usuarioRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            UserInfo userInfo = user.getUserInfo();

            // Update fields if provided
            if (request.getFirstName() != null) {
                userInfo.setFirstName(request.getFirstName());
            }
            if (request.getLastName() != null) {
                userInfo.setLastName(request.getLastName());
            }

            // Handle email change logic
            if (request.getMail() != null && !request.getMail().equals(userInfo.getMail())) {
               
                // Check if new email is already taken
                userInfoRepository.findByMail(request.getMail()).ifPresent(u -> {
                    throw new RuntimeException("El nuevo email ya está en uso.");
                });
                
                userInfo.setMail(request.getMail());
                userInfo.setConfirmMail(false);

                
                // Clear existing OTP if any
                if (user.getOtp() != null) {
                    otpRepository.delete(user.getOtp());
                }

               
                // Generate new OTP for new email verification
                Otp newOtp = Otp.builder().otp(this.otpGen(8)).timestamp(Instant.now()).build();
                Otp savedOtp = otpRepository.save(newOtp);
                user.setOtp(savedOtp);
                usuarioRepository.save(user);
            }
            
            userInfoRepository.save(userInfo);
        }

        /**
         * Deactivates a user (soft delete).
         *
         * @param userId The ID of the user.
         * @throws RuntimeException if the user is not found.
         */
        @Transactional
        public void deleteUser(Integer userId) {
            Usuario user = usuarioRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            user.setActive(false);
            usuarioRepository.save(user);
        }

        /**
         * Authenticates a user.
         *
         * @param request The authentication credentials.
         * @return The authentication response containing the access token.
         */
        public AuthenticationResponse authenticate(AuthenticationRequest request) {
                // Delegate authentication to AuthenticationManager
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getEmail(),
                                                request.getPassword()));

                // Fetch user info and user entity to generate token
                var userInfo = userInfoRepository.findByMail(request.getEmail())
                                .orElseThrow();

                var user = usuarioRepository.findByUserInfo_UserInfoId(userInfo.getUserInfoId())
                                .orElseThrow();

                var jwtToken = jwtService.generateToken(user);
                return AuthenticationResponse.builder()
                                .accessToken(jwtToken)
                                .build();
        }

        /**
         * Retrieves the current authenticated user's details.
         *
         * @return The UserDTO.
         */
        public UserDTO getMe() {
                // Get current user from SecurityContext
                var userContext = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                var user = usuarioRepository.findById(userContext.getUser_ID()).orElseThrow();
                var userInfo = userInfoRepository.findById(user.getUserInfo().getUserInfoId()).orElseThrow();

                return UserDTO.builder()
                        .id(user.getUser_ID().toString())
                        .name(userInfo.getFirstName() + " " + userInfo.getLastName())
                        .email(userInfo.getMail())
                        .roles(List.of(user.getAuthLevel().name()))
                        .build();
        }

    /**
     * Changes the current user's password.
     *
     * @param passwordChangeDTO The password change details.
     * @throws RuntimeException if the old password is invalid.
     */
    public void changePassword(PasswordChangeDTO passwordChangeDTO) {
        Usuario user = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Verify old password
        if (!passwordEncoder.matches(passwordChangeDTO.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid old password");
        }

        // Encode and save new password
        user.setPasskey(passwordEncoder.encode(passwordChangeDTO.getNewPassword()));
        usuarioRepository.save(user);
    }

    /**
     * Retrieves the OTP for a user (admin).
     *
     * @param userId The ID of the user.
     * @return The OtpDTO containing the OTP.
     * @throws RuntimeException if the user is not found.
     */
    public OtpDTO adminCheckOtp(Integer userId) {
        Usuario user = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getOtp() == null) {
            return new OtpDTO("No OTP found");
        }
        return new OtpDTO(user.getOtp().getOtp());
    }

    /**
     * Regenerates the OTP for a user (admin).
     *
     * @param userId The ID of the user.
     * @return The OtpDTO containing the new OTP.
     * @throws RuntimeException if the user is not found.
     */
    @Transactional
    public OtpDTO adminRegenerateOtp(Integer userId) {
        Usuario user = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Remove old OTP if exists
        if (user.getOtp() != null) {
            otpRepository.delete(user.getOtp());
        }

        // Generate and save new OTP
        Otp newOtp = Otp.builder().otp(this.otpGen(8)).timestamp(Instant.now()).build();
        Otp savedOtp = otpRepository.save(newOtp);
        user.setOtp(savedOtp);
        usuarioRepository.save(user);

        return new OtpDTO(savedOtp.getOtp());
    }

    /**
     * Activates an account by email and OTP.
     *
     * @param accountActivationDTO The activation details.
     * @throws RuntimeException if the user is not found.
     */
    @Transactional
    public void activateAccountByEmail(AccountActivationDTO accountActivationDTO) {
        // Find user by email
        UserInfo userInfo = userInfoRepository.findByMail(accountActivationDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Usuario user = usuarioRepository.findByUserInfo_UserInfoId(userInfo.getUserInfoId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Call activation logic
        activateAccount(user.getUser_ID(), accountActivationDTO.getOtp());
    }
}
