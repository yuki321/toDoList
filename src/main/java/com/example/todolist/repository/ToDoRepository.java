package com.example.todolist.repository;

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
		
		int num = jdbc.update("INSERT INTO todo VALUES(?, ?, ?, ?, ?)",
				todo.getId(),
				todo.getUserId(),
				todo.getContent(),
				todo.getMemo(),
				todo.getStatus());
		
		return num;
	}

	
	/**
	 * タスク編集
	 * @param ToDo todo
	 * @return int num
	 * @throws DataAccessException
	 */
	public int updateRecord(ToDo todo) throws DataAccessException {
		
System.out.println("Id: " + todo.getId());
System.out.println("UserId: " + todo.getUserId());
System.out.println("Content: " + todo.getContent());
System.out.println("Memo: " + todo.getMemo());
		
		String sql = "UPDATE todo SET user_id = ?, content = ?, "
				+ "memo = ?, status = ? WHERE id=?";
		int num = jdbc.update(sql,
				todo.getUserId(),
				todo.getContent(),
				todo.getMemo(),
				todo.getStatus(),
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


