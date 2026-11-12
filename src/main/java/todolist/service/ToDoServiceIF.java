package todolist.service;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import todolist.entity.ToDo;

@Service
public interface ToDoServiceIF {
	
	List<ToDo> findAllToDo(final UserDetails userDetails);

	List<ToDo> findAllCompletedToDo(final UserDetails userDetails);

	Map<String, Object> getUserInfo(final UserDetails userDetails);

}
