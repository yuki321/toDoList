package todolist.repository;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import todolist.entity.ToDo;


@Repository
public interface ToDoRepositoryIF {

    public List<Map<String, Object>> findAllToDo(@AuthenticationPrincipal final UserDetails userDetails);
    
    public List<Map<String, Object>> findAllCompletedToDo(final UserDetails userDetails);
    
    int completeTask(final ToDo todo) throws DataAccessException;

    int undoCompletedTask(final ToDo todo) throws DataAccessException;

    Map<String, Object> getUserInfo(final UserDetails userDetails);
    
    public int insertRecord(final ToDo todo) throws DataAccessException;
    
    public int updateRecord(final ToDo todo) throws DataAccessException;
    
    public int deleteRecord(final Long id) throws DataAccessException;
    
    public List<Map<String, Object>> getTasksDueTomorrow();
    
    public List<Map<String, Object>> getTasksDueInOneWeek();


}



