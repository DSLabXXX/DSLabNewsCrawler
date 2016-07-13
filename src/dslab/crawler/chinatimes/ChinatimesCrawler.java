package dslab.crawler.chinatimes;


import java.io.IOException;

import org.json.JSONException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import dslab.crawler.pack.Crawler;
import dslab.crawler.pack.CrawlerPack;

public class ChinatimesCrawler extends Crawler {
	String l_date;
	String l_url;
	String l_dirPath;
	String l_category;
	String l_source;
	
	@Override
	public void customerProcessNewsList(String category, String url, String date, String dirPath, Document contain) throws IOException, JSONException{
		l_category = category;
		l_url = url;
		l_date = date;
		l_dirPath = dirPath;
		processNewsContain(commentNewsParseProcess(contain));
	}
	
	@Override
	public void customerRunProcess(){
		int page = 0;
		String currentPage;
		
		setUrl();
    	newsLinks = CrawlerPack.start().getFromXml(url);
    	for (Element elem : newsLinks.select("div.pagination.clear-fix").select("li")){
    		if(elem.text().equals("最後一頁") && !elem.select("a[href]").attr("href").equals(""))
    			page = Integer.parseInt(elem.select("a[href]").attr("href").split("page=")[1]);
    	}
    	
    	for (Integer i = 1; i <= page; i++){
    		currentPage = url + "?page=" + i.toString();
    		newsLinks = CrawlerPack.start().getFromXml(currentPage);
			for (Element elem : newsLinks.select("section.np_alllist").select("div.listRight").select("li")) {
				newsCategory = elem.select("div.kindOf").text();
				addNewsLinkList("http://www.chinatimes.com" + elem.select("a[href]").attr("href"), newsCategory, pastday);
			}
    	}
	}	
	
	public void setUrl(){
		url = "http://www.chinatimes.com/history-by-date/" + pastdayOfYear + "-" + pastdayOfMonth + "-" + pastdayOfdate + "-2601";
		setSource("ChinaTimes");
	}
	
	private String[] loadInfo(){
		String[] newscont = new String[20];
		newscont[0] = l_url;
		newscont[1] = l_date;
		newscont[2] = l_source;
		newscont[3] = l_category;
		for(int i = 4; i < newscont.length; i++)
			newscont[i] = "";
		return newscont;
	}
	
	public void setSource(String source){
		l_source = source;
	}
	
	private String[] commentNewsParseProcess(Document contain){
		String[] newscontent = loadInfo();
		setSelectElement();
		
		for (Element elem : contain.select(elemString)) {
			newscontent[4] = elem.select("header").select("h1").text();
			newscontent[5] = elem.select("article.clear-fix").select("p").text();
		}
		for(Element elem : contain.select(elemString).select("article.clear-fix").select("div.img_view").select("img")){
			if(!elem.attr("title").equals("") || !elem.attr("src").equals("")){
				newscontent[7] += elem.attr("title") + "::::" + elem.attr("src").replace("=", "%3D") + "====";
			}
		}
		return newscontent;
	}
	
	public void setSelectElement(){
		elemString = "div.page_container.stack.clear-fix.newspapers_ad";
	}
}
