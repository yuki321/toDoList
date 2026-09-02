package com.example.todolist.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import com.example.todolist.entity.PasswordChange;
import com.example.todolist.entity.PasswordReset;
import com.example.todolist.repository.PasswordResetTokenRepositoryIF;
import com.example.todolist.repository.UserRepository;

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
	
	@Autowired
	private UserRepository userRepository;
	
	

	/**
	 * パスワードチェック
	 * @param passwordReset
	 * @param rawToken
	 * @return List<String>
	 */
	public List<String> checkPassword(final PasswordReset passwordReset, final String rawToken){
		
		List<String> errors = new ArrayList<>();
		
		List<Map<String, Object>> resultList = passwordResetTokenRepository.findAllTokenHash();

		String email = getEmail(resultList, rawToken);
		
		// 新パスワード
		String newPassword = passwordReset.getNewPassword();
		// 新パスワード（確認用）
		String confirmedPassword = passwordReset.getConfirmPassword();
		// DBから取得した現在のパスワード
		String DBPassword = getDBPassword(email);
		
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
	 * @param String email
	 * @return String password
	 */
	private String getDBPassword(final String email) {
		
		String sql = "SELECT password FROM users WHERE email=?";
		Map<String, Object> getMap = jdbc.queryForMap(sql, email);
		String password = (String)getMap.get("password");
		
		return password;
	}
	
	
	/**
	 * メールアドレスの取得
	 * @param List<Map<String, Object>> resultList
	 * @param String rawToken
	 * @return String
	 */
	private String getEmail(final List<Map<String, Object>> resultList, final String rawToken) {
		
		if(resultList == null || resultList.isEmpty()) return null;
		if(rawToken == null || rawToken.isEmpty()) return null;
		
		boolean matchResult = false;
		String email = null;
		for(Map<String, Object> map: resultList) {
	
			String token_hash = (String)map.get("token_hash");
			matchResult = passwordEncoder.matches(rawToken, token_hash);
	
			if(matchResult) {
				// マッチした組み合わせを次の判定で利用
				email = map.get("email").toString();
				break;
			}
			
		}
		
		return email;
	}
	
	
	/**
	 * パスワードリセットメールを送信する
	 * @param token
	 * @param email
	 */
	public void sendPasswordResetEmail(final String email, final String token) {
		
		String subject = "[todolist]パスワードリセットのご案内";
		String resetLink = "http://localhost:8080/validate_token?token=" + token + "&kind=reset";
		String text = "パスワードリセットのリクエストを受け付けました。下記リンクからリセットをしてください。\n"
				+ resetLink
				+ "\nメールに心当たりがない場合、このメールを削除してください。\n"
				+ "\n"
				+ "todolist";

		sendEmail(email, subject, text);
	}
	
	
	/**
	 * ユーザー登録のメールを送信する
	 * @param String email
	 * @param String token
	 */
	public void sendUserCreateEmail(final String email, final String token) {
		
		String subject = "[todolist]ユーザー登録のご案内";
		String resetLink = "http://localhost:8080/validate_token?token=" + token + "&kind=registration";
		String text = "ユーザー登録のリクエストを受け付けました。下記リンクからリセットをしてください。\n"
				+ resetLink
				+ "\nメールに心当たりがない場合、このメールを削除してください。\n"
				+ "\n"
				+ "todolist";

		sendEmail(email, subject, text);
	}
	
	
	/**
	 * タスクの期限が近づいていることを通知するメールを送信する
	 * @param String email
	 * @param String taskName
	 * @param String deadline
	 */
	public void sendTaskDeadlineEmail(final String email, final String taskName, final String deadline) {
		
		String subject = "[todolist]タスクの期限が近づいています";
		String text = "以下のタスクの期限が近づいています。\n"
				+ "タスク: " + taskName + "\n"
				+ "期限: " + deadline + "\n"
				+ "\n"
				+ "todolist";

		sendEmail(email, subject, text);
	}
	
	
	/**
	 * メール送信内容
	 * @param String to
	 * @param String subject
	 * @param String text
	 */
	private void sendEmail(final String to, final String subject, final String text) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setFrom(mailFrom);
		message.setSubject(subject);
		message.setText(text);
		mailSender.send(message);
	}
	
	
	/**
	 * メール送信処理
	 * @param PasswordChange mail
	 * @param Model model
	 * @return String
	 */
	@Override
	public String sendMailProcess(final PasswordChange mail, Model model) {
		
		// メール送信先のメールアドレス
		String email = mail.getPasswordChange();
		boolean isEmailExists = false;
		// メールアドレスの存在チェック
		try {
			isEmailExists = userRepository.existsByEmail(email);
			
		}catch (Exception e) {
			System.out.println("/reset-password/send sendMail()");
			return "login";
		}
		
		String kind = mail.getKind();
		if((!isEmailExists) && kind.equals("PW_RESET")) {
			// ユーザーが見つからない場合の処理
			model.addAttribute("errorMessage", "メールアドレスが登録されていません。");
			return "login";
		}

		
		if(kind.equals("PW_RESET")) {
			
			// 過去に作成した、未使用トークンが存在する場合は削除する
			int count = passwordResetTokenRepository.selectCountByEmail(email);
			
			if(count > 0) {
				// 過去に作成した、未使用トークンが存在する場合は削除する
				int num = passwordResetTokenRepository.deleteResetToken(email);
				if(num < 0) {
					// トークンの削除に失敗した場合の処理
					model.addAttribute("errorMessage", "トークンの削除に失敗しました。");
					return "login";
				}
			}
		}
		
		
		// ランダムなトークンを生成し、メールに設定する
		String token = UUID.randomUUID().toString(); 
		
		// トークンをエンコード(BCryptPasswordEncoder) → DBに保存する
		String encodedToken = passwordEncoder.encode(token);
		

		// エンコードされたトークンをDBに保存する処理
		int num = passwordResetTokenRepository.insertRecord(email, encodedToken);
		
		if(num < 0) {
			// トークンの保存に失敗した場合の処理
			model.addAttribute("errorMessage", "トークンの保存に失敗しました。");
			return "login";
		}
		
		// パスワードリセットメールを送信する
		if(kind.equals("PW_RESET")) {
			sendPasswordResetEmail(email, token);
		}
		
		// ユーザー登録のメールを送信する
		if(kind.equals("USER_CREATE")) {
			sendUserCreateEmail(email, token);
		}

		model.addAttribute("passwordChange", new PasswordChange());
		
		return "login";
	}
		
	
}


