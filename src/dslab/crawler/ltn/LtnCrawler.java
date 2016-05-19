package dslab.crawler.ltn;


import java.io.IOException;
import java.util.ArrayList;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import dslab.crawler.pack.Crawler;
import dslab.crawler.pack.CrawlerPack;

public class LtnCrawler extends Crawler{
	
	@Override
	public void customerProcessNewsList(String tag, String url, String date, String dirPath, Document contain) throws IOException{
		if (tag.equals("社論") || tag.equals("自由廣場") || tag.equals("自由談"))
			processNewsContain(editorialNewsParseProcess(contain), date, dirPath);
		else if (tag.equals("影視焦點"))
			processNewsContain(entertainmentNewsParseProcess(contain), date, dirPath);
		else if (tag.equals("體育新聞") || tag.equals("運動彩卷"))
			processNewsContain(sportNewsParseProcess(contain), date, dirPath);
		else if (tag.equals("鏗鏘集"))
			processNewsContain(talkNewsParseProcess(contain), date, dirPath);
		else
			processNewsContain(commentNewsParseProcess(contain), date, dirPath);
	}
	
	@Override
	public void customerRunProcess(){
		String[] category = {"focus", "politics", "society", "local", "life", "opinion", "world", "business", "sports", "entertainment", "consumer", "supplement"};
		ArrayList<String> urlList = new ArrayList<String>();
		
    	for (int i = 0; i < category.length; i++) {
			urlList = new ArrayList<String>();
			url = "http://news.ltn.com.tw/newspaper/" + category[i] + "/" + pastday;
			newsLinks = CrawlerPack.start().getFromXml(url);
			urlList.add(url);
			for (Element elem : newsLinks.select("div#page.boxTitle.boxText").select("a[href]")) {
				urlList.add("http://news.ltn.com.tw" + elem.attr("href"));
			}
			for (String urllink : urlList) {
				newsLinks = CrawlerPack.start().getFromXml(urllink);
				for (Element elem : newsLinks.select("ul#newslistul.boxTitle").select("li.lipic")) {
					newsTag = elem.select("span").text();
					addNewsLinkList("http://news.ltn.com.tw" + elem.select("a[href]").attr("href"), newsTag, pastday);
				}
			}
		}	    	
	}
	
	/**
	 * �@��s�D���γB�z
	 * 
	 * @param elem
	 * @return
	 * @throws IOException 
	 */
	private String[] commentNewsParseProcess(Document contain) throws IOException{
		String[] newscontent = {"",""};
		
		for (Element elem : contain.select("div.content")) {
			// �I��s�D���D�B���e
			newscontent[0] = elem.select("h1").text();
			for (Element elem2 : elem.select("div#newstext.text.boxTitle")) {
				newscontent[1] += elem2.select("p").text();
			}
		}
		return newscontent;
	}
	
	/**
	 * �v��J�I�s�D���γB�z
	 * 
	 * @param elem
	 * @return
	 */
	private String[] entertainmentNewsParseProcess(Document contain) throws IOException{
		String[] newscontent = {"",""};
		
		for (Element elem : contain.select("div.content").select("div.news_content")) {
			// �I��s�D���D�B���e
			newscontent[0] = elem.select("h1").text();
			newscontent[1] += elem.select("p").text();
		}
		return newscontent;
	}
	
	/**
	 * ��|�s�D�B�B�ʱm�����γB�z
	 * 
	 * @param elem
	 * @return
	 */
	private String[] sportNewsParseProcess(Document contain) throws IOException{
		String[] newscontent = {"",""};
		
		for (Element elem : contain.select("div.content").select("div.news_content")) {
			// �I��s�D���D�B���e
			newscontent[0] = elem.select("div.Btitle").text();
			newscontent[1] += elem.select("p").text();
		}
		return newscontent;
	}
	
	/**
	 * ���סB�ۥѼs���B�ۥѽͷs�D���γB�z
	 * 
	 * @param elem
	 * @return
	 */
	private String[] editorialNewsParseProcess(Document contain) throws IOException{
		String[] newscontent = {"",""};
		
		for (Element elem : contain.select("div.content.page-name")) {
			// �I��s�D���D�B���e
			newscontent[0] = elem.select("h2").text();
			for (Element elem2 : elem.select("div.cont")) {
				newscontent[1] += elem2.select("p").text();
			}
			for (Element elem2 : elem.select("div.conbox")) {
				newscontent[1] += elem2.select("p").text();
			}
		}
		return newscontent;
	}
	
	/**
	 * ���򶰷s�D���γB�z
	 * 
	 * @param elem
	 * @return
	 */
	private String[] talkNewsParseProcess(Document contain) throws IOException{
		String[] newscontent = {"",""};
		
		for (Element elem : contain.select("div#rightmain.rightmain_c").select("div.content.page-name")) {
			// �I��s�D���D�B���e
			newscontent[0] = elem.select("h2").text();
			for (Element elem2 : elem.select("div.cont")) {
				newscontent[1] += elem2.select("p").text();
			}
		}
		return newscontent;
	}
}
