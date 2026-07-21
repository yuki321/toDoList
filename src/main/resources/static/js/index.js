
// ユーザー削除ダイアログ
function onClickDeleteUserBtn(){
	const dialog = document.querySelector("dialog");
	dialog.showModal();
}
function closeUserDeleteDialog(){
	const dialog = document.querySelector("dialog");
	dialog.close();
}


// タスク作成ダイアログ
function onClickCreateToDoBtn(){
	const dialog = document.querySelector("#toDoCreateDialog");
	dialog.showModal();
}
function closeToDoCreateDialog(){
	const dialog = document.querySelector("#toDoCreateDialog");
	dialog.close();
}


