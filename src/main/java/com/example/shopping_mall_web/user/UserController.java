package com.example.shopping_mall_web.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    // JWT 토큰을 통해 인증된 사용자 정보 반환 메서드
    @GetMapping("/userinfo")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null) {
            User user = userDetails.getUser();
            UserDto userDto = new UserDto(user.getUserId(), user.getName(), user.getEmail(), user.getAddress(), user.getGender(), user.getPhoneNumber());
            return ResponseEntity.ok(userDto);
        } else {
            return ResponseEntity.badRequest().body("User details not found");
        }
    }

    // 사용자 ID로 사용자 정보 조회 메서드 (인증 필요)
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        Optional<UserDto> userDto = userService.findUserById(id);
        return userDto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 모든 사용자 정보 조회 메서드 (관리자 권한 필요)
    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<UserDto> getAllUsers() {
        return userService.findAllUsers();
    }

    // 사용자 정보 업데이트 메서드 (관리자 권한 필요)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        try {
            UserDto updatedUser = userService.updateUser(id, userDto);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 로그인된 사용자의 정보를 업데이트하는 엔드포인트
    @PutMapping("/user/update")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDto> updateUserInfo(Authentication authentication, @RequestBody UserDto userDto) {
        try {
            UserDto updatedUser = userService.updateUserInfo(authentication, userDto);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 사용자 삭제 메서드 (관리자 권한 필요)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    // 로그인된 사용자를 삭제하는 엔드포인트
    @DeleteMapping("/user/delete")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteUser(Authentication authentication) {
        try {
            userService.deleteUser(authentication);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 사용자 프로필 조회 엔드포인트
    @GetMapping("/user/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDto> getUserProfile(Authentication authentication) {
        try {
            UserDto userDto = userService.getUserProfile(authentication);
            return ResponseEntity.ok(userDto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 관리자가 사용자에게 ROLE_SELLER 역할을 부여하는 엔드포인트
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/{userId}/grant-seller")
    public ResponseEntity<UserDto> grantSellerRole(@PathVariable Long userId) {
        try {
            User user = userService.grantSellerRole(userId);
            UserDto userDto = new UserDto(user.getUserId(), user.getName(), user.getEmail(), user.getAddress(), user.getGender(), user.getPhoneNumber());
            return ResponseEntity.ok(userDto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
