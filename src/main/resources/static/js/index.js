
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

	const todoId = element.dataset.id;
	const todoContent = element.dataset.content;
	const todoMemo = element.dataset.memo;
	const todoDeadline = element.dataset.deadline;
	const todoPriority = element.dataset.priority;

	document.querySelector("#editId").value = todoId;
	if(todoContent == undefined){
		document.querySelector("#input-edit-content").value = "";
	}else{
		document.querySelector("#input-edit-content").value = todoContent;
	}

	if(todoMemo){
		document.querySelector("#input-edit-memo").value = todoMemo;
	}
/** 
	if(todoDeadline){
		document.querySelector("#input-edit-deadline").value = todoDeadline;
	}

	if(todoPriority){
		document.querySelector("select[name='priority']").value = todoPriority;
	}
	*/
		
	if (todoDeadline) {
        // 例: "2026-08-03T15:30" から "2026-08-03" を抽出
        document.getElementById('input-edit-deadline').value = todoDeadline.split('T')[0];
    } else {
        document.getElementById('input-edit-deadline').value = '';
    }

    // 優先度（ドロップダウン）の設定
    // option の value（"1", "2", "3", ""）と一致していれば自動的に選択状態になります
    document.getElementById('input-edit-priority').value = todoPriority || '';
	

	const dialog = document.querySelector("#toDoEditDialog");
	dialog.showModal();
}

function closeToDoEditDialog(){
	const dialog = document.querySelector("#toDoEditDialog");
	/**
	 * 値を空にしないと、メモなしタスクの編集ボタンを押下すると、直前で開いたタスクのメモが表示されてしまう
	 */
	document.querySelector("#input-edit-memo").value = "";
	document.querySelector("#input-edit-content").value = "";
	document.querySelector("#input-edit-deadline").value = "";
	document.querySelector("select[name='priority']").value = "";

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



