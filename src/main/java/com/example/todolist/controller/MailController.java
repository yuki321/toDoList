package com.example.todolist.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
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
import com.example.todolist.entity.PasswordReset;
import com.example.todolist.entity.User;
import com.example.todolist.repository.PasswordResetTokenRepository;
import com.example.todolist.repository.UserRepository;
import com.example.todolist.service.PasswordResetService;
import com.example.todolist.service.UserServiceIF;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

@Controller
@RequestMapping("/reset-password")
public class MailController {
	
	@Autowired
	private MailSender mailSender;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserServiceIF userService;
	
	@Autowired
	private PasswordResetTokenRepository passwordResetTokenRepository;
	
	@Autowired
	private PasswordResetService passwordResetService;
	
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
		sendPasswordResetEmail(email, token);

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
		
		List<String> errors = checkPassword(passwordReset, rawToken);
		
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
	
	private List<String> checkPassword(PasswordReset passwordReset, String rawToken){
		
		List<String> errors = new ArrayList<>();
		
		List<Map<String, Object>> resultList = passwordResetTokenRepository.findAllTokenHash();

		Long userId_L = getUserId(resultList, rawToken);
		
		// 新パスワード
		String newPassword = passwordReset.getNewPassword();
		// 新パスワード（確認用）
		String confirmedPassword = passwordReset.getConfirmPassword();
		// DBから取得した現在のパスワード
		String DBPassword = getDbPassword(userId_L);
		
		// 新パスワードと新パスワード（確認用）が一致しない
		if(!newPassword.equals(confirmedPassword)) {
			errors.add("入力したパスワードが一致しません");
		}
		// DBの現在のパスワードと入力したパスワードが一致する
		if(passwordEncoder.matches(newPassword, DBPassword)) {
			errors.add("登録されているパスワードと入力したパスワードが同じです");
		}
		
		return errors;
	}
	
	/**
	 * パスワードをusersテーブルから取得
	 * @param Long userId
	 * @return String password
	 */
	private String getDbPassword(Long userId) {
		
		String sql = "SELECT password FROM users WHERE id=?";
		Map<String, Object> getMap = jdbc.queryForMap(sql, userId);
		String password = (String)getMap.get("password");
		
		return password;
	}
	
	/**
	 * ユーザーIDの取得
	 * @param List<Map<String, Object>> resultList
	 * @param String rawToken
	 * @return Long userId
	 */
	private Long getUserId(List<Map<String, Object>> resultList, String rawToken) {
		
		boolean matchResult = false;
		String userId = null;
		for(Map<String, Object> map: resultList) {
	
			String token_hash = (String)map.get("token_hash");
			matchResult = passwordEncoder.matches(rawToken, token_hash);
	
			if(matchResult) {
				// マッチした組み合わせを次の判定で利用
				userId = map.get("user_id").toString();
				break;
			}
			
		}
		
		return Long.valueOf(userId);
	}
	
	
	
	/**
	 * パスワードリセットメールを送信する
	 * @param token
	 * @param email
	 */
	private void sendPasswordResetEmail(String email, String token) {
		
		String subject = "[todolist]パスワードリセットのご案内";
		String resetLink = "http://localhost:8080/validate_token?token=" + token;
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
