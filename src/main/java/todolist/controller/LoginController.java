package todolist.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import todolist.entity.PasswordChange;

@Controller
public class LoginController {
	@GetMapping("/login")
	public String login(final Model model) {
		model.addAttribute("passwordChange", new PasswordChange());
		return "login";
	}

}
