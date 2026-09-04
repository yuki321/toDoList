package todolist.repository;

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
	 * @return int
	 * @throws 
	 */
	@Override
	public int selectCountByEmail(final String email) throws DataAccessException {
		final String sql = "SELECT COUNT(*) FROM password_reset_tokens WHERE email = ?";
		return jdbc.queryForObject(sql, Integer.class, email);
	}
	
	/**
	 * すべてのパスワードリセットトークンを取得する
	 * @return List<Map<String, Object>> 
	 * @throws DataAccessException
	 */
	@Override
	public List<Map<String, Object>> findAllTokenHash() throws DataAccessException {
		final String sql = "SELECT * FROM password_reset_tokens";
		return jdbc.queryForList(sql);
	}
	
	
	/**
	 * パスワードリセットトークンのレコードを挿入する
	 * @param String email
	 * @param String tokenHash トークンのハッシュ値
	 * @return int 1:挿入成功, 0:挿入失敗
	 * @throws DataAccessException データアクセス例外
	 */
	@Override
	public int insertRecord(final String email, final String tokenHash) throws DataAccessException {
		
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
	 * @throws 
	 */	
	@Override
	public int deleteResetToken(final String email) throws DataAccessException {
		
		final String sql = "DELETE FROM password_reset_tokens WHERE email = ?";
		return jdbc.update(sql, email);
	}
	

}


