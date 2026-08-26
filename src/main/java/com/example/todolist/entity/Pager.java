package com.example.todolist.entity;

import java.util.ArrayList;
import java.util.List;


public class Pager {

	private final int USER_PER_PAGE = 10;
	
	public Pager() {
	
	}
	
	
	/**
	 * ページ数の取得
	 * @param int userCount
	 * @return int
	 */
	public int getPageCount(int userCount) {
		if(userCount <= USER_PER_PAGE) return 1;
		
		return (userCount / USER_PER_PAGE) + 1;
	}
	
	
	/**
	 * 最終ページのデータ数の取得
	 * @param int userCount
	 * @return int 
	 */
	public int getPageCountOffset(int userCount) {
		if(userCount >= USER_PER_PAGE) return 0;
		
		return userCount % USER_PER_PAGE;
	}
	
	
	/**
	 * 1ページにつきのデータ数の取得
	 * @return int
	 */
	public int getUserPerPage() {
		return USER_PER_PAGE;
	}
	
	
	public List<User> getUserListPerPage(List<User> userList, int currentPage){
		
		int userCount = userList.size();
		/**
		 * 取得するデータのインデックス（先頭、末尾）
		 */
		int topIndex = getTopIndex(currentPage, userCount);
		int lastIndex = getLastIndex(currentPage, userCount);
//		if(currentPage == 1) {
//			topIndex = 1;
//			lastIndex = USER_PER_PAGE - 1;
//		}
		
		List<User> currentPageUserList = new ArrayList<>();
		
		for(int i = topIndex; i < lastIndex; i++) {
			currentPageUserList.add(userList.get(i));
		}
		
		return currentPageUserList;
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