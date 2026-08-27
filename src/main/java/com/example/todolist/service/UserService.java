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
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.ByteOrderMark;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import com.example.todolist.entity.CSV;
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
	 * 全件取得（Pager）
	 * @param Pageable pageable
	 * @return Page<User>
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<User> getAllUsers(Pageable pageable){
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
	public Page<User> searchUsers(User _user, Pageable pageable){
    	
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

		user.setUserName((String)getMap.get("user_name"));
		user.setEmail((String)getMap.get("email"));
		user.setPassword(passwordEncoder.encode(newPassword));
		user.setRole((String)getMap.get("role"));
		user.setUpdatedAt(LocalDateTime.now());
		
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
            
            /**
             *  ループ内でデータ取得するとパフォーマンス低下を招くので、
             *  ここでユーザーデータを取得し、ループ内でチェックに利用する
             */
            List<User> users = getAllUsers();
			
			while ((line = br.readLine()) != null) {
				
				
				String[] values = line.split(",");
				if (values.length >= 4) {
					
					/**
					 * CSVファイルのチェック
					 * 全項目のチェックに通過した場合、trueを返す
					 */
					CSV csv = new CSV();
					boolean inputCheckResult = csv.inputCheck(values, users);
					if(!inputCheckResult) continue;

					
					String userNameFromCSV = values[0].trim();
					String emailFromCSV = values[1].replace("\"", "").trim();
					User user = new User();
					
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


