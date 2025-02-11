package com.aivle.TermCompass.controller;

import com.aivle.TermCompass.domain.User;
import com.aivle.TermCompass.dto.UserCreateForm;
import com.aivle.TermCompass.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UserService userService;

	@Test
	@DisplayName("회원가입 성공 테스트")
	void testSignup_Success() throws Exception {
		// Given
		UserCreateForm form = new UserCreateForm();
		form.setName("test");
		form.setEmail("test@test.com");
		form.setAccount_type(User.AccountType.PERSONAL);
		form.setPassword1("12345678");
		form.setPassword2("12345678");

		Mockito.doNothing().when(userService).create(
				any(String.class), any(String.class), any(String.class), any(User.AccountType.class)
		);

		// When & Then
		mockMvc.perform(post("/signup")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("name", form.getName())
						.param("email", form.getEmail())
						.param("password1", form.getPassword1())
						.param("password2", form.getPassword2())
						.param("account_type", form.getAccount_type().toString()))
				.andExpect(status().isOk());
	}

}
