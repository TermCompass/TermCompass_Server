package com.aivle.TermCompass.controller;

import com.aivle.TermCompass.domain.User;
import com.aivle.TermCompass.dto.UserCreateForm;
import com.aivle.TermCompass.dto.LoginRequest;
import com.aivle.TermCompass.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

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
    public ResponseEntity<Object> login(@RequestBody LoginRequest request, BindingResult bindingResult) {
        User user = userService.authenticate(request.getEmail(), request.getPassword());

        if(user == null) {
            bindingResult.rejectValue("email", "NotExistingUser",
                    "잘못된 이메일 혹은 비밀번호 입니다.");
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errorMessages);
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("name", user.getName());
        userInfo.put("email", user.getEmail());
        userInfo.put("account_type", user.getAccount_type());

        return ResponseEntity.ok(userInfo);
    }
}
