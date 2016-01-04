package dslab.crawler.apple;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import dslab.crawler.pack.Crawler;
import dslab.crawler.pack.CrawlerPack;

public class AppleCrawler extends Crawler{
	
	/**
	 * 處理新聞連結清單
	 * 
	 * @param date
	 * @throws IOException
	 */
	private void processNewsList(String date) throws IOException{
		
		String dirPath = null;
		String tag = null;
		String url = null;
		File dir;
		
		for (int i = 0; i < newsTagLinkList.size(); i++) {

			//建路徑資料夾(時間/新聞分類)
			tag = newsTagLinkList.get(i)[0];
			url = newsTagLinkList.get(i)[1].toString();
			System.out.println(tag);	
			System.out.println(url);
			
			dirPath = "./蘋果日報/" + tag;
			dir = new File(dirPath);
			dir.mkdirs();			
			
			Document contain = CrawlerPack.getFromXml(url);
			
			if (contain != null) {
				if(tag.equals("地產焦點"))
					saveNewsToFile(houseNewsParseProcess(contain), date, dirPath);
				else if(tag.equals("房產王") || tag.equals("家居王") || tag.equals("豪宅王") || tag.equals("地產王"))
					saveNewsToFile(housekingNewsParseProcess(contain), date, dirPath);
				else
					saveNewsToFile(commentNewsParseProcess(contain), date, dirPath);
			} else{
				transferFail(dirPath, i, url);
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
	
	@SuppressWarnings("static-access")
	public void run() throws IOException{
		
		dateProcess();
		
	    while(Integer.parseInt(pastday) < Integer.parseInt(today)){
	    	pastdayOfYear = String.format("%04d", C.get(Calendar.YEAR));
	    	pastdayOfMonth = String.format("%02d", C.get(Calendar.MONTH) + 1);
	    	pastdayOfdate = String.format("%02d", C.get(Calendar.DAY_OF_MONTH));
	    	pastday = pastdayOfYear + pastdayOfMonth + pastdayOfdate;	
	    	
	    	url = "http://www.appledaily.com.tw/appledaily/archive/" + pastday;
	    	newsLinks = CrawlerPack.getFromHtml(url);

			for (Element elem : newsLinks.select("h2.nust.clearmen")) {
				newsTag = elem.text();
				for (Element elem2 : elem.nextElementSibling().select("a[href]")) {
					if (elem2.attr("href").contains("http:"))
						addNewsLinkList(elem2.attr("href"), newsTag);
					else
						addNewsLinkList("http://www.appledaily.com.tw" + elem2.attr("href"), newsTag);
				}
			}

			// 儲存新聞內容
			processNewsList(pastday);

			C.add(C.DATE, Integer.parseInt("1"));
	    }
	}
}
