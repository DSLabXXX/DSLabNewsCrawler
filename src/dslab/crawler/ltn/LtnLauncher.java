package dslab.crawler.ltn;

import java.io.IOException;

public class LtnLauncher {

	public static void main(String[] args) throws IOException {
		
		LtnCrawler ltn = new LtnCrawler();
		
//		pastdayOfYear = args[0];
//		pastdayOfMonth = args[1];
//		pastdayOfdate = args[2];
		
		ltn.pastdayOfYear = "2016";
		ltn.pastdayOfMonth = "01";
		ltn.pastdayOfdate = "04";
		
		ltn.run();
	}

}
