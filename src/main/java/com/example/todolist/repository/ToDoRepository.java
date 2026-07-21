package com.example.todolist.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.todolist.entity.ToDo;
import com.example.todolist.entity.User;
import com.example.todolist.service.UserService;

@Repository
public class ToDoRepository {
	
	@Autowired
	JdbcTemplate jdbc;
	
	@Autowired
	UserService userService;
	
	public List<ToDo> findAllToDo(@AuthenticationPrincipal UserDetails userDetails){
		
		List<ToDo> todolist = new ArrayList<>();
		String userName = userDetails.getUsername();
		
		// ToDoテーブルのデータを全件取得
		List<Map<String, Object>> getList = jdbc.queryForList(
				"SELECT * FROM todo t INNER JOIN users u ON t.user_id = u.id WHERE u.user_name=?"
				, userName);
		
		for(Map<String, Object> map: getList) {
			ToDo todo = new ToDo();
			todo.setId((Long)map.get("id"));
			todo.setUserId((Long)map.get("user_id"));
			todo.setContent((String)map.get("content"));
			todo.setStatus((Boolean)map.get("status"));
				
			todolist.add(todo);
		}
		
		return todolist;
	};
	
	// ログイン中のユーザー情報の取得
	public Map<String, Object> getUserInfo(@AuthenticationPrincipal UserDetails userDetails){
	
		String userName = userDetails.getUsername();
		String sql = "SELECT * FROM users WHERE user_name=?";
		Map<String, Object> userInfo = jdbc.queryForMap(sql, userName);
		
		return userInfo;
	}
	

	
}
