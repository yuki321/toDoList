package com.example.todolist.service;

import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.example.todolist.entity.PasswordChange;
import com.example.todolist.entity.User;

public interface UserServiceIF {

	List<User> getAllUsers();

	List<User> searchUsers(String userName, String email, String role);

	Optional<User> getUserById(Long id);

	List<User> findByEmail(String email);

	User createUser(User user);

	User updateUser(Long id, User user);

	void deleteUser(Long id);

	User savePassword(User user, String newPassword);

	List<String> checkPassword(PasswordChange form, User user);

	String getDbPassword(User user);

	void uploadCsvFile(MultipartFile file) throws Exception;

	
}
