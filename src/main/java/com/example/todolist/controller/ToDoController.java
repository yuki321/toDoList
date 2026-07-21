package com.example.todolist.controller;

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
		
		// ログイン情報を基にタスク一覧を表示
		List<ToDo> todos = toDoService.findAllToDo(userDetails);
		model.addAttribute("todo", todos);
		
		Map<String, Object> userIfo = toDoService.getUserInfo(userDetails);
		model.addAttribute("role", userIfo.get("role"));
		
		return "index";
	}
	
	@PostMapping("/")
	public String createToDo(// @Validated(ToDo.class), 
			@ModelAttribute ToDo todo, BindingResult bindingResult) {
		
		if(bindingResult.hasErrors()) {
			return "index";
		}
		
		todo.setId(todo.getId());
		todo.setUserId(todo.getUserId());
		todo.setContent(todo.getContent());
		todo.setStatus(true);
		
		try {
			boolean result = toDoService.insertRecord(todo);
			
			if(result) {
				System.out.println("作成成功！");
			}else {
				System.out.println("作成失敗");
			}
			
//			model.addAttribute(createdToDo);
			
			return "redirect:/";
		}catch(IllegalArgumentException e) {
			bindingResult.reject("error.create", e.getMessage());
			return "index";
		}
		
	}
	


	
	
	
}
