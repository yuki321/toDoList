package com.example.todolist.service;

import java.util.List;
import java.util.Map;

import com.example.todolist.entity.PasswordReset;

public interface MailServiceIF {

	List<String> checkPassword(PasswordReset passwordReset, String rawToken);

	public void sendPasswordResetEmail(String email, String token);
	
	public void sendUserCreateEmail(String email, String token);
	
	
}
