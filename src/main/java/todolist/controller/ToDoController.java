package todolist.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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

import common.Logger;
import jakarta.validation.Valid;
import todolist.entity.PasswordChange;
import todolist.entity.ToDo;
import todolist.entity.User;
import todolist.service.ToDoServiceIF;
import todolist.service.UserServiceIF;

@Controller
@RequestMapping("/")
public class ToDoController {

	@Autowired
	private ToDoServiceIF toDoService;
	
	@Autowired
	private UserServiceIF userService;
	
	private final String CLASS_NAME = this.getClass().getSimpleName();
	
	
	/**
	 * 全件取得
	 * @param UserDetails userDetails
	 * @param User user
	 * @param Model model
	 * @return String
	 */
	@GetMapping("/")
	public String getAllToDo(@AuthenticationPrincipal final UserDetails userDetails, 
			@ModelAttribute final User user,
			final Model model){
		
		Logger.log(CLASS_NAME, "getAllToDo: タスク全件取得");

		// ログイン情報を基にタスク一覧を表示
		final List<ToDo> todos = toDoService.findAllToDo(userDetails);
		final int todoCount = todos.size();
		
		model.addAttribute("todos", todos);
		model.addAttribute("todoCount", todoCount);
		model.addAttribute("todoCreate", new ToDo());
		model.addAttribute("todoForm", new ToDo());
		model.addAttribute("todoComplete", new ToDo());
		model.addAttribute("undoTask", new ToDo());
		
		// 完了済みタスク
		final List<ToDo> completedTodos = toDoService.findAllCompletedToDo(userDetails);
		final int completedCount = completedTodos.size();
		model.addAttribute("completedTodos", completedTodos);
		model.addAttribute("completedCount", completedCount);

		// ロールを取得（ロールがAdminの場合、管理者画面へのリンクを表示する）
		final Map<String, Object> userInfo = toDoService.getUserInfo(userDetails);
		model.addAttribute("role", userInfo.get("role"));

		// ユーザー編集 
		model.addAttribute("userId", userInfo.get("id"));
		return "index";
	}
	
	
	/**
	 * タスク完了
	 * @param ToDo todo
	 * @param BindingResult bindingResult
	 * @param UserDetails userDetails
	 * @param Model model
	 * @return String
	 */
	@PostMapping("/complete")
	public String getTaskCompleted(
			@Valid @ModelAttribute("todoComplete") final ToDo todo, 
			final BindingResult bindingResult, 
			@AuthenticationPrincipal final UserDetails userDetails,
			final Model model
			) {
		
		Logger.log(CLASS_NAME, "getTaskCompleted: タスク完了処理開始");
		
		// userIdを取得
		final Map<String, Object> userInfo = toDoService.getUserInfo(userDetails);
		todo.setUserId((Long) userInfo.get("id"));		
		model.addAttribute("todo", todo);
		
		try {
			final boolean result = toDoService.completeTask(todo);
			if (result) {
				Logger.log(CLASS_NAME, "getTaskCompleted: タスク完了処理成功");
			} else {
				Logger.log(CLASS_NAME, "[Error] getTaskCompleted: タスク完了処理失敗");
			}
			
			return "redirect:/";
		}catch(IllegalArgumentException e) {
			Logger.log(CLASS_NAME, e + " [Error]  getTaskCompleted: タスク完了処理失敗");
			return "redirect:/";
		}
		
	}
	
	
	/**
	 * タスク完了の取り消し
	 * @param ToDo todo
	 * @param BindingResult bindingResult
	 * @param UserDetails userDetails
	 * @param Model model
	 * @return String
	 */
	@PostMapping("/undoTask")
	public String undoCompletedTask(
			@Valid @ModelAttribute("undoTask") final ToDo todo, 
			final BindingResult bindingResult, 
			@AuthenticationPrincipal final UserDetails userDetails,
			final Model model
			) {
		
		Logger.log(CLASS_NAME, "undoCompletedTask: タスク完了の取り消し処理開始");
		
		// userIdを取得
		final Map<String, Object> userInfo = toDoService.getUserInfo(userDetails);
		todo.setUserId((Long) userInfo.get("id"));		
		model.addAttribute("todo", todo);
		
		try {
			final boolean result = toDoService.undoCompletedTask(todo);
			if (result) {
				Logger.log(CLASS_NAME, "undoCompletedTask: タスク完了の取り消し処理成功");
			} else {
				Logger.log(CLASS_NAME, "[Error] undoCompletedTask: タスク完了の取り消し処理失敗");
			}
			
			return "redirect:/";
		}catch(IllegalArgumentException e) {
			Logger.log(CLASS_NAME, e + " [Error] undoCompletedTask: タスク完了の取り消し処理失敗");
			return "redirect:/";
		}
		
	}
	
	
	/**
	 * 編集ページへ遷移
	 * @param Long id
	 * @param Model model
	 * @return String
	 */
	@GetMapping("others/{id}")
	public String getOthers(@AuthenticationPrincipal final UserDetails userDetails, 
			@PathVariable final Long id, 
			final Model model){
		// idがLong型でない場合
		if(!(id instanceof Long)) {
			return "redirect:/";
		}
		
		Logger.log(CLASS_NAME, "getOthers: ユーザー編集画面へ遷移");

		try {
			final Optional<User> user = userService.getUserById(id);

			if(user.isPresent()) {
				model.addAttribute("user", user.orElse(null));
				
				// ロールを取得（ロールがAdminの場合、アカウント削除ボタンは非表示）
				final Map<String, Object> userInfo = toDoService.getUserInfo(userDetails);
				model.addAttribute("role", userInfo.get("role"));
				model.addAttribute("id", userInfo.get("id"));

				return "userOthers";
			}else {
				Logger.log(CLASS_NAME, "[Error] getOthers: 該当ユーザーが存在しません");
				return "redirect:/";
			}
			
		}catch(IllegalArgumentException e) {
			Logger.log(CLASS_NAME, e + " [Error] getOthers: ユーザー編集画面へ遷移失敗");
			return "redirect:/";
		}
		
	}
	
	
	/**
	 * パスワード変更画面へ遷移
	 * @param Long id
	 * @param Model model
	 * @param User user
	 * @return String
	 */
	@GetMapping("others/{id}/change-password-todo")
	public String changePasswordFromToDo(@PathVariable final Long id, 
			final Model model, 
			@ModelAttribute final User user) {
		
		Logger.log(CLASS_NAME, "changePasswordFromToDo: パスワード変更画面へ遷移");
		
		model.addAttribute("user", user);
		
		// changePasswordメソッドで利用
		model.addAttribute("passwordChange", new PasswordChange());
		return "changePasswordFromToDo";
	}
	
	
	/**
	 * パスワード変更処理
	 * @param user
	 * @param id
	 * @param userDetails
	 * @param passwordChange
	 * @param bindingResult
	 * @param model
	 * @return
	 */
	@PostMapping("others/{id}/change-password-todo")
	public String changePasswordFromToDo(
			final User user,
			@PathVariable final Long id, 
			@AuthenticationPrincipal final UserDetails userDetails,
			@Validated(PasswordChange.PasswordUpdate.class) 
			@ModelAttribute final PasswordChange passwordChange,
			final BindingResult bindingResult,
			final Model model
			) {
		
		Logger.log(CLASS_NAME, "changePasswordFromToDo: パスワード変更処理開始");
		
		if(bindingResult.hasErrors()) {
			Logger.log(CLASS_NAME, "[Error] changePasswordFromToDo: バリデーションエラーが発生しました。" + bindingResult.getAllErrors());
			return "changePasswordFromToDo";
		}
		
		try {
			
			// 入力したパスワードが現在のパスワードと一致しているか確認
			final List<String> errors = userService.checkPassword(passwordChange, user);
			
			if(!errors.isEmpty()) {
				for(String error: errors) {
					Logger.log(CLASS_NAME, "[Error] changePasswordFromToDo: パスワード変更処理失敗。" + error);
					bindingResult.reject("error.passwordChange", error);
				}
				return "changePasswordFromToDo";
			}
			
			final String newPassword = passwordChange.getNewPassword();
			userService.savePassword(user, newPassword);
			
			return "redirect:/";
		}catch(IllegalArgumentException e) {
			Logger.log(CLASS_NAME, e + " [Error] changePasswordFromToDo: パスワード変更処理失敗");
			return "redirect:/";
		}
		
	}
	
	
	/**
	 * ユーザーアカウント削除
	 * ※画面のGetmappingがToDoControllerに実装
	 * されているため、UserControllerではなく、こちらに実装
	 * @param Long id
	 * @return String
	 */
	@PostMapping("/others/{id}/deleteAccount")
    public String deleteUserAccount(@PathVariable("id") final Long id) {

		Logger.log(CLASS_NAME, "deleteUserAccount: ユーザーアカウント削除処理開始");
		try {
            userService.deleteUser(id);
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
        	Logger.log(CLASS_NAME, e + " [Error] deleteUserAccount: ユーザーアカウント削除処理失敗");
        	return "redirect:/api/users/user?page=0";
        }
    }
	

