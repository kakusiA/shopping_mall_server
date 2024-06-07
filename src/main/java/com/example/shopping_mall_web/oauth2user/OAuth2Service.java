package com.example.shopping_mall_web.oauth2user;

import com.example.shopping_mall_web.user.User;
import com.example.shopping_mall_web.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class OAuth2Service implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final OAuth2UserRepository oAuth2UserRepository;
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest
                .getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        Map<String, Object> attributes = oAuth2User.getAttributes();
        OAuth2Dto userProfile = OAuthAttributes.extract(registrationId, attributes);
        userProfile.setProvider(registrationId);

        User user = findOrCreateUser(userProfile);
        userProfile.setUserId(user.getUserId());

        OAuthUser oauthUser = updateOrSaveOAuthUser(userProfile, user);

        Map<String, Object> customAttribute = getCustomAttribute(registrationId, userNameAttributeName, attributes, userProfile);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("USER")),
                customAttribute,
                userNameAttributeName);
    }

    private Map<String, Object> getCustomAttribute(String registrationId,
                                                   String userNameAttributeName,
                                                   Map<String, Object> attributes,
                                                   OAuth2Dto userProfile) {
        Map<String, Object> customAttribute = new ConcurrentHashMap<>();
        customAttribute.put(userNameAttributeName, attributes.get(userNameAttributeName));
        customAttribute.put("provider", registrationId);
        customAttribute.put("name", userProfile.getName());
        customAttribute.put("email", userProfile.getEmail());
        return customAttribute;
    }

    private OAuthUser updateOrSaveOAuthUser(OAuth2Dto OAuthDto, User user) {
        return oAuth2UserRepository
                .findByEmailAndProvider(OAuthDto.getEmail(), OAuthDto.getProvider())
                .map(existingOAuthUser -> {
                    existingOAuthUser.updateUser(OAuthDto.getName(), OAuthDto.getEmail());
                    return existingOAuthUser;
                })
                .orElseGet(() -> {
                    OAuthUser newOAuthUser = new OAuthUser(user, OAuthDto.getName(), OAuthDto.getEmail(), OAuthDto.getProvider());
                    return oAuth2UserRepository.save(newOAuthUser);
                });
    }

    private User findOrCreateUser(OAuth2Dto OAuthDto) {
        return userRepository.findByEmail(OAuthDto.getEmail())
                .orElseGet(() -> {
                    User newUser = new User(OAuthDto.getName(), OAuthDto.getEmail(), "", "", false);
                    return userRepository.save(newUser);
                });
    }
}
