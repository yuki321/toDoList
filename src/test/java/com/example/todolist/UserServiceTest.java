package com.example.todolist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

import java.util.Arrays;
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


@DisplayName("isValidRecordStructure メソッドのテスト")
class isValidRecordStructureTest {
    @Test
    @DisplayName("項目数や各項目が空白でない場合、trueを返すこと")
    void isValidRecordStructure_Success() {

    	UserServiceForUnitTest test = new UserServiceForUnitTest();
    	String[] record = {"aaaaa", "aaaaa@aa.com", "$2a$10$jcS0u1UODf.Bix8lC6A0euv09dogg9ECnUSeTTrQoimAZBMy.D1eq", "2"};
    	
    	boolean result = test.isValidRecordStructure(record);
    	
    	assertTrue(result);
    }

    @Test
    @DisplayName("項目に不具合がある場合、falseを返すこと")
    void isValidRecordStructure_Failure() {

    	// 準備
    	UserServiceForUnitTest test = new UserServiceForUnitTest();
    	String[] record1 = {"aaaaa", "aaaaa@aa.com", "2"};
    	String[] record2 = null;
    	String[] record3 = {"aaaaa", "aaaaa@aa.com", "", "2"};
    	String[] record4 = {"aaaaa", "aaaaa@aa.com", " ", "2"};
    	
    	
    	// 実行・検証
    	List<String[]> userNameList = Arrays.asList(record1, record2, record3, record4);
    	userNameList.stream()
    		.forEach(i -> assertFalse(test.isValidRecordStructure(i)));
    	
    }
    
}


@DisplayName("isValidUserName メソッドのテスト")
class isValidUserNameTest {
    @Test
    @DisplayName("ユーザー名の文字数や文字種が問題ない場合、trueを返すこと")
    void isValidUserNameTest_Success() {

    	// 準備
    	UserServiceForUnitTest test = new UserServiceForUnitTest();
    	String userName1 = "user";
    	String userName2 = "ユーザー";
    	String userName3 = "ゆーざー";
    	String userName4 = "氏名1";
    	
    	// 実行・検証
    	List<String> userNameList = Arrays.asList(userName1, userName2, userName3, userName4);
    	userNameList.stream()
    		.forEach(i -> assertTrue(test.isValidUserName(i)));
    	
    }

    @Test
    @DisplayName("ユーザー名の文字数や文字種に問題がある場合、falseを返すこと")
    void isValidUserNameTest_Failure() {

    	// 準備
    	UserServiceForUnitTest test = new UserServiceForUnitTest();
    	String userName1 = "";
    	String userName2 = "usernamelengthcheckqqqqqqusernamelengthcheckqqqqqqzzz";
    	String userName3 = "USERNAME";
    	String userName4 = "_user@name";

    	// 実行・検証
    	List<String> userNameList = Arrays.asList(userName1, userName2, userName3, userName4);
    	userNameList.stream()
    		.forEach(i -> assertFalse(test.isValidUserName(i)));
    	    	
    }

}


@DisplayName("isValidEmail メソッドのテスト")
class isValidEmailTest {
    @Test
    @DisplayName("メールアドレスの文字数や文字種が問題ない場合、trueを返すこと")
    void isValidEmailTest_Success() {

    	// 準備
    	UserServiceForUnitTest test = new UserServiceForUnitTest();
    	String email1 = "test-user-name1@gmail.com";
    	String email2 = "a@aa.com";
    	String email3 = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstu@example-long-domain-name-specification-string.com";
    	
    	// 実行・検証
    	List<String> emailList = Arrays.asList(email1, email2, email3);
    	emailList.stream()
    		.forEach(i -> assertTrue(test.isValidEmail(i)));
    	
    }

