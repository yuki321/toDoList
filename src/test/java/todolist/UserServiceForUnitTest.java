package todolist;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.ByteOrderMark;
import org.assertj.core.util.VisibleForTesting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import todolist.entity.PasswordChange;
import todolist.entity.User;
import todolist.repository.UserRepository;
import tools.jackson.dataformat.csv.CsvMapper;
import tools.jackson.dataformat.csv.CsvSchema;

@Service
@Transactional
public class UserServiceForUnitTest {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired 
	private JdbcTemplate jdbc;
	
	
	/**
	 * パスワードのチェック
	 * @param Password form
	 * @param UserDetails userDetails
	 * @return List<String>
	 */
	@VisibleForTesting
	List<String> checkPassword(PasswordChange form, User user) {
		
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
	@VisibleForTesting
	String getDbPassword(@PathVariable User user) {
		
		String sql = "SELECT password FROM users WHERE id=?";
		Map<String, Object> getMap = jdbc.queryForMap(sql, user.getId());
		String password = (String)getMap.get("password");
		
		return password;
	}
	
	
	/**
	 * CSVファイルアップロード
	 * @param MultipartFile file
	 */
	@Transactional
	@VisibleForTesting
	void uploadCsvFile(MultipartFile file) throws Exception {
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
					boolean inputCheckResult = inputCheck(values, users);
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
	 * CSVファイルのチェック
	 * 全項目のチェックに通過した場合、trueを返す
	 * @param String[] record
	 * @param List<User> users
	 * @return boolean
	 */
	@VisibleForTesting
	boolean inputCheck(String[] record, List<User> users) {
	    // 1. 配列チェック
	    if (!isValidRecordStructure(record)) {
	        return false;
	    }

	    String userName = record[0].trim();
	    String email = record[1].replace("\"", "").trim();
	    String role = record[3].replace("\"", "").trim();

	    // 2. 各項目チェック
	    if (!isValidUserName(userName) || !isValidEmail(email) || !isValidRole(role)) {
	        return false;
	    }

	    // 3. DBとCSVファイルの相関チェック（重複チェック）
	    if (isDuplicateUser(userName, email, users)) {
	        return false;
	    }

	    return true;
	}


	/**
	 * 項目数チェック
	 * 空白などが各項目にないかチェック
	 * @param String[] record
	 * @return boolean
	 */
	@VisibleForTesting
	boolean isValidRecordStructure(String[] record) {
	    if (record == null || record.length != 4) {
	        return false;
	    }
	    for (String r : record) {
	        if (r == null || r.isBlank()) return false;
	    }
	    
	    return true;
	}

	
	/**
	 * ユーザー名チェック
	 * @param String userName
	 * @return boolean
	 */
	@VisibleForTesting
	boolean isValidUserName(String userName) {
	    if (userName.length() < 1 || userName.length() > 50) {
	        return false;
	    }
	    
	    // 日本語（ひらがな・カタカナ・漢字）、英小文字、数字のみ
	    Pattern pattern = Pattern.compile("[^a-z0-9\\u3040-\\u309F\\u30A0-\\u30FF\\u4E00-\\u9FFF\\u3000]");
	    Matcher userNameMatch = pattern.matcher(userName);
		if(userNameMatch.find()) return false;
		
	    return true;
	}

	
	/**
	 * メールアドレスチェック
	 * @param String email
	 * @return boolean
	 */
	@VisibleForTesting
	boolean isValidEmail(String email) {
	    if (email.length() < 8 || email.length() > 100) {
	        return false;
	    }
	    if(!email.contains("@") || !email.contains(".")) {
	    	return false;
	    }
	    
	    // 英小文字、数字、@,-のみメールアドレスに含める
	    Pattern pattern = Pattern.compile("[^a-z0-9@.-]");
	    Matcher userNameMatch = pattern.matcher(email);
	    if(userNameMatch.find()) return false;
	    
	    return true;
	}

	
	/**
	 * ロールチェック("1"または"2"以外はfalseを返す)
	 * @param String role
	 * @return boolean
	 */
	@VisibleForTesting
	boolean isValidRole(String role) {
	    return "1".equals(role) || "2".equals(role);
	}

	
	/**
	 * ユーザー名とメールアドレスの重複チェック
	 * CSVデータとDBデータで重複がないかチェック
	 * @param String userName
	 * @param String email
	 * @param List<User> users
	 * @return
	 */
	@VisibleForTesting
	boolean isDuplicateUser(String userName, String email, List<User> users) {

	    for (User u : users) {
	        if (userName.equals(u.getUserName()) || email.equals(u.getEmail())) {
	            return true;
	        }
	    }
	    
	    return false;
	}
	
	
	/**
	 * CSVファイルのダウンロード
	 */
	@Transactional
	@VisibleForTesting
	void downloadCsvFile() throws Exception {

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
	
	
	// 以下はテスト用
	List<User> getAllUsers(){
		return userRepository.findAll();
	}
	
	User createUserFromCsv(User user) {
		
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
	
	
	
}


