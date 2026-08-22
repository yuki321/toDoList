package com.example.todolist.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.todolist.entity.PasswordReset;
import com.example.todolist.repository.PasswordResetTokenRepositoryIF;

import org.springframework.beans.factory.annotation.Value;


@Service
@Transactional
public class MailService implements MailServiceIF {
	
	// application.propertiesに設定した送信元メールアドレスを取得
	@Value("${app.mail.from}")
	private String mailFrom;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired 
	private JdbcTemplate jdbc;
	
	@Autowired
	private PasswordResetTokenRepositoryIF passwordResetTokenRepository;

	@Autowired
	private MailSender mailSender;
	

	/**
	 * パスワードチェック
	 * @param passwordReset
	 * @param rawToken
	 * @return
	 */
	public List<String> checkPassword(PasswordReset passwordReset, String rawToken){
		
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
	public void sendPasswordResetEmail(String email, String token) {
		
		String subject = "[todolist]パスワードリセットのご案内";
		String resetLink = "http://localhost:8080/validate_token?token=" + token + "&kind=reset";
		String text = "パスワードリセットのリクエストを受け付けました。下記リンクからリセットをしてください。\n"
				+ resetLink
				+ "\nメールに心当たりがない場合、このメールを削除してください。\n"
				+ "\n"
				+ "todolist";

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(email);
		message.setFrom(mailFrom);
		message.setSubject(subject);
		message.setText(text);
		mailSender.send(message);
		
	}
	
	
	/**
	 * ユーザー登録のメールを送信する
	 * @param token
	 * @param email
	 */
	public void sendUserCreateEmail(String email, String token) {
		
		String subject = "[todolist]ユーザー登録のご案内";
		String resetLink = "http://localhost:8080/validate_token?token=" + token + "&kind=registration";
		String text = "ユーザー登録のリクエストを受け付けました。下記リンクからリセットをしてください。\n"
				+ resetLink
				+ "\nメールに心当たりがない場合、このメールを削除してください。\n"
				+ "\n"
				+ "todolist";

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(email);
		message.setFrom(mailFrom);
		message.setSubject(subject);
		message.setText(text);
		mailSender.send(message);
		
	}
	
	
}


