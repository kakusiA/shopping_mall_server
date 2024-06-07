package com.example.shopping_mall_web.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long userId;
    private String name;
    private String email;
    private String password; // 회원가입 시 사용
    private String address;
    private String gender; // 추가된 필드
    private String phoneNumber; // 추가된 필드

    // 회원 정보를 조회할 때 사용할 생성자 추가
    public UserDto(Long userId, String name, String email, String address) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.address = address;
    }

    // 회원 정보 수정 시 사용할 생성자 추가
    public UserDto(Long userId, String name, String email, String address, String gender, String phoneNumber) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.address = address;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
    }
}
