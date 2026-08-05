package com.example.todolist.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.todolist.entity.PasswordReset;
import com.example.todolist.repository.PasswordResetTokenRepositoryIF;

import org.springframework.ui.Model;

@Controller
@RequestMapping("/")
public class ValidateTokenController {
	
	@Autowired
	private PasswordResetTokenRepositoryIF passwordResetTokenRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	
	
	@GetMapping("/validate_token")
	public String validateToken(@RequestParam("token") String token, Model model) {

		PasswordReset passwordReset = new PasswordReset();
		
		// トークンの検証
		boolean result = passwordReset.validatePasswordResetToken(token, passwordEncoder, passwordResetTokenRepository);

		if(!result) {
			model.addAttribute("errorMessage", "トークンが不正です");
			
			return "redirect:/login";  
		}
		
		model.addAttribute("resetPassword", new PasswordReset());
			
		// 検証が問題ない場合、パスワード再設定フォームへ遷移
		return "resetPassword";
	}


	
}




