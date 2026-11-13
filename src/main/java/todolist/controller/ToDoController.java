package todolist.controller;

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

import todolist.common.Logger;
import jakarta.validation.Valid;
import todolist.entity.PasswordChange;
import todolist.entity.ToDo;
import todolist.entity.User;
import todolist.service.ToDoScenario;
import todolist.service.ToDoServiceIF;
import todolist.service.ToDoUpdateServiceIF;
import todolist.service.UserServiceIF;
import todolist.service.UserUpdateServiceIF;

@Controller
@RequestMapping("/")
public class ToDoController {

	@Autowired
	private ToDoServiceIF toDoService;
	
	@Autowired
	private ToDoUpdateServiceIF toDoUpdateService;
	
	@Autowired
	private ToDoScenario toDoScenario;
	
	@Autowired
	private UserServiceIF userService;
	
	@Autowired
	private UserUpdateServiceIF userUpdateService;
	
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
		toDoScenario.getAllToDoData(userDetails, model);
		
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
				
		return toDoScenario.taskCompleted(todo, userDetails, model);
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
		
		return toDoScenario.undoCompletedTask(todo, userDetails, model);
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
		
		return toDoScenario.getOthers(userDetails, id, model);
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
		
		return toDoScenario.changePasswordFromToDo(user, passwordChange, bindingResult, model);
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
            userUpdateService.deleteUser(id);
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
				
		return toDoScenario.createToDo(todo, bindingResult, userDetails, model);
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
		
		return toDoScenario.editToDo(todo, userDetails, model);
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
			toDoUpdateService.deleteRecord(Long.parseLong(id));
			return "redirect:/";
		}catch(IllegalArgumentException e) {
			Logger.log(CLASS_NAME, e + " [Error] deleteToDo: タスク削除処理失敗");
			return "redirect:/";
		}
		
	}

	
}
