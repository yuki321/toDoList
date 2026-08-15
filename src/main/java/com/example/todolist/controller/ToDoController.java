package com.example.todolist.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.todolist.entity.ToDo;
import com.example.todolist.entity.User;
import com.example.todolist.service.ToDoServiceIF;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/")
public class ToDoController {

	@Autowired
	private ToDoServiceIF toDoService;
	
	
	/**
	 * 全件取得
	 * @param UserDetails userDetails
	 * @param User user
	 * @param Model model
	 * @return String
	 */
	@GetMapping("/")
	public String getAllToDo(@AuthenticationPrincipal UserDetails userDetails, 
			@ModelAttribute User user,
			Model model){

		// ログイン情報を基にタスク一覧を表示
		List<ToDo> todos = toDoService.findAllToDo(userDetails);
		final int todoCount = todos.size();
		
		model.addAttribute("todos", todos);
		model.addAttribute("todoCount", todoCount);
		model.addAttribute("todoCreate", new ToDo());
		model.addAttribute("todoForm", new ToDo());
		model.addAttribute("todoComplete", new ToDo());
		model.addAttribute("undoTask", new ToDo());
		
		
		// 完了済みタスク
		List<ToDo> completedTodos = toDoService.findAllCompletedToDo(userDetails);
		final int completedCount = completedTodos.size();
		model.addAttribute("completedTodos", completedTodos);
		model.addAttribute("completedCount", completedCount);

		// ロールを取得（ロールがAdminの場合、管理者画面へのリンクを表示する）
		Map<String, Object> userIfo = toDoService.getUserInfo(userDetails);
		model.addAttribute("role", userIfo.get("role"));

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
			@Valid @ModelAttribute("todoComplete") ToDo todo, 
			BindingResult bindingResult, 
			@AuthenticationPrincipal UserDetails userDetails,
			Model model
			) {
		
		// userIdを取得
		Map<String, Object> userInfo = toDoService.getUserInfo(userDetails);
		todo.setUserId((Long) userInfo.get("id"));		
		model.addAttribute("todo", todo);
		
		try {
			boolean result = toDoService.completeTask(todo);
			if (result) {
				System.out.println("編集成功！");
			} else {
				System.out.println("編集失敗");
			}
			
			return "redirect:/";
		}catch(IllegalArgumentException e) {
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
			@Valid @ModelAttribute("undoTask") ToDo todo, 
			BindingResult bindingResult, 
			@AuthenticationPrincipal UserDetails userDetails,
			Model model
			) {
		
		// userIdを取得
		Map<String, Object> userInfo = toDoService.getUserInfo(userDetails);
		todo.setUserId((Long) userInfo.get("id"));		
		model.addAttribute("todo", todo);
		
		try {
			boolean result = toDoService.undoCompletedTask(todo);
			if (result) {
				System.out.println("編集成功！");
			} else {
				System.out.println("編集失敗");
			}
			
			return "redirect:/";
		}catch(IllegalArgumentException e) {
			return "redirect:/";
		}
		
	}
	

	@PostMapping("/")
	public String createToDo(@Validated @ModelAttribute("todoCreate") ToDo todo,
			BindingResult bindingResult,
			@AuthenticationPrincipal UserDetails userDetails,
			Model model) {

		if (bindingResult.hasErrors()) {
			
			System.out.println(bindingResult);
			addIndexModelAttributes(userDetails, model);
			
			/**
			 * FIXME タスク作成をダイアログではなく、別ページで実装する？
			 * 　　　　　作成ボタン押下時にエラー発生しても、ダイアログが消えるため、
			 * 　　　　　次ダイアログを表示したタイミングでエラーが表示される
			 */
			return "index";
		}

		Map<String, Object> userInfo = toDoService.getUserInfo(userDetails);
		todo.setUserId((Long) userInfo.get("id"));
		todo.setStatus(true);

		try {
			boolean result = toDoService.insertRecord(todo);

			if (result) {
				System.out.println("作成成功！");
			} else {
				System.out.println("作成失敗");
			}

			return "redirect:/";
		} catch (IllegalArgumentException e) {
			bindingResult.reject("error.create", e.getMessage());
			addIndexModelAttributes(userDetails, model);
			return "index";
		}

	}

	private void addIndexModelAttributes(UserDetails userDetails, Model model) {
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
			@Valid @ModelAttribute("todoForm") ToDo todo, 
			BindingResult bindingResult, 
			@AuthenticationPrincipal UserDetails userDetails,
			Model model
			) {
		
		
		// userIdを取得
		Map<String, Object> userInfo = toDoService.getUserInfo(userDetails);
		todo.setUserId((Long) userInfo.get("id"));		
		model.addAttribute("todo", todo);
		
		try {
			boolean result = toDoService.updateRecord(todo);
			if (result) {
				System.out.println("編集成功！");
			} else {
				System.out.println("編集失敗");
			}
			
			return "redirect:/";
		}catch(IllegalArgumentException e) {
			return "redirect:/";
		}
		
		
	}
	
	
	/**
	 * タスク削除
	 * @param UserDetails userDetails
	 * @return
	 */
	@PostMapping("/delete")
	public String deleteToDo(@RequestParam String id) {
		
		try {
			toDoService.deleteRecord(Long.parseLong(id));
			return "redirect:/";
		}catch(IllegalArgumentException e) {
			return "redirect:/";
		}
		
	}

	
}
