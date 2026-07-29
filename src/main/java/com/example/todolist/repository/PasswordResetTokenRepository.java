package com.example.todolist.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import com.example.todolist.entity.ToDo;
import com.example.todolist.service.UserService;

@Repository
public class PasswordResetTokenRepository {
	
	@Autowired
	JdbcTemplate jdbc;
	
	/**
	 * ユーザーIDに紐づくパスワードリセットトークンの件数を取得する
	 * @param Long userId
	 * @return int count
	 * @throws 
	 */
	public int selectCountByUserId(Long userId) throws DataAccessException {
		String sql = "SELECT COUNT(*) FROM password_reset_tokens WHERE user_id = ?";
		int count = jdbc.queryForObject(sql, Integer.class, userId);
		return count;
	}
	
	/**
	 * すべてのパスワードリセットトークンを取得する
	 * @return List<Map<String, Object>> list
	 * @throws DataAccessException
	 */
	public List<Map<String, Object>> findAllTokenHash() throws DataAccessException {
		String sql = "SELECT token_hash, expires_at FROM password_reset_tokens";
		List<Map<String, Object>> list = jdbc.queryForList(sql);
		
		return list;
	}
	
	
	/**
	 * パスワードリセットトークンのレコードを挿入する
	 * @param userId ユーザーID
	 * @param tokenHash トークンのハッシュ値
	 * @return 1:挿入成功, 0:挿入失敗
	 * @throws DataAccessException データアクセス例外
	 */
	public int insertRecord(Long userId, String tokenHash) throws DataAccessException {
		
		// トークンの有効期限（時間単位）
		int EXPIRATION_HOUR_UNIT = 1; 
		
		String sql = "INSERT INTO password_reset_tokens "
				+ "(user_id, token_hash, created_at, expires_at, used_at) "
				+ "VALUES(?, ?, ?, ?, ?)";
		int num = jdbc.update(sql,
				userId,
				tokenHash,
				LocalDateTime.now(),
				// 有効期限を1時間後に設定
				LocalDateTime.now().plusHours(EXPIRATION_HOUR_UNIT), 
				// used_atはnullで初期化(トークンを利用した時間)
				null );
		
		return num;
	}
	
	/**
	 * ユーザーIDに紐づくパスワードリセットトークンのレコードを削除する
	 * @param Long userId
	 * @return int num 
	 * @throws 
	 */	
	public int deleteResetToken(Long userId) throws DataAccessException {
		
		String sql = "DELETE FROM password_reset_tokens WHERE user_id = ?";
		int num = jdbc.update(sql, userId);
		
		return num;
	}
	
	

}


