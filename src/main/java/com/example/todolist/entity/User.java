package com.example.todolist.entity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.Default;

@Entity
@Table(name = "users")
public class User {

	/** 作成時のバリデーショングループ */
	public interface Create extends Default {}

	/** 更新時のバリデーショングループ */
	public interface Update extends Default {}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotEmpty(message = "ユーザー名を入力してください")
	@Size(max = 50, message = "ユーザー名は1～50桁を入力してください")
	@Column(name = "user_name", nullable = false, unique = false, length = 50)
	private String userName;

	@NotEmpty(message = "メールアドレスを入力してください")
	@Size(min = 8, max = 100, message = "メールアドレスは8～100桁を入力してください")
	@Column(name = "email", nullable = false, unique = true, length = 100)
	private String email;

	@NotEmpty(groups = Create.class, message = "パスワードを入力してください")
	@Size(min = 4, max = 20, groups = Create.class, message = "パスワードは4～20桁を入力してください")
	@Column(name = "password", nullable = false)
	private String password;

	@NotEmpty(groups = Create.class, message = "ロールを選択してください")
	@Column(name = "role", nullable = false, length = 10)
	private String role = "General";

	@Column(name = "enabled", nullable = false)
	private Boolean enabled = true;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false, updatable = true)
	private LocalDateTime updatedAt;

	// アカウントの有効期限が切れていない場合はtrueに設定
	@Column(name = "account_non_expired", nullable = false, updatable = true)
	private boolean accountNonExpired = true;
	
	// 資格情報の有効期限が切れていない場合はtrueに設定
	@Column(name = "credentials_non_expired", nullable = false, updatable = true)
	private boolean credentialsNonExpired = true;
	
	// アカウントがロックされていない場合はtrueに設定
	@Column(name = "account_non_locked", nullable = false, updatable = true)
	private boolean accountNonLocked = true;

	@Transient
	private Collection<? extends GrantedAuthority> authorities;


	// コンストラクタ
	public User() {}

	public User(Long id, String userName, String email, String password, String role, Boolean enabled,
			LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.id = id;
		this.userName = userName;
		this.email = email;
		this.password = password;
		this.role = role;
		this.enabled = enabled;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
	
	public User(String userName, String password, boolean enabled, boolean accountNonExpired, 
			boolean credentialsNonExpired, boolean accountNonLocked, 
			Collection<? extends GrantedAuthority> authorities) {
		this.userName = userName;
		this.password = password;
		this.enabled = enabled;
		this.accountNonExpired = accountNonExpired;
		this.credentialsNonExpired = credentialsNonExpired;
		this.accountNonLocked = accountNonLocked;
		this.authorities = authorities;
	}


	 // Setter / Getterメソッド
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	public void setAccountNonExpired(boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}

	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}

	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	public void setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
		this.authorities = authorities;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}


	@Override
	public int hashCode() {
		return Objects.hash(id);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return Objects.equals(id, other.id);
	}



}