    @Test
    @DisplayName("メールアドレスの文字数や文字種に問題がある場合、falseを返すこと")
    void isValidEmailTest_Failure() {

    	// 準備
    	UserServiceForUnitTest test = new UserServiceForUnitTest();
    	String email1 = "EMAIL_111@gmail.com";
    	String email2 = "a@a.com";
    	String email3 = "zzzzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstu@example-long-domain-name-specification-string.com";
    	String email4 = "sampleaacom";

    	// 実行・検証
    	List<String> emailList = Arrays.asList(email1, email2, email3, email4);
    	emailList.stream()
    		.forEach(i -> assertFalse(test.isValidEmail(i)));
    	    	
    }

}


@DisplayName("isValidRole メソッドのテスト")
class isValidRoleTest {
    @Test
    @DisplayName("値が'1'または'2'の場合、trueを返すこと")
    void isValidRoleTest_Success() {

    	UserServiceForUnitTest test = new UserServiceForUnitTest();
    	
    	// 実行・検証
    	List<String> roleList = Arrays.asList("1", "2");
    	roleList.stream()
    		.forEach(i -> assertTrue(test.isValidRole(i)));
    	
    }

    @Test
    @DisplayName("値が'1'または'2'以外の場合、falseを返すこと")
    void isValidRoleTest_Failure() {

    	UserServiceForUnitTest test = new UserServiceForUnitTest();
    	
    	// 実行・検証
    	List<String> roleList = Arrays.asList("3", "-1", " ");
    	roleList.stream()
    		.forEach(i -> assertFalse(test.isValidRole(i)));
    	
    }
    
}


@DisplayName("isDuplicateUser メソッドのテスト")
class isDuplicateUserTest {

    @Mock
    private PasswordEncoder passwordEncoder;
        
    @Mock
    private UserRepository userRepository; 
	
    @InjectMocks
    private UserService userService;
    
    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    @DisplayName("CSVデータのユーザー名とメールアドレスがDBデータと重複しない場合、trueを返す")
    void isDuplicateUserTest_Success() {

    	// 準備
    	UserServiceForUnitTest test = new UserServiceForUnitTest();
    	User user1 = getUser1();
    	List<User> userList = List.of(user1);

        when(getAllUsers()).thenReturn(userList);

        
        // 実行
        List<User> result = getAllUsers();

        // 検証
        assertFalse(test.isDuplicateUser("user", "testuser@gmail.com", result));

    	
    }
    
    
    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    @DisplayName("CSVデータのユーザー名とメールアドレスがDBデータと重複する場合、falseを返す")
    void isDuplicateUserTest_Failure() {

    	// 準備
    	UserServiceForUnitTest test = new UserServiceForUnitTest();
    	User user1 = getUser1();
    	User user2 = getUser2();
    	User user3 = getUser3();
    	List<User> userList = List.of(user1, user2, user3);

        when(getAllUsers()).thenReturn(userList);

        
        // 実行
        List<User> result = getAllUsers();

        // 検証
        assertTrue(test.isDuplicateUser("testUser", "testtest@gmail.com", result));
        assertTrue(test.isDuplicateUser("testUser99", "test@gmail.com", result));
        assertTrue(test.isDuplicateUser("testUser", "test2@gmail.com", result));
        
    }
    
    private User getUser1() {
		User user = new User();
		user.setId(1L);
		user.setUserName("testUser");
		user.setEmail("test@gmail.com");
		user.setPassword("1111");
		user.setRole("1");
		
		return user;
    }
    
    private User getUser2() {
		User user = new User();
		user.setId(2L);
		user.setUserName("testUser2");
		user.setEmail("test2@gmail.com");
		user.setPassword("1111");
		user.setRole("1");
		
		return user;
    }
    
    private User getUser3() {
		User user = new User();
		user.setId(3L);
		user.setUserName("testUser3");
		user.setEmail("test3@gmail.com");
		user.setPassword("1111");
		user.setRole("1");
		
		return user;
    }
    
    private List<User> getAllUsers(){
		return userRepository.findAll();
	}

    
    
}





