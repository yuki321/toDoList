package todolist.service;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import todolist.entity.ToDo;

@Service
public interface ToDoUpdateServiceIF {
	
	boolean completeTask(final ToDo todo) throws DataAccessException;

	boolean undoCompletedTask(final ToDo todo) throws DataAccessException;

	boolean insertRecord(final ToDo todo);

	boolean updateRecord(final ToDo todo) throws DataAccessException;

	boolean deleteRecord(final Long id) throws DataAccessException;
	

}
