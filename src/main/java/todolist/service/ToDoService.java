package todolist.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import common.Logger;
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
		
		Logger.log(this.getClass().getSimpleName(), "findAllToDo: ToDo全件取得");
		return toDoRepository.findAllToDo(userDetails)
				.stream()
				.map(m -> toDo.mapToEntity(m))
				.toList();
	}
	
	/**
	 * 完了ToDoの全件取得
	 * @param UserDetails userDetails
	 * @return List<ToDo>
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ToDo> findAllCompletedToDo(@AuthenticationPrincipal final UserDetails userDetails){
		
		Logger.log(this.getClass().getSimpleName(), "findAllCompletedToDo: 完了ToDo全件取得");
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
		Logger.log(this.getClass().getSimpleName(), "completeTask: タスク完了処理");
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
		Logger.log(this.getClass().getSimpleName(), "undoCompletedTask: タスク完了取消処理");
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
		Logger.log(this.getClass().getSimpleName(), "getUserInfo: ログインユーザー情報取得処理");
		return toDoRepository.getUserInfo(userDetails);
	}
	
	
	/**
	 * ToDo作成
	 * @param ToDo todo
	 * @return boolean result
	 */
	@Override
	public boolean insertRecord(final ToDo todo) {
		Logger.log(this.getClass().getSimpleName(), "insertRecord: ToDo作成処理");
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
		Logger.log(this.getClass().getSimpleName(), "updateRecord: ToDo編集処理");
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
		Logger.log(this.getClass().getSimpleName(), "deleteRecord: ToDo削除処理");
		return toDoRepository.deleteRecord(id) > 0;
	}
		
	
}
