package com.uade.back.security.config;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import com.uade.back.entity.Role;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Security configuration for the application.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    /**
     * Configures the security filter chain.
     *
     * @param http The HttpSecurity object.
     * @return The configured SecurityFilterChain.
     * @throws Exception if an error occurs.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
        throws Exception {
        http
            .cors(withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(req ->
                req
                    // Public endpoints
                    .requestMatchers(
                        "/api/v1/auth/register",
                        "/api/v1/auth/authenticate",
                        "/api/v1/auth/activate"
                    )
                    .permitAll()
                    .requestMatchers(
                        HttpMethod.GET,
                        "/api/v1/products",
                        "/api/v1/categories/tree",
                        "/api/v1/images/{imageId}"
                    )
                    .permitAll()
                    .requestMatchers("/health", "/error/**")
                    .permitAll()
                    .requestMatchers(
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/webjars/**"
                    )
                    .permitAll()
                    // User endpoints
                    .requestMatchers(
                        "/api/v1/auth/me",
                        "/api/v1/auth/change-password"
                    )
                    .hasAnyAuthority(Role.USER.name(), Role.ADMIN.name())
                    .requestMatchers(
                        HttpMethod.GET,
                        "/api/v1/products/{productId}",
                        "/api/v1/categories/byid/{categoryId}"
                    )
                    .hasAnyAuthority(Role.USER.name(), Role.ADMIN.name())
                    .requestMatchers("/api/v1/cart/**")
                    .hasAnyAuthority(Role.USER.name(), Role.ADMIN.name())
                    .requestMatchers("/api/v1/addresses/**")
                    .hasAnyAuthority(Role.USER.name(), Role.ADMIN.name())
                    .requestMatchers(HttpMethod.POST, "/api/v1/orders")
                    .hasAnyAuthority(Role.USER.name(), Role.ADMIN.name())
                    .requestMatchers(HttpMethod.GET, "/api/v1/orders/my-orders")
                    .hasAnyAuthority(Role.USER.name(), Role.ADMIN.name())
                    .requestMatchers(
                        HttpMethod.POST,
                        "/api/v1/orders/{orderId}/retry-payment"
                    )
                    .hasAnyAuthority(Role.USER.name(), Role.ADMIN.name())
                    .requestMatchers(HttpMethod.PUT, "/api/v1/users/me")
                    .hasAnyAuthority(Role.USER.name(), Role.ADMIN.name())
                    // Admin endpoints
                    .requestMatchers("/api/v1/admin/**")
                    .hasAuthority(Role.ADMIN.name())
                    .requestMatchers(
                        "/api/v1/users/{userId}/upgrade",
                        "/api/v1/users/{userId}/downgrade"
                    )
                    .hasAuthority(Role.ADMIN.name())
                    .requestMatchers(HttpMethod.POST, "/api/v1/products/**")
                    .hasAuthority(Role.ADMIN.name())
                    .requestMatchers(HttpMethod.PUT, "/api/v1/products/**")
                    .hasAuthority(Role.ADMIN.name())
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/products/**")
                    .hasAuthority(Role.ADMIN.name())
                    .requestMatchers(HttpMethod.POST, "/api/v1/categories")
                    .hasAuthority(Role.ADMIN.name())
                    .requestMatchers(HttpMethod.PUT, "/api/v1/categories")
                    .hasAuthority(Role.ADMIN.name())
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/categories/**")
                    .hasAuthority(Role.ADMIN.name())
                    .requestMatchers(HttpMethod.POST, "/api/v1/images/upload")
                    .hasAuthority(Role.ADMIN.name())
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/images")
                    .hasAuthority(Role.ADMIN.name())
                    .requestMatchers(HttpMethod.POST, "/api/v1/cupones")
                    .hasAuthority(Role.ADMIN.name())
                    .requestMatchers(HttpMethod.GET, "/api/v1/cupones")
                    .hasAuthority(Role.ADMIN.name())
                    .requestMatchers(HttpMethod.PUT, "/api/v1/cupones/**")
                    .hasAuthority(Role.ADMIN.name())
                    .requestMatchers(
                        HttpMethod.PATCH,
                        "/api/v1/orders/{orderId}/delivery-status"
                    )
                    .hasAuthority(Role.ADMIN.name())
                    .requestMatchers(
                        HttpMethod.PATCH,
                        "/api/v1/orders/payment/{paymentId}"
                    )
                    .hasAuthority(Role.ADMIN.name())
                    .anyRequest()
                    .authenticated()
            )
            .sessionManagement(session ->
                session.sessionCreationPolicy(STATELESS)
            )
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(
                jwtAuthFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    /**
     * Configures CORS.
     *
     * @return The CorsConfigurationSource.
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
