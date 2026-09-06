package todolist.service;

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
		

	ToDo toDo = new ToDo();
	
	/**
	 * 全件取得
	 * @param UserDetails userDetails
	 * @return List<ToDo>
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ToDo> findAllToDo(@AuthenticationPrincipal final UserDetails userDetails){
		
		return toDoRepository.findAllToDo(userDetails)
				.stream()
				.map(m -> toDo.mapToEntity(m))
				.toList();
	}
	
	/**
	 * 完了済みToDoの全件取得
	 * @param UserDetails userDetails
	 * @return List<ToDo>
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ToDo> findAllCompletedToDo(@AuthenticationPrincipal final UserDetails userDetails){
		
		return toDoRepository.findAllCompletedToDo(userDetails)
				.stream()
				.map(m -> toDo.mapToEntity(m))
				.toList();
	}
	
	
	/**
	 * タスク完了
	 * @param ToDo todo
	 * @return boolean result
	 * @throws DataAccessException
	 */
	@Override
	public boolean completeTask(final ToDo todo) throws DataAccessException {
		return toDoRepository.completeTask(todo) > 0;
	}
	

	/**
	 * タスク完了の取り消し
	 * @param ToDo todo
	 * @return boolean result
	 * @throws DataAccessException
	 */
	@Override
	public boolean undoCompletedTask(final ToDo todo) throws DataAccessException {
		return toDoRepository.undoCompletedTask(todo) > 0;
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
		return toDoRepository.insertRecord(todo) > 0;
	}
	

	/**
	 * タスク編集
	 * @param ToDo todo
	 * @return boolean result
	 * @throws DataAccessException
	 */
	@Override
	public boolean updateRecord(final ToDo todo) throws DataAccessException {
		return toDoRepository.updateRecord(todo) > 0;
	}
	
	/**
	 * タスク削除
	 * @param Long id
	 * @return boolean result
	 * @throws DataAccessException
	 */
	@Override
	public boolean deleteRecord(final Long id) throws DataAccessException {
		return toDoRepository.deleteRecord(id) > 0;
	}
		
	
}
