package com.example.todolist.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.todolist.entity.PasswordChange;
import com.example.todolist.entity.User;
import com.example.todolist.service.ToDoServiceIF;
import com.example.todolist.service.UserServiceIF;

@Controller
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	private UserServiceIF userService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private ToDoServiceIF toDoService;
		
	/**
	 * 全件取得
	 * @param Model model
	 * @return String
	 */
	@GetMapping("user")
	public String getAllUsers(@AuthenticationPrincipal UserDetails userDetails,Model model){
		
		Map<String, Object> userIfo = toDoService.getUserInfo(userDetails);
		String role = (String) userIfo.get("role");
		
		if(!"Admin".equals(role)) {
			return "redirect:/";
		}
		
		List<User> users = userService.getAllUsers();
		
		model.addAttribute("users", users);
		model.addAttribute("user", new User());
		
		return "user";
	}
	
	
	/**
	 * ユーザー検索
	 * @param User user
	 * @param Model model
	 * @return
	 */
	@GetMapping("search")
	public String searchUsers(@ModelAttribute("user") User user, Model model) {
		
		List<User> users = userService.searchUsers(user);
		model.addAttribute("users", users);
		model.addAttribute("user", new User());
		
		return "user";
	}
	
	
	/**
	 * IDで指定したユーザーを取得
	 * @param Long id
	 * @param Madel model
	 * @return String
	 */
	@GetMapping("user/{id}")
	public String getUserById(@PathVariable Long id, Model model){
		
		// idがLong型でない場合
		if(!(id instanceof Long)) {
			return "redirect:/api/users/user";
		}
		
		try {
			Optional<User> user = userService.getUserById(id);
			
			if(user.isPresent()) {
				model.addAttribute("user", user.orElse(null));
	            return "userDetail";
			}else {
				return "redirect:/api/users/user";
			}
			
		}catch(IllegalArgumentException e) {
			return "redirect:/api/users/user";
		}
		
	}
	
	
	// ユーザー作成画面へ遷移
	@GetMapping("create")
	public String userCreate(@RequestParam(defaultValue = "false")String login , Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("login", login);
		return "userCreate";
	}
	
	// パスワード変更画面へ遷移
	@GetMapping("user/{id}/change-password")
	public String changePassword(@PathVariable Long id, Model model, @ModelAttribute User user) {
		model.addAttribute("user", user);
		
		// changePasswordメソッドで利用
		model.addAttribute("passwordChange", new PasswordChange());
		return "changePassword";
	}
	
	/**
	 * ユーザー作成
	 * @param User user
	 * @param Model model
	 * @return String
	 */
	@PostMapping("create")
	public String createUser(@Validated(User.Create.class) @ModelAttribute User user,
			BindingResult bindingResult, Model model){

		if (bindingResult.hasErrors()) {
			return "userCreate";
		}

		try {
			User createdUser = userService.createUser(user);
			model.addAttribute(createdUser);
			
			return "redirect:/api/users/user";
		}catch(IllegalArgumentException e) {
			bindingResult.reject("error.create", e.getMessage());
			return "userCreate";
		}
		
	}
	
	/**
	 * 更新
	 * @param Long id
	 * @param User user
	 * @param String userName
	 * @param String email
	 * @param BindingResult bindingResult
	 * @param Model model
	 * @return String
	 */
	@PostMapping("user/{id}")
	public String updateUser(@PathVariable Long id,
			@Validated(User.Update.class) @ModelAttribute User user,
			BindingResult bindingResult, Model model){

		if (bindingResult.hasErrors()) {
			userService.restoreUserDisplayFields(id, user);
			return "userDetail";
		}

		try {
			User updatedUser = userService.updateUser(id, user);
			model.addAttribute("user", updatedUser);
			
			return "redirect:/api/users/user";
		}catch(IllegalArgumentException e) {
			bindingResult.reject("error.update", e.getMessage());
			userService.restoreUserDisplayFields(id, user);
			return "userDetail";
		}
		
	}
	
	@PostMapping("user/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return "redirect:/api/users/user";
        } catch (IllegalArgumentException e) {
        	return "redirect:/api/users/user";
        }
    }
	
	
	@PostMapping("user/{id}/change-password")
	public String changePassword(
			User user,
			@PathVariable Long id, 
			@AuthenticationPrincipal UserDetails userDetails,
			@Validated(PasswordChange.PasswordUpdate.class) 
			@ModelAttribute PasswordChange passwordChange,
			BindingResult bindingResult,
			Model model
			) {
		
		if(bindingResult.hasErrors()) {
			return "changePassword";
		}
		
		try {
			
			// 入力したパスワードが現在のパスワードと一致しているか確認
			List<String> errors = userService.checkPassword(passwordChange, user);
			
			if(!errors.isEmpty()) {
				for(String error: errors) {
					bindingResult.reject("error.passwordChange", error);
				}
				return "changePassword";
			}
			
			String newPassword = passwordChange.getNewPassword();
			userService.savePassword(user, newPassword);
			
			return "redirect:/api/users/user";
		}catch(IllegalArgumentException e) {
			return "redirect:/api/users/user";
		}
		
	}
		
	
	@PostMapping("/upload")
	public String uploadCsvFile(@RequestParam("file") MultipartFile file, Model model) {
	    try {
	        userService.uploadCsvFile(file);
	        return "redirect:/api/users/user";
	    } catch (Exception e) {
	        model.addAttribute("errorMessage", "CSVファイルのインポート中にエラーが発生しました: " + e.getMessage());
	        return "user";
	    }
	}
	
	
	
	
}
