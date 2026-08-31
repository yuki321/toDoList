package com.example.todolist.service;

import java.util.List;

import org.springframework.ui.Model;

import com.example.todolist.entity.PasswordChange;
import com.example.todolist.entity.PasswordReset;

public interface MailServiceIF {

	List<String> checkPassword(final PasswordReset passwordReset, final String rawToken);

	public void sendPasswordResetEmail(final String email, final String token);
	
	public void sendUserCreateEmail(final String email, final String token);
	
	public String sendMailProcess(final PasswordChange mail, Model model);
	
}
