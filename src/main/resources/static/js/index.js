
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
	document.querySelector("#input-create-content").value = "";
	const dialog = document.querySelector("#toDoCreateDialog");
	
	dialog.close();
}

// タスク編集ダイアログ
function onClickEditToDoBtn(element){

	// id
	const todoId = element.dataset.id;
	document.querySelector("#editId").value = todoId;

	// content
	const todoContent = element.dataset.content;
	
	// memo
	const todoMemo = element.dataset.memo;
	
	if(todoContent == undefined){
		document.querySelector("#input-edit-content").value = "";
	}else{
		document.querySelector("#input-edit-content").value = todoContent;
	}
	
	if(todoMemo){
		document.querySelector("#input-edit-memo").value = todoMemo;
	}
	
	const dialog = document.querySelector("#toDoEditDialog");
	dialog.showModal();
}
function closeToDoEditDialog(){
	const dialog = document.querySelector("#toDoEditDialog");
	dialog.close();
}

// パスワードリセットダイアログ
function onClickPWResetDialog(){
	const dialog = document.querySelector("#mailSend");
	dialog.showModal();
}
function closePWResetDialog(){
	const dialog = document.querySelector("#mailSend");
	dialog.close();
}



