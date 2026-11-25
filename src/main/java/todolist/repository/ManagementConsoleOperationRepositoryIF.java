package todolist.repository;

import org.springframework.stereotype.Repository;


@Repository
public interface ManagementConsoleOperationRepositoryIF {

	public void insertOperationLog(String operationType, String userName, String content);

}



