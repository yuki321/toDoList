package com.example.todolist.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


@Repository
public class PasswordResetTokenRepository implements PasswordResetTokenRepositoryIF {
	
	@Autowired
	JdbcTemplate jdbc;
	
	/**
	 * メールアドレスに紐づくパスワードリセットトークンの件数を取得する
	 * @param String email
	 * @return int count
	 * @throws 
	 */
	@Override
	public int selectCountByEmail(String email) throws DataAccessException {
		String sql = "SELECT COUNT(*) FROM password_reset_tokens WHERE email = ?";
		int count = jdbc.queryForObject(sql, Integer.class, email);
		return count;
	}
	
	/**
	 * すべてのパスワードリセットトークンを取得する
	 * @return List<Map<String, Object>> list
	 * @throws DataAccessException
	 */
	@Override
	public List<Map<String, Object>> findAllTokenHash() throws DataAccessException {
		String sql = "SELECT * FROM password_reset_tokens";
		List<Map<String, Object>> list = jdbc.queryForList(sql);
	
		return list;
	}
	
	
	/**
	 * パスワードリセットトークンのレコードを挿入する
	 * @param String email
	 * @param String tokenHash トークンのハッシュ値
	 * @return int 1:挿入成功, 0:挿入失敗
	 * @throws DataAccessException データアクセス例外
	 */
	@Override
	public int insertRecord(String email, String tokenHash) throws DataAccessException {
		
		// トークンの有効期限（時間単位）
		int EXPIRATION_HOUR_UNIT = 1; 
		
		String sql = "INSERT INTO password_reset_tokens "
				+ "(email, token_hash, created_at, expires_at, used_at) "
				+ "VALUES(?, ?, ?, ?, ?)";
		int num = jdbc.update(sql,
				email,
				tokenHash,
				LocalDateTime.now(),
				// 有効期限を1時間後に設定
				LocalDateTime.now().plusHours(EXPIRATION_HOUR_UNIT), 
				// used_atはnullで初期化(トークンを利用した時間)
				null );
		
		return num;
	}
	
	/**
	 * メールアドレスに紐づくパスワードリセットトークンのレコードを削除する
	 * @param String email
	 * @return int num 
	 * @throws 
	 */	
	@Override
	public int deleteResetToken(String email) throws DataAccessException {
		
		String sql = "DELETE FROM password_reset_tokens WHERE email = ?";
		int num = jdbc.update(sql, email);
		
		return num;
	}
	
	

}


