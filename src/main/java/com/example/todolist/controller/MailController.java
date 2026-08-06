package com.example.todolist.controller;

import java.util.List;
import java.util.UUID;

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
import com.example.todolist.entity.User;
import com.example.todolist.repository.PasswordResetTokenRepositoryIF;
import com.example.todolist.repository.UserRepository;
import com.example.todolist.service.MailServiceIF;
import com.example.todolist.service.PasswordResetServiceIF;

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
	public String sendMail(@ModelAttribute PasswordChange mail, Model model) {

		// メール送信先のメールアドレス
		String email = mail.getPasswordChange();
		boolean isExistsByEmail = false;
		// メールアドレスの存在チェック
		try {
			isExistsByEmail = userRepository.existsByEmail(email);
			
		}catch (Exception e) {
			return "login";
		}
		
		if(!isExistsByEmail) {
			// ユーザーが見つからない場合の処理
			model.addAttribute("errorMessage", "メールアドレスが登録されていません。");
			return "login";
		}

		List<User> userList = userRepository.findByEmail(email);

		// メールアドレスはユニークなため、get(0)で取得して問題ない
		Long userId = userList.get(0).getId();

		
		// 過去に作成した、未使用トークンが存在する場合は削除する
		int count = passwordResetTokenRepository.selectCountByUserId(userId);
		
		if(count > 0) {
			// 過去に作成した、未使用トークンが存在する場合は削除する
			int num = passwordResetTokenRepository.deleteResetToken(userId);
			if(num < 0) {
				// トークンの削除に失敗した場合の処理
				model.addAttribute("errorMessage", "トークンの削除に失敗しました。");
				return "login";
			}
		}
		
		
		// ランダムなトークンを生成し、メールに設定する
		String token  = UUID.randomUUID().toString(); 
		
		// トークンをエンコード(BCryptPasswordEncoder) → DBに保存する
		String encodedToken = passwordEncoder.encode(token);
		

		// エンコードされたトークンをDBに保存する処理
		int num = passwordResetTokenRepository.insertRecord(userId, encodedToken);
		
		if(num < 0) {
			// トークンの保存に失敗した場合の処理
			model.addAttribute("errorMessage", "トークンの保存に失敗しました。");
			return "login";
		}
		
		// パスワードリセットメールを送信する
		mailService.sendPasswordResetEmail(email, token);

		model.addAttribute("passwordChange", new PasswordChange());
		
		return "login";
	}
	
	@PostMapping
	public String resetPassword(@ModelAttribute("resetPassword") PasswordReset passwordReset,
			@RequestParam("token") String rawToken, 
			BindingResult bindingResult, 
			Model model
			) {
		
		if(bindingResult.hasErrors()) {
			return "resetPassword";
		}
		
		List<String> errors = mailService.checkPassword(passwordReset, rawToken);
		
		if(!errors.isEmpty()) {
			for(String error: errors) {
				bindingResult.reject("error.resetPassword", error);
			}
			return "resetPassword";
		}
		
		
		/**
		 * 処理の流れ
		 * 1.password-reset-tokensテーブルからuser_idを取得
		 * 2.password-reset-tokensテーブルのuserd_atにタイムスタンプを格納
		 * 3.Userテーブルのパスワードを更新
		 * 4.password-reset-tokensテーブルの該当レコードを削除する
		 */
		boolean result = passwordResetService.passwordResetTransanction(rawToken, passwordReset.getNewPassword(), model);

		if(!result) {

			model.addAttribute("error-reset-pw", "パスワードの再設定に失敗しました。");
			return "error";  
		}
		
		return "redirect:/reset-password/complete";  
	}

	
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
