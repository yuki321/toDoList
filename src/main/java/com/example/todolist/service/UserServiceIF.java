package com.example.todolist.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import com.example.todolist.entity.PasswordChange;
import com.example.todolist.entity.User;

public interface UserServiceIF {

	List<User> getAllUsers();
	
	Page<User> getAllUsers(Pageable pageable);

	Page<User> searchUsers(User user, Pageable pageable);

	Optional<User> getUserById(Long id);

	List<User> findByEmail(String email);

	User createUser(User user);

	User updateUser(Long id, User user);

	void deleteUser(Long id);

	User savePassword(User user, String newPassword);

	List<String> checkPassword(PasswordChange form, User user);

	List<String> uploadCsvFile(MultipartFile file) throws Exception;
	
	void downloadCsvFile() throws Exception;

	void restoreUserDisplayFields(Long id, User user);
	
	
	
	
	
}
