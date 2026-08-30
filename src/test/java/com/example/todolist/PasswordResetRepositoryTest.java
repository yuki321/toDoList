package com.example.todolist;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
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

import com.example.todolist.repository.PasswordResetRepository;
import com.example.todolist.service.UserService;

@ExtendWith(MockitoExtension.class)
public class PasswordResetRepositoryTest {
	
    @Mock
	private JdbcTemplate jdbc;
    
    @Mock
    private UserService userService;
    
    @Mock
    private PasswordEncoder passwordEncoder;
        
    @InjectMocks
    PasswordResetRepository passwordResetRepository;
    
    List<Map<String, Object>> list;
        
    
	@Test
	@MockitoSettings(strictness = Strictness.LENIENT)
	@DisplayName("updateResetTokenUsedAt - "
			+ "指定したメールアドレスのトークン使用日時が正常に更新される")
	void updateResetTokenUsedAtTest() {
	
		String sql = "UPDATE password_reset_tokens SET used_at = ? WHERE email=?";
        String email = "aaaaa@gmail.com";
		int expectedNum = 1;

        // jdbc.update が呼ばれたら 1 を返すようにモックを設定
        // ※ 第2引数の LocalDateTime.now() は実行ごとに値が変わるため any(LocalDateTime.class) を使用
        when(jdbc.update(eq(sql), any(LocalDateTime.class), eq(email)))
                .thenReturn(expectedNum);

        // 実行
        int actualNum = passwordResetRepository.updateResetTokenUsedAt(email);

        // 検証
        assertEquals(expectedNum, actualNum);
        verify(jdbc, times(1)).update(eq(sql), any(LocalDateTime.class), eq(email));
		
	}
	
	@Test
	@MockitoSettings(strictness = Strictness.LENIENT)
	@DisplayName("updateResetTokenUsedAt - "
			+ "存在しないメールアドレスの場合、使用日時が更新されない")
	void updateResetTokenUsedAtTest_NotFound() {
	
        String sql = "UPDATE password_reset_tokens SET used_at = ? WHERE email=?";
        String email = "notfound@example.com";
        int expectedNum = 0;

        when(jdbc.update(eq(sql), any(LocalDateTime.class), eq(email)))
                .thenReturn(expectedNum);

        // 実行
        int actualNum = passwordResetRepository.updateResetTokenUsedAt(email);

        // 検証
        assertEquals(expectedNum, actualNum);
        verify(jdbc, times(1)).update(eq(sql), any(LocalDateTime.class), eq(email));
		
	}
	
	@Test
	@MockitoSettings(strictness = Strictness.LENIENT)
	@DisplayName("resetPassword - Userテーブルのパスワードが更新される")
	void resetPasswordTest() {
	
		String sql = "UPDATE users SET password = ? WHERE email=?";
		String password = "1111";
		String encodedPassword = passwordEncoder.encode(password);
		String email = "notfound@example.com";
		int expectedNum = 1;

        when(jdbc.update(sql, encodedPassword, email)).thenReturn(expectedNum);

        // 実行
        int actualNum = passwordResetRepository.resetPassword(email, password);

        // 検証
        assertEquals(expectedNum, actualNum);
        verify(jdbc, times(1)).update(sql, encodedPassword, email);
		
	}
	
	@Test
	@MockitoSettings(strictness = Strictness.LENIENT)
	@DisplayName("resetPassword - メールアドレスが存在しない場合、Userテーブルのパスワードは更新されない")
	void resetPasswordTest_nonExisting_emailAddress() {
	
		String sql = "UPDATE users SET password = ? WHERE email=?";
		String password = "1111";
		String encodedPassword = passwordEncoder.encode(password);
		String nonExistingEmail = "notfound@example.com";
		int expectedNum = 0;

        when(jdbc.update(sql, encodedPassword, nonExistingEmail)).thenReturn(expectedNum);

        // 実行
        int actualNum = passwordResetRepository.resetPassword(nonExistingEmail, password);

        // 検証
        assertEquals(expectedNum, actualNum);
        verify(jdbc, times(1)).update(sql, encodedPassword, nonExistingEmail);
		
	}
	
