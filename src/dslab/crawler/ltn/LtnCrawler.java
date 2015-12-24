package dslab.crawler.ltn;


import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import dslab.crawler.pack.CrawlerPack;

public class LtnCrawler {

	public static void main(String[] args) {
		String api = "http://news.ltn.com.tw/newspaper/focus/20151201";
        // 轉化為 jsoup 物件
        Document jsoupDoc = CrawlerPack.getFromXml(api);
        
		for (Element elem : jsoupDoc.select("ul#newslistul.boxTitle").select("a[href]")) {
			System.out.println(elem.attr("href"));
			System.out.println(elem.text());
		}
	}
}
