package com.example.todolist.repository;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;

public interface PasswordResetRepositoryIF {
	
	int updateResetTokenUsedAt(Long userId);
	
	int resetPassword(Long userId, String newPassword) throws DataAccessException;

	int deleteRecord(Long userId) throws DataAccessException;

	int selectCountByUserId(Long userId) throws DataAccessException;

	List<Map<String, Object>> findAllTokenHash() throws DataAccessException;

	int insertRecord(Long userId, String tokenHash) throws DataAccessException;

	int deleteResetToken(Long userId) throws DataAccessException;

	
}
