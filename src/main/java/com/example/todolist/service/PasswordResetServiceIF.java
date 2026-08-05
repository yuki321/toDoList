package com.example.todolist.service;

import org.springframework.ui.Model;

public interface PasswordResetServiceIF {

	boolean passwordResetTransanction(String rawToken, String newPassword, Model model);

}
