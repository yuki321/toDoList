package todolist.entity;

import todolist.service.MailService;

public class PasswordResetMail implements MailSend {

    private final MailService mailService;

    public PasswordResetMail(MailService mailService) {
    	this.mailService = mailService;
    }

    /**
     * パスワードリセットメール送信
     * @param email
     * @param token
     */
    @Override
	public void sendMail(final String email, final String token) {
		
		String subject = "[todolist]パスワードリセットのご案内";
		String resetLink = "http://localhost:8080/validate_token?token=" + token + "&kind=reset";
		String text = "パスワードリセットのリクエストを受け付けました。下記リンクからリセットをしてください。\n"
				+ resetLink
				+ "\nメールに心当たりがない場合、このメールを削除してください。\n"
				+ "\n"
				+ "todolist";

		mailService.sendEmail(email, subject, text);
		
	}
	
}
