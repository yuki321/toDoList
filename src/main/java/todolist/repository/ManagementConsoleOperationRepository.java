package todolist.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;


public class ManagementConsoleOperationRepository implements ManagementConsoleOperationRepositoryIF {

	@Autowired
	JdbcTemplate jdbc;
	
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

	
    
}



