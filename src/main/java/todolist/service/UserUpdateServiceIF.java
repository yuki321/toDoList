package todolist.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import todolist.entity.User;

public interface UserUpdateServiceIF {

	User createUser(final User user);

	User updateUser(final Long id, final User user);

	void deleteUser(final Long id);

	User savePassword(final User user, final String newPassword);

	List<String> uploadCsvFile(final MultipartFile file) throws Exception;
	
	void downloadCsvFile() throws Exception;
	
}
