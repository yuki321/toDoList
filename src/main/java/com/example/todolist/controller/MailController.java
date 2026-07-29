package com.example.todolist.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.todolist.entity.PasswordChange;
import com.example.todolist.entity.User;
import com.example.todolist.repository.PasswordResetTokenRepository;
import com.example.todolist.repository.UserRepository;
import com.example.todolist.service.UserService;

import org.springframework.ui.Model;

@Controller
@RequestMapping("/reset-password")
public class MailController {
	
	@Autowired
	private MailSender mailSender;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PasswordResetTokenRepository passwordResetTokenRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	

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
		sendPasswordResetEmail(email, token);

		model.addAttribute("passwordChange", new PasswordChange());
		
		return "login";
	}
	
	/**
	 * パスワードリセットメールを送信する
	 * @param token
	 * @param email
	 */
	private void sendPasswordResetEmail(String email, String token) {
		
		String subject = "[todolist]パスワードリセットのご案内";
		String resetLink = "https://localhost:8080/reset-password?token=" + token;
		String text = "パスワードリセットのリクエストを受け付けました。下記リンクからリセットをしてください。\n"
				+ resetLink
				+ "\nメールに心当たりがない場合、このメールを削除してください。\n"
				+ "\n"
				+ "tidolist";

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(email);
		message.setFrom(mailFrom);
		message.setSubject(subject);
		message.setText(text);
		mailSender.send(message);
		
	}
	

}
