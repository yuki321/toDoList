package todolist.repository;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import todolist.entity.ToDo;
import todolist.service.UserService;

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
		return jdbc.queryForList(sql, userName);
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
		return jdbc.queryForList(sql, userName);
	};
	

	/**
	 * タスク完了
	 * @param ToDo todo
	 * @return int
	 * @throws DataAccessException
	 */
	@Override
	public int completeTask(final ToDo todo) throws DataAccessException {
		
		final String sql = "UPDATE todo SET status = 2 WHERE id=? and status = 1";
		return jdbc.update(sql, todo.getId());
	}
	
	
	/**
	 * タスク完了を取り消す
	 * @param ToDo todo
	 * @return int
	 * @throws DataAccessException
	 */
	@Override
	public int undoCompletedTask(final ToDo todo) throws DataAccessException {
	
		final String sql = "UPDATE todo SET status = 1 WHERE id=? and status = 2";
		return jdbc.update(sql, todo.getId());
	}
	

	/**
	 * ログイン中のユーザー情報の取得
	 * @param userDetails userDetails
	 * @return Map<String, Object> 
	 */
	@Override
	public Map<String, Object> getUserInfo(@AuthenticationPrincipal final UserDetails userDetails){
	
		final String userName = userDetails.getUsername();
		final String sql = "SELECT * FROM users WHERE user_name=?";
		return jdbc.queryForMap(sql, userName);
	}
	
	
	/**
	 * ToDo作成
	 * @param Todo todo
	 * @return int num
	 * @throws DataAccessException
	 */
	@Override
	public int insertRecord(final ToDo todo) throws DataAccessException {
		
		return jdbc.update("INSERT INTO todo VALUES(?, ?, ?, ?, ?, ?, ?)",
				todo.getId(),
				todo.getUserId(),
				todo.getContent(),
				todo.getMemo(),
				todo.getStatus(),
				todo.getDeadline(),
				todo.getPriority());
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
		
		return jdbc.update(sql,
				todo.getUserId(),
				todo.getContent(),
				todo.getMemo(),
				todo.getStatus(),
				todo.getDeadline(),
				todo.getPriority(),
				todo.getId());
	}
	
	
	/**
	 * タスク削除
	 * @param Long id
	 * @return int
	 * @throws DataAccessException
	 */
	@Override
	public int deleteRecord(Long id) throws DataAccessException {
		
		String sql = "DELETE FROM todo WHERE id=?";
		return jdbc.update(sql, id);
	}
	
	
	/**
	 * 期限切れ1週間前のタスクを取得
	 * @return List<Map<String, Object>> taskList
	 */
	@Override
	public List<Map<String, Object>> getTasksDueInOneWeek(){
		
		// 締め切り1週間前～締め切り翌日0時までのタスクを抽出
		final String sql = "SELECT u.id, u.email, t.content, t.deadline FROM todo t "
		        + "INNER JOIN users u ON t.user_id = u.id "
		        + "WHERE t.status = 1 AND t.deadline >= CURDATE() "
		        + "AND t.deadline < CURDATE() + INTERVAL 8 DAY;";
		
		// 締め切り1週間前のタスクを抽出
		final List<Map<String, Object>> taskList = jdbc.queryForList(sql);
		if(taskList.isEmpty()) {
			System.out.println("期限切れ1週間前のタスクはありません");
			return taskList;
		}
		
		System.out.println("email：" + taskList.get(0).get("email") + " task_name：" + taskList.get(0).get("content") + " deadline：" + taskList.get(0).get("deadline"));
		return taskList;
	}
	

}


