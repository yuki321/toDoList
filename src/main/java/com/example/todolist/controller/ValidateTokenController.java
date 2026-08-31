package com.example.todolist.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.todolist.entity.PasswordReset;
import com.example.todolist.entity.User;
import com.example.todolist.repository.PasswordResetTokenRepositoryIF;
import com.example.todolist.service.MailServiceIF;

import org.springframework.ui.Model;

@Controller
@RequestMapping("/")
public class ValidateTokenController {
	
	@Autowired
	private PasswordResetTokenRepositoryIF passwordResetTokenRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private MailServiceIF mailService;
	
	
	
	/**
	 * トークン検証
	 * @param String token
	 * @param String kind
	 * @param Model model
	 * @return String
	 */
	@GetMapping("/validate_token")
	public String validateToken(@RequestParam("token") final String token, @RequestParam("kind") final String kind, final Model model) {

		final PasswordReset passwordReset = new PasswordReset();
		
		// トークンの検証
		final boolean result = passwordReset.validatePasswordResetToken(token, passwordEncoder, passwordResetTokenRepository);
		if(!result) {
			model.addAttribute("errorMessage", "トークンが不正です");
			
			return "redirect:/login";  
		}
		
		model.addAttribute("resetPassword", new PasswordReset());
			
		// 検証が問題ない場合、パスワード再設定または新規登録画面へ遷移
		// a.パスワード再設定 
		if(kind.equals("reset")) {
			return "resetPassword";
		}
		
		// b.新規登録
		model.addAttribute("user", new User()); 
		model.addAttribute("login", false);
		return "userCreate";
	}

	
}




