package com.example.todolist.entity;

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



}
