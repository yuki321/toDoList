package todolist.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import todolist.common.Logger;
import todolist.entity.PasswordChange;
import todolist.entity.ToDo;
import todolist.entity.User;
import todolist.repository.ToDoRepositoryIF;

@Service
@Transactional
public class ToDoScenario {
	
	@Autowired
	private ToDoRepositoryIF toDoRepository;
	
	@Autowired
	private ToDoServiceIF toDoService;
	
	@Autowired
	private ToDoUpdateServiceIF toDoUpdateService;
	
	@Autowired
	private UserServiceIF userService;
	
	@Autowired
	private UserUpdateServiceIF userUpdateService;
	
	private final String CLASS_NAME = this.getClass().getSimpleName();
		

	/**
	 * タスク一覧を取得する
	 * @param UserDetails userDetails
	 * @param Model model
	 * @return String
	 */
	public String getAllToDoData(
			final UserDetails userDetails, 
			final Model model){
		

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
		
		return "";
	}
	
	
	/**
	 * タスク完了処理
	 * @param ToDo todo
	 * @param UserDetails userDetails
	 * @param Model model
	 * @return String
	 */
	public String taskCompleted(
			final ToDo todo, 
			final UserDetails userDetails,
			final Model model
			) {
		
		
		// userIdを取得
		final Map<String, Object> userInfo = toDoService.getUserInfo(userDetails);
		todo.setUserId((Long) userInfo.get("id"));		
		model.addAttribute("todo", todo);
		
		try {
			final boolean result = toDoUpdateService.completeTask(todo);
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
	 * タスク完了の取り消し処理
	 * @param ToDo todo
	 * @param UserDetails userDetails
	 * @param Model model
	 * @return String
	 */
	public String undoCompletedTask(
			final ToDo todo, 
			final UserDetails userDetails,
			final Model model
			) {
		
		
		// userIdを取得
		final Map<String, Object> userInfo = toDoService.getUserInfo(userDetails);
		todo.setUserId((Long) userInfo.get("id"));		
		model.addAttribute("todo", todo);
		
		try {
			final boolean result = toDoUpdateService.undoCompletedTask(todo);
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
	 * ユーザー編集画面へ遷移
	 * @param UserDetails userDetails
	 * @param Long id
	 * @param Model model
	 * @return String
	 */
	public String getOthers(
			final UserDetails userDetails, 
			final Long id, 
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
	 * パスワード変更処理
	 * @param User user
	 * @param PasswordChange passwordChange
	 * @param BindingResult bindingResult
	 * @param Model model
	 * @return String
	 */
	public String changePasswordFromToDo(
			final User user,
			final PasswordChange passwordChange,
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
			userUpdateService.savePassword(user, newPassword);
			
			return "redirect:/";
		}catch(IllegalArgumentException e) {
			Logger.log(CLASS_NAME, e + " [Error] changePasswordFromToDo: パスワード変更処理失敗");
			return "redirect:/";
		}
		
	}
	
	
	/**
	 * タスク作成処理
	 * @param ToDo todo
	 * @param BindingResult bindingResult
	 * @param UserDetails userDetails
	 * @param Model model
	 * @return String
	 */
	public String createToDo(
			final ToDo todo,
			final BindingResult bindingResult,
			final UserDetails userDetails,
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
			final boolean result = toDoUpdateService.insertRecord(todo);

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
	 * タスク編集処理
	 * @param ToDo todo
	 * @param UserDetails userDetails
	 * @param Model model
	 * @return String
	 */
	public String editToDo(
			final ToDo todo, 
			final UserDetails userDetails,
			final Model model
			) {
		Logger.log(CLASS_NAME, "editToDo: タスク編集処理開始");
		
		// userIdを取得
		final Map<String, Object> userInfo = toDoService.getUserInfo(userDetails);
		todo.setUserId((Long) userInfo.get("id"));		
		model.addAttribute("todo", todo);
		
		try {
			final boolean result = toDoUpdateService.updateRecord(todo);
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
	
}
