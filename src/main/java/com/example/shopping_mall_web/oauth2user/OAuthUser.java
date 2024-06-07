package com.example.shopping_mall_web.oauth2user;

import com.example.shopping_mall_web.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "oauth_users")
@Builder
public class OAuthUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "oauth_user_id")
    private Long oauthUserId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String provider;

    @Column(name = "provider_id", nullable = true)
    private Long providerId;

    @Column(nullable = true, length = 10)
    private String gender;

    @Column(nullable = true, length = 20)
    private String phoneNumber;

    private String address;

    @Column(nullable = false)
    private boolean isSeller = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public OAuthUser(User user, String name, String email, String provider) {
        this.user = user;
        this.name = name;
        this.email = email;
        this.provider = provider;
    }

    public OAuthUser updateUser(String name, String email) {
        this.name = name;
        this.email = email;
        return this;
    }

    public void mergeWith(OAuthUser other) {
        if (other.getName() != null && !other.getName().isEmpty()) {
            this.name = other.getName();
        }
        if (other.getAddress() != null && !other.getAddress().isEmpty()) {
            this.address = other.getAddress();
        }
        if (other.getGender() != null && !other.getGender().isEmpty()) {
            this.gender = other.getGender();
        }
        if (other.getPhoneNumber() != null && !other.getPhoneNumber().isEmpty()) {
            this.phoneNumber = other.getPhoneNumber();
        }
        if (other.isSeller) {
            this.isSeller = true;
        }
    }
}

