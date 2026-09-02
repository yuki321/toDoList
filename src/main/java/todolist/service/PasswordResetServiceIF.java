package todolist.service;

import org.springframework.ui.Model;

public interface PasswordResetServiceIF {

	boolean passwordResetTransaction(final String rawToken, final String newPassword, final Model model);

}
