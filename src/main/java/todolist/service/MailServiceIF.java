package todolist.service;

import java.util.List;

import org.springframework.ui.Model;

import todolist.entity.PasswordChange;
import todolist.entity.PasswordReset;

public interface MailServiceIF {

	List<String> checkPassword(final PasswordReset passwordReset, final String rawToken);
	
	public String sendMailProcess(final PasswordChange mail, Model model);

	public void sendTaskDeadlineEmail(String email, String taskName, String deadline);
	
}
