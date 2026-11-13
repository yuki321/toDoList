package todolist.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import todolist.entity.Pager;
import todolist.entity.User;
import todolist.repository.UserRepository;

@Service
@Transactional
public class UserScenario {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserServiceIF userService;
	
	@Autowired
	private ToDoServiceIF toDoService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	private final String CLASS_NAME = this.getClass().getSimpleName();
	
	
	/**
	 * ユーザー情報を全件取得
	 * @param UserDetails userDetails
	 * @param Model model
	 * @param Pageable pageable
	 * @param int page
	 * @return String
	 */
	public String getAllUsersData(
			final UserDetails userDetails,
			final Model model, 
			final Pageable pageable, 
			final int page) {
		
		final Map<String, Object> userIfo = toDoService.getUserInfo(userDetails);
		final String role = (String) userIfo.get("role");
		
		if(!role.equals("Admin")) {
			return "redirect:/";
		}
		
		final List<User> users = userService.getAllUsers();
		final Page<User> usersPerPage = userService.getAllUsers(pageable);
		
		// 表示するユーザーデータ
		model.addAttribute("users", usersPerPage.getContent());
		model.addAttribute("userCount", users.size());
		model.addAttribute("user", new User());

		// ページャー関連のデータ
		final Pager pager = new Pager();
		int topIndex = pager.getTopIndex(page, usersPerPage.getSize());
		int lastIndex = pager.getLastIndex(page, usersPerPage.getSize());
		
		model.addAttribute("pages", usersPerPage);
		model.addAttribute("topIndex", topIndex);
		model.addAttribute("lastIndex", lastIndex);
		
		// データ件数・ページャーの表示
		model.addAttribute("display", true);
		
		
		return "";
	}
	
	
	/**
	 * ユーザー情報を検索
	 * @param User user
	 * @param Model model
	 * @param Pageable pageable
	 * @return String
	 */
	public String searchUsersData(
			final User user, 
			final Model model, 
			final Pageable pageable) {
		
		final Page<User> users = userService.searchUsers(user, pageable);
		final int userCount = users.getContent().size();


		if(userCount < 0) return "redirect:/api/users/user?page=0";

		model.addAttribute("users", users.getContent());
		model.addAttribute("user", new User());
		model.addAttribute("userCount", userCount);
		
		
		// ページャー関連のデータ
		final Pager pager = new Pager();
		final int topIndex = pager.getTopIndex(0, users.getSize());
		final int lastIndex = pager.getLastIndex(0, users.getSize());
		
		model.addAttribute("pages", users);
		model.addAttribute("topIndex", topIndex);
		model.addAttribute("lastIndex", lastIndex);
		
		// データ件数・ページャーの非表示
		model.addAttribute("display", false);
		
		return "";
	}
	
}


