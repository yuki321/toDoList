package com.example.todolist.repository;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;

public interface PasswordResetRepositoryIF {
	
	int updateResetTokenUsedAt(String email);
	
	int resetPassword(String email, String newPassword) throws DataAccessException;

	int deleteRecord(String email) throws DataAccessException;

	int selectCountByUserId(String email) throws DataAccessException;

	List<Map<String, Object>> findAllTokenHash() throws DataAccessException;

	int insertRecord(String email, String tokenHash) throws DataAccessException;

	int deleteResetToken(String email) throws DataAccessException;

	
}
