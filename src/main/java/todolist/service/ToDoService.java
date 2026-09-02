package todolist.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import todolist.entity.ToDo;
import todolist.repository.ToDoRepositoryIF;

@Service
@Transactional
public class ToDoService implements ToDoServiceIF {
	
	@Autowired
	private ToDoRepositoryIF toDoRepository;
	
	/**
	 * 全件取得
	 * @param UserDetails userDetails
	 * @return List<ToDo>
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ToDo> findAllToDo(@AuthenticationPrincipal final UserDetails userDetails){
		
		List<ToDo> todolist = new ArrayList<>();
		List<Map<String, Object>> getList = toDoRepository.findAllToDo(userDetails);
		
		for(Map<String, Object> map: getList) {
			ToDo todo = new ToDo();
			todo.setId((Long)map.get("id"));
			todo.setUserId((Long)map.get("user_id"));
			todo.setContent((String)map.get("content"));
			todo.setMemo((String)map.get("memo"));
			todo.setStatus((Boolean)map.get("status"));
			todo.setDeadline((LocalDateTime)map.get("deadline"));
			todo.setPriority((String)map.get("priority"));
				
			todolist.add(todo);
		}
		
		return todolist;
	}
	
	/**
	 * 完了済みToDoの全件取得
	 * @param UserDetails userDetails
	 * @return List<ToDo>
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ToDo> findAllCompletedToDo(@AuthenticationPrincipal final UserDetails userDetails){
		
		List<ToDo> todolist = new ArrayList<>();
		List<Map<String, Object>> getList = toDoRepository.findAllCompletedToDo(userDetails);
		
		for(Map<String, Object> map: getList) {
			ToDo todo = new ToDo();
			todo.setId((Long)map.get("id"));
			todo.setUserId((Long)map.get("user_id"));
			todo.setContent((String)map.get("content"));
			todo.setMemo((String)map.get("memo"));
			todo.setStatus((Boolean)map.get("status"));
			todo.setDeadline((LocalDateTime)map.get("deadline"));
			todo.setPriority((String)map.get("priority"));
				
			todolist.add(todo);
		}
		
		return todolist;
	}
	
	
	/**
	 * タスク完了
	 * @param ToDo todo
	 * @return boolean result
	 * @throws DataAccessException
	 */
	@Override
	public boolean completeTask(final ToDo todo) throws DataAccessException {

		int num = toDoRepository.completeTask(todo);

		boolean result = false;
		if(num > 0) {
			result = true;
		}
		
		return result;
	}
	

	/**
	 * タスク完了の取り消し
	 * @param ToDo todo
	 * @return boolean result
	 * @throws DataAccessException
	 */
	@Override
	public boolean undoCompletedTask(final ToDo todo) throws DataAccessException {

		int num = toDoRepository.undoCompletedTask(todo);
		
		boolean result = false;
		if(num > 0) {
			result = true;
		}
		
		return result;
	}
	
	
	/**
	 * ログイン中のユーザー情報の取得
	 * @param UserDetails userDetails
	 * @return Map<String, Object>
	 */
	@Override
	@Transactional(readOnly = true)
	public Map<String, Object> getUserInfo(@AuthenticationPrincipal final UserDetails userDetails){
		return toDoRepository.getUserInfo(userDetails);
	}
	
	
	/**
	 * ToDo作成
	 * @param ToDo todo
	 * @return boolean result
	 */
	@Override
	public boolean insertRecord(final ToDo todo) {
		int num = toDoRepository.insertRecord(todo);
		
		boolean result = false;
		if(num > 0) {
			// insert成功の場合
			result = true;
		}
		return result;
	}
	

	/**
	 * タスク編集
	 * @param ToDo todo
	 * @return boolean result
	 * @throws DataAccessException
	 */
	@Override
	public boolean updateRecord(final ToDo todo) throws DataAccessException {
		
		int num = toDoRepository.updateRecord(todo);
		
		boolean result = false;
		if(num > 0) {
			result = true;
		}
		
		return result;
	}
	
	/**
	 * タスク削除
	 * @param Long id
	 * @return boolean result
	 * @throws DataAccessException
	 */
	@Override
	public boolean deleteRecord(final Long id) throws DataAccessException {
		int num = toDoRepository.deleteRecord(id);
		
		boolean result = false;
		if(num > 0) {
			result = true;
		}
		
		return result;
	}
	
	
}
