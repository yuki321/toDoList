package com.example.todolist.batch;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.todolist.repository.ToDoRepositoryIF;
import com.example.todolist.service.MailServiceIF;


@Component
public class MailScheduler {
	
	@Autowired
	private ToDoRepositoryIF toDoRepository;
	
	@Autowired
	private MailServiceIF mailService;
	
	
	// 毎日6時間ごとに処理を実施
	@Scheduled(cron = "${cron.task:0 0 */6 * * *}", zone = "Asia/Tokyo")
//	@Scheduled(fixedDelay = 20000) // 20秒ごとに実行（テスト用）
	public void sendTaskDeadlineEmail() {
		
		System.out.println("タスク期限切れ前日メール送信処理開始");
		
		// 該当タスク（期限切れ前日）を取得
		List<Map<String, Object>> taskList = toDoRepository.getTasksDueTomorrow();
	
		for(Map<String, Object> task : taskList) {
			String email = (String)task.get("email");
			String taskName = (String)task.get("content");
			String deadline = deadlineConvert(task.get("deadline").toString());
			
			System.out.println("タスク期限切れタスク" + email + " " + taskName + " " + deadline);			
	
			// タスクの期限が近づいていることを通知するメールを送信
			mailService.sendTaskDeadlineEmail(email, taskName, deadline);
		}
		
	}
	
	
	/**
	 * 期限切れ前日メール送信のための期限日付変換
	 * @param String deadline
	 * @return String
	 */
	String deadlineConvert(String deadline) {
		return deadline.split("T")[0];
	}
	

}
