package com.example.todolist.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.todolist.repository.PasswordResetTokenRepositoryIF;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.Default;



@Entity
public class PasswordReset {
	
	
	// トークンの有効期限（時間単位）
	int EXPIRATION_HOUR_UNIT = 1; 

	/** パスワード変更時のバリデーショングループ */
	public interface PasswordUpdate extends Default {}
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	 
    @NotBlank(message = "新しいパスワードを入力してください")
	@Size(min = 4, max = 20, groups = PasswordUpdate.class, 
	message = "パスワードは4～20文字で入力してください")
    private String newPassword;

    @NotBlank(message = "確認用パスワードを入力してください")
	@Size(min = 4, max = 20, groups = PasswordUpdate.class, 
	message = "パスワードは4～20文字で入力してください")
    private String confirmPassword;
    
    
    // Setter / Getterのメソッド
	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	
	
	// コンストラクタ
	public PasswordReset() {};
	
	public PasswordReset(String currentPassword, String newPassword, String confirmPassword) {
		super();
		this.newPassword = newPassword;
		this.confirmPassword = confirmPassword;
	}
	
	
	/**
	 * トークンの検証
	 * @param String token
	 * @param PasswordEncoder passwordEncoder
	 * @param PasswordResetTokenRepositoryIF passwordResetTokenRepository
	 * @return boolean
	 */
	public boolean validatePasswordResetToken(String rawToken, 
			PasswordEncoder passwordEncoder,
			PasswordResetTokenRepositoryIF passwordResetTokenRepository
			) {
		
		// 1.トークンが存在するか確認する処理
		// 全有効トークンを取得して、ハッシュ化されていない平文トークンと比較
		List<Map<String, Object>> resultList = passwordResetTokenRepository.findAllTokenHash();

		
		boolean matchResult = false;
		String expires_time = null;
		for(Map<String, Object> m: resultList) {
			
			String token_hash = (String)m.get("token_hash");
			matchResult = passwordEncoder.matches(rawToken, token_hash);

			if(matchResult) {
				// マッチした組み合わせを次の判定で利用
				expires_time = m.get("expires_at").toString();
				break;
			}
			
		}
		
		// 一致しなければfalse
		if(!matchResult) {
			return false;
		}
		
		if(expires_time == null) {
			return false;
		}
		
		// 2.トークンが使用済みか(nullでなければfalse)
		if(resultList.get(0).get("used_at") != null) {
			return false;
		}
		
		// 3.トークンの有効期限内か
		expires_time = expires_time.replace("T", " ");
		// "yyyy/MM/dd HH:mm:ss" => "yyyy-MM-dd HH:mm:ss"
		LocalDateTime expired_at = toLocalDateTime(expires_time, "yyyy-MM-dd HH:mm:ss");
			
		// トークンの有効期限が切れている場合、false
		LocalDateTime now = LocalDateTime.now();
		if(expired_at.isBefore(now)) {
			return false;
		}
		
		
		return true; 
	}
	
	
	// String => LocalDateTimeへ変換
	public static LocalDateTime toLocalDateTime(String date, String format) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.parse(date, dtf);
    }



}
