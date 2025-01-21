package com.aivle.TermCompass.controller;

import com.aivle.TermCompass.domain.User;
import com.aivle.TermCompass.dto.UserCreateForm;
import com.aivle.TermCompass.dto.LoginRequest;
import com.aivle.TermCompass.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public void signup(@RequestBody UserCreateForm userCreateForm, BindingResult bindingResult) {

        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
        }

        if (userCreateForm.getAccount_type().equals(User.AccountType.COMPANY)) {
            userService.create(userCreateForm.getName(),
                    userCreateForm.getEmail(), userCreateForm.getPassword1(), userCreateForm.getAccount_type(), userCreateForm.getBusinessNumber());
        }
        else {
            userService.create(userCreateForm.getName(),
                    userCreateForm.getEmail(), userCreateForm.getPassword1(), userCreateForm.getAccount_type());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest request) {
        User user = userService.authenticate(request.getEmail(), request.getPassword());

        if(user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password. Please try again.");
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("name", user.getName());
        userInfo.put("email", user.getEmail());
        userInfo.put("account_type", user.getAccount_type());

        return ResponseEntity.ok(userInfo);
    }
}
