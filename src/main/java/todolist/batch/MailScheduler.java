package todolist.batch;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import todolist.repository.ToDoRepositoryIF;
import todolist.service.MailServiceIF;


@Component
public class MailScheduler {
	
	@Autowired
	private ToDoRepositoryIF toDoRepository;
	
	@Autowired
	private MailServiceIF mailService;
	
	
	// 毎日6時間ごとに処理を実施
	@Scheduled(cron = "${cron.task:0 0 */6 * * *}", zone = "Asia/Tokyo")
//	@Scheduled(cron = "${cron.task:*/1 * * * * *}", zone = "Asia/Tokyo")  // テスト用
//	@Scheduled(fixedDelay = 20000) // 20秒ごとに実行（テスト用）
	public void sendTaskDeadlineEmail() {
		
		System.out.println("タスク期限切れ前日メール送信処理開始");
		
		// 該当タスク（期限切れ前日）を取得
		List<Map<String, Object>> taskList = toDoRepository.getTasksDueTomorrow();
	
		if(taskList.isEmpty()) {
			System.out.println("期限切れ前日タスクはありません");
			return;
		}
		
		sendTaskDeadlineEmailProcess(taskList);
		
		System.out.println("タスク期限切れ1週間前メール送信処理開始");
		List<Map<String, Object>> taskListWeek = toDoRepository.getTasksDueInOneWeek();
		
		if(taskListWeek.isEmpty()) {
			System.out.println("期限切れ1週間前のタスクはありません");
			return;
		}
		
		sendTaskDeadlineEmailProcess(taskListWeek);
		
	}
	
	
	/**
	 * 期限切れメール送信処理(前日・1週間前)
	 * @param List<Map<String, Object>> taskList
	 */
	private void sendTaskDeadlineEmailProcess(final List<Map<String, Object>> taskList) {
	    taskList.forEach(task -> {
	        if (task.get("email") instanceof String email &&
	            task.get("content") instanceof String taskName) {

	            String deadline = deadlineConvert(Objects.toString(task.get("deadline"), ""));

	            System.out.println("タスク期限切れタスク %s %s %s".formatted(email, taskName, deadline));
	            mailService.sendTaskDeadlineEmail(email, taskName, deadline);
	        }
	    });
	}
	
	
	/**
	 * 期限切れ前日メール送信のための期限日付変換
	 * @param String deadline
	 * @return String
	 */
	private String deadlineConvert(String deadline) {
		return deadline.split("T")[0];
	}
	

}
