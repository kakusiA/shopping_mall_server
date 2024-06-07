package com.example.shopping_mall_web.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = true, length = 255)
    private String address;

    @Column(nullable = true, length = 10)
    private String gender;

    @Column(nullable = true, length = 20)
    private String phoneNumber;

    @Column(nullable = false)
    private boolean isSeller = false;

    public User() {
    }

    public User(String name, String email, String password, String address, boolean isSeller) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.address = address;
        this.isSeller = isSeller;
    }
}
