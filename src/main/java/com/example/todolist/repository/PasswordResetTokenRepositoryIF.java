package com.example.todolist.repository;

import java.util.List;

import org.springframework.dao.DataAccessException;

public interface PasswordResetTokenRepositoryIF {
	int selectCountByEmail(final String email);
	
	List<java.util.Map<String, Object>> findAllTokenHash();

	int insertRecord(final String email, final String tokenHash);
	
	int deleteResetToken(final String email) throws DataAccessException;

}