	@Test
	@MockitoSettings(strictness = Strictness.LENIENT)
	@DisplayName("deleteRecord - password-reset-tokensテーブルの該当レコードを削除する")
	void deleteRecordTest() {
	
		String sql = "DELETE FROM password_reset_tokens WHERE email=?";
        String email = "aaaaa@gmail.com";
		int expectedNum = 1;

        when(jdbc.update(sql, email)).thenReturn(expectedNum);

        // 実行
        int actualNum = passwordResetRepository.deleteRecord(email);

        // 検証
        assertEquals(expectedNum, actualNum);
        verify(jdbc, times(1)).update(sql, email);
		
	}
	
	@Test
	@MockitoSettings(strictness = Strictness.LENIENT)
	@DisplayName("deleteRecord - 存在しないメールアドレスの場合、レコードが削除されない")
	void deleteRecordTest_NotFound() {
	
		String sql = "DELETE FROM password_reset_tokens WHERE email=?";
        String email = "notfound@example.com";
        int expectedNum = 0;

        when(jdbc.update(sql, email)).thenReturn(expectedNum);

        // 実行
        int actualNum = passwordResetRepository.deleteRecord(email);

        // 検証
        assertEquals(expectedNum, actualNum);
        verify(jdbc, times(1)).update(sql, email);
		
	}
	
	private void setup() {
		list = new ArrayList<>();
		
		Map<String, Object> map1 = new HashMap<>();
		map1.put("id", 1);
		map1.put("email", "aaaaa@gmail.com");
		map1.put("token_hash", "$2a$10$8vYbj2A/yJQYvTwVKL1u.um/PIkbOzfpMqYQyIslADwDjZ7PJHFN6");
		map1.put("created_at", "2026-08-29 01:42:52");
		map1.put("expires_at", "2026-08-29 02:42:52");
		map1.put("used_at", null);
		list.add(map1);
		
		Map<String, Object> map2 = new HashMap<>();
		map2.put("id", 2);
		map2.put("email", "bbbbb@gmail.com");
		map2.put("token_hash", "$2a$10$8vYbj2A/yJQYvTwVKL1u.um/PIkbOzfpMqYQyIslADwDjZ7PJHFN6");
		map2.put("created_at", "2026-08-29 01:42:52");
		map2.put("expires_at", "2026-08-29 02:42:52");
		map2.put("used_at", null);
		list.add(map2);
		
	}
	
	@Test
	@MockitoSettings(strictness = Strictness.LENIENT)
	@DisplayName("findAllTokenHash - 全てのリセットトークンを取得する")
	void findAllTokenHashTest() {
	
		setup();
		String sql = "SELECT token_hash, expires_at FROM password_reset_tokens";

        when(jdbc.queryForList(sql)).thenReturn(list);

        // 実行
        List<Map<String, Object>> ActualList = passwordResetRepository.findAllTokenHash();

        // 検証
        assertEquals(list, ActualList);
        verify(jdbc, times(1)).queryForList(sql);
		
	}
	
	@Test
	@MockitoSettings(strictness = Strictness.LENIENT)
	@DisplayName("findAllTokenHash - レコードが存在しない")
	void findAllTokenHash_NotFound() {
	
		String sql = "SELECT token_hash, expires_at FROM password_reset_tokens";
        List<Map<String, Object>> expectedList = null;
		when(jdbc.queryForList(sql)).thenReturn(expectedList);

        // 実行
        List<Map<String, Object>> ActualList = passwordResetRepository.findAllTokenHash();

        // 検証
        assertEquals(expectedList, ActualList);
        verify(jdbc, times(1)).queryForList(sql);
		
	}


	
	
	

}






