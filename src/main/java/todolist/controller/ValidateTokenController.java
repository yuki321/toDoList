package todolist.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import common.Logger;
import todolist.entity.PasswordReset;
import todolist.entity.User;
import todolist.repository.PasswordResetTokenRepositoryIF;
import todolist.service.MailServiceIF;

import org.springframework.ui.Model;

@Controller
@RequestMapping("/")
public class ValidateTokenController {
	
	@Autowired
	private PasswordResetTokenRepositoryIF passwordResetTokenRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private MailServiceIF mailService;
	
	private final String CLASS_NAME = this.getClass().getSimpleName();
	
	/**
	 * トークン検証
	 * @param String token
	 * @param String kind
	 * @param Model model
	 * @return String
	 */
	@GetMapping("/validate_token")
	public String validateToken(@RequestParam("token") final String token, @RequestParam("kind") final String kind, final Model model) {

		Logger.log(CLASS_NAME, "validateToken: トークン検証開始");
		final PasswordReset passwordReset = new PasswordReset();
		
		// トークンの検証
		final boolean result = passwordReset.validatePasswordResetToken(token, passwordEncoder, passwordResetTokenRepository);
		if(!result) {
			model.addAttribute("errorMessage", "トークンが不正です");
			Logger.log(CLASS_NAME, "[Error] validateToken: トークンが不正です");
			return "redirect:/login";  
		}
		
		model.addAttribute("resetPassword", new PasswordReset());
			
		// 検証が問題ない場合、パスワード再設定または新規登録画面へ遷移
		// a.パスワード再設定 
		if(kind.equals("reset")) {
			return "resetPassword";
		}
		
		// b.新規登録
		model.addAttribute("user", new User()); 
		model.addAttribute("login", false);
		return "userCreate";
	}

	
}




