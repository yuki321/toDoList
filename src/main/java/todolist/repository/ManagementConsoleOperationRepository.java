package todolist.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import todolist.common.Logger;
import todolist.entity.OperationTypeLog;

@Repository
public class ManagementConsoleOperationRepository implements ManagementConsoleOperationRepositoryIF {

	@Autowired
	JdbcTemplate jdbc;
	
	
	private final String CLASS_NAME = this.getClass().getSimpleName();
	
	/**
	 * 管理画面で操作した情報を記録する
	 * 記録する情報は以下の通り
	 * 1. 操作したユーザー名
	 * 2. 操作した日時
	 * 3. 操作した内容
	 * 4. 操作種別
	 * 	a. ユーザー作成
	 * 	b. 編集ボタン押下(user id)
	 * 	c. CSVダウンロード
	 * 	d. CSVアップロード
	 * 	e. パスワード変更
	 * 	f. 戻るボタン押下
	 * 	g. ログアウト
	 * 	h. ユーザー情報更新
	 * 	i. ユーザー削除
	 * 	j. ユーザー情報検索
	 * 
	 */
	
	/**
	 * 管理画面の操作ログを記録する
	 * @param String operationType
	 * @param String userName
	 * @param String content
	 */
	@Override
	public void insertOperationLog(final String operationType, final String userName, final String content) {
		
		OperationTypeLog log = new OperationTypeLog();
		// 引数チェック
		if(!log.checkInput(operationType, userName, content)) {
			Logger.log(CLASS_NAME, "[Warning]操作履歴処理の引数に抜けがあります");
		}
		
		Logger.log(this.getClass().getSimpleName(), "insertOperationLog: 管理画面操作履歴の記録");
		final String sql = "INSERT INTO operation_history (operation_type, user_name, content, operation_time ) VALUES (?, ?, ?, NOW())";
		jdbc.update(sql, operationType, userName, content);
	}

    
}



