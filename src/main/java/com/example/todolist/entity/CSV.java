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
	 * @param List<String> errors
	 * @return boolean
	 */
	public boolean inputCheck(String[] record, List<User> users, List<String> errors) {
	    // 1. 配列チェック
	    if (!isValidRecordStructure(record)) {
	    	
	    	String message = "レコードの項目が不足しています（空白が存在します）";
	    	setErrorMessage(message, errors);
	    	
			System.out.println("CSVレコードの項目数チェック: エラー発生!!");
	        return false;
	    }

	    String userName = record[0].trim();
	    String email = record[1].replace("\"", "").trim();
	    String role = record[3].replace("\"", "").trim();

	    // 2. 各項目チェック
	    if (!isValidUserName(userName, errors) || !isValidEmail(email, errors) 
	    		|| !isValidRole(role, errors)) {
			System.out.println("CSV各項目チェック: エラー発生!!");
	        return false;
	    }

	    // 3. DBとCSVファイルの相関チェック（重複チェック）
	    if (isDuplicateUser(userName, email, users, errors)) {
			System.out.println("CSV項目重複チェック: エラー発生!!");
	        return false;
	    }

	    return true;
	}
	
	
	/**
	 * CSVファイルチェック
	 * @param MultipartFile file
	 * @param List<String> errors
	 * @return boolean
	 */
	public boolean isCsvFile(MultipartFile file, List<String> errors) {

		if (file == null || file.isEmpty()) {
			
	    	String message = "・ファイルが存在していない。またはファイルサイズが0です";
	    	setErrorMessage(message, errors);
            return false;
        }

        // 元のファイル名を取得
        String originalFilename = file.getOriginalFilename();

        // ファイル名が存在しない場合は false（ファイルを選択せずフォームを送信した場合）
        if (!StringUtils.hasText(originalFilename)) {
        	
	    	String message = "・ファイルが選択されていない可能性があります";
	    	setErrorMessage(message, errors);
            return false;
        }

        // 大文字・小文字を区別せずに .csv で終わるかチェック
        if(!originalFilename.toLowerCase().endsWith(".csv")) {
        	
	    	String message = "・CSVファイルではないファイルがインポートされました";
	    	setErrorMessage(message, errors);
        	return false;
        }
        
        return true;
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
	 * @param List<String> errors
	 * @return boolean
	 */
	private boolean isValidUserName(String userName, List<String> errors) {
	    if (userName.length() < 1 || userName.length() > 50) {
	    	
	    	String message = "・ユーザー名の文字数は1~50にしてください";
	    	setErrorMessage(message, errors);
	    	return false;
	    }
	    
	    // 日本語（ひらがな・カタカナ・漢字）、英小文字、数字のみ
	    Pattern pattern = Pattern.compile("[^a-z0-9\\u3040-\\u309F\\u30A0-\\u30FF\\u4E00-\\u9FFF\\u3000]");
	    Matcher userNameMatch = pattern.matcher(userName);
		if(userNameMatch.find()) {
	    	
			String message = "・ユーザー名の文字種は「日本語（ひらがな・カタカナ・漢字）、英小文字、数字のみ」使用可能です";
	    	setErrorMessage(message, errors);
			return false;
		}
		
	    return true;
	}

	
	/**
	 * メールアドレスチェック
	 * @param String email
	 * @param List<String> errors
	 * @return boolean
	 */
	private boolean isValidEmail(String email, List<String> errors) {
	    if (email.length() < 8 || email.length() > 100) {
	    	
	    	String message = "・メールアドレスの文字数は8~100にしてください";
	    	setErrorMessage(message, errors);
	    	
	        return false;
	    }
	    if(!email.contains("@") || !email.contains(".")) {

	    	String message = "・メールアドレスには'@', '.'を必ず含めてください";
	    	setErrorMessage(message, errors);
	    	return false;
	    }
	    
	    // 英小文字、数字、@,-のみメールアドレスに含める
	    Pattern pattern = Pattern.compile("[^a-z0-9@.-]");
	    Matcher userNameMatch = pattern.matcher(email);
	    if(userNameMatch.find()) {

	    	String message = "・メールアドレスには'英小文字、数字、@,-'のみ使用可能です";
	    	setErrorMessage(message, errors);
	    	return false;
	    }
	    
	    return true;
	}

	
	/**
	 * ロールチェック("1"または"2"以外はfalseを返す)
	 * @param String role
	 * @param List<String> errors
	 * @return boolean
	 */
	private boolean isValidRole(String role, List<String> errors) {
		
		String message = "・roleには'1'または'2'のみ設定してください";
    	setErrorMessage(message, errors);
	    return "1".equals(role) || "2".equals(role);
	}

	
	/**
	 * ユーザー名とメールアドレスの重複チェック
	 * CSVデータとDBデータで重複がないかチェック
	 * @param String userName
	 * @param String email
	 * @param List<User> users
	 * @param List<String> errors
	 * @return
	 */
	private boolean isDuplicateUser(String userName, String email, 
			List<User> users, List<String> errors) {

	    for (User u : users) {
	        if (userName.equals(u.getUserName()) || email.equals(u.getEmail())) {
	    		
	        	String message = "・ユーザー名またはメールアドレスはすでに登録されています"
	    				+ "ユーザー名 : " + userName + " / メールアドレス : " + email;
		    	setErrorMessage(message, errors);
	            return true;
	        }
	    }
	    
	    return false;
	}

	
	/**
	 * エラーメッセージをListに格納
	 * @param String errorMessage
	 * @param List<String> errors
	 */
	public void setErrorMessage(String errorMessage, List<String> errors) {
		
		if(!errors.contains(errorMessage)) {
			errors.add(errorMessage);
		}
	}
	
	
}
