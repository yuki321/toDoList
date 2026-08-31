package com.example.todolist.repository;

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
public class ToDoRepository implements ToDoRepositoryIF {
	
	@Autowired
	JdbcTemplate jdbc;
	
	@Autowired
	UserService userService;
	
	/**
	 * ToDoのデータ全件取得
	 * @param userDetails userDetails
	 * @return List<ToDo>
	 */
	@Override
	public List<Map<String, Object>> findAllToDo(@AuthenticationPrincipal final UserDetails userDetails){
		
		final String userName = userDetails.getUsername();
		final String sql = "SELECT * FROM todo t INNER JOIN users u ON t.user_id = u.id "
				+ "WHERE u.user_name=? and t.status = 1 ORDER BY t.priority ASC";
		
		// ToDoテーブルのデータを全件取得
		final List<Map<String, Object>> getList = jdbc.queryForList(sql, userName);
		
		return getList;
	};
	
	
	/**
	 * 完了済みToDoのデータ全件取得
	 * @param UserDetails userDetails
	 * @return List<ToDo>
	 */
	@Override
	public List<Map<String, Object>> findAllCompletedToDo(@AuthenticationPrincipal final UserDetails userDetails){
		
		final String userName = userDetails.getUsername();
		final String sql = "SELECT * FROM todo t INNER JOIN users u ON t.user_id = u.id "
				+ "WHERE u.user_name=? and t.status = 2 ORDER BY t.priority ASC";
		
		// ToDoテーブルのデータを全件取得
		final List<Map<String, Object>> getList = jdbc.queryForList(sql, userName);
		
		return getList;
	};
	

	/**
	 * タスク完了
	 * @param ToDo todo
	 * @return int num
	 * @throws DataAccessException
	 */
	@Override
	public int completeTask(final ToDo todo) throws DataAccessException {
		
		final String sql = "UPDATE todo SET status = 2 WHERE id=? and status = 1";
		final int num = jdbc.update(sql, todo.getId());
		
		return num;
	}
	
	
	/**
	 * タスク完了を取り消す
	 * @param ToDo todo
	 * @return int num
	 * @throws DataAccessException
	 */
	@Override
	public int undoCompletedTask(final ToDo todo) throws DataAccessException {
	
		final String sql = "UPDATE todo SET status = 1 WHERE id=? and status = 2";
		final int num = jdbc.update(sql, todo.getId());
		
		return num;
	}
	

	/**
	 * ログイン中のユーザー情報の取得
	 * @param userDetails userDetails
	 * @return Map<String, Object> userInfo
	 */
	@Override
	public Map<String, Object> getUserInfo(@AuthenticationPrincipal final UserDetails userDetails){
	
		final String userName = userDetails.getUsername();
		final String sql = "SELECT * FROM users WHERE user_name=?";
		final Map<String, Object> userInfo = jdbc.queryForMap(sql, userName);

		return userInfo;
	}
	
	
	/**
	 * ToDo作成
	 * @param Todo todo
	 * @return int num
	 * @throws DataAccessException
	 */
	@Override
	public int insertRecord(final ToDo todo) throws DataAccessException {
		
		final int num = jdbc.update("INSERT INTO todo VALUES(?, ?, ?, ?, ?, ?, ?)",
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
	@Override
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
	@Override
	public int deleteRecord(Long id) throws DataAccessException {
		
		String sql = "DELETE FROM todo WHERE id=?";
		int num = jdbc.update(sql, id);
		
		return num;
	}

}


