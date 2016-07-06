package dslab.crawler.apple;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import dslab.crawler.pack.Crawler;
import dslab.crawler.pack.CrawlerPack;

public class AppleCrawler extends Crawler{
	String l_date;
	String l_url;
	String l_dirPath;
	String l_category;
	
	@Override
	public void customerProcessNewsList(String category, String url, String date, String dirPath, Document contain) throws IOException, JSONException{
		l_category = category;
		l_url = url;
		l_date = date;
		l_dirPath = dirPath;
		
		if (category.equals("地產焦點"))
			processNewsContain(houseNewsParseProcess(contain));
		else if (category.equals("房產王") || category.equals("家居王") || category.equals("豪宅王") || category.equals("地產王"))
			processNewsContain(housekingNewsParseProcess(contain));
		else
			processNewsContain(commentNewsParseProcess(contain));
	}
		
	@Override
	public void customerRunProcess(){
		url = "http://www.appledaily.com.tw/appledaily/archive/" + pastday;
    	newsLinks = CrawlerPack.start().getFromHtml(url);

		for (Element elem : newsLinks.select("h2.nust.clearmen")) {
			newsCategory = elem.text();
			for (Element elem2 : elem.nextElementSibling().select("a[href]")) {
				if (elem2.attr("href").contains("http:"))
					addNewsLinkList(elem2.attr("href"), newsCategory, pastday);
				else
					addNewsLinkList("http://www.appledaily.com.tw" + elem2.attr("href"), newsCategory, pastday);
			}
		}
	}
	
	private String[] loadInfo(){
		String[] newscont = new String[15];
		newscont[0] = l_url;
		newscont[1] = l_date;
		newscont[2] = "AppleDaily";
		newscont[3] = l_category;
		newscont[7] = "";
		return newscont;
	}
	
	private String[] commentNewsParseProcess(Document contain){
		String[] newscontent = loadInfo();
		for (Element elem : contain.select("article#maincontent.vertebrae")) {
			newscontent[4] = elem.select("header").select("hgroup").text();
			newscontent[5] = elem.select("p").text();
		}
		for(Element elem : contain.select("div.articulum").select("figure")){
			newscontent[7] += elem.select("a").attr("title")+ "::::" + elem.select("a").attr("href").replace("=", "%3D") + "====";
		}		
		return newscontent;
	}
	
	private String[] housekingNewsParseProcess(Document contain){
		String[] newscontent = loadInfo();
		
		for (Element elem : contain.select("article#maincontent.vertebrae")) {
			newscontent[4] = elem.select("div.ncbox_cont").select("h1").text();
			newscontent[5] += elem.select("div.articulum").select("p").text();
		}
		
		for(Element elem : contain.select("div.ncbox_cont").select("div.articulum").select("figure.lbimg.sgimg")){
			newscontent[7] += elem.select("a").attr("title")+ "::::" + elem.select("a").attr("href").replace("=", "%3D") + "====";
		}
		return newscontent;
	}
	
	private String[] houseNewsParseProcess(Document contain){
		String[] newscontent = loadInfo();
		
		for (Element elem : contain.select("article#maincontent.vertebrae")) {
			newscontent[4] = elem.select("div.ncbox_cont").select("h1").text();
			newscontent[5] = elem.select("p").text();
		}
		
		for(Element elem : contain.select("div.ncbox_cont").select("div.articulum").select("figure.lbimg.sgimg")){
			newscontent[7] += elem.select("a").attr("title")+ "::::" + elem.select("a").attr("href").replace("=", "%3D") + "====";
		}		
		return newscontent;
	}

}
