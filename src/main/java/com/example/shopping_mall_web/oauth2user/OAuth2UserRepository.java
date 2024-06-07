package com.example.shopping_mall_web.oauth2user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OAuth2UserRepository extends JpaRepository<OAuthUser, Long> {
    Optional<OAuthUser> findByEmail(String email);
    Optional<OAuthUser> findByEmailAndProvider(String email, String provider);
}