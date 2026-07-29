package com.example.todolist.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.todolist.entity.PasswordReset;
import com.example.todolist.repository.PasswordResetTokenRepository;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/")
public class ValidateTokenController {
	
	@Autowired
	private PasswordResetTokenRepository passwordResetTokenRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	
	
	@GetMapping("/validate_token")
	public String validateToken(@RequestParam("token") String token, Model model) {

		// トークンの検証
		boolean result = validatePasswordResetToken(token);

		if(!result) {
			model.addAttribute("errorMessage", "トークンが不正です");
			
			return "redirect:/login";  
		}
		
		model.addAttribute("resetPassword", new PasswordReset());
			
		// 検証が問題ない場合、パスワード再設定フォームへ遷移
		return "resetPassword";
	}
	
	
	/**
	 * トークンの検証
	 * @param String token
	 * @return boolean
	 */
	public boolean validatePasswordResetToken(String rawToken) {
		
		// 1.トークンが存在するか確認する処理
		// 全有効トーケンを取得して、ハッシュ化されていない平文トークンと比較
		List<Map<String, Object>> resultList = passwordResetTokenRepository.findAllTokenHash();

		
		boolean matchResult = false;
		String expires_time = null;
		for(Map<String, Object> m: resultList) {
			
			String token_hash = (String)m.get("token_hash");
			matchResult = passwordEncoder.matches(rawToken, token_hash);

			if(matchResult) {
				// マッチした組み合わせを次の判定で利用
				expires_time = m.get("expires_at").toString();
				break;
			}
			
		}
		// 一致しなければfalse
		if(!matchResult) {
			return false;
		}
		
		// 2.トークンの有効期限内か
		LocalDateTime now = LocalDateTime.now();
		
		if(expires_time == null) {
			return false;
		}
		
		expires_time = expires_time.replace("T", " ");
		// "yyyy/MM/dd HH:mm:ss" => "yyyy-MM-dd HH:mm:ss"
		LocalDateTime expired_at = toLocalDateTime(expires_time, "yyyy-MM-dd HH:mm:ss");
			
		// トークンの有効期限が切れている場合、false
		if(expired_at.isBefore(now)) {
			return false;
		}
		
		// 3.トークンが使用済みか(nullでなければfalse)
		if(resultList.get(0).get("used_at") != null) {
			return false;
		}
		
		return true; 
	}	
	

	// String => LocalDateTimeへ変換
	public static LocalDateTime toLocalDateTime(String date, String format) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.parse(date, dtf);
    }
	
}




