package com.example.todolist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
	@DisplayName("正常系：ユーザーデータが取得できること")
	void getAllUsersTest_Success() {
	
		User user1 = initTestData();
		User user2 = initTestData2();
		
        List<User> userList = List.of(user1, user2);

        // userRepository.findAll() が呼ばれたら userList を返すように設定
        when(userRepository.findAll()).thenReturn(userList);

        
        // 実行
        List<User> result = userService.getAllUsers();

        // 検証
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2); // 取得件数の確認
        assertThat(result.get(0).getUserName()).isEqualTo("testUser"); // 中身の確認
        assertThat(result.get(1).getUserName()).isEqualTo("testUser02");

        // findAll() が1回だけ呼ばれたことを検証
        verify(userRepository, times(1)).findAll();
		
		
	}
	
	
	@Test
	@MockitoSettings(strictness = Strictness.LENIENT)
	@DisplayName("正常系：IDでユーザーデータを取得できること")
	void getUserByIdTest_Success() {
	
		// 準備
        User user1 = initTestData();
        User user2 = initTestData2();

        // userRepository.findById() が呼ばれたら user1 を返すように設定
        when(userRepository.findById((long)1)).thenReturn(Optional.ofNullable(user1));
        when(userRepository.findById((long)2)).thenReturn(Optional.ofNullable(user2));

        
        // 実行
        Optional<User> result = userService.getUserById((long)1);
        Optional<User> result2 = userService.getUserById((long)2);
        
        // 検証
        assertThat(result).isNotNull();
        assertEquals(result, Optional.of(user1)); 
        assertEquals("testUser", result.get().getUserName()); 
        assertThat(result2).isNotNull();
        assertEquals(result2, Optional.of(user2)); 
        assertEquals("testUser02", result2.get().getUserName()); 
        assertNotEquals(1, result2.get().getId()); 
        assertNotEquals("testUser", result2.get().getUserName()); 

        
        // findById() が1回だけ呼ばれたことを検証
        verify(userRepository, times(1)).findById((long)1);
		
		
	}
	
	
	@Test
	@MockitoSettings(strictness = Strictness.LENIENT)
	@DisplayName("正常系：メールアドレスでユーザーデータを取得できること")
	void findByEmailTest_Success() {
	
		// 準備
		final String email = "test@gmail.com";
        User user1 = initTestData();
        List<User> userList = List.of(user1);

        // userRepository.findByEmail() が呼ばれたら user1 を返すように設定
        when(userRepository.findByEmail(email)).thenReturn(userList);
        
        // 実行
        List<User> result = userService.findByEmail(email);
        
        // 検証
        assertThat(result).isNotNull();
        assertEquals("testUser", result.get(0).getUserName()); 

        
        // findByEmail() が1回だけ呼ばれたことを検証
        verify(userRepository, times(1)).findByEmail(email);
		
		
	}
	
	
	@Test
	@MockitoSettings(strictness = Strictness.LENIENT)
	@DisplayName("正常系：findByEmail()の結果が0件場合")
	void findByEmailTest_EmptyList() {
		
		final String email = "test000@gmail.com";

        // userRepository.findByEmail() が呼ばれたら user1 を返すように設定
        when(userRepository.findByEmail(email)).thenReturn(Collections.emptyList());
        
        // 実行
        List<User> result = userService.findByEmail(email);

        // Then (検証)
        assertThat(result).isNotNull();
        assertThat(result).isEmpty(); // 空のリストであることを確認

        // findByEmail()は一度しか実行されないことを検証
        verify(userRepository, times(1)).findByEmail(email);
	    
		
	}
	
    
	@Test
	@MockitoSettings(strictness = Strictness.LENIENT)
	@DisplayName("正常系：重複のないメールアドレスで登録した場合、ユーザーが保存されて返されること")
	void createUserTest_Success() {
		

		// 1. テストデータの準備
		User inputUser = initTestData();
	    
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
	void dupulicateEmailTest_Failure() {
		
		// テストデータ初期化
		User inputUser = initTestData();
		
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
	void dupulicateUserNameTest_Failure() {
		
		// テストデータ初期化
		User inputUser = initTestData();
		
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
	
	
	// テストデータ初期化
	private User initTestData() {
		
		User inputUser = new User();
	    inputUser.setId(1L);
	    inputUser.setUserName("testUser");
	    inputUser.setEmail("test@gmail.com");
	    inputUser.setPassword("1111");
	    inputUser.setRole("1");
	    
	    return inputUser;
	}
	
	private User initTestData2() {
		
		User inputUser = new User();
	    inputUser.setId(2L);
	    inputUser.setUserName("testUser02");
	    inputUser.setEmail("test02@gmail.com");
	    inputUser.setPassword("1111");
	    inputUser.setRole("1");
	    
	    return inputUser;
	}
	

}
