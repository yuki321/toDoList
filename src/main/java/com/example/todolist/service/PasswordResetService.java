package com.example.todolist.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.example.todolist.repository.PasswordResetRepositoryIF;
import com.example.todolist.repository.PasswordResetTokenRepositoryIF;

@Service
public class PasswordResetService implements PasswordResetServiceIF {
	
	@Autowired
	PasswordResetTokenRepositoryIF passwordResetTokenRepository;
	
	@Autowired
	PasswordResetRepositoryIF passwordResetRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Override
	public boolean passwordResetTransanction(String rawToken, String newPassword, Model model) {

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
		
		// 2.password-reset-tokensテーブルのuserd_atにタイムスタンプを格納
		Long userId_L = Long.valueOf(userId);

		try {
			int num = passwordResetRepository.updateResetTokenUsedAt(userId_L);

			if(num < 0) {
				return false;
			}

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

		}catch(DataAccessException e) {
			/**
			 * 例外処理
			 * ここではfalseで返す
			 */
			System.out.println("Error Message: " + e);
			model.addAttribute("error-reset-pw", "データ変更に失敗しました。");
			return false;
		}

		return true;
	}
	
	
}
