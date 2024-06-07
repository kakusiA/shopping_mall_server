package com.example.shopping_mall_web.oauth2user;

import com.example.shopping_mall_web.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class OAuth2UserController {

    @Autowired
    private OAuth2UserRepository oAuth2UserRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/login")
    public String oauth2Login() {
        return "login";
    }

    @GetMapping("/merge")
    public String mergePage(@RequestParam String email, @RequestParam String provider, Model model) {
        model.addAttribute("email", email);
        model.addAttribute("provider", provider);
        return "merge";
    }

    @PostMapping("/merge")
    public ResponseEntity<?> mergeAccounts(@RequestBody Map<String, String> mergeRequest) {
        String email = mergeRequest.get("email");
        String provider = mergeRequest.get("provider");

        Optional<OAuthUser> existingUser = oAuth2UserRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            OAuthUser userToMerge = oAuth2UserRepository.findByEmailAndProvider(email, provider)
                    .orElse(null);

            if (userToMerge == null) {
                return ResponseEntity.badRequest().body("해당 제공자에 대한 계정을 찾을 수 없습니다.");
            }

            OAuthUser mainUser = existingUser.get();
            mainUser.mergeWith(userToMerge);

            oAuth2UserRepository.save(mainUser);
            oAuth2UserRepository.delete(userToMerge);

            return ResponseEntity.ok().body(Map.of("success", true, "message", "계정이 성공적으로 통합되었습니다."));
        }

        return ResponseEntity.badRequest().body(Map.of("success", false, "message", "중복 계정이 없습니다."));
    }
}
