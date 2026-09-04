package todolist.repository;

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
	 * @return int
	 */
	@Override
	@Transactional
	public int updateResetTokenUsedAt(final String email) {
		
		final String sql = "UPDATE password_reset_tokens SET used_at = ? WHERE email=?";
		return jdbc.update(sql, LocalDateTime.now(), email);
	}
	
	
	/**
	 * 3.Userテーブルのパスワードを更新
	 * @param String email
	 * @param String newPassword
	 * @return int
	 */
	@Override
	@Transactional
	public int resetPassword(final String email, final String newPassword) {
		final String sql = "UPDATE users SET password = ? WHERE email=?";

		final String encodedPassword = passwordEncoder.encode(newPassword);
		return jdbc.update(sql, encodedPassword, email);
	}
	
	
	/**
	 * 4.password-reset-tokensテーブルの該当レコードを削除する
	 * @param String email
	 * @return int num
	 */
	@Override
	@Transactional
	public int deleteRecord(final String email) {
		final String sql = "DELETE FROM password_reset_tokens WHERE email=?";
		return jdbc.update(sql, email);
	}
	
	
	
	/**
	 * メールアドレスに紐づくパスワードリセットトークンの件数を取得する
	 * @param String email
	 * @return int
	 */
	@Override
	public int selectCountByUserId(final String email) {
		final String sql = "SELECT COUNT(*) FROM password_reset_tokens WHERE email = ?";
		return jdbc.queryForObject(sql, Integer.class, email);
	}
	
	/**
	 * すべてのパスワードリセットトークン（リスト）を取得する
	 * @return List<Map<String, Object>> 
	 */
	@Override
	public List<Map<String, Object>> findAllTokenHash() {
		final String sql = "SELECT token_hash, expires_at FROM password_reset_tokens";
		return jdbc.queryForList(sql);
	}
	
	
	/**
	 * パスワードリセットトークンのレコードを挿入する
	 * @param String email
	 * @param tokenHash トークンのハッシュ値
	 * @return 1:挿入成功, 0:挿入失敗
	 */
	@Override
	public int insertRecord(final String email, final String tokenHash) {
		
		// トークンの有効期限（時間単位）
		final int EXPIRATION_HOUR_UNIT = 1; 
		
		final String sql = "INSERT INTO password_reset_tokens "
				+ "(email, token_hash, created_at, expires_at, used_at) "
				+ "VALUES(?, ?, ?, ?, ?)";
		final int num = jdbc.update(sql,
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
	 * @return int
	 */	
	@Override
	public int deleteResetToken(final String email) {
		
		final String sql = "DELETE FROM password_reset_tokens WHERE email = ?";
		return jdbc.update(sql, email);
	}

}


