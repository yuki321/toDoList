package todolist.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import todolist.common.Logger;
import todolist.entity.PasswordChange;
import todolist.entity.User;
import todolist.repository.UserRepository;

@Service
@Transactional
public class UserService implements UserServiceIF {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired 
	private JdbcTemplate jdbc;
	
	
	
	/**
	 * 全件取得
	 * @return List<User>
	 */
	@Override
	@Transactional(readOnly = true)
	public List<User> getAllUsers(){
		Logger.log(this.getClass().getSimpleName(), "getAllUsers: ユーザーデータ全件取得処理");
		return userRepository.findAll();
	}
	
	
	/**
	 * 全件取得（Pager）
	 * @param Pageable pageable
	 * @return Page<User>
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<User> getAllUsers(final Pageable pageable){
		Logger.log(this.getClass().getSimpleName(), "getAllUsers: ユーザーデータ全件取得処理(Pager)");
		return userRepository.findAll(pageable);
	}
	
	
	/**
	 * ユーザー検索
	 * @param User _user
	 * @param Pageable pageable
	 * @return Page<User>
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<User> searchUsers(final User _user, final Pageable pageable){
    	
		Logger.log(this.getClass().getSimpleName(), "searchUsers: ユーザー検索処理");
		// 検索条件を取得
		String userName = _user.getUserName();
		String email = _user.getEmail();
		String role = _user.getRole();
		
		if(userName == null || userName.isEmpty()) {
			userName = "%";
		}else {
			userName = "%" + userName + "%";
		}
		
		if(email == null || email.isEmpty()) {
			email = "%";
		}else {
			email = "%" + email + "%";
		}
		
		if(role == null || role.isEmpty()) {
			role = "%";
		}else {
			role = "%" + role + "%";
		}

		
		/**
		 * 1.クエリを実行
		 * 2.クエリ実行結果をモデルに詰める
		 */
    	String sql = "SELECT * FROM users "
    			+ "WHERE user_name LIKE ? AND email LIKE ? AND role LIKE ? "
    			+ "ORDER BY role ASC, updated_at DESC ";

    	List<Map<String, Object>> resultList = jdbc.queryForList(sql, userName, email, role);

    	
    	List<User> userlist = new ArrayList<>();
    	for(Map<String, Object> map: resultList) {
			User user = new User();
			user.setId((Long)map.get("id"));
			user.setUserName((String)map.get("user_name"));
			user.setEmail((String)map.get("email"));
			user.setRole((String)map.get("role"));
			user.setCreatedAt((LocalDateTime)map.get("created_at"));
			user.setUpdatedAt((LocalDateTime)map.get("updated_at"));
			
			userlist.add(user);
		}
    	
    	
    	return new PageImpl<>(userlist, pageable, userlist.size());
	}
	
	
	/**
	 * IDで指定して取得
	 * @param Long id
	 * @return User user
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<User> getUserById(final Long id){
		Logger.log(this.getClass().getSimpleName(), "getUserById: ユーザーをIDで指定して取得");
		return userRepository.findById(id);
	}
	
	/**
	 * ユーザー名で指定して取得
	 * @param String email
	 * @return List<User> user
	 */
	@Override
	public List<User> findByEmail(final String email){
		Logger.log(this.getClass().getSimpleName(), "findByEmail: ユーザーをメールアドレスで指定して取得");
		return userRepository.findByEmail(email);
	}
	
	
	/**
	 * パスワードのチェック
	 * @param Password form
	 * @param UserDetails userDetails
	 * @return List<String>
	 */
	@Override
	public List<String> checkPassword(final PasswordChange form, final User user) {
		
		Logger.log(this.getClass().getSimpleName(), "checkPassword: パスワードチェック");
		List<String> errors = new ArrayList<>();
		
		String dbPassword = getDbPassword(user);
		
		// 現在のパスワード（入力）
		String currentPW = form.getCurrentPassword();
		// 新規パスワード（入力）
		String newPW = form.getNewPassword();
		// 確認パスワード（入力）
		String confirmPW = form.getConfirmPassword();
		
		
		// 現在のパスワードと入力したパスワードが一致しているかチェック
		if(!passwordEncoder.matches(currentPW, dbPassword)) {
			errors.add("現在のパスワードと一致しません");
		}
		// 現在のパスワードと新しいパスワードが一致しているかチェック（一致しているとエラー）
		if(currentPW.equals(newPW) || currentPW.equals(confirmPW)) {
			errors.add("現在のパスワードと新しいパスワードが一致しています");
		}
		// 新しいパスワードが一致してしているかチェック
		if(!newPW.equals(confirmPW)) {
			errors.add("新しいパスワードが一致していません");
		}
		
		return errors;
	}
	
	/**
	 * ログインユーザーのパスワードをDBから取得する
	 * @param User user
	 * @return String password
	 */
	private String getDbPassword(@PathVariable final User user) {
		
		Logger.log(this.getClass().getSimpleName(), "getDbPassword: ログインユーザーのパスワードをDBから取得");
		String sql = "SELECT password FROM users WHERE id=?";
		Map<String, Object> getMap = jdbc.queryForMap(sql, user.getId());
		String password = (String)getMap.get("password");
		
		return password;
	}
	
	
	/**
	 * バリデーションエラー時に表示用の項目を補完する
	 * @param Long id
	 * @param User user
	 */
	public void restoreUserDisplayFields(final Long id, final User user) {
		
		Logger.log(this.getClass().getSimpleName(), "restoreUserDisplayFields: バリデーションエラー時に表示用の項目を補完");
		getUserById(id).ifPresent(existing -> {
			user.setId(existing.getId());
			user.setRole(existing.getRole());
			user.setCreatedAt(existing.getCreatedAt());
			user.setUpdatedAt(existing.getUpdatedAt());
		});
	}
	
	
	/**
	 * ユーザーIDでS3の画像URLを取得する
	 * @param Long id
	 * @return
	 */
	@Override
    public String getS3ImageUrlByUserId(final Long id) {
    	
    	final String sql = "SELECT s3_image_url FROM users WHERE id = " + id;
    	try {
			final Map<String, Object> result = jdbc.queryForMap(sql);
			
			String s3ImageUrl = (String) result.get("s3_image_url");
			// S3の画像URLが存在しない場合はnullを返す
			if (s3ImageUrl == null || s3ImageUrl.isEmpty()) {
				return null; 
			}
			return result.get("s3_image_url").toString();
		} catch (Exception e) {
			Logger.log(this.getClass().getSimpleName(), "getS3ImageUrlByUserId: ユーザーIDでS3の画像URLを取得する処理でエラーが発生しました。");
			Logger.log(this.getClass().getSimpleName(), "Error: " + e.getMessage());
			return null;
		}
    	
    }
	
	
}


