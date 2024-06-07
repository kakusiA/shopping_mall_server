package com.example.shopping_mall_web.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 새로운 사용자를 등록하는 메서드 (회원가입)
    public User registerNewUser(String name, String email, String password, String address) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalStateException("Email already taken");
        }
        User user = new User(name, email, passwordEncoder.encode(password), address, false);
        return userRepository.save(user);
    }

    // 판매자 역할 부여 메서드
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public User grantSellerRole(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setSeller(true);
        return userRepository.save(user);
    }

    // 사용자를 인증하는 메서드 (로그인)
    public boolean authenticateUser(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.isPresent() && passwordEncoder.matches(password, user.get().getPassword());
    }

    // 사용자 정보를 ID로 조회하는 메서드 (인증 필요)
    public Optional<UserDto> findUserById(Long userId) {
        return userRepository.findById(userId).map(this::convertToDTO);
    }

    // 모든 사용자 정보를 조회하는 메서드 (관리자만 접근 가능)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<UserDto> findAllUsers() {
        return userRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // 사용자 정보를 업데이트하는 메서드 (인증 필요)
    @PreAuthorize("isAuthenticated()")
    public UserDto updateUser(Long userId, UserDto userDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setEmail(userDto.getEmail());
        user.setName(userDto.getName());
        user.setAddress(userDto.getAddress());
        user.setGender(userDto.getGender());
        user.setPhoneNumber(userDto.getPhoneNumber());
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        return convertToDTO(userRepository.save(user));
    }

    // 로그인된 사용자의 정보를 업데이트하는 메서드
    public UserDto updateUserInfo(Authentication authentication, UserDto userDto) {
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        currentUser.setName(userDto.getName());
        currentUser.setAddress(userDto.getAddress());
        currentUser.setGender(userDto.getGender());
        currentUser.setPhoneNumber(userDto.getPhoneNumber());

        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            currentUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        return convertToDTO(userRepository.save(currentUser));
    }

    // 사용자를 삭제하는 메서드 (관리자 권한 필요)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    // 로그인된 사용자를 삭제하는 메서드
    public void deleteUser(Authentication authentication) {
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(currentUser);
    }

    // 사용자 프로필 조회 메서드
    public UserDto getUserProfile(Authentication authentication) {
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return convertToDTO(currentUser);
    }

    // 이메일로 사용자를 조회하는 메서드 (인증 필요)
    @PreAuthorize("isAuthenticated()")
    public Optional<UserDto> findByEmail(String email) {
        return userRepository.findByEmail(email).map(this::convertToDTO);
    }

    private UserDto convertToDTO(User user) {
        return new UserDto(user.getUserId(), user.getName(), user.getEmail(), user.getAddress(), user.getGender(), user.getPhoneNumber());
    }
}
