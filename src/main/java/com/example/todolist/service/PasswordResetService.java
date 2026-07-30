package com.example.todolist.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import com.example.todolist.repository.PasswordResetRepository;
import com.example.todolist.repository.PasswordResetTokenRepository;

@Service
public class PasswordResetService {
	
	@Autowired
	PasswordResetTokenRepository passwordResetTokenRepository;
	
	@Autowired
	PasswordResetRepository passwordResetRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	
	public boolean passwordResetTransanction(String rawToken, String newPassword, Model model) {
System.out.println("Start!! : passwordResetTransanction()");

		// 1.password-reset-tokensテーブルからuser_idを取得
		// 全有効トーケンを取得して、ハッシュ化されていない平文トークンと比較
		List<Map<String, Object>> resultList = passwordResetTokenRepository.findAllTokenHash();

		boolean matchResult = false;
		String userId = null;
		for(Map<String, Object> m: resultList) {
	
			String token_hash = (String)m.get("token_hash");
			matchResult = passwordEncoder.matches(rawToken, token_hash);
	
			if(matchResult) {
				// マッチした組み合わせを次の判定で利用
				userId = m.get("user_id").toString();
				break;
			}
			
		}
		// 一致しなければログイン画面へ遷移(false)
		if(!matchResult) {
			return false;
		}
//System.out.println("通過１ : findAllTokenHash()");	FIXME	
		
		// 2.password-reset-tokensテーブルのuserd_atにタイムスタンプを格納
		Long userId_L = Long.valueOf(userId);
		
//System.out.println("userId_L : " + userId_L); FIXME

		try {
			int num = passwordResetRepository.updateResetTokenUsedAt(userId_L);

//System.out.println("updateResetTokenUsedAt num : " + num); FIXME

			if(num < 0) {
				return false;
			}
//System.out.println("通過２ : updateResetTokenUsedAt");		FIXME

		}catch(DataAccessException e) {
			/**
			 * 例外処理
			 * ここではfalseで返す
			 */
			System.out.println("Error Message: " + e);
			model.addAttribute("error-reset-pw", "データ変更に失敗しました。");
			return false;
		}
		
		
		// 3.Userテーブルのパスワードを更新
		try {
			int num = passwordResetRepository.resetPassword(userId_L, newPassword);
			if(num < 0) {
				return false;
			}
System.out.println("通過３ : resetPassword");				
		}catch(DataAccessException e) {
			/**
			 * 例外処理
			 * ここではfalseで返す
			 */
			System.out.println("Error Message: " + e);
			model.addAttribute("error-reset-pw", "データ変更に失敗しました。");
			return false;
			
		}
		
		// 4.password-reset-tokensテーブルの該当レコードを削除する
		try {
			int num = passwordResetRepository.deleteRecord(userId_L);
			if(num < 0) {
				return false;
			}
System.out.println("通過４ : deleteRecord");			
		}catch(DataAccessException e) {
			/**
			 * 例外処理
			 * ここではfalseで返す
			 */
			System.out.println("Error Message: " + e);
			model.addAttribute("error-reset-pw", "データ変更に失敗しました。");
			return false;
		}

System.out.println("通過！：Queryすべて完了！");
		return true;
	}
	
	
}
