package kr.ac.hansung.controller;

import kr.ac.hansung.dto.UserDto;
import kr.ac.hansung.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("user", new UserDto());
        return "signup";
    }

    @PostMapping("/signup")
    public String signupProcess(@ModelAttribute("user") UserDto dto, Model model) {
        if (userService.existsByEmail(dto.getEmail())) {
            model.addAttribute("emailExists", true);
            return "signup";
        }
        userService.signup(dto);
        return "redirect:/login?registered";
    }

    // 비밀번호 변경 화면 띄우기
    @GetMapping("/password")
    public String passwordChangeForm(org.springframework.ui.Model model) {
        model.addAttribute("passwordChangeDto", new kr.ac.hansung.dto.PasswordChangeDto());
        return "password-change";
    }

    // 비밀번호 변경 처리
    @PostMapping("/password")
    public String changePassword(@jakarta.validation.Valid @org.springframework.web.bind.annotation.ModelAttribute("passwordChangeDto") kr.ac.hansung.dto.PasswordChangeDto dto,
                                 org.springframework.validation.BindingResult bindingResult,
                                 java.security.Principal principal,
                                 org.springframework.ui.Model model) {

        if (bindingResult.hasErrors()) {
            return "password-change";
        }

        try {
            // 로그인한 사용자의 이메일(principal.getName())을 넘겨서 비밀번호 변경
            userService.changePassword(principal.getName(), dto);
            return "redirect:/logout"; // 성공하면 강제 로그아웃 시켜서 다시 로그인하게 만듦

        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "password-change";
        }
    }
}
