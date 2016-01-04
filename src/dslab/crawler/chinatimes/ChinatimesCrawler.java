package dslab.crawler.chinatimes;


import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import dslab.crawler.pack.Crawler;
import dslab.crawler.pack.CrawlerPack;

public class ChinatimesCrawler extends Crawler {

	public void run() throws IOException{
		
		dateProcess();
		
		String api = "http://www.chinatimes.com/history-by-date/2015-12-01-260407";
        // 轉化為 jsoup 物件
        Document jsoupDoc = CrawlerPack.getFromXml(api);
        
		for (Element elem : jsoupDoc.select("section.np_alllist").select("a[href]")) {
			System.out.println(elem.attr("href"));
			System.out.println(elem.text());
		}
	}
}
