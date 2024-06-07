package com.example.shopping_mall_web.oauth2user;

import com.example.shopping_mall_web.jwt.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private OAuth2UserRepository oAuth2UserRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String provider = oAuth2User.getAttribute("provider");
        String name = oAuth2User.getAttribute("name");
        String address = oAuth2User.getAttribute("address");

        if (address == null) {
            address = "";
        }

        Optional<OAuthUser> existingUser = oAuth2UserRepository.findByEmail(email);
        Optional<OAuthUser> providerUser = oAuth2UserRepository.findByEmailAndProvider(email, provider);

        OAuthUser user;

        if (providerUser.isPresent()) {
            user = providerUser.get();
        } else if (existingUser.isPresent()) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getWriter().write("Account with this email already exists with a different provider");
            response.getWriter().flush();
            return;
        } else {
            user = OAuthUser.builder()
                    .email(email)
                    .provider(provider)
                    .name(name)
                    .address(address)
                    .build();
            oAuth2UserRepository.save(user);
        }

        String jwtToken = jwtUtil.generateToken(user.getEmail(), user.getName(), user.getAddress());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        Map<String, String> tokenResponse = new HashMap<>();
        tokenResponse.put("token", jwtToken);
        tokenResponse.put("refreshToken", refreshToken);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(tokenResponse));
        response.getWriter().flush();
    }
}
