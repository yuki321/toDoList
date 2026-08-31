package com.example.todolist.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.todolist.entity.PasswordChange;
import com.example.todolist.entity.PasswordReset;
import com.example.todolist.repository.PasswordResetTokenRepositoryIF;
import com.example.todolist.repository.UserRepository;
import com.example.todolist.service.MailServiceIF;
import com.example.todolist.service.PasswordResetServiceIF;

import jakarta.validation.Valid;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

@Controller
@RequestMapping("/reset-password")
public class MailController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private MailServiceIF mailService;
	
	@Autowired
	private PasswordResetTokenRepositoryIF passwordResetTokenRepository;
	
	@Autowired
	private PasswordResetServiceIF passwordResetService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private JdbcTemplate jdbc;
	

	// application.propertiesに設定した送信元メールアドレスを取得
	@Value("${app.mail.from}")
	private String mailFrom;
	
	
	@PostMapping("/send")
	public String sendMail(@ModelAttribute final PasswordChange mail, final BindingResult bindingResult, final Model model) {

		if (bindingResult.hasErrors()) {
	        System.out.println("Validation Errors: " + bindingResult.getAllErrors());
	        return "index"; 
	    }
		
		return mailService.sendMailProcess(mail, model);
	}
	
	
	/**
	 * パスワード再設定
	 * @param PasswordReset passwordReset
	 * @param String rawToken
	 * @param BindingResult bindingResult
	 * @param Model model
	 * @return String
	 */
	@PostMapping
	public String resetPassword(@Valid @ModelAttribute("resetPassword") final PasswordReset passwordReset,
			@RequestParam("token") final String rawToken, 
			final BindingResult bindingResult, 
			final Model model
			) {
		
		if(bindingResult.hasErrors()) {
			return "resetPassword";
		}
		
		final List<String> errors = mailService.checkPassword(passwordReset, rawToken);
		
		if(!errors.isEmpty()) {
			for(String error: errors) {
				bindingResult.reject("error.resetPassword", error);
			}
			return "resetPassword";
		}
		
		
		/**
		 * 処理の流れ
		 * 1.password-reset-tokensテーブルからメールアドレスを取得
		 * 2.password-reset-tokensテーブルのuserd_atにタイムスタンプを格納
		 * 3.Userテーブルのパスワードを更新
		 * 4.password-reset-tokensテーブルの該当レコードを削除する
		 */
		final boolean result = passwordResetService.passwordResetTransaction(rawToken, passwordReset.getNewPassword(), model);

		if(!result) {

			model.addAttribute("error-reset-pw", "パスワードの再設定に失敗しました。");
			return "error";  
		}
		
		return "redirect:/reset-password/complete";  
	}

	
	/**
	 * 完了画面へ遷移
	 * @return String
	 */
	@GetMapping("/complete")
	public String resetComplete() {
		return "complete";
	}
	
	
	/**
	 * エラー画面への画面遷移
	 * @return
	 */
	@GetMapping("/error")
	public String moveToError() {
		return "error";
	}
	

}
