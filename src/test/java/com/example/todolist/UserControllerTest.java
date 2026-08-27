package com.example.todolist;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.example.todolist.controller.UserController;
import com.example.todolist.entity.User;
import com.example.todolist.service.ToDoService;
import com.example.todolist.service.UserService;


@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

		
	@Mock
	private ToDoService toDoService;
	
	@Mock
    private PasswordEncoder passwordEncoder;
	
	@Mock
    private UserDetails userDetails;
	
	@Mock
	private Model model;

	@Mock
	private UserService userService;
	
	@Mock
	private User user;
	
//	@Mock
//	Pageable pageable;
//	
//	@Mock
//	MockMvc mockMvc;
	
	@InjectMocks
	private UserController userController;

	
	private List<User> userList;
	
	private User user1;
	
	private User user2;
	
	@BeforeEach
	void setup() {
		
		userList = new ArrayList<>();
		
		user1 = new User();
		user1.setId(1L);
		user1.setUserName("testUser");
		user1.setEmail("test@gmail.com");
		user1.setPassword("1111");
		user1.setRole("1");
		userList.add(user1);
		
		user2 = new User();
		user2.setId(2L);
		user2.setUserName("testUser2");
		user2.setEmail("test2@gmail.com");
		user2.setPassword("1111");
		user2.setRole("1");
		userList.add(user2);
		
//		mockMvc = MockMvcBuilders.standaloneSetup(userController)
//	            // Pageableを正しく解決するためのリゾルバーを追加
//	            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
//	            .build();
		
		
	}

	@Nested
    @DisplayName("getAllUsers　のテスト")
    class getAllUsersTest {
	
		@Test
		@MockitoSettings(strictness = Strictness.LENIENT)
		@DisplayName("getAllUsers　のテスト")
		void getAllUsersTest_Success() throws Exception {
			
			when(userService.getAllUsers()).thenReturn(userList);
			List<User> userList = userService.getAllUsers();
			
			Map<String, Object> userInfo = Map.of("role", "Admin");
			when(toDoService.getUserInfo(userDetails)).thenReturn(userInfo);
			
			int page = 0;
			Pageable pageable = PageRequest.of(0, 10);
			Page<User> mockPage = new PageImpl<>(userList, pageable, userList.size());
		    when(userService.getAllUsers(pageable)).thenReturn(mockPage);
			
	        String viewName = userController.getAllUsers(userDetails, model, pageable, page);
	        
	        // 検証
	        assertEquals(viewName, "user");

			assertThat(userList.size()).isEqualTo(2);
			assertThat(userList.get(0).getId()).isEqualTo(1L);
			assertThat(userList.get(0).getUserName()).isEqualTo("testUser");
			assertThat(userList.get(0).getEmail()).isEqualTo("test@gmail.com");
			assertThat(userList.get(0).getPassword()).isEqualTo("1111");
			assertThat(userList.get(0).getRole()).isEqualTo("1");
			
		
		}
		
		@Test
		@MockitoSettings(strictness = Strictness.LENIENT)
		@DisplayName("管理者でない場合、ログインページにリダイレクトされる")
		void getAllUsersTest_Redirect_Non_Admin_User() throws Exception {
			
			Map<String, Object> userInfo = Map.of("role", "General");
			when(toDoService.getUserInfo(userDetails)).thenReturn(userInfo);
			
			int page = 0;
			Pageable pageable = PageRequest.of(0, 10);
			Page<User> mockPage = new PageImpl<>(userList, pageable, userList.size());
		    when(userService.getAllUsers(pageable)).thenReturn(mockPage);
	        
		    String viewName = userController.getAllUsers(userDetails, model, pageable, page);
	        
	        // 検証
	        assertEquals("redirect:/", viewName);

	        // 管理者でないため、ユーザー一覧は取得しにいかないことを検証
	        verifyNoInteractions(userService);
	        verifyNoInteractions(model);
		
		}
		
		@Test
		@MockitoSettings(strictness = Strictness.LENIENT)
		@DisplayName("getAllUsers　の返り値がなしの場合")
		void getAllUsersTest_List_isEmpty() throws Exception {
			
			List<User> emptyList = new ArrayList<>();
			when(userService.getAllUsers()).thenReturn(emptyList);
			List<User> userList = userService.getAllUsers();
			
	        
	        // 検証
			assertThat(userList.size()).isEqualTo(0);
		
		}
    
	}
	
	
	@Nested
    @DisplayName("userCreate　のテスト")
    class userCreateTest {
	
		@Test
		@MockitoSettings(strictness = Strictness.LENIENT)
		@DisplayName("userCreate　のテスト")
		void userCreateTest_Success() throws Exception {
			
			when(userService.createUser(user1)).thenReturn(user1);
	        String viewName = userController.userCreate("true", model);
	        
	        // 検証
	        assertEquals(viewName, "userCreate");

			assertThat(userList.size()).isEqualTo(2);
			assertThat(userList.get(0).getId()).isEqualTo(1L);
			assertThat(userList.get(0).getUserName()).isEqualTo("testUser");
			assertThat(userList.get(0).getEmail()).isEqualTo("test@gmail.com");
			assertThat(userList.get(0).getPassword()).isEqualTo("1111");
			assertThat(userList.get(0).getRole()).isEqualTo("1");
		
		}
    
	}
	
	@Nested
    @DisplayName("changePassword　のテスト")
    class changePasswordTest {
	
		@Test
		@MockitoSettings(strictness = Strictness.LENIENT)
		@DisplayName("changePassword　のテスト")
		void changePasswordTest_Get() throws Exception {
						
	        String viewName = userController.changePassword(1L, model, user);
	        
	        // 検証
	        assertEquals(viewName, "changePassword");

		}
	
	}


}


