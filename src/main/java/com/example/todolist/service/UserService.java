package com.example.todolist.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import com.example.todolist.entity.PasswordChange;
import com.example.todolist.entity.User;
import com.example.todolist.repository.UserRepository;

@Service
@Transactional
public class UserService {
	
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
	@Transactional(readOnly = true)
	public List<User> getAllUsers(){
		return userRepository.findAll();
	}
	
	
	@Transactional(readOnly = true)
	public List<User> searchUsers(String userName, String email, String role){
    	
    	List<User> userlist = new ArrayList<>();
    	String sql = "SELECT * FROM users "
    			+ "WHERE user_name LIKE ? AND email LIKE ? AND role LIKE ?";
    	List<Map<String, Object>> resultList = jdbc.queryForList(sql, userName, email, role);
    	
    	
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
    	
    	return userlist;
	}
	
	
	
	/**
	 * IDで指定して取得
	 * @param Long id
	 * @return User user
	 */
	@Transactional(readOnly = true)
	public Optional<User> getUserById(Long id){
		return userRepository.findById(id);
	}
	
	/**
	 * ユーザー名で指定して取得
	 * @param String email
	 * @return List<User> user
	 */
	public List<User> findByEmail(String email){
		return userRepository.findByEmail(email);
	}
	
	
	/**
	 * 	ユーザー作成
	 * @param User user
	 * @return User
	 */
	public User createUser(User user) {
		
		if(userRepository.existsByUserName(user.getUserName())) {
			throw new IllegalArgumentException("すでにそのユーザー名は存在しています");			
		}
		if(userRepository.existsByEmail(user.getEmail())) {
			throw new IllegalArgumentException("すでにそのメールアドレスは存在しています");			
		}
		

		user.setId(user.getId());
		user.setUserName(user.getUserName());
		user.setEmail(user.getEmail());
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setRole(user.getRole());
		user.setEnabled(true);
		user.setAccountNonExpired(true);
		user.setCredentialsNonExpired(true);
		user.setAccountNonLocked(true);
		user.setCreatedAt(LocalDateTime.now());
		user.setUpdatedAt(LocalDateTime.now());
		
		return userRepository.save(user);
	}
	
	/**
	 * 更新
	 * @param Long id
	 * @param User user
	 * @param String userName
	 * @param String email
	 * @return User
	 */
	public User updateUser(Long id, User user) {
		
		User updatingUser = userRepository.findById(id)
		        .orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません"));
		
		if(!updatingUser.getUserName().equals(user.getUserName())
		        && userRepository.existsByUserName(user.getUserName())) {
			throw new IllegalArgumentException("すでにそのユーザー名は存在しています");			
		}
		if(!updatingUser.getEmail().equals(user.getEmail())
		        && userRepository.existsByEmail(user.getEmail())) {
			throw new IllegalArgumentException("すでにそのメールアドレスは存在しています");			
		}
		
		
		updatingUser.setUserName(user.getUserName());
		updatingUser.setEmail(user.getEmail());

		if(user.getId() == 1) {
			updatingUser.setRole("Admin");
		}else{
			updatingUser.setRole(user.getRole());
		}
		
		updatingUser.setUpdatedAt(LocalDateTime.now());		
		
		return userRepository.save(updatingUser);
	}
	
	/**
	 * 削除
	 * @param Long id
	 */
	@Transactional
	public void deleteUser(Long id) {
		
		if(!userRepository.existsById(id)) {
			throw new IllegalArgumentException("ユーザーが存在しません");
		}
		
		userRepository.deleteById(id);
	}
	
	/**
	 * パスワード変更
	 * @param User user
	 * @param String newPassword
	 * @return
	 */
	@Transactional
	public User savePassword(User user, String newPassword) {
		
		String sql = "SELECT * FROM users WHERE id=?";
		Map<String, Object> getMap = jdbc.queryForMap(sql, user.getId());
		String userName = (String)getMap.get("user_name");
		String email =  (String)getMap.get("email");
		String role =  (String)getMap.get("role");
		LocalDateTime updatedAt =  (LocalDateTime)getMap.get("updated_at");
		
		String encodedPassword = passwordEncoder.encode(newPassword);
		user.setUserName(userName);
		user.setEmail(email);
		user.setPassword(encodedPassword);
		user.setRole(role);
		user.setUpdatedAt(updatedAt);
		
		return userRepository.save(user);
	}
	
	
	/**
	 * パスワードのチェック
	 * @param Password form
	 * @param UserDetails userDetails
	 * @return List<String>
	 */
	public List<String> checkPassword(PasswordChange form, User user) {
		
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
	private String getDbPassword(@PathVariable User user) {
		
		String sql = "SELECT password FROM users WHERE id=?";
		Map<String, Object> getMap = jdbc.queryForMap(sql, user.getId());
		String password = (String)getMap.get("password");
		
		return password;
	}
	
	
	@Transactional
	public void uploadCsvFile(MultipartFile file) throws Exception {
		// CSVファイルを読み込む
		try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
			String line;
			//ヘッダーレコードを飛ばすために１行だけ読み取る
            line = br.readLine();
			
			while ((line = br.readLine()) != null) {
				
				
				String[] values = line.split(",");
				if (values.length >= 4) {
					User user = new User();
					user.setUserName(values[0].trim());
					user.setEmail(values[1].trim());
					user.setPassword(passwordEncoder.encode(values[2].trim()));
					if(values[3].trim().equals("1")) {
						user.setRole("Admin");
					} else if(values[3].trim().equals("2")) {
						user.setRole("General");
					} else {
						throw new IllegalArgumentException("不正なロールが指定されています: " + values[3].trim());
					}
//					user.setRole(values[3].trim());
					user.setEnabled(true);
					user.setAccountNonExpired(true);
					user.setCredentialsNonExpired(true);
					user.setAccountNonLocked(true);
					user.setCreatedAt(LocalDateTime.now());
					user.setUpdatedAt(LocalDateTime.now());
					
					// ユーザーを保存
					createUser(user);
				}
			}
		} catch (IOException e) {
			throw new Exception("CSVファイルの読み込みに失敗しました", e);
		}

		
	}
	
	
}


