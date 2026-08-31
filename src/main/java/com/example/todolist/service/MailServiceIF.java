package com.example.todolist.service;

import java.util.List;
import java.util.Map;

import org.springframework.ui.Model;

import com.example.todolist.entity.PasswordChange;
import com.example.todolist.entity.PasswordReset;

public interface MailServiceIF {

	List<String> checkPassword(PasswordReset passwordReset, String rawToken);

	public void sendPasswordResetEmail(String email, String token);
	
	public void sendUserCreateEmail(String email, String token);
	
	public String sendMailProcess(final PasswordChange mail, Model model);
	
}
