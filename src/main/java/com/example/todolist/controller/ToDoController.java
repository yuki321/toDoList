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
import com.example.todolist.service.ToDoService;

@Controller
@RequestMapping("/")
public class ToDoController {

	@Autowired
	private ToDoService toDoService;
	
	
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
		;
		// ログイン情報を基にタスク一覧を表示
		List<ToDo> todos = toDoService.findAllToDo(userDetails);
		
		model.addAttribute("todos", todos);
		model.addAttribute("todoForm", new ToDo());

		Map<String, Object> userIfo = toDoService.getUserInfo(userDetails);
		model.addAttribute("role", userIfo.get("role"));

		return "index";
	}

	@PostMapping("/")
	public String createToDo(@Validated @ModelAttribute("todoForm") ToDo todo,
			BindingResult bindingResult,
			@AuthenticationPrincipal UserDetails userDetails,
			Model model) {

		if (bindingResult.hasErrors()) {
			addIndexModelAttributes(userDetails, model);
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
			@ModelAttribute("todoForm") ToDo todo, 
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
				System.out.println("作成成功！");
			} else {
				System.out.println("作成失敗");
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
