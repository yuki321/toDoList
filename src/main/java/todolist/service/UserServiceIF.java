package todolist.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import todolist.entity.PasswordChange;
import todolist.entity.User;

public interface UserServiceIF {

	List<User> getAllUsers();
	
	Page<User> getAllUsers(final Pageable pageable);

	Page<User> searchUsers(final User user, final Pageable pageable);

	Optional<User> getUserById(final Long id);

	List<User> findByEmail(final String email);

	List<String> checkPassword(final PasswordChange form, final User user);

	void restoreUserDisplayFields(final Long id, final User user);
	

}
