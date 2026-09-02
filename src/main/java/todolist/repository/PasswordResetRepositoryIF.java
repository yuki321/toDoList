package todolist.repository;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;

public interface PasswordResetRepositoryIF {
	
	int updateResetTokenUsedAt(final String email);
	
	int resetPassword(final String email, final String newPassword) throws DataAccessException;

	int deleteRecord(final String email) throws DataAccessException;

	int selectCountByUserId(final String email) throws DataAccessException;

	List<Map<String, Object>> findAllTokenHash() throws DataAccessException;

	int insertRecord(final String email, final String tokenHash) throws DataAccessException;

	int deleteResetToken(final String email) throws DataAccessException;

	
}
