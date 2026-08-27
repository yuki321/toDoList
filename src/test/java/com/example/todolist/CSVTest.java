package com.example.todolist;

import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
public class CSVTest {

	@Nested
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


	@Nested
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


	@Nested
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

	
	@Nested
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


	@Nested
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

}



