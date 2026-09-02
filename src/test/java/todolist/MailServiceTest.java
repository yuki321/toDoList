package todolist;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import todolist.service.MailService;

@ExtendWith(MockitoExtension.class)
public class MailServiceTest {
	
    @Mock
	private JdbcTemplate jdbc;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    MailService mailService;
    
    List<Map<String, Object>> list;
    
    Map<String, Object> map1;
    
    Map<String, Object> map2;
    
    Map<String, Object> userMap;
    
    
	@Test
	@MockitoSettings(strictness = Strictness.LENIENT)
	@DisplayName("getDBPassword メソッドのテスト")
	void getDBPasswordTest() {
	
		String sql = "SELECT password FROM users WHERE email=?";
		String email = "aaaaa@gmail.com";
		
		userMap = userDataSetup();
		
        when(jdbc.queryForMap(sql, email)).thenReturn(userMap);
        String expectedPW = (String)userMap.get("password");

        String actualPW = getDBPassword(email);

        // 検証
        assertEquals(expectedPW, actualPW);
        verify(jdbc, times(1)).queryForMap(sql, email);
		
	}
	
	@Test
	@MockitoSettings(strictness = Strictness.LENIENT)
	@DisplayName("getDBPassword メソッドのテスト(存在しないメールアドレス)")
	void getDBPasswordTest_NonExistsEmail() {
	
		String sql = "SELECT password FROM users WHERE email=?";
		String email = "notfound@gmail.com";
		Map<String, Object> expectedMap = null;

        when(jdbc.queryForMap(sql, email)).thenReturn(expectedMap);

        // 検証
        assertNull(expectedMap);
		
	}
	
	// MailServiceではprivateメソッドのため、ここで同じものをテスト用に定義する
	private String getDBPassword(String email) {
		
		String sql = "SELECT password FROM users WHERE email=?";
		Map<String, Object> getMap = jdbc.queryForMap(sql, email);
		String password = (String)getMap.get("password");
		
		return password;
	} 
	
	// ユーザーデータ準備
	private Map<String, Object> userDataSetup() {
		
		userMap = new HashMap<>();
		userMap.put("id", 1L);
		userMap.put("user_name", "testUser");
		userMap.put("email", "test@gmail.com");
		userMap.put("password", "$2a$10$jcS0u1UODf.Bix8lC6A0euv09dogg9ECnUSeTTrQoimAZBMy.D1eq");
		userMap.put("role", "General");
		userMap.put("enabled", 1);
		userMap.put("created_at", "2026-08-29 01:42:52");
		userMap.put("updated_at", "2026-08-29 01:42:52");
		
		return userMap;
	}
	
	
	@Test
	@MockitoSettings(strictness = Strictness.LENIENT)
	@DisplayName("getEmail - メールアドレスの取得をテスト")
	void getEmailTest() {
	
		// 準備
		setup();
		
		String rawToken = "1111";
		String token_hash = (String)map1.get("token_hash");
		String expected = "aaaaa@gmail.com";

		when(passwordEncoder.matches(rawToken, token_hash)).thenReturn(true);

		String actual = getEmail(list, rawToken);
		
        // 検証
        assertEquals(expected, actual);
	}
	
	@Test
	@MockitoSettings(strictness = Strictness.LENIENT)
	@DisplayName("getEmail - password_reset_tokensのレコードが存在しない場合のテスト")
	void getEmailTest_NoRecord() {
	
		// 準備
		List<Map<String, Object>> nullList = null;
		
		String rawToken = "1111";
		String actual = getEmail(nullList, rawToken);

        // 検証
        assertNull(actual);
	}
	
	@Test
	@MockitoSettings(strictness = Strictness.LENIENT)
	@DisplayName("getEmail - トークンが存在しない場合のテスト")
	void getEmailTest_NoRawToken() {
	
		// 準備
		setup();
		
		String rawToken = null;
		String actual = getEmail(list, rawToken);

        // 検証
        assertNull(actual);
	}
	
	@Test
	@MockitoSettings(strictness = Strictness.LENIENT)
	@DisplayName("getEmail - DBデータと一致するトークンが存在しない場合のテスト")
	void getEmailTest_NonMatchRawToken() {
	
		// 準備
		setup();
		
		String rawToken = "2222";
		String actual = getEmail(list, rawToken);

        // 検証
        assertNull(actual);
	}
	

	// データ準備
	private void setup() {
		list = new ArrayList<>();
		
		map1 = new HashMap<>();
		map1.put("id", 1);
		map1.put("email", "aaaaa@gmail.com");
		map1.put("token_hash", "$2a$10$jcS0u1UODf.Bix8lC6A0euv09dogg9ECnUSeTTrQoimAZBMy.D1eq");
		map1.put("created_at", "2026-08-29 01:42:52");
		map1.put("expires_at", "2026-08-29 02:42:52");
		map1.put("used_at", null);
		list.add(map1);
		
		map2 = new HashMap<>();
		map2.put("id", 2);
		map2.put("email", "bbbbb@gmail.com");
		map2.put("token_hash", "$2a$10$8vYbj2A/yJQYvTwVKL1u.um/PIkbOzfpMqYQyIslADwDjZ7PJHFN6");
		map2.put("created_at", "2026-08-29 01:42:52");
		map2.put("expires_at", "2026-08-29 02:42:52");
		map2.put("used_at", null);
		list.add(map2);

	}
	
	// MailService ではprivateメソッドのため、ここで定義
	private String getEmail(List<Map<String, Object>> resultList, String rawToken) {
		
		if(resultList == null || resultList.isEmpty()) return null;
		if(rawToken == null || rawToken.isEmpty()) return null;
		
		boolean matchResult = false;
		String email = null;
		for(Map<String, Object> map: resultList) {
	
			String token_hash = (String)map.get("token_hash");
			matchResult = passwordEncoder.matches(rawToken, token_hash);
	
			if(matchResult) {
				// マッチした組み合わせを次の判定で利用
				email = map.get("email").toString();
				break;
			}
			
		}
		
		return email;
	}

}






