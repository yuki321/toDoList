package com.example.todolist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.todolist.entity.PasswordReset;
import com.example.todolist.repository.PasswordResetTokenRepositoryIF;


@ExtendWith(MockitoExtension.class)
public class PasswordResetTest {
	

	@Mock
	private PasswordReset passwordReset;
	
    @Mock
    private PasswordEncoder passwordEncoder;
        
    @Mock
    private PasswordResetTokenRepositoryIF passwordResetTokenRepository;
    
    
	@Test
	@MockitoSettings(strictness = Strictness.LENIENT)
	@DisplayName("正常系： リセットトークンの検証")
	void validatePasswordResetTokenTest_Success() {
	
		// データ準備		
		List<Map<String, Object>> tokenList = initTokenList();
		
        when(passwordResetTokenRepository.findAllTokenHash()).thenReturn(tokenList);

        // 実行
        List<Map<String, Object>> resultList = passwordResetTokenRepository.findAllTokenHash();
        
        String rawToken = "aaaaa";
        boolean result = passwordReset.validatePasswordResetToken(rawToken, passwordEncoder, passwordResetTokenRepository);

        // 検証
        assertNotNull(resultList.get(0).get("token_hash"));
		assertEquals("aaaaa", resultList.get(0).get("token_hash"));
		assertNotEquals("ccccc", resultList.get(0).get("token_hash"));
		
        assertNotNull(resultList.get(1).get("token_hash"));
		assertEquals("bbbbb", resultList.get(1).get("token_hash"));
		assertThat(resultList).hasSize(2);
		
	}
	
	
	/**
	 * データ初期化
	 * @return
	 */
	private List<Map<String, Object>> initTokenList(){
		
		List<Map<String, Object>> tokenList = new ArrayList<>();
		Map<String, Object> tokenMap1 = new HashMap<>();
		tokenMap1.put("token_hash", "aaaaa");
		
		tokenList.add(tokenMap1);
		
		Map<String, Object> tokenMap2 = new HashMap<>();
		tokenMap2.put("token_hash", "bbbbb");
		tokenList.add(tokenMap2);
		
		return tokenList;
	}
	
	
	
	@Test
	@MockitoSettings(strictness = Strictness.LENIENT)
	@DisplayName("正常系： リセットトークンの検証_トーケンが利用済みの場合")
	void ResetTokenTest_isUsed() {
	
		// データ準備		
		List<Map<String, Object>> tokenList = new ArrayList<>();
		Map<String, Object> tokenMap = new HashMap<>();
		tokenMap.put("token_hash", "aaaaa");
		tokenMap.put("used_at", "2026-12-12 11:11:11");
		tokenList.add(tokenMap);
		
        when(passwordResetTokenRepository.findAllTokenHash()).thenReturn(tokenList);

        // 実行
        List<Map<String, Object>> resultList = passwordResetTokenRepository.findAllTokenHash();
        
        String rawToken = "aaaaa";
        boolean result = passwordReset.validatePasswordResetToken(rawToken, passwordEncoder, passwordResetTokenRepository);

        // 検証
        assertFalse(result);
		
	}
	
	
	
	@Test
	@MockitoSettings(strictness = Strictness.LENIENT)
	@DisplayName("正常系： リセットトークンの検証_トークンが存在しない場合")
	void ResetTokenTest_null() {
	
		// データ準備
		List<Map<String, Object>> tokenList = null;
		
        when(passwordResetTokenRepository.findAllTokenHash()).thenReturn(tokenList);

        // 実行
        List<Map<String, Object>> resultList = passwordResetTokenRepository.findAllTokenHash();

        // 検証
        assertNull(resultList);
		
	}
	


	
	
	
	
	@Test
	@DisplayName("正常系： StringからLocalDateTimeへ変換できる")
	void toLocalDateTimeTest() {
		
		// データ準備
		String time = "2026-12-12 11:11:11";
		String format = "yyyy-MM-dd HH:mm:ss";
		LocalDateTime expired_at = PasswordReset.toLocalDateTime(time, format);

		// 検証
		assertTrue(expired_at instanceof LocalDateTime);
		
	}
	
	
	@Test
	@DisplayName("異常系： フォーマットが正しくない場合、DateTimeParseException　の例外が発生する")
	void toLocalDateTimeTest_Failure() {
		
		// データ準備
		String time = "2026-12-12 1111";
		String format = "yyyy-MM-dd HH:mm:ss";

		// 検証
		assertThrows(DateTimeParseException.class, () -> PasswordReset.toLocalDateTime(time, format));
		
	}
	
	

}
