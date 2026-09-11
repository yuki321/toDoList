package common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

@Component
public class Logger {

	// シングルトン
	private static Logger instance = null;
	
	private Logger() {
	}
	
	public static synchronized Logger getInstance() {
		if(instance == null) {
			instance = new Logger();
		}
		return instance;
	}
	
	
	/**
	 * ログ出力処理
	 * @param String className
	 * @param String message
	 */
	public static void log(final String className, final String message) {		
		String formattedTime = getFormattedTime();
		System.out.println("[Log] " + formattedTime + " [" + className + "]" + message);
	}
	
	
	/**
	 * yyyy/MM/dd HH:mm:ss形式のデータ取得
	 * @return String
	 */
	private static String getFormattedTime() {
		LocalDateTime time = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		return time.format(formatter);
	}
	
}
