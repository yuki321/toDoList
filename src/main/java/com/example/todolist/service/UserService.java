package com.example.todolist.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.ByteOrderMark;
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
import tools.jackson.dataformat.csv.CsvMapper;
import tools.jackson.dataformat.csv.CsvSchema;

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
		return userRepository.findAll();
	}
	
	
	/**
	 * ユーザー検索
	 * @param User _user
	 * @return List<User>
	 */
	@Override
	@Transactional(readOnly = true)
	public List<User> searchUsers(User _user){
    	
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
    	List<User> userlist = new ArrayList<>();
    	String sql = "SELECT * FROM users "
    			+ "WHERE user_name LIKE ? AND email LIKE ? AND role LIKE ? ORDER BY role ASC, updated_at DESC";
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
	@Override
	@Transactional(readOnly = true)
	public Optional<User> getUserById(Long id){
		return userRepository.findById(id);
	}
	
	/**
	 * ユーザー名で指定して取得
	 * @param String email
	 * @return List<User> user
	 */
	@Override
	public List<User> findByEmail(String email){
		return userRepository.findByEmail(email);
	}
	
	
	/**
	 * 	ユーザー作成
	 * @param User user
	 * @return User
	 */
	@Override
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
	 * CSVファイルのデータを読み込みユーザーを作成
	 * @param User user
	 * @return
	 */
	public User createUserFromCsv(User user) {
		
		user.setId(user.getId());
		user.setUserName(user.getUserName());
		user.setEmail(user.getEmail());
		user.setPassword(user.getPassword());
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
	 * @return User
	 */
	@Override
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
	@Override
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
	@Override
	@Transactional
	public User savePassword(User user, String newPassword) {
		
		String sql = "SELECT * FROM users WHERE id=?";
		Map<String, Object> getMap = jdbc.queryForMap(sql, user.getId());
		String userName = (String)getMap.get("user_name");
		String email =  (String)getMap.get("email");
		String role =  (String)getMap.get("role");
		LocalDateTime updatedAt = LocalDateTime.now();
		
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
	@Override
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
	
	
	/**
	 * CSVファイルアップロード
	 * @param MultipartFile file
	 */
	@Override
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
					
					String userNameFromCSV = values[0].trim();
					String emailFromCSV = values[1].replace("\"", "").trim();
					
					/**
					 * レコードの各項目のチェック
			 		 * nullまたは空白文字または長さ0の文字列の時、falseを返す
					 */
					boolean emptyCheckResult = emptyCheck(values);
					if(!emptyCheckResult) continue;
					
					/**
					 *  CSV記載のユーザー名とメールアドレスとDBデータの重複をチェック（重複している場合 false を返す）
					 *  重複している場合、処理を飛ばす
					 */
					boolean duplicatedCheckResult = duplicatedCheck(userNameFromCSV, emailFromCSV);
					if(!duplicatedCheckResult) continue;
					
					// レコードに抜けがないかチェック（項目が4つか） / 抜けがある場合 false を返す
					boolean recordCheckResult = recordCheck(values);
					if(!recordCheckResult) continue;
					
					user.setUserName(userNameFromCSV);
					user.setEmail(emailFromCSV);
					user.setPassword(values[2].replace("\"", "").trim());
					if(values[3].trim().equals("1")) {
						user.setRole("Admin");
					} else if(values[3].trim().equals("2")) {
						user.setRole("General");
					} else {
						throw new IllegalArgumentException("不正なロールが指定されています: " + values[3].trim());
					}
					user.setEnabled(true);
					user.setAccountNonExpired(true);
					user.setCredentialsNonExpired(true);
					user.setAccountNonLocked(true);
					user.setCreatedAt(LocalDateTime.now());
					user.setUpdatedAt(LocalDateTime.now());
					
					// ユーザーを保存
					createUserFromCsv(user);
				}
			}
		} catch (IOException e) {
			throw new Exception("CSVファイルの読み込みに失敗しました", e);
		}

	}
	

	/**
	 * CSV記載のユーザー名 / メールアドレスとDBデータの重複をチェック
	 * 重複している場合 false を返す
	 * 
	 * @param String emailFromCSV
	 * @return boolean
	 */
	private boolean duplicatedCheck(String userNameFromCSV, String emailFromCSV) {
		
		List<User> users = getAllUsers();
		
		for(User u: users) {
			if(userNameFromCSV.equals(u.getUserName())) {
				return false;
			}
			
			if(emailFromCSV.equals(u.getEmail())) {
				return false;
			}
		}
		
		return true;
	}
	
	
	/**
	 * レコードに抜けがないかチェック（項目が4つか）
	 * 抜けがある場合 false を返す
	 * @param String[] record
	 * @return boolean
	 */
	private boolean recordCheck(String[] record) {
		
		if(record.length != 4) return false;
		
		return true;
	}
	
	
	/**
	 * レコードの各項目のチェック
	 * nullまたは空白文字または長さ0の文字列の時、falseを返す
	 * @param String[] record
	 * @return boolean
	 */
	private boolean emptyCheck(String[] record) {
		
		for(String r: record) {
			if(r == null || r.isBlank()) return false;
		}
		
		return true;
	}
	
	
	/**
	 * CSVファイルのダウンロード
	 */
	@Override
	@Transactional
	public void downloadCsvFile() throws Exception {

		// Header
		CsvSchema.Builder builder = CsvSchema.builder()
				.addColumn("ユーザー名")
				.addColumn("メールアドレス")
				.addColumn("パスワード")
				.addColumn("ロール");
		
		List<List<String>> outputList = new ArrayList<>();
		List<User> record = userRepository.findAll();
		
		for(User s: record) {

			List<String> list = new ArrayList<>();
			list.add(s.getUserName());
			list.add(s.getEmail());
			list.add(s.getPassword());
			list.add(s.getRole().equals("Admin") ? "1" : "2");
			outputList.add(list);
		}

		CsvMapper mapper = new CsvMapper();
		CsvSchema schema = builder.build()
		    .withHeader()
		    .withColumnSeparator(',')
		    .withQuoteChar('"')
		    .withEscapeChar('\"')
		    .withLineSeparator("\r\n");

		// ファイルパス
		Path path = Path.of(System.getProperty("user.home"), "Downloads");
		String downloadPath = path.toString();
		LocalDateTime time = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
		String formattedTime = time.format(formatter);
		String fileName = "data_" + formattedTime + ".csv";
		
		//CSV出力
		try(FileOutputStream stream = new FileOutputStream(downloadPath + "\\" + fileName)) {
			//BOM出力
		    byte[] bom = ByteOrderMark.UTF_8.getBytes();
		    stream.write(bom);

		    // CSVのデータをファイルにUTF-8で出力
		    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream, "UTF-8"));
		    mapper.writer(schema).writeValues(writer).writeAll(outputList).flush();
		}
		
	}
	
	
	/**
	 * バリデーションエラー時に表示用の項目を補完する
	 * @param Long id
	 * @param User user
	 */
	public void restoreUserDisplayFields(Long id, User user) {
		getUserById(id).ifPresent(existing -> {
			user.setId(existing.getId());
			user.setRole(existing.getRole());
			user.setCreatedAt(existing.getCreatedAt());
			user.setUpdatedAt(existing.getUpdatedAt());
		});
	}
	
	
}


