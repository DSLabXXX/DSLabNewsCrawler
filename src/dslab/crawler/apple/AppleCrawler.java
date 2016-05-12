package dslab.crawler.apple;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import dslab.crawler.pack.Crawler;
import dslab.crawler.pack.CrawlerPack;

public class AppleCrawler extends Crawler{
	
	@Override
	public void customerProcessNewsList(String tag, String url, String date, String dirPath, Document contain) throws IOException{
		if (tag.equals("地產焦點"))
			processNewsContain(houseNewsParseProcess(contain), date, dirPath);
		else if (tag.equals("房產王") || tag.equals("家居王") || tag.equals("豪宅王") || tag.equals("地產王"))
			processNewsContain(housekingNewsParseProcess(contain), date, dirPath);
		else
			processNewsContain(commentNewsParseProcess(contain), date, dirPath);
	}
	
	@Override
	public void customerRunProcess(){
		url = "http://www.appledaily.com.tw/appledaily/archive/" + pastday;
    	newsLinks = CrawlerPack.start().getFromHtml(url);

		for (Element elem : newsLinks.select("h2.nust.clearmen")) {
			newsTag = elem.text();
			for (Element elem2 : elem.nextElementSibling().select("a[href]")) {
				if (elem2.attr("href").contains("http:"))
					addNewsLinkList(elem2.attr("href"), newsTag, pastday);
				else
					addNewsLinkList("http://www.appledaily.com.tw" + elem2.attr("href"), newsTag, pastday);
			}
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
		
		for (Element elem : contain.select("article#maincontent.vertebrae")) {
			// 截取新聞標題、內容
			newscontent[0] = elem.select("header").select("hgroup").text();
			newscontent[1] = elem.select("p").text();
		}
		return newscontent;
	}
	
	/**
	 * 房產王、家居王、豪宅王、地產王新聞爬蟲處理
	 * 
	 * @param elem
	 * @return
	 */
	private String[] housekingNewsParseProcess(Document contain){
		String[] newscontent = {"",""};
		
		for (Element elem : contain.select("article#maincontent.vertebrae")) {
			// 截取新聞標題、內容
			newscontent[0] = elem.select("div.ncbox_cont").select("h1").text();
			newscontent[1] = elem.select("div.articulum").select("p").text();
		}
		return newscontent;
	}
	
	/**
	 * 房產新聞爬蟲處理
	 * 
	 * @param elem
	 * @return
	 */
	private String[] houseNewsParseProcess(Document contain){
		String[] newscontent = {"",""};
		
		for (Element elem : contain.select("article#maincontent.vertebrae")) {
			// 截取新聞標題、內容
			newscontent[0] = elem.select("div.ncbox_cont").select("h1").text();
			newscontent[1] = elem.select("p").text();
		}
		return newscontent;
	}

}
