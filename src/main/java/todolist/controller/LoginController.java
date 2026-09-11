package todolist.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import common.Logger;
import todolist.entity.PasswordChange;

@Controller
public class LoginController {
	private final String CLASS_NAME = this.getClass().getSimpleName();
	
	@GetMapping("/login")
	public String login(final Model model) {
		Logger.log(CLASS_NAME, "login: ログイン画面表示");
		model.addAttribute("passwordChange", new PasswordChange());
		return "login";
	}

}
