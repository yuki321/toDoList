package com.example.todolist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.todolist.entity.User;
import com.example.todolist.repository.UserRepository;
import com.example.todolist.service.UserService;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
	

    @Mock
    private PasswordEncoder passwordEncoder;
        
    @Mock
    private UserRepository userRepository; 
	
    @InjectMocks
    private UserService userService;
    
	@Test
	@MockitoSettings(strictness = Strictness.LENIENT)
	@DisplayName("正常系：重複のないメールアドレスで登録した場合、ユーザーが保存されて返されること")
	void createUserTest_Success() {
		

		// 1. テストデータの準備（固定の日時を変数に保持）
		User inputUser = new User();
	    inputUser.setId(1L);
	    inputUser.setUserName("testUser");
	    inputUser.setEmail("test@gmail.com");
	    inputUser.setPassword("1111");
	    inputUser.setRole("1");
		
	    
	    // 2. 依存するモックの振る舞いを定義
	    when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
	    when(userRepository.existsByUserName("testUser")).thenReturn(false);
	    when(passwordEncoder.encode("1111")).thenReturn("encoded_1111");
	    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

	    // 3. テスト対象の実行
	    User result = userService.createUser(inputUser);
	    
	    // 4. アサーション（結果検証）
		assertNotNull(result);
		assertEquals(1L, result.getId());
		assertEquals("testUser", result.getUserName());
		assertEquals("test@gmail.com", result.getEmail());
		assertEquals("encoded_1111", result.getPassword()); // 暗号化後の値になっているか
		assertEquals("1", result.getRole());
		assertTrue(result.getEnabled());
		assertTrue(result.isAccountNonExpired());
	    assertTrue(result.isCredentialsNonExpired());
	    assertTrue(result.isAccountNonLocked());
		assertNotNull(result.getCreatedAt()); // LocalDateTime.now() は null でないことのみ検証
		assertNotNull(result.getUpdatedAt());

	    // 依存メソッドが正しく呼ばれたかの検証
	    verify(userRepository).existsByUserName("testUser");
	    verify(userRepository).existsByEmail("test@gmail.com");
	    verify(passwordEncoder).encode("1111");
	    verify(userRepository).save(any(User.class));
		
		
	}
	
	@Test
	@MockitoSettings(strictness = Strictness.LENIENT)
	@DisplayName("異常系：既に存在するメールアドレスの場合、IllegalArgumentExceptionが発生すること")
	void dupulicateEmailTest() {
		
		User inputUser = new User();
	    inputUser.setId(1L);
	    inputUser.setUserName("testUser");
	    inputUser.setEmail("test@gmail.com");
	    inputUser.setPassword("1111");
	    inputUser.setRole("1");
		
	    // ユーザー名は重複しない
	    when(userRepository.existsByUserName(anyString())).thenReturn(false);
	    
	    // メールアドレスは重複する
	    when(userRepository.existsByEmail(anyString())).thenReturn(true);

	    
	    IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> userService.createUser(inputUser)
        );
	    
	    
	    // 例外メッセージの検証
	    assertThat(exception.getMessage()).isEqualTo("すでにそのメールアドレスは存在しています");

	    // 重複エラーの場合は save メソッドが一度も呼ばれていない
        verify(userRepository, never()).save(any(User.class));
		
		
	}
	
	
	@Test
	@MockitoSettings(strictness = Strictness.LENIENT)
	@DisplayName("異常系：既に存在するユーザー名の場合、IllegalArgumentExceptionが発生すること")
	void dupulicateUserNameTest() {
		
		User inputUser = new User();
	    inputUser.setId(1L);
	    inputUser.setUserName("testUser");
	    inputUser.setEmail("test@gmail.com");
	    inputUser.setPassword("1111");
	    inputUser.setRole("1");
		
	    // ユーザー名は重複する
	    when(userRepository.existsByUserName(anyString())).thenReturn(true);
	    
	    // メールアドレスは重複しない
	    when(userRepository.existsByEmail(anyString())).thenReturn(false);

	    
	    IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> userService.createUser(inputUser)
        );
	    
	    
	    // 例外メッセージの検証
	    assertThat(exception.getMessage()).isEqualTo("すでにそのユーザー名は存在しています");

	    // 重複エラーの場合は save メソッドが一度も呼ばれていない
        verify(userRepository, never()).save(any(User.class));
		
		
	}
	
	
	
	
	

}
