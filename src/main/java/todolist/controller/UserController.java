package todolist.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import todolist.entity.Pager;
import todolist.entity.PasswordChange;
import todolist.entity.User;
import todolist.service.ToDoServiceIF;
import todolist.service.UserServiceIF;

@Controller
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	private UserServiceIF userService;
	
	@Autowired
	private ToDoServiceIF toDoService;
	

	
	/**
	 * 全件取得
	 * @param UserDetails userDetails
	 * @param Model model
	 * @param Pageable pageable
	 * @param int page Xページ目（インデックスは0始まり）
	 * @return String
	 */
	@GetMapping("user")
	public String getAllUsers(@AuthenticationPrincipal final UserDetails userDetails, final Model model, 
			@PageableDefault(page = 0, size = 10) final Pageable pageable, 
			@RequestParam(name = "page") final int page){
		
		final Map<String, Object> userIfo = toDoService.getUserInfo(userDetails);
		final String role = (String) userIfo.get("role");
		
		if(!"Admin".equals(role)) {
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
		
		return "user";
	}
	
	
	/**
	 * ユーザー検索
	 * @param User user
	 * @param Model model
	 * @return
	 */
	@GetMapping("search")
	public String searchUsers(@ModelAttribute("user") final User user, final Model model
			, @PageableDefault(page = 0, size = 10) final Pageable pageable){

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
		
		
		return "user";
	}
	
	
	/**
	 * IDで指定したユーザーを取得
	 * @param Long id
	 * @param Madel model
	 * @return String
	 */
	@GetMapping("user/{id}")
	public String getUserById(@PathVariable final Long id, final Model model){
		
		// idがLong型でない場合
		if(!(id instanceof Long)) {
			return "redirect:/api/users/user?page=0";
		}

		try {
			final Optional<User> user = userService.getUserById(id);
			
			if(user.isPresent()) {
				model.addAttribute("user", user.orElse(null));
	            return "userDetail";
			}else {
				return "redirect:/api/users/user?page=0";
			}
			
		}catch(IllegalArgumentException e) {
			return "redirect:/api/users/user?page=0";
		}
		
	}
	
	
	/**
	 * ユーザー作成画面へ遷移
	 * @param String login(デフォルトはfalse)
	 * @param model
	 * @return String
	 */
	@GetMapping("create")
	public String userCreate(@RequestParam(defaultValue = "false") final String login , final Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("login", login);
		return "userCreate";
	}
	

	/**
	 * パスワード変更画面へ遷移
	 * @param Long id
	 * @param Model model
	 * @param User user
	 * @return String
	 */
	@GetMapping("user/{id}/change-password")
	public String changePassword(@PathVariable final Long id, final Model model, @ModelAttribute final User user) {
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
	public String createUser(@Validated(User.Create.class) @ModelAttribute final User user,
			BindingResult bindingResult, Model model){

		if (bindingResult.hasErrors()) {
			return "userCreate";
		}

		try {
			final User createdUser = userService.createUser(user);
			
			model.addAttribute(createdUser);
			return "redirect:/api/users/user?page=0";
		}catch(IllegalArgumentException e) {
			bindingResult.reject("error.create", e.getMessage());
			return "userCreate";
		}
		
	}
	
	
	/**
	 * ユーザー更新
	 * @param Long id
	 * @param User user
	 * @param BindingResult bindingResult
	 * @param Model model
	 * @return String
	 */
	@PostMapping("user/{id}")
	public String updateUser(@PathVariable final Long id,
			@Validated(User.Update.class) @ModelAttribute final User user,
			final BindingResult bindingResult, final Model model){

		if (bindingResult.hasErrors()) {
			userService.restoreUserDisplayFields(id, user);
			return "userDetail";
		}

		try {
			final User updatedUser = userService.updateUser(id, user);
			model.addAttribute("user", updatedUser);
			
			return "redirect:/api/users/user?page=0";
		}catch(IllegalArgumentException e) {
			bindingResult.reject("error.update", e.getMessage());
			userService.restoreUserDisplayFields(id, user);
			return "userDetail";
		}
		
	}
	
	
	/**
	 * ユーザー削除
	 * @param Long id
	 * @return String
	 */
	@PostMapping("user/{id}/delete")
    public String deleteUser(@PathVariable final Long id) {
        try {
            userService.deleteUser(id);
            return "redirect:/api/users/user?page=0";
        } catch (IllegalArgumentException e) {
        	return "redirect:/api/users/user?page=0";
        }
    }
	
	
	/**
	 * パスワード変更（再設定とは別）
	 * @param User user
	 * @param Long id
	 * @param UserDetails userDetails
	 * @param PasswordChange passwordChange
	 * @param BindingResult bindingResult
	 * @param Model model
	 * @return String
	 */
	@PostMapping("user/{id}/change-password")
	public String changePassword(
			final User user,
			@PathVariable final Long id, 
			@AuthenticationPrincipal final UserDetails userDetails,
			@Validated(PasswordChange.PasswordUpdate.class) @ModelAttribute final PasswordChange passwordChange,
			final BindingResult bindingResult,
			final Model model
			) {
		
		if(bindingResult.hasErrors()) {
			return "changePassword";
		}
		
		try {
			
			// 入力したパスワードが現在のパスワードと一致しているか確認
			final List<String> errors = userService.checkPassword(passwordChange, user);
			
			if(!errors.isEmpty()) {
				for(String error: errors) {
					bindingResult.reject("error.passwordChange", error);
				}
				return "changePassword";
			}
			
			final String newPassword = passwordChange.getNewPassword();
			userService.savePassword(user, newPassword);
			
			return "redirect:/api/users/user?page=0";
		}catch(IllegalArgumentException e) {
			return "redirect:/api/users/user?page=0";
		}
		
	}
		
	
	/**
	 * CSVアップロード
	 * @param MultipartFile file
	 * @RedirectAttributes redirectAttributes
	 * @return String
	 */
	@PostMapping("/upload")
	public String uploadCsvFile(@RequestParam("file") final MultipartFile file, final RedirectAttributes redirectAttributes) {
	    try {
	    	final List<String> errors = userService.uploadCsvFile(file);
	        
	        if(!errors.isEmpty()) {
	        	redirectAttributes.addFlashAttribute("CSV_errors", errors);	        	
	        }
	        
	    } catch (Exception e) {
	    	redirectAttributes.addFlashAttribute("errorMessage", "CSVファイルのインポート中にエラーが発生しました: " + e.getMessage());
	    }
	    
        return "redirect:/api/users/user?page=0";
	}
	
	
	/**
	 * CSVダウンロード
	 * @param model
	 * @return String
	 */
	@PostMapping("/download")
	public String downloadCsvFile(final Model model) {
		try {
			userService.downloadCsvFile();
			return "redirect:/api/users/user?page=0";
	    } catch (Exception e) {
	        model.addAttribute("errorMessage", "CSVファイルのダウンロード中にエラーが発生しました: " + e.getMessage());
	
	        final List<User> users = userService.getAllUsers();
			final int userCount = users.size();
	        model.addAttribute("user", new User()); 
	        model.addAttribute("users", users); 
	        model.addAttribute("userCount", userCount); 
	        
	        return "user";
	    }
		
	}
	
	
}