	/**
	 * タスク作成
	 * @param ToDo todo
	 * @param BindingResult bindingResult
	 * @param UserDetails userDetails
	 * @param Model model
	 * @return String
	 */
	@PostMapping("/")
	public String createToDo(@Validated @ModelAttribute("todoCreate") final ToDo todo,
			final BindingResult bindingResult,
			@AuthenticationPrincipal final UserDetails userDetails,
			final Model model) {
		
		Logger.log(CLASS_NAME, "createToDo: タスク作成処理開始");

		if (bindingResult.hasErrors()) {
			Logger.log(CLASS_NAME, "[Error] createToDo: バリデーションエラーが発生しました。" + bindingResult.getAllErrors());
			addIndexModelAttributes(userDetails, model);
			
			/**
			 * FIXME タスク作成をダイアログではなく、別ページで実装する？
			 * 　　　　　作成ボタン押下時にエラー発生しても、ダイアログが消えるため、
			 * 　　　　　次ダイアログを表示したタイミングでエラーが表示される
			 */
			return "index";
		}

		final Map<String, Object> userInfo = toDoService.getUserInfo(userDetails);
		todo.setUserId((Long) userInfo.get("id"));
		todo.setStatus(true);

		try {
			final boolean result = toDoService.insertRecord(todo);

			if (result) {
				Logger.log(CLASS_NAME, "createToDo: タスク作成処理成功");
			} else {
				Logger.log(CLASS_NAME, "[Error] createToDo: タスク作成処理失敗");
			}

			return "redirect:/";
		} catch (IllegalArgumentException e) {
			bindingResult.reject("error.create", e.getMessage());
			addIndexModelAttributes(userDetails, model);
			Logger.log(CLASS_NAME, e + " [Error] createToDo: タスク作成処理失敗");
			return "index";
		}

	}

