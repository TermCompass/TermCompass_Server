package com.aivle.TermCompass.controller;

import com.aivle.TermCompass.domain.User;
import com.aivle.TermCompass.dto.*;
import com.aivle.TermCompass.repository.UserRepository;
import com.aivle.TermCompass.service.JwtTokenProvider;
import com.aivle.TermCompass.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @PostMapping("/signup")
    public ResponseEntity<Object> signup(@RequestBody UserCreateForm userCreateForm, BindingResult bindingResult) {

        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
        }
        if (!userService.checkDuplicate(userCreateForm.getEmail())) {
            bindingResult.rejectValue("email", "emailDuplicate",
                    "이미 존재하는 이메일입니다.");
        }

        if (bindingResult.hasErrors()) {
            // 검증 오류 메시지를 수집하여 반환
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errorMessages);
        }

        if (userCreateForm.getAccount_type().equals(User.AccountType.COMPANY)) {
            userService.create(userCreateForm.getName(),
                    userCreateForm.getEmail(), userCreateForm.getPassword1(), userCreateForm.getAccount_type(), userCreateForm.getBusinessNumber());
        }
        else {
            userService.create(userCreateForm.getName(),
                    userCreateForm.getEmail(), userCreateForm.getPassword1(), userCreateForm.getAccount_type());
        }

        return ResponseEntity.ok("Signup successful");
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest request, BindingResult bindingResult, HttpServletResponse response) {
        User user = userService.authenticate(request.getEmail(), request.getPassword());

        if(user == null) {
            bindingResult.rejectValue("email", "NotExistingUser",
                    "잘못된 이메일 혹은 비밀번호 입니다.");
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errorMessages);
        }

        String token = jwtTokenProvider.createToken(user.getEmail(), user.getId());
        if (token == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to generate token");
        }

        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);

        response.addCookie(cookie);

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("name", user.getName());
        userInfo.put("email", user.getEmail());
        userInfo.put("account_type", user.getAccount_type());
        userInfo.put("created_at", user.getCreated_at());
        userInfo.put("businessNumber", user.getBusinessNumber());

        userService.incrementLoginCount();

        return ResponseEntity.ok(userInfo);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        // SecurityContextLogoutHandler를 통해 세션 무효화
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, null);

        SecurityContextHolder.clearContext();

        // 성공적인 로그아웃 응답 반환
        return ResponseEntity.ok("로그아웃 성공");
    }

    @PostMapping("/admin/login")
    public ResponseEntity<Object> adminLogin(@RequestBody LoginRequest request, BindingResult bindingResult) {
        User user = userService.adminAuthenticate(request.getEmail(), request.getPassword());

        if(user == null) {
            bindingResult.rejectValue("email", "NotExistingUser",
                    "잘못된 이메일 혹은 비밀번호 입니다.");
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errorMessages);
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("email", user.getEmail());

        return ResponseEntity.ok(userInfo);
    }

    @PostMapping("/change-password")
    public ResponseEntity<Object> changePassword(@RequestBody ChangePasswordRequest request, BindingResult bindingResult, HttpServletRequest httpServletRequest) {
        String email = (String) httpServletRequest.getAttribute("email");
        if (email == null) {
            return ResponseEntity.badRequest().body("인증되지 않은 사용자입니다.");
        }

        User user = userService.findByEmail(email);
        if (!userService.checkPassword(user, request.getOldPassword())) {
            bindingResult.rejectValue("oldPassword", "IncorrectPassword", "기존 비밀번호가 일치하지 않습니다.");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "passwordMismatch", "새 비밀번호가 일치하지 않습니다.");
        }

        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errorMessages);
        }

        userService.updatePassword(user, request.getNewPassword());
        return ResponseEntity.ok("비밀번호 변경이 완료되었습니다.");
    }

    @GetMapping("/logout-success")
    public void logoutRedirect() {}

    @GetMapping("/user")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        // 1️⃣ 쿠키에서 JWT 추출
        String token = jwtTokenProvider.getTokenFromCookie(request);
        if (token == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        // 2️⃣ JWT 검증 후 사용자 정보 조회
        String email = jwtTokenProvider.getEmailFromToken(token);
        User user = userService.findByEmail(email);

        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        if (user.getAccount_type() == User.AccountType.PERSONAL) {
            return ResponseEntity.ok(new PersonalDTO(user));
        } else if (user.getAccount_type() == User.AccountType.COMPANY) {
            return ResponseEntity.ok(new CompanyDTO(user));
        } else {
            return ResponseEntity.status(401).body("Unauthorized");
        }
    }

    @GetMapping("/users")
    public ResponseEntity<Integer> getUsersNumber() {
        int userCount = (int) userRepository.count(); // 성능 최적화
        return ResponseEntity.ok(userCount);
    }
}
