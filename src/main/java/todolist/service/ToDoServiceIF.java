package todolist.service;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import todolist.entity.ToDo;

@Service
public interface ToDoServiceIF {
	
	List<ToDo> findAllToDo(final UserDetails userDetails);

	List<ToDo> findAllCompletedToDo(final UserDetails userDetails);

	boolean completeTask(final ToDo todo) throws DataAccessException;

	boolean undoCompletedTask(final ToDo todo) throws DataAccessException;

	Map<String, Object> getUserInfo(final UserDetails userDetails);

	boolean insertRecord(final ToDo todo);

	boolean updateRecord(final ToDo todo) throws DataAccessException;

	boolean deleteRecord(final Long id) throws DataAccessException;
	

}
