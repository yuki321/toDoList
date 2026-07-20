package com.example.todolist.entity;

import java.util.Objects;

import org.springframework.data.relational.core.mapping.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;


@Entity
@Table(name = "todos")
public class ToDo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "user_id", nullable = false, unique = true, length = 20)
	private Long userId;
	
//	@Column(name = "category_id", nullable = false, unique = true, length = 20)
//	private Long categoryId;
		
	@Column(name = "content", nullable = false, unique = false, length = 500)
	private String content;
	
	@Column(name = "status", nullable = false)
	private Boolean status = true;
	
    
    // コンストラクタ
	public ToDo(Long id, Long userId, String content, Boolean status) {
		super();
		this.id = id;
		this.userId = userId;
		this.content = content;
		this.status = status;
	}


	// Setter / Getterメソッド
	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public Long getUserId() {
		return userId;
	}


	public void setUserId(Long userId) {
		this.userId = userId;
	}


//	public Long getCategoryId() {
//		return categoryId;
//	}
//
//
//	public void setCategoryId(Long categoryId) {
//		this.categoryId = categoryId;
//	}


	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}


	public Boolean getStatus() {
		return status;
	}


	public void setStatus(Boolean status) {
		this.status = status;
	}




	@Override
	public int hashCode() {
		return Objects.hash(id);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ToDo other = (ToDo) obj;
		return Objects.equals(id, other.id);
	}
	

}
