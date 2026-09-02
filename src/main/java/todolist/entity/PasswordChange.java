package todolist.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.Default;

@Entity
public class PasswordChange {
	
	// トークンの有効期限（時間単位）
	int EXPIRATION_HOUR_UNIT = 1; 

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
    
    // 処理の種類（パスワード再設定または新規登録）
    private String kind;

    
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
	
	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	// コンストラクタ
	public PasswordChange() {};
	
	public PasswordChange(String currentPassword, String newPassword, 
			String confirmPassword, String passwordChange, String kind) {
		super();
		this.currentPassword = currentPassword;
		this.newPassword = newPassword;
		this.confirmPassword = confirmPassword;
		this.passwordChange = passwordChange;
		this.kind = kind;
	}


	







}
