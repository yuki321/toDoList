package com.example.todolist.repository;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import com.example.todolist.entity.ToDo;
import com.example.todolist.entity.User;


@Repository
public interface ToDoRepositoryImpl {

    public List<ToDo> findAllToDo(@AuthenticationPrincipal UserDetails userDetails);

}



