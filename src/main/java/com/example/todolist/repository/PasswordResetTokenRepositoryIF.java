package com.example.todolist.repository;

import java.util.List;

import org.springframework.dao.DataAccessException;

public interface PasswordResetTokenRepositoryIF {
	int selectCountByUserId(Long userId);
	
	List<java.util.Map<String, Object>> findAllTokenHash();

	int insertRecord(Long userId, String tokenHash);
	
	int deleteResetToken(Long userId) throws DataAccessException;

}
