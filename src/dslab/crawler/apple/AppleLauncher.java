package dslab.crawler.apple;

import java.io.IOException;

public class AppleLauncher {

	public static void main(String[] args) throws IOException {
		
		AppleCrawler apple = new AppleCrawler();
		
//		pastdayOfYear = args[0];
//		pastdayOfMonth = args[1];
//		pastdayOfdate = args[2];
		
		apple.pastdayOfYear = "2010";
		apple.pastdayOfMonth = "01";
		apple.pastdayOfdate = "01";
		
		apple.today = "20110101";		
		
		// 抓取新聞連結
		apple.run();
		
		// 儲存新聞內容
		apple.processNewsList("./蘋果日報/");
	}
}
