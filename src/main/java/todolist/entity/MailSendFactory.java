package todolist.entity;

import java.util.HashMap;
import java.util.Map;

import common.Logger;
import todolist.service.MailService;

public class MailSendFactory {
	
	/**
	 * メール送信処理の種類を取得する
	 * @param String kind
	 * @param MailService mailService
	 * @return MailSend
	 */
	static public MailSend getMailSendKind(String kind, MailService mailService) {
		
		Logger.log("MailSendFactory", "getMailSendKind: メール送信処理の種類を取得する kind=" + kind);
		Map<String, MailSend> mailType = new HashMap<>();
		mailType.put("PW_RESET", new PasswordResetMail(mailService));
		mailType.put("USER_CREATE", new UserCreateMail(mailService));
		return mailType.get(kind);
	}

}
