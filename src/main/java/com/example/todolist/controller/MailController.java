package com.example.todolist.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.todolist.entity.PasswordChange;

import org.springframework.ui.Model;

@Controller
@RequestMapping("/login")
public class MailController {
	
	@Autowired
	private MailSender mailSender;

	@Value("${app.mail.from}")
	private String mailFrom;
	
	
	@PostMapping("/login/mail")
	public String sendMail(@RequestParam("PasswordChange")PasswordChange mail, Model model) {
  
 
//	public String sendMail(@ModelAttribute PasswordChange mail, Model model) {
System.out.println("★★★ 通過！！！");
		// 件名
		String subject = "[todolist]パスワードリセットのご案内";
		
		// 本文
		String text = "パスワードリセットのリクエストを受け付けました。下記リンクからリセットをしてください。\n"
				+ "" //　TODO URL(tokenを含む)
				+ "メールに心当たりがない場合、このメールを削除してください。\n"
				+ "\n"
				+ "tidolist";
		SimpleMailMessage message = new SimpleMailMessage();
		
System.out.println("★★★ 通過！！！");
		
		// FIXME ダイアログで送信した内容を設定
		message.setTo(mail.getPasswordChange());
		message.setFrom(mailFrom);
		message.setSubject(subject);
		message.setText(text);
		mailSender.send(message);
		
		model.addAttribute("passwordChange", new PasswordChange());
		
		return "login";
	}

}
