package todolist.batch;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import common.Logger;
import todolist.repository.ToDoRepositoryIF;
import todolist.service.MailServiceIF;


@Component
public class MailScheduler {
	
	@Autowired
	private ToDoRepositoryIF toDoRepository;
	
	@Autowired
	private MailServiceIF mailService;
	
	private final String CLASS_NAME = this.getClass().getSimpleName();
	
	
	// 毎日6時間ごとに処理を実施
	@Scheduled(cron = "${cron.task:0 0 */6 * * *}", zone = "Asia/Tokyo")
//	@Scheduled(cron = "${cron.task:*/1 * * * * *}", zone = "Asia/Tokyo")  // テスト用
//	@Scheduled(fixedDelay = 20000) // 20秒ごとに実行（テスト用）
	public void sendTaskDeadlineEmail() {
		
		Logger.log(CLASS_NAME, "sendTaskDeadlineEmail: 期限切れ1週間前のタスクはありません");
		List<Map<String, Object>> taskListWeek = toDoRepository.getTasksDueInOneWeek();
		
		if(taskListWeek.isEmpty()) {
			Logger.log(CLASS_NAME, "sendTaskDeadlineEmail: 期限切れ1週間前のタスクはありません");
			return;
		}
		
		taskListWeek.forEach(task -> {
	        if (task.get("email") instanceof String email &&
	            task.get("content") instanceof String taskName) {

	            String deadline = deadlineConvert(Objects.toString(task.get("deadline"), ""));

				Logger.log(CLASS_NAME, "sendTaskDeadlineEmail: タスク期限切れタスク %s %s %s".formatted(email, taskName, deadline));
	            mailService.sendTaskDeadlineEmail(email, taskName, deadline);
	        }
	    });
		
	}
	
	
	/**
	 * 期限切れメール送信のための期限日付変換
	 * @param String deadline
	 * @return String
	 */
	private String deadlineConvert(String deadline) {
		return deadline.split("T")[0];
	}
	

}
