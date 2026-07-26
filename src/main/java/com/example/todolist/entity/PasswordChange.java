package com.example.todolist.entity;

import java.util.Collection;
import java.util.Objects;

import com.example.todolist.entity.User.Create;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.Default;

@Entity
public class PasswordChange {

	/** パスワード変更時のバリデーショングループ */
	public interface PasswordUpdate extends Default {}
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	 
	@NotBlank(message = "現在のパスワードを入力してください")
    private String currentPassword;

    @NotBlank(message = "新しいパスワードを入力してください")
	@Size(min = 4, max = 20, groups = PasswordUpdate.class, 
	message = "パスワードは4～20文字で入力してください")
    private String newPassword;

    @NotBlank(message = "確認用パスワードを入力してください")
	@Size(min = 4, max = 20, groups = PasswordUpdate.class, 
	message = "パスワードは4～20文字で入力してください")
    private String confirmPassword;
    
    private String passwordChange;

    
    // Setter / Getterのメソッド
	public String getCurrentPassword() {
		return currentPassword;
	}

	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
	}

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
	
	public String getPasswordChange() {
		return passwordChange;
	}

	public void setPasswordChange(String passwordChange) {
		this.passwordChange = passwordChange;
	}


	
	// コンストラクタ
	public PasswordChange() {};
	
	public PasswordChange(String currentPassword, String newPassword, 
			String confirmPassword, String passwordChange) {
		super();
		this.currentPassword = currentPassword;
		this.newPassword = newPassword;
		this.confirmPassword = confirmPassword;
		this.passwordChange = passwordChange;
	}


	







}
