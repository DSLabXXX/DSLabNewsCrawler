package dslab.crawler.udn;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import dslab.crawler.pack.Crawler;
import dslab.crawler.pack.CrawlerPack;

public class UdnCrawler extends Crawler{
	
	@Override
	public void customerProcessNewsList(String tag, String url, String date, String dirPath, Document contain) throws IOException{
		

	}
	
	@Override
	public void customerRunProcess(){
		setUrl();
		//找出最後一頁頁數		
		int page = 0;
		String currentPage;
		
		newsLinks = CrawlerPack.start().getFromHtml(url);
		if (newsLinks != null) {
			for (Element elem : newsLinks.select("div.pagelink").select("href")) {
				if (elem.text().equals("最後一頁") && !elem.select("a[href]").attr("href").equals("")){
					page = Integer.parseInt(elem.select("a[href]").attr("href").split("page=")[1]);
					System.out.println(page);
				}
			}

//			for (Integer i = 1; i <= page; i++) {
//				currentPage = url + "?page=" + i.toString();
//				newsLinks = CrawlerPack.start().getFromXml(currentPage);
//				for (Element elem : newsLinks.select("section.np_alllist").select("div.listRight").select("li")) {
//					newsTag = elem.select("div.kindOf").text();
//					addNewsLinkList("http://www.chinatimes.com" + elem.select("a[href]").attr("href"), newsTag,
//							pastday);
//				}
//			}
		} else
			System.err.println("Error, can't get url contain.");
	}
	
	public void setUrl(){
		url = "http://udn.com/news/archive/0/0/" + pastdayOfYear + "/" + pastdayOfMonth + "/" + pastdayOfdate;
		System.out.println(url);
	}
	
	/**
	 * 一般新聞爬蟲處理
	 * 
	 * @param elem
	 * @return
	 */
	private String[] commentNewsParseProcess(Document contain){
		String[] newscontent = {"",""};
		setSelectElement();
		for (Element elem : contain.select(elemString)) {
			// 截取新聞標題、內容
			newscontent[0] = elem.select("header").select("h1").text();
			newscontent[1] = elem.select("article.clear-fix").select("p").text();
		}
		return newscontent;
	}
	
	public void setSelectElement(){
		elemString = "div.page_container.stack.clear-fix.newspapers_ad";
	}
}
