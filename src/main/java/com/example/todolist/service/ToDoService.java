package com.example.todolist.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.todolist.entity.ToDo;
import com.example.todolist.entity.User;
import com.example.todolist.repository.ToDoRepository;
import com.example.todolist.repository.UserRepository;

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
	 * ログイン中のユーザー情報の取得
	 * @param UserDetails userDetails
	 * @return Map<String, Object>
	 */
	@Transactional(readOnly = true)
	public Map<String, Object> getUserInfo(@AuthenticationPrincipal UserDetails userDetails){
		return toDoRepository.getUserInfo(userDetails);
	}
	

	
	/**
	 * 	ユーザー作成
	 * @param User user
	 * @return User
	 */
//	public User createUser(User user) {
//		
//		if(userRepository.existsByUserName(user.getUserName())) {
//			throw new IllegalArgumentException("すでにそのユーザー名は存在しています");			
//		}
//		if(userRepository.existsByEmail(user.getEmail())) {
//			throw new IllegalArgumentException("すでにそのメールアドレスは存在しています");			
//		}
//		
//
//		user.setId(user.getId());
//		user.setUserName(user.getUserName());
//		user.setEmail(user.getEmail());
//		user.setPassword(passwordEncoder.encode(user.getPassword()));
//		user.setRole(user.getRole());
//		user.setEnabled(true);
//		user.setAccountNonExpired(true);
//		user.setCredentialsNonExpired(true);
//		user.setAccountNonLocked(true);
//		user.setCreatedAt(LocalDateTime.now());
//		user.setUpdatedAt(LocalDateTime.now());
//		
//		return userRepository.save(user);
//	}
	
	/**
	 * 更新
	 * @param Long id
	 * @param User user
	 * @param String userName
	 * @param String email
	 * @return User
	 */
//	public User updateUser(Long id, User user) {
//		
//		User updatingUser = userRepository.findById(id)
//		        .orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません"));
//		
//		if(!updatingUser.getUserName().equals(user.getUserName())
//		        && userRepository.existsByUserName(user.getUserName())) {
//			throw new IllegalArgumentException("すでにそのユーザー名は存在しています");			
//		}
//		if(!updatingUser.getEmail().equals(user.getEmail())
//		        && userRepository.existsByEmail(user.getEmail())) {
//			throw new IllegalArgumentException("すでにそのメールアドレスは存在しています");			
//		}
//		
//		
//		updatingUser.setUserName(user.getUserName());
//		updatingUser.setEmail(user.getEmail());
//
//		if(user.getId() == 1) {
//			updatingUser.setRole("Admin");
//		}else{
//			updatingUser.setRole(user.getRole());
//		}
//		
//		updatingUser.setUpdatedAt(LocalDateTime.now());		
//		
//		return userRepository.save(updatingUser);
//	}
	
	/**
	 * 削除
	 * @param Long id
	 */
//	@Transactional
//	public void deleteUser(Long id) {
//		
//		if(!userRepository.existsById(id)) {
//			throw new IllegalArgumentException("ユーザーが存在しません");
//		}
//		
//		userRepository.deleteById(id);
//	}
	
	
}
