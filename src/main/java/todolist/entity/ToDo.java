package todolist.entity;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

import org.springframework.data.relational.core.mapping.Table;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
	
	@Column(name = "user_id", nullable = false, unique = false, length = 20)
	private Long userId;
			
	@NotEmpty(message = "タスクを入力してください")
	@Size(max = 100, message = "タスクは100文字以内で入力してください")
	@Column(name = "content", nullable = false, unique = false, length = 500)
	@Pattern(
	    regexp = "^[a-z0-9\u3040-\u309F\u30A0-\u30FF\u4E00-\u9FFF]+$", 
	    message = "日本語（ひらがな・カタカナ・漢字）、英小文字、数字のみ入力可能です"
	)
	private String content;
	
	@Size(max = 100, message = "メモは100文字以内で入力してください")
	@Column(name = "memo", nullable = true, unique = false, length = 100)
	private String memo;
	
	@Column(name = "status", nullable = false)
	private Boolean status = true;
	
	@Column(name = "deadline", nullable = true)
	private LocalDateTime deadline;
	
	@NotEmpty(message = "優先度を選択してください")
	@Column(name = "priority", nullable = false)
	private String priority;
	
    
    // コンストラクタ
	public ToDo() {};
	
	public ToDo(Long id, Long userId, String content, String memo, 
			Boolean status, LocalDateTime deadline, String priority) {
		super();
		this.id = id;
		this.userId = userId;
		this.content = content;
		this.memo = memo;
		this.status = status;
		this.deadline = deadline;
		this.priority = priority;
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


	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}
	
	
	public String getMemo() {
		return memo;
	}


	public void setMemo(String memo) {
		this.memo = memo;
	}


	public Boolean getStatus() {
		return status;
	}


	public void setStatus(Boolean status) {
		this.status = status;
	}


	public LocalDateTime getDeadline() {
		return deadline;
	}

	public void setDeadline(LocalDateTime deadline) {
		this.deadline = deadline;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
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
	
	
	/**
	 * Map<String, Object>をToDoエンティティに変換
	 * @param Map<String, Object> map
	 * @return ToDo todo
	 */
	public ToDo mapToEntity(final Map<String, Object> map) {
		ToDo todo = new ToDo();
		todo.setId((Long)map.get("id"));
		todo.setUserId((Long)map.get("user_id"));
		todo.setContent((String)map.get("content"));
		todo.setMemo((String)map.get("memo"));
		todo.setStatus((Boolean)map.get("status"));
		todo.setDeadline((LocalDateTime)map.get("deadline"));
		todo.setPriority((String)map.get("priority"));
		
		return todo;
	}
	
	

}
