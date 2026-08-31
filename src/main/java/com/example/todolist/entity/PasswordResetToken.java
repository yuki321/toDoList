package com.example.todolist.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.data.relational.core.mapping.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;


@Entity
@Table(name = "PasswordResetToken")
public class PasswordResetToken {
	
	int EXPIRATION_HOUR_UNIT = 1; // トークンの有効期限（時間単位）
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "user_id", nullable = false, unique = true)
	private Long userId;
	
	@Column(name = "token_hash", nullable = false, unique = true)
	private String token_hash;
	
	@Column(name = "created_at", nullable = false)
	private LocalDateTime created_at = LocalDateTime.now();

	// トークンの有効期限を設定するためのカラム
	@Column(name = "expires_at", nullable = false, unique = false)
	private LocalDateTime expires_at = LocalDateTime.now().plusHours(EXPIRATION_HOUR_UNIT);
	
	// トークンが使用された日時を保持するカラム
	@Column(name = "used_at", nullable = true)
	private LocalDateTime used_at; 
	
    
    // コンストラクタ
	public PasswordResetToken() {};
	
	public PasswordResetToken(Long id, Long user_id, String token_hash, 
			LocalDateTime created_at, LocalDateTime expires_at, LocalDateTime used_at) {
		super();
		this.id = id;
		this.userId = user_id;
		this.token_hash = token_hash;
		this.created_at = created_at;
		this.expires_at = expires_at;
		this.used_at = used_at;
		
	}


	// Setter / Getterメソッド
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getExpiredAt() {
		return expires_at;
	}

	public void setExpiredAt(LocalDateTime expires_at) {
		this.expires_at = expires_at;
	}

	public Long getUserId() {
		return userId;
	}


	public void setUserId(Long userId) {
		this.userId = userId;
	}


	public String getToken_hash() {
		return token_hash;
	}


	public void setToken_hash(String token_hash) {
		this.token_hash = token_hash;
	}


	public LocalDateTime getCreated_at() {
		return created_at;
	}


	public void setCreated_at(LocalDateTime created_at) {
		this.created_at = created_at;
	}


	public LocalDateTime getExpires_at() {
		return expires_at;
	}


	public void setExpires_at(LocalDateTime expires_at) {
		this.expires_at = expires_at;
	}


	public LocalDateTime getUsed_at() {
		return used_at;
	}


	public void setUsed_at(LocalDateTime used_at) {
		this.used_at = used_at;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PasswordResetToken other = (PasswordResetToken) obj;
		return Objects.equals(id, other.id);
	}
	

}
