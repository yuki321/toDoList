package todolist.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import todolist.common.Logger;
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
	
	
}
