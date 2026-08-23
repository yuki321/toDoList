package com.example.todolist.repository;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import com.example.todolist.entity.ToDo;


@Repository
public interface ToDoRepositoryIF {

    public List<Map<String, Object>> findAllToDo(@AuthenticationPrincipal UserDetails userDetails);
    
    public List<Map<String, Object>> findAllCompletedToDo(UserDetails userDetails);
    
    int completeTask(ToDo todo) throws DataAccessException;

    int undoCompletedTask(ToDo todo) throws DataAccessException;

    Map<String, Object> getUserInfo(UserDetails userDetails);
    
    public int insertRecord(ToDo todo) throws DataAccessException;
    
    public int updateRecord(ToDo todo) throws DataAccessException;
    
    public int deleteRecord(Long id) throws DataAccessException;


}



