package com.example.todolist.entity;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public class CSV {

	public CSV() {
	
	}
	
	
	/**
	 * CSVファイルのチェック
	 * 全項目のチェックに通過した場合、trueを返す
	 * @param String[] record
	 * @param List<User> users
	 * @return boolean
	 */
	public boolean inputCheck(String[] record, List<User> users) {
	    // 1. 配列チェック
	    if (!isValidRecordStructure(record)) {
	        return false;
	    }

	    String userName = record[0].trim();
	    String email = record[1].replace("\"", "").trim();
	    String role = record[3].replace("\"", "").trim();

	    // 2. 各項目チェック
	    if (!isValidUserName(userName) || !isValidEmail(email) || !isValidRole(role)) {
	        return false;
	    }

	    // 3. DBとCSVファイルの相関チェック（重複チェック）
	    if (isDuplicateUser(userName, email, users)) {
	        return false;
	    }

	    return true;
	}
	
	
	/**
	 * CSVファイルチェック
	 * @param MultipartFile file
	 * @return boolean
	 */
	public boolean isCsvFile(MultipartFile file) {

		if (file == null || file.isEmpty()) {
            return false;
        }

        // 元のファイル名を取得
        String originalFilename = file.getOriginalFilename();

        // ファイル名が存在しない場合は false（ファイルを選択せずフォームを送信した場合）
        if (!StringUtils.hasText(originalFilename)) {
            return false;
        }

        // 大文字・小文字を区別せずに .csv で終わるかチェック
        return originalFilename.toLowerCase().endsWith(".csv");
	}


	/**
	 * 項目数チェック
	 * 空白などが各項目にないかチェック
	 * @param String[] record
	 * @return boolean
	 */
	private boolean isValidRecordStructure(String[] record) {
	    if (record == null || record.length != 4) {
	        return false;
	    }
	    for (String r : record) {
	        if (r == null || r.isBlank()) return false;
	    }
	    
	    return true;
	}

	
	/**
	 * ユーザー名チェック
	 * @param String userName
	 * @return boolean
	 */
	private boolean isValidUserName(String userName) {
	    if (userName.length() < 1 || userName.length() > 50) {
	        return false;
	    }
	    
	    // 日本語（ひらがな・カタカナ・漢字）、英小文字、数字のみ
	    Pattern pattern = Pattern.compile("[^a-z0-9\\u3040-\\u309F\\u30A0-\\u30FF\\u4E00-\\u9FFF\\u3000]");
	    Matcher userNameMatch = pattern.matcher(userName);
		if(userNameMatch.find()) return false;
		
	    return true;
	}

	
	/**
	 * メールアドレスチェック
	 * @param String email
	 * @return boolean
	 */
	private boolean isValidEmail(String email) {
	    if (email.length() < 8 || email.length() > 100) {
	        return false;
	    }
	    if(!email.contains("@") || !email.contains(".")) {
	    	return false;
	    }
	    
	    // 英小文字、数字、@,-のみメールアドレスに含める
	    Pattern pattern = Pattern.compile("[^a-z0-9@.-]");
	    Matcher userNameMatch = pattern.matcher(email);
	    if(userNameMatch.find()) return false;
	    
	    return true;
	}

	
	/**
	 * ロールチェック("1"または"2"以外はfalseを返す)
	 * @param String role
	 * @return boolean
	 */
	private boolean isValidRole(String role) {
	    return "1".equals(role) || "2".equals(role);
	}

	
	/**
	 * ユーザー名とメールアドレスの重複チェック
	 * CSVデータとDBデータで重複がないかチェック
	 * @param String userName
	 * @param String email
	 * @param List<User> users
	 * @return
	 */
	private boolean isDuplicateUser(String userName, String email, List<User> users) {

	    for (User u : users) {
	        if (userName.equals(u.getUserName()) || email.equals(u.getEmail())) {
	            return true;
	        }
	    }
	    
	    return false;
	}

	
}