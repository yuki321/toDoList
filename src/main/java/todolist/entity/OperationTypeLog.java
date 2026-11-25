package todolist.entity;

import org.springframework.data.relational.core.mapping.Table;

import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;


@Entity
@Table(name = "operation_history")
public class OperationTypeLog {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "operation_type", nullable = false, unique = false)
	private String operationType;
	
	@Column(name = "user_name", nullable = false, unique = false)
	private String userName;
	
	@Column(name = "content", nullable = false, unique = false)
	private String content;
	
	@Column(name = "operation_time", nullable = false, unique = false)
	private String operationTime;
	
	
	
	
	/**
	 * 引数チェック
	 * @param String operationType
	 * @param String userName
	 * @param String content
	 * @return boolean
	 */
	public boolean checkInput(final String operationType, final String userName, final String content) {

		if(operationType == null || operationType.isEmpty()) return false;
		if(userName == null || userName.isEmpty()) return false;
		if(content == null || content.isEmpty()) return false;
		
		return true;
	}
	
	
	
	
	
	

}
