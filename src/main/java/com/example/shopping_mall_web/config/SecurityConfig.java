package com.example.shopping_mall_web.config;

import com.example.shopping_mall_web.jwt.JwtRequestFilter;
import com.example.shopping_mall_web.oauth2user.OAuth2Service;
import com.example.shopping_mall_web.oauth2user.OAuth2LoginSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final OAuth2Service oAuth2Service;
    private final JwtRequestFilter jwtRequestFilter;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Autowired
    public SecurityConfig(OAuth2Service oAuth2Service, JwtRequestFilter jwtRequestFilter, OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler) {
        this.oAuth2Service = oAuth2Service;
        this.jwtRequestFilter = jwtRequestFilter;
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeRequests(auth -> auth
                        .requestMatchers("/auth/**", "/oauth2/**", "/css/**", "/images/**", "/js/**").permitAll() // 로그인 및 기타 공용 엔드포인트 접근 허용
                        .requestMatchers("/api/user/profile", "/api/user/update", "/api/user/delete", "/api/userinfo").authenticated() // 로그인된 사용자 접근 허용
                        .requestMatchers("/api/products/create", "/api/products/update/**", "/api/products/delete/**").access("hasRole('ROLE_USER') and @userSecurity.isSeller(authentication)") // SELLER 권한만 접근 가능
                        .requestMatchers("/api/users/**", "/api/all", "/api/{userId}/grant-seller").hasRole("ADMIN") // ADMIN 권한만 접근 가능
                        .anyRequest().authenticated()) // 나머지 요청은 인증 필요
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessUrl("/auth/login")
                        .permitAll())
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(user -> user.userService(oAuth2Service))
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler(oAuth2LoginFailureHandler()));

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationFailureHandler oAuth2LoginFailureHandler() {
        return (request, response, exception) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("OAuth2 Login Failed");
            response.getWriter().flush();
        };
    }
}
