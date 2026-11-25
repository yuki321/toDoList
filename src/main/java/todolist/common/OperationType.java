package todolist.common;

public enum OperationType {

	CREATE_USER("ユーザー作成"),
	EDIT_BUTTON("編集ボタン押下"),
	CSV_DOWNLOAD("CSVダウンロード"),
	CSV_UPLOAD("CSVアップロード"),
	CHANGE_PASSWORD("パスワード変更"),
//	BACK_BUTTON("戻るボタン押下"),
	LOGOUT("ログアウト"),
	UPDATE_USER("ユーザー情報更新"),
	DELETE_USER("ユーザー削除"),
	SEARCH_USER("ユーザー情報検索");

	OperationType(String string) {}
	
	
}
