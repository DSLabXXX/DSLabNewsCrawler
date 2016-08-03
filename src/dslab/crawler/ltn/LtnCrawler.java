package dslab.crawler.ltn;


import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import dslab.crawler.pack.Crawler;
import dslab.crawler.pack.CrawlerPack;

public class LtnCrawler extends Crawler{
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
		
		if (category.equals("社論") || category.equals("自由廣場") || category.equals("自由談"))
			processNewsContain(editorialNewsParseProcess(contain));
		else if (category.equals("影視焦點"))
			processNewsContain(entertainmentNewsParseProcess(contain));
		else if (category.equals("體育新聞") || category.equals("運動彩卷"))
			processNewsContain(sportNewsParseProcess(contain));
		else if (category.equals("鏗鏘集"))
			processNewsContain(talkNewsParseProcess(contain));
		else
			processNewsContain(commentNewsParseProcess(contain));
	}
	
	@Override
	public void customerRunProcess(){
		clearErrTitleCnt();
		String[] subCategory = {"focus", "politics", "society", "local", "life", "opinion", "world", "business", "sports", "entertainment", "consumer", "supplement"};
		ArrayList<String> urlList = new ArrayList<String>();
		
    	for (int i = 0; i < subCategory.length; i++) {
			urlList = new ArrayList<String>();
			url = "http://news.ltn.com.tw/newspaper/" + subCategory[i] + "/" + pastday;
			newsLinks = CrawlerPack.start().getFromXml(url);
			urlList.add(url);
			for (Element elem : newsLinks.select("div#page.boxTitle.boxText").select("a[href]")) {
				urlList.add("http://news.ltn.com.tw" + elem.attr("href"));
			}
			for (String urllink : urlList) {
				newsLinks = CrawlerPack.start().getFromXml(urllink);
				for (Element elem : newsLinks.select("ul#newslistul.boxTitle").select("li.lipic")) {
					newsCategory = elem.select("span").text();
					addNewsLinkList("http://news.ltn.com.tw" + elem.select("a[href]").attr("href"), newsCategory, pastday);
				}
			}
		}	    	
	}
	
	private String[] loadInfo(){
		String[] newscont = new String[20];
		newscont[0] = l_url;
		newscont[1] = l_date;
		newscont[2] = "LTN";
		newscont[3] = l_category;
		for(int i = 4; i < newscont.length; i++)
			newscont[i] = "";
		return newscont;
	}	

	private String[] commentNewsParseProcess(Document contain) throws IOException{
		String[] newscontent = loadInfo();
		for (Element elem : contain.select("div.content")) {

			newscontent[4] = elem.select("h1").text();
			for (Element elem2 : elem.select("div#newstext.text.boxTitle")) {
				newscontent[5] += elem2.select("p").text();
			}
			for (Element elem2 : elem.select("div#newstext.text.boxTitle").select("ul#newsphoto").select("li").select("img")) {
				if(!elem2.attr("title").equals("") || !elem2.attr("src").replace("=", "%3D").equals("")){
					newscontent[7] += elem2.attr("title") + "::::" + elem2.attr("src").replace("=", "%3D") + "====";
				}
			}
		}
		return newscontent;
	}

	private String[] entertainmentNewsParseProcess(Document contain) throws IOException{
		String[] newscontent = loadInfo();
		
		for (Element elem : contain.select("div.content").select("div.news_content")) {
			newscontent[4] = elem.select("h1").text();
			newscontent[5] += elem.select("p").text();
			
		}
		for (Element elem : contain.select("div.content").select("div.news_content").select("p").select("span.ph_b.ph_d1")) {
			if(!elem.select("span.ph_d").text().equals("") || !elem.select("span.ph_i").select("img").attr("src").replace("=", "%3D").equals("")){
				newscontent[7] += elem.select("span.ph_d").text() + "::::" + elem.select("span.ph_i").select("img").attr("data-original").replace("=", "%3D") + "====";
				System.err.println(newscontent[7]);
			}
		}
		return newscontent;
	}

	private String[] sportNewsParseProcess(Document contain) throws IOException{
		String[] newscontent = loadInfo();
		
		for (Element elem : contain.select("div.content").select("div.news_content")) {
			newscontent[4] = elem.select("h1").text();
			newscontent[5] += elem.select("p").text();
		}		
		for (Element elem : contain.select("div.content").select("div.news_content").select("span.ph_b.ph_d1")) {
			if(!elem.select("span.ph_d").text().equals("") || !elem.select("span.ph_i").select("img").attr("src").replace("=", "%3D").equals("")){
				newscontent[7] += elem.select("span.ph_d").text() + "::::" + elem.select("span.ph_i").select("img").attr("src").replace("=", "%3D") + "====";
			}
		}
		return newscontent;
	}

	private String[] editorialNewsParseProcess(Document contain) throws IOException{
		String[] newscontent = loadInfo();
		
		for (Element elem : contain.select("div.content.page-name")) {
			newscontent[4] = elem.select("h2").text();
			for (Element elem2 : elem.select("div.cont")) {
				newscontent[5] += elem2.select("p").text();
			}
			for (Element elem2 : elem.select("div.cont").select("p").select("span.ph_b.ph_d1")) {
				if(!elem2.select("span.ph_d").text().equals("") || !elem2.select("span.ph_i").select("img").attr("src").replace("=", "%3D").equals("")){
					newscontent[7] += elem2.select("span.ph_d").text() + "::::" + elem2.select("span.ph_i").select("img").attr("src").replace("=", "%3D") + "====";
				}
			}
			for (Element elem2 : elem.select("div.conbox")) {
				newscontent[5] += elem2.select("p").text();				
			}
		}
		return newscontent;
	}

	private String[] talkNewsParseProcess(Document contain) throws IOException{
		String[] newscontent = loadInfo();
		
		for (Element elem : contain.select("div#rightmain.rightmain_c").select("div.content.page-name")) {
			newscontent[4] = elem.select("h2").text();
			for (Element elem2 : elem.select("div.cont")) {
				newscontent[5] += elem2.select("p").text();
			}
		}
		return newscontent;
	}
}
