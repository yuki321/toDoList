package todolist.service;

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

import org.apache.commons.io.ByteOrderMark;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import todolist.common.Logger;
import todolist.entity.CSV;
import todolist.entity.User;
import todolist.repository.UserRepository;
import tools.jackson.dataformat.csv.CsvMapper;
import tools.jackson.dataformat.csv.CsvSchema;

@Service
@Transactional
public class UserUpdateService implements UserUpdateServiceIF {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired 
	private JdbcTemplate jdbc;
	
	
	
	/**
	 * 	ユーザー作成
	 * @param User user
	 * @return User
	 */
	@Override
	public User createUser(final User user) {
		Logger.log(this.getClass().getSimpleName(), "createUser: ユーザー作成処理開始");
		
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
	public User createUserFromCsv(final User user) {
		
		Logger.log(this.getClass().getSimpleName(), "createUserFromCsv: ユーザー作成処理開始(CSVファイルインポート)");
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
	public User updateUser(final Long id, final User user) {
		
		Logger.log(this.getClass().getSimpleName(), "updateUser: ユーザー更新処理開始");
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
	public void deleteUser(final Long id) {
		
		Logger.log(this.getClass().getSimpleName(), "deleteUser: ユーザー削除処理開始");
		if(!userRepository.existsById(id)) {
			Logger.log(this.getClass().getSimpleName(), "[Error] deleteUser: ユーザーが存在しません");
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
	public User savePassword(final User user, final String newPassword) {
		
		Logger.log(this.getClass().getSimpleName(), "savePassword: パスワード変更処理開始");
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
	 * CSVファイルアップロード
	 * @param MultipartFile file
	 * @return List<String>
	 */
	@Override
	@Transactional
	public List<String> uploadCsvFile(final MultipartFile file) throws Exception {
		
		Logger.log(this.getClass().getSimpleName(), "uploadCsvFile: CSVファイルアップロード処理開始");
		// CSVファイルを読み込む
		try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
			
			CSV csv = new CSV();
			List<String> errors = new ArrayList<>();
			
			// CSVファイルチェック
			boolean csvFileCheckResult = csv.isCsvFile(file, errors);
			if(!csvFileCheckResult) {
				
				String message = "・CSVファイルに問題が発生しています";
				csv.setErrorMessage(message, errors);
				Logger.log(this.getClass().getSimpleName(), "[Error] uploadCsvFile: " + message);
			}
			
			
			String line;
			//ヘッダーレコードを飛ばすために１行だけ読み取る
            line = br.readLine();
            
            /**
             *  ループ内でデータ取得するとパフォーマンス低下を招くので、
             *  ここでユーザーデータを取得し、ループ内でチェックに利用する
             */
            List<User> users = userService.getAllUsers();
			
			while ((line = br.readLine()) != null) {
				
				
				String[] values = line.split(",");
				if (values.length >= 4) {
					
					/**
					 * CSVファイルのチェック
					 * 全項目のチェックに通過した場合、trueを返す
					 */
					boolean inputCheckResult = csv.inputCheck(values, users, errors);
					if(!inputCheckResult) {
						Logger.log(this.getClass().getSimpleName(), "[Error] uploadCsvFile: エラー発生!!");
						continue;
					}

					
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
			
			return errors;
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

		Logger.log(this.getClass().getSimpleName(), "downloadCsvFile: CSVファイルダウンロード処理開始");
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
		
	
}


