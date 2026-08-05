package com.example.todolist.service;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.todolist.entity.ToDo;

@Service
public interface ToDoServiceIF {
	
	List<ToDo> findAllToDo(UserDetails userDetails);

	List<ToDo> findAllCompletedToDo(UserDetails userDetails);

	boolean completeTask(ToDo todo) throws DataAccessException;

	boolean undoCompletedTask(ToDo todo) throws DataAccessException;

	Map<String, Object> getUserInfo(UserDetails userDetails);

	boolean insertRecord(ToDo todo);

	boolean updateRecord(ToDo todo) throws DataAccessException;

	boolean deleteRecord(Long id) throws DataAccessException;
	

}
