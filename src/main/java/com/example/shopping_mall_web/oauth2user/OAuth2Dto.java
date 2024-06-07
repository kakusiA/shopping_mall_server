package com.example.shopping_mall_web.oauth2user;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OAuth2Dto {
    private String name; // 사용자 이름
    private String provider; // 로그인한 서비스
    private String email; // 사용자의 이메일
    private Long userId; // users 테이블의 user_id를 참조하는 필드

    // 기본 생성자
    public OAuth2Dto() {
    }

    // 매개변수가 있는 생성자
    public OAuth2Dto(String name, String provider, String email) {
        this.name = name;
        this.provider = provider;
        this.email = email;
    }
}
