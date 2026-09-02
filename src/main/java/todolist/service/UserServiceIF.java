package todolist.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import todolist.entity.PasswordChange;
import todolist.entity.User;

public interface UserServiceIF {

	List<User> getAllUsers();
	
	Page<User> getAllUsers(final Pageable pageable);

	Page<User> searchUsers(final User user, final Pageable pageable);

	Optional<User> getUserById(final Long id);

	List<User> findByEmail(final String email);

	User createUser(final User user);

	User updateUser(final Long id, final User user);

	void deleteUser(final Long id);

	User savePassword(final User user, final String newPassword);

	List<String> checkPassword(final PasswordChange form, final User user);

	List<String> uploadCsvFile(final MultipartFile file) throws Exception;
	
	void downloadCsvFile() throws Exception;

	void restoreUserDisplayFields(final Long id, final User user);
	
	
	
	
	
}
