package com.example.todolist.entity;


public class Pager {

	private final int USER_PER_PAGE = 10;
	
	public Pager() {
	
	}
	
	
	/**
	 * ページに表示する先頭データのインデックスを取得
	 * @param int currentPage Xページ目(RequestParam(page)から取得)
	 * @param int dataCountPerPage   1ページにつきのデータ表示数
	 * @return int
	 */
	public int getTopIndex(int currentPage, int dataCountPerPage) {
		// Pathvariableのpageは 0 から始まるため、currentPage + 1とする
		if((currentPage + 1) == 1) return 1;
		
		// 表示ページ2ページ目(page=1)、10レコード/page の場合、先頭データのインデックスは
		// ((1 + 1) * 10) - (10 - 1) = 11
		int result = ((currentPage + 1) * dataCountPerPage) - (dataCountPerPage - 1);
		
		return result;
	}
	
	
	/**
	 * ページに表示する末尾データのインデックスを取得
	 * @param int currentPage Xページ目(RequestParam(page)から取得)
	 * @param int dataCountPerPage   1ページにつきのデータ表示数
	 * @return int
	 */
	public int getLastIndex(int currentPage, int dataCountPerPage) {
		// Pathvariableのpageは 0 から始まるため、currentPage + 1とする
		if((currentPage + 1) == 1) return dataCountPerPage;
		
		// 表示ページ2ページ目、10レコード/page の場合、インデックスは
		// 2 * 10 - 1 = 19
		int result = ((currentPage + 1) * USER_PER_PAGE);
		
		return result;
	}
	
	
}