package com.example.todolist.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.todolist.entity.User;
import com.example.todolist.repository.UserRepository;

@Service
@Transactional
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	/**
	 * 全件取得
	 * @return List<User>
	 */
	@Transactional(readOnly = true)
	public List<User> getAllUsers(){
		return userRepository.findAll();
	}
	
	/**
	 * IDで指定して取得
	 * @param Long id
	 * @return User user
	 */
	@Transactional(readOnly = true)
	public Optional<User> getUserById(Long id){
		return userRepository.findById(id);
	}
	
	/**
	 * 	ユーザー作成
	 * @param User user
	 * @return User
	 */
	public User createUser(User user) {
		
		if(userRepository.existsByUserName(user.getUserName())) {
			throw new IllegalArgumentException("すでにそのユーザー名は存在しています");			
		}
		if(userRepository.existsByEmail(user.getEmail())) {
			throw new IllegalArgumentException("すでにそのメールアドレスは存在しています");			
		}
		

		user.setId(user.getId());
		user.setUserName(user.getUserName());
		user.setEmail(user.getEmail());
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setRole(user.getRole());
		user.setEnabled(true);
		user.setAccountNonExpired(true);
		user.setCredentialsNonExpired(true);
		user.setAccountNonLocked(true);
		user.setCreatedAt(LocalDateTime.now());
		user.setUpdatedAt(LocalDateTime.now());
		
		return userRepository.save(user);
	}
	
	/**
	 * 更新
	 * @param Long id
	 * @param User user
	 * @return User
	 */
	public User updateUser(Long id, User user) {
		
		User updatingUser = userRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません"));
		
		updatingUser.setUserName(user.getUserName());
		updatingUser.setEmail(user.getEmail());
		updatingUser.setUpdatedAt(LocalDateTime.now());		
		
		return userRepository.save(updatingUser);
	}
	
	/**
	 * 削除
	 * @param Long id
	 */
	@Transactional
	public void deleteUser(Long id) {
		
		if(!userRepository.existsById(id)) {
			throw new IllegalArgumentException("ユーザーが存在しません");
		}
		
		userRepository.deleteById(id);
	}
	
	
}