	private void addIndexModelAttributes(final UserDetails userDetails, final Model model) {
		model.addAttribute("todos", toDoService.findAllToDo(userDetails));
		model.addAttribute("role", toDoService.getUserInfo(userDetails).get("role"));
	}
	

	/**
	 * タスク編集
	 * @param ToDo todo
	 * @param BindingResult bindingResult
	 * @param UserDetails userDetails
	 * @param Model model
	 * @return
	 */
	@PostMapping("/edit")
	public String editToDo(
			@Valid @ModelAttribute("todoForm") final ToDo todo, 
			final BindingResult bindingResult, 
			@AuthenticationPrincipal final UserDetails userDetails,
			final Model model
			) {
		Logger.log(CLASS_NAME, "editToDo: タスク編集処理開始");
		
		// userIdを取得
		final Map<String, Object> userInfo = toDoService.getUserInfo(userDetails);
		todo.setUserId((Long) userInfo.get("id"));		
		model.addAttribute("todo", todo);
		
		try {
			final boolean result = toDoService.updateRecord(todo);
			if (result) {
				Logger.log(CLASS_NAME, "editToDo: タスク編集処理成功");
			} else {
				Logger.log(CLASS_NAME, "[Error] editToDo: タスク編集処理失敗");
			}
			
			return "redirect:/";
		}catch(IllegalArgumentException e) {
			Logger.log(CLASS_NAME, e + " [Error] editToDo: タスク編集処理失敗");
			return "redirect:/";
		}
		
		
	}
	
	
	/**
	 * タスク削除
	 * @param UserDetails userDetails
	 * @return
	 */
	@PostMapping("/delete")
	public String deleteToDo(@RequestParam final String id) {
		
		Logger.log(CLASS_NAME, "deleteToDo: タスク削除処理開始");
		try {
			toDoService.deleteRecord(Long.parseLong(id));
			return "redirect:/";
		}catch(IllegalArgumentException e) {
			Logger.log(CLASS_NAME, e + " [Error] deleteToDo: タスク削除処理失敗");
			return "redirect:/";
		}
		
	}

	
}
