package todolist.entity;

import todolist.service.MailService;

public class UserCreateMail implements MailSend {

    private final MailService mailService;

    public UserCreateMail(MailService mailService) {
    	this.mailService = mailService;
    }

    /**
     * ユーザー登録メール送信
     * @param email
     * @param token
     */
	@Override
	public void sendMail(final String email, final String token) {
		
		String subject = "[todolist]ユーザー登録のご案内";
		String resetLink = "http://localhost:8080/validate_token?token=" + token + "&kind=registration";
		String text = "ユーザー登録のリクエストを受け付けました。下記リンクからリセットをしてください。\n"
				+ resetLink
				+ "\nメールに心当たりがない場合、このメールを削除してください。\n"
				+ "\n"
				+ "todolist";

		mailService.sendEmail(email, subject, text);
		
	}
	
}
