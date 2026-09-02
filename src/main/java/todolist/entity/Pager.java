package todolist.entity;


public class Pager {

	public Pager() {
	
	}
	
	
	/**
	 * ページに表示する先頭データのインデックスを取得
	 * @param int currentPage Xページ目(RequestParam(page)から取得)
	 * @param int dataCountPerPage   1ページにつきのデータ表示数
	 * @return int
	 */
	public int getTopIndex(final int currentPage, final int dataCountPerPage) {
		// Pathvariableのpageは 0 から始まるため、currentPage + 1とする
		if((currentPage + 1) <= 1) return 1;
		
		// 表示ページ2ページ目(page=1)、10レコード/page の場合、先頭データのインデックスは
		// ((1 + 1) * 10) - (10 - 1) = 11
		final int result = ((currentPage + 1) * dataCountPerPage) - (dataCountPerPage - 1);
		
		return result;
	}
	
	
	/**
	 * ページに表示する末尾データのインデックスを取得
	 * @param int currentPage Xページ目(RequestParam(page)から取得)
	 * @param int dataCountPerPage   1ページにつきのデータ表示数
	 * @return int
	 */
	public int getLastIndex(final int currentPage, final int dataCountPerPage) {
		// Pathvariableのpageは 0 から始まるため、currentPage + 1とする
		if((currentPage + 1) <= 1) return dataCountPerPage;
		
		// 表示ページ2ページ目、10レコード/page の場合、インデックスは
		// (1 + 1) * 10 = 20
		final int result = ((currentPage + 1) * dataCountPerPage);
		
		return result;
	}
	
	
}