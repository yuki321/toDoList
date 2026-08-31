package com.example.todolist.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public class PasswordResetRepository implements PasswordResetRepositoryIF {
	
	@Autowired
	JdbcTemplate jdbc;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	/**
	 * 1.password-reset-tokensテーブルからメールアドレスを取得
	 *  ServiceクラスでpasswordResetTokenRepository.findAllTokenHash()を利用して
	 *  メールアドレスを取得
	 */

	
	
	/**
	 * 2.password-reset-tokensテーブルのuserd_atにタイムスタンプを格納
	 * @param String email
	 * @return int num
	 */
	@Override
	@Transactional
	public int updateResetTokenUsedAt(String email) {
		
		String sql = "UPDATE password_reset_tokens "
				+ "SET used_at = ? WHERE email=?";
		int num = jdbc.update(sql, LocalDateTime.now(), email);
			
		return num;
	}
	
	
	/**
	 * 3.Userテーブルのパスワードを更新
	 * @param String email
	 * @param String newPassword
	 * @return int num
	 */
	@Override
	@Transactional
	public int resetPassword(String email, String newPassword) {
		String sql = "UPDATE users SET password = ? WHERE email=?";

		String encodedPassword = passwordEncoder.encode(newPassword);
		int num = jdbc.update(sql, encodedPassword, email);
		
		return num;
	}
	
	
	/**
	 * 4.password-reset-tokensテーブルの該当レコードを削除する
	 * @param String email
	 * @return int num
	 */
	@Override
	@Transactional
	public int deleteRecord(String email) {
		String sql = "DELETE FROM password_reset_tokens WHERE email=?";
		int num = jdbc.update(sql, email);

		return num;
	}
	
	
	
	/**
	 * メールアドレスに紐づくパスワードリセットトークンの件数を取得する
	 * @param String email
	 * @return int count
	 */
	@Override
	public int selectCountByUserId(String email) {
		String sql = "SELECT COUNT(*) FROM password_reset_tokens WHERE email = ?";
		int count = jdbc.queryForObject(sql, Integer.class, email);
		return count;
	}
	
	/**
	 * すべてのパスワードリセットトークンを取得する
	 * @return List<Map<String, Object>> list
	 */
	@Override
	public List<Map<String, Object>> findAllTokenHash() {
		String sql = "SELECT token_hash, expires_at FROM password_reset_tokens";
		List<Map<String, Object>> list = jdbc.queryForList(sql);
		
		return list;
	}
	
	
	/**
	 * パスワードリセットトークンのレコードを挿入する
	 * @param String email
	 * @param tokenHash トークンのハッシュ値
	 * @return 1:挿入成功, 0:挿入失敗
	 */
	@Override
	public int insertRecord(String email, String tokenHash) {
		
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
	 */	
	@Override
	public int deleteResetToken(String email) {
		
		String sql = "DELETE FROM password_reset_tokens WHERE email = ?";
		int num = jdbc.update(sql, email);
		
		return num;
	}
	

}


