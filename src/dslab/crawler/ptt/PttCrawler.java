package dslab.crawler.ptt;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import dslab.crawler.pack.Crawler;
import dslab.crawler.pack.XTrustProvider;

public class PttCrawler extends Crawler{
	
	@Override
	public void customerProcessNewsList(String tag, String url, String date, String dirPath, Document contain) throws IOException{
		
		saveNewsToFile(commentNewsParseProcess(contain), date, dirPath);
	}
	
	@Override
	public void run(){
		
		Integer i = Integer.parseInt(startIdx);
		XTrustProvider.install();
		
		while(true){
			url = "https://www.ptt.cc/bbs/Gossiping/index" + i + ".html";
	    	newsLinks = PttCrawlerPack.getFromXml(url);	
			for (Element elem : newsLinks.select("div.r-ent")) {
				addNewsLinkList("https://www.ptt.cc" + elem.select("a[href]").attr("href"));
			}
			i++;
		}
	}
		
	/**
	 * 一般新聞爬蟲處理
	 * 
	 * @param elem
	 * @return
	 */
	private String[] commentNewsParseProcess(Document contain){
		String[] newscontent = {"",""};
		
		for (Element elem : contain.select("div.page_container.stack.clear-fix.newspapers_ad")) {
			// 截取新聞標題、內容
			newscontent[0] = elem.select("header").select("h1").text();
			newscontent[1] = elem.select("article.clear-fix").select("p").text();
		}
		return newscontent;
	}

}
