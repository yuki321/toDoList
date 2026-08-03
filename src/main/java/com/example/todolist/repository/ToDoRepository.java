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
public class ToDoRepository {
	
	@Autowired
	JdbcTemplate jdbc;
	
	@Autowired
	UserService userService;
	
	/**
	 * ToDoのデータ全件取得
	 * @param userDetails userDetails
	 * @return List<ToDo>
	 */
	public List<ToDo> findAllToDo(@AuthenticationPrincipal UserDetails userDetails){
		
		List<ToDo> todolist = new ArrayList<>();
		String userName = userDetails.getUsername();
		String sql = "SELECT * FROM todo t INNER JOIN users u ON t.user_id = u.id WHERE u.user_name=?";
		
		// ToDoテーブルのデータを全件取得
		List<Map<String, Object>> getList = jdbc.queryForList(sql, userName);
		
		for(Map<String, Object> map: getList) {
			ToDo todo = new ToDo();
			todo.setId((Long)map.get("id"));
			todo.setUserId((Long)map.get("user_id"));
			todo.setContent((String)map.get("content"));
			todo.setMemo((String)map.get("memo"));
			todo.setStatus((Boolean)map.get("status"));
			todo.setDeadline((LocalDateTime)map.get("deadline"));
			
//			String priority = (String)map.get("priority");
//			if(priority.equals("1")){
//				todo.setPriority("優先度高");
//			}else if(priority.equals("2")){
//				todo.setPriority("優先度中");
//			}else if(priority.equals("3")){
//				todo.setPriority("優先度低");
//			}else{
//				todo.setPriority("優先度なし");
//			}
			
			todo.setPriority((String)map.get("priority"));
				
			todolist.add(todo);
		}
		
		return todolist;
	};
	

	/**
	 * ログイン中のユーザー情報の取得
	 * @param userDetails userDetails
	 * @return Map<String, Object> userInfo
	 */
	public Map<String, Object> getUserInfo(@AuthenticationPrincipal UserDetails userDetails){
	
		String userName = userDetails.getUsername();
		String sql = "SELECT * FROM users WHERE user_name=?";
		Map<String, Object> userInfo = jdbc.queryForMap(sql, userName);
		
		return userInfo;
	}
	
	
	/**
	 * ToDo作成
	 * @param Todo todo
	 * @return int num
	 * @throws DataAccessException
	 */
	public int insertRecord(ToDo todo) throws DataAccessException {
		
		int num = jdbc.update("INSERT INTO todo VALUES(?, ?, ?, ?, ?, ?, ?)",
				todo.getId(),
				todo.getUserId(),
				todo.getContent(),
				todo.getMemo(),
				todo.getStatus(),
				todo.getDeadline(),
				todo.getPriority());
		
		return num;
	}

	
	/**
	 * タスク編集
	 * @param ToDo todo
	 * @return int num
	 * @throws DataAccessException
	 */
	public int updateRecord(ToDo todo) throws DataAccessException {
		
		String sql = "UPDATE todo SET user_id = ?, content = ?, "
				+ "memo = ?, status = ?, deadline = ? ,"
				+ "priority = ? WHERE id=?";
		int num = jdbc.update(sql,
				todo.getUserId(),
				todo.getContent(),
				todo.getMemo(),
				todo.getStatus(),
				todo.getDeadline(),
				todo.getPriority(),
				todo.getId());
		
		return num;
	}
	
	
	/**
	 * タスク削除
	 * @param Long id
	 * @return int num
	 * @throws DataAccessException
	 */
	public int deleteRecord(Long id) throws DataAccessException {
		
		String sql = "DELETE FROM todo WHERE id=?";
		int num = jdbc.update(sql, id);
		
		return num;
	}
	
	
}


