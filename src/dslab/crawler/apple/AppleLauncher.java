package dslab.crawler.apple;

import java.io.IOException;

public class AppleLauncher {

	public static void main(String[] args) throws IOException {
		
		AppleCrawler apple = new AppleCrawler();
		
//		pastdayOfYear = args[0];
//		pastdayOfMonth = args[1];
//		pastdayOfdate = args[2];
		
		apple.pastdayOfYear = "2016";
		apple.pastdayOfMonth = "01";
		apple.pastdayOfdate = "04";
		
		apple.dateProcess();
		apple.run();
	}
}
