package com.example.shopping_mall_web.oauth2user;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

public enum OAuthAttributes {

    GOOGLE("google", (attribute) -> {
        String name = (String) attribute.get("name");
        String email = (String) attribute.get("email");
        return new OAuth2Dto(name, "google", email);
    }),

    NAVER("naver", (attribute) -> {
        Map<String, String> responseValue = (Map) attribute.get("response");
        String name = responseValue.get("name");
        String email = responseValue.get("email");
        return new OAuth2Dto(name, "naver", email);
    }),

    KAKAO("kakao", (attribute) -> {
        Map<String, Object> account = (Map) attribute.get("kakao_account");
        Map<String, String> profile = (Map) account.get("profile");
        String name = profile.get("nickname");
        String email = (String) account.get("email");
        return new OAuth2Dto(name, "kakao", email);
    });

    private final String registrationId;
    private final Function<Map<String, Object>, OAuth2Dto> of;

    OAuthAttributes(String registrationId, Function<Map<String, Object>, OAuth2Dto> of) {
        this.registrationId = registrationId;
        this.of = of;
    }

    public static OAuth2Dto extract(String registrationId, Map<String, Object> attributes) {
        return Arrays.stream(values())
                .filter(value -> registrationId.equals(value.registrationId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid OAuth Provider: " + registrationId))
                .of.apply(attributes);
    }
}
