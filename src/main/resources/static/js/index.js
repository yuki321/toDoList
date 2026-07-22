
// タスク削除ダイアログ
function onClickDeleteToDoBtn(element){
	
	const todoId = element.dataset.id;
	document.querySelector("#deleteTodoId").value = todoId;
	
	const dialog = document.querySelector("#deleteDialog");
	dialog.showModal();
}
function closeToDoDeleteDialog(){
	const dialog = document.querySelector("#deleteDialog");
	dialog.close();
}

// ユーザー削除ダイアログ
function onClickDeleteBtn(){
	const dialog = document.querySelector("#deleteDialog");
	dialog.showModal();
}
function closeUserDeleteDialog(){
	const dialog = document.querySelector("#deleteDialog");
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

// タスク編集ダイアログ
function onClickEditToDoBtn(element){
	const todoId = element.dataset.id;
	document.querySelector("#editId").value = todoId;
	const todoUserId = element.dataset.userId;
	document.querySelector("#editUserId").value = todoUserId;
	
	const dialog = document.querySelector("#toDoEditDialog");
	dialog.showModal();
}
function closeToDoEditDialog(){
	const dialog = document.querySelector("#toDoEditDialog");
	dialog.close();
}






