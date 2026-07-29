package com.example.todolist.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import com.example.todolist.entity.ToDo;
import com.example.todolist.service.UserService;

@Repository
public class PasswordResetTokenRepository {
	
	@Autowired
	JdbcTemplate jdbc;
	
	
	/**
	 * パスワードリセットトークンのレコードを挿入する
	 * @param userId ユーザーID
	 * @param tokenHash トークンのハッシュ値
	 * @return 1:挿入成功, 0:挿入失敗
	 * @throws DataAccessException データアクセス例外
	 */
	public int insertRecord(Long userId, String tokenHash) throws DataAccessException {
		
		// トークンの有効期限（時間単位）
		int EXPIRATION_HOUR_UNIT = 1; 
		
		String sql = "INSERT INTO password_reset_tokens "
				+ "(user_id, token_hash, created_at, expires_at, used_at) "
				+ "VALUES(?, ?, ?, ?, ?)";
		int num = jdbc.update(sql,
				userId,
				tokenHash,
				LocalDateTime.now(),
				// 有効期限を1時間後に設定
				LocalDateTime.now().plusHours(EXPIRATION_HOUR_UNIT), 
				// used_atはnullで初期化(トークンを利用した時間)
				null );
		
		return num;
	}
	
	
	
	
	public void delete(Long id) throws DataAccessException {
		
// TODO 
		
//		return num;
	}
	
	
	
	
	
//	public List<ToDo> findAllToDo(@AuthenticationPrincipal UserDetails userDetails){
//		
//		List<ToDo> todolist = new ArrayList<>();
//		String userName = userDetails.getUsername();
//		String sql = "SELECT * FROM todo t INNER JOIN users u ON t.user_id = u.id WHERE u.user_name=?";
//		
//		// ToDoテーブルのデータを全件取得
//		List<Map<String, Object>> getList = jdbc.queryForList(sql, userName);
//		
//		for(Map<String, Object> map: getList) {
//			ToDo todo = new ToDo();
//			todo.setId((Long)map.get("id"));
//			todo.setUserId((Long)map.get("user_id"));
//			todo.setContent((String)map.get("content"));
//			todo.setMemo((String)map.get("memo"));
//			todo.setStatus((Boolean)map.get("status"));
//				
//			todolist.add(todo);
//		}
//		
//		return todolist;
//	};
//	
//
//	/**
//	 * ログイン中のユーザー情報の取得
//	 * @param userDetails userDetails
//	 * @return Map<String, Object> userInfo
//	 */
//	public Map<String, Object> getUserInfo(@AuthenticationPrincipal UserDetails userDetails){
//	
//		String userName = userDetails.getUsername();
//		String sql = "SELECT * FROM users WHERE user_name=?";
//		Map<String, Object> userInfo = jdbc.queryForMap(sql, userName);
//		
//		return userInfo;
//	}
//	
//	
//	/**
//	 * ToDo作成
//	 * @param Todo todo
//	 * @return int num
//	 * @throws DataAccessException
//	 */
//	public int insertRecord(ToDo todo) throws DataAccessException {
//		
//		int num = jdbc.update("INSERT INTO todo VALUES(?, ?, ?, ?, ?)",
//				todo.getId(),
//				todo.getUserId(),
//				todo.getContent(),
//				todo.getMemo(),
//				todo.getStatus());
//		
//		return num;
//	}
//
//	
//	/**
//	 * タスク編集
//	 * @param ToDo todo
//	 * @return int num
//	 * @throws DataAccessException
//	 */
//	public int updateRecord(ToDo todo) throws DataAccessException {
//		
//		String sql = "UPDATE todo SET user_id = ?, content = ?, "
//				+ "memo = ?, status = ? WHERE id=?";
//		int num = jdbc.update(sql,
//				todo.getUserId(),
//				todo.getContent(),
//				todo.getMemo(),
//				todo.getStatus(),
//				todo.getId());
//		
//		return num;
//	}
//	
	

	
}


