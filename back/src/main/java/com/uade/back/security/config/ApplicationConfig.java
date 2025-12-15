package com.uade.back.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.uade.back.repository.UserInfoRepository;
import com.uade.back.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

/**
 * Configuration class for application security beans.
 */
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
    
    private final UserInfoRepository userInfoRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Creates a UserDetailsService bean.
     *
     * @return The UserDetailsService.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            var userInfo = userInfoRepository.findByMail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

            return usuarioRepository.findByUserInfo_UserInfoId(userInfo.getUserInfoId())
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        };
    }

    /**
     * Creates an AuthenticationProvider bean.
     *
     * @return The AuthenticationProvider.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    /**
     * Creates an AuthenticationManager bean.
     *
     * @param config The authentication configuration.
     * @return The AuthenticationManager.
     * @throws Exception if an error occurs.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Creates a PasswordEncoder bean.
     *
     * @return The PasswordEncoder (BCrypt).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
