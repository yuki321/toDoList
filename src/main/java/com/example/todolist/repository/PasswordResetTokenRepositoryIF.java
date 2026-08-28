package com.example.todolist.repository;

import java.util.List;

import org.springframework.dao.DataAccessException;

public interface PasswordResetTokenRepositoryIF {
	int selectCountByEmail(String email);
	
	List<java.util.Map<String, Object>> findAllTokenHash();

	int insertRecord(String email, String tokenHash);
	
	int deleteResetToken(String email) throws DataAccessException;

}
