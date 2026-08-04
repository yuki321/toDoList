package com.example.todolist.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.todolist.entity.ToDo;
import com.example.todolist.repository.ToDoRepository;

@Service
@Transactional
public class ToDoService {
	
	@Autowired
	private ToDoRepository toDoRepository;
	
	/**
	 * 全件取得
	 * @param UserDetails userDetails
	 * @return List<ToDo>
	 */
	@Transactional(readOnly = true)
	public List<ToDo> findAllToDo(@AuthenticationPrincipal UserDetails userDetails){
		return toDoRepository.findAllToDo(userDetails);
	}
	
	/**
	 * 完了済みToDoの全件取得
	 * @param UserDetails userDetails
	 * @return List<ToDo>
	 */
	@Transactional(readOnly = true)
	public List<ToDo> findAllCompletedToDo(@AuthenticationPrincipal UserDetails userDetails){
		return toDoRepository.findAllCompletedToDo(userDetails);
	}
	
	/**
	 * ログイン中のユーザー情報の取得
	 * @param UserDetails userDetails
	 * @return Map<String, Object>
	 */
	@Transactional(readOnly = true)
	public Map<String, Object> getUserInfo(@AuthenticationPrincipal UserDetails userDetails){
		return toDoRepository.getUserInfo(userDetails);
	}
	
	
	/**
	 * ToDo作成
	 * @param ToDo todo
	 * @return boolean result
	 */
	public boolean insertRecord(ToDo todo) {
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
	public boolean updateRecord(ToDo todo) throws DataAccessException {
		
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
	public boolean deleteRecord(Long id) throws DataAccessException {
		int num = toDoRepository.deleteRecord(id);
		
		boolean result = false;
		if(num > 0) {
			result = true;
		}
		
		return result;
	}
	
	
}
