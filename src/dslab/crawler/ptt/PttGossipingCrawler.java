package dslab.crawler.ptt;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.json.JSONException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import dslab.crawler.pack.Crawler;
import dslab.crawler.pack.CrawlerPack;
import dslab.crawler.pack.XTrustProvider;

public class PttGossipingCrawler extends Crawler{
	String l_date;
	String l_url;
	String l_dirPath;
	String l_source;
	
	@Override
	public void customerProcessNewsList(String tag, String url, String date, String dirPath, Document contain) throws IOException, JSONException{
		l_url = url;
		l_date = date;
		l_dirPath = dirPath;
		
		processNewsContain(commentNewsParseProcess(contain));
	}
	
	@Override
	public void processNewsContain(String[] newscontent) throws IOException, JSONException {

		String filePath =  newscontent[1] + "_" + newscontent[4] + "_" + newscontent[15];
		if (newscontent[4].equals("")) {
			filePath =  newscontent[1] + "---------No title---------" + new Random().nextInt(10000000) + ".txt";
			saveNewsFile(createJsonFile(newscontent), l_dirPath + filePath);
		} else {
			filePath.replaceAll("[\\\\/:*?\"<>| ]", "-");
			try {
				saveNewsFile(createJsonFile(newscontent), l_dirPath + filePath.replace("/", "-"));
			} catch (IOException e) {
				filePath = newscontent[1] + "---------Get Title Error---------" + newscontent[15] + new Random().nextInt(10000000) + ".txt";
				saveNewsFile(createJsonFile(newscontent), l_dirPath + filePath.replace("/", "-"));
				e.printStackTrace();
			}
		}
	}
	
	public void run(){

		XTrustProvider.install();

		url = "https://www.ptt.cc/bbs/Gossiping/index" + startIdx + ".html";
		newsLinks = CrawlerPack.start().addCookie("over18","1").getFromXml(url);
		for (Element elem : newsLinks.select("div.r-ent")) {
			addNewsLinkList("https://www.ptt.cc" + elem.select("a[href]").attr("href"));
		}
		clearErrTitleCnt();
	}
	
	@Override
	public void processNewsList(String newsName) throws IOException, JSONException {

		String dirPath = null;
		String url = null;
		String tag = null;
		String date = null;
		File dir;

		for (int i = 0; i < newsLinkList.size(); i++) {

			url = newsLinkList.get(i);

			dirPath = newsName;
			dir = new File(dirPath);
			dir.mkdirs();

			Document contain = CrawlerPack.start().addCookie("over18","1").getFromXml(url);

			for (int j = 0; j < 5; j++) {
				if (contain != null) {
					customerProcessNewsList(tag, url, date, dirPath, contain);
					break;
				}
				else if (j == 4){
					transferFail(dirPath, i, url);
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				contain = CrawlerPack.start().addCookie("over18","1").getFromXml(url);
			}
		}
	}
	
	private String[] loadInfo(){
		String[] newscont = new String[20];
		newscont[0] = l_url;
		newscont[1] = l_date;
		newscont[2] = "PttGossiping";
		for(int i = 4; i < newscont.length; i++)
			newscont[i] = "";
		return newscont;
	}
	
	private String[] commentNewsParseProcess(Document contain){
		String[] newscontent = loadInfo();
		String[] tmp;
		
		for (Element elem : contain.select("div#main-container").select("div#main-content.bbs-screen.bbs-content")) {
			for(Element elem2 : elem.select("div.article-metaline").select("span.article-meta-tag")){
				if(elem2.text().equals("作者"))
					newscontent[15] = elem2.parent().select("span.article-meta-value").text();
				else if(elem2.text().equals("標題"))
					newscontent[4] = elem2.parent().select("span.article-meta-value").text();
				else if(elem2.text().equals("時間")){
					newscontent[1] = dateProcess(elem2.parent().select("span.article-meta-value").text());
				}
			}
			try {
				newscontent[16] += elem.ownText().split("來自: ")[1];
			} catch (Exception e) {
//				e.printStackTrace();
				try {
					tmp = elem.html().split("</div>")[4].split("<span class=\"f2\">");
					newscontent[16] += tmp[tmp.length - 1].split(": ")[2].split("<div")[0].split("</span>")[0];
				} catch (Exception e1) {
//					e1.printStackTrace();
					try {
						newscontent[16] += elem.ownText().split("◆ From: ")[1];						
					} catch (Exception e2) {
						newscontent[16] += "無法截取IP";
					}
				}
			}
			
			try {
				newscontent[5] += elem.html().split("</div>")[4].split("<div")[0] + "\n";
			} catch (Exception e) {
				newscontent[5] += "";
			}
			
			for(Element elem2 : elem.select("div.push")){
				for(Element elem3 : elem2.select("span")){
					if(elem3.className().equals("f1 hl push-tag"))
						newscontent[17] += elem3.text() + "hl push-taghl push-taghl push-tag";
					else if(elem3.className().equals("hl push-tag"))
						newscontent[17] += elem3.text() + "hl push-taghl push-taghl push-tag";
					else if (elem3.className().equals("f3 hl push-userid"))
						newscontent[17] += elem3.text() + "f3 hl push-useridf3 hl push-useridf3 hl push-userid";
					else if (elem3.className().equals("f3 push-content"))
						newscontent[17] += elem3.text() + "f3 push-contentf3 push-contentf3 push-content";
					else if (elem3.className().equals("push-ipdatetime"))
						newscontent[17] += elem3.text();
				}
				newscontent[17] += "\n";
			}
			newscontent[18] += elem.select("a").attr("href").replace("=", "%3D") + "====";
		}
		return newscontent;
	}
	
	private String dateProcess(String dateString){
		String year;
		String month;
		String time;
		String date;
		
		try {
			year = dateString.split(" ")[4];
		} catch (Exception e) {
			e.printStackTrace();
			year = "2xxx";
		}
		
	    if(dateString.split(" ")[1].equals("Jan"))
	        month = "01";
	    else if(dateString.split(" ")[1].equals("Feb"))
	        month = "02";
	    else if(dateString.split(" ")[1].equals("Mar"))
	        month = "03";
	    else if(dateString.split(" ")[1].equals("Apr"))
	        month = "04";
	    else if(dateString.split(" ")[1].equals("May"))
	        month = "05";
	    else if(dateString.split(" ")[1].equals("Jun"))
	        month = "06";
	    else if(dateString.split(" ")[1].equals("Jul"))
	        month = "07";
	    else if(dateString.split(" ")[1].equals("Aug"))
	        month = "08";
	    else if(dateString.split(" ")[1].equals("Sep"))
	        month = "09";
	    else if(dateString.split(" ")[1].equals("Oct"))
	        month = "10";
	    else if(dateString.split(" ")[1].equals("Nov"))
	        month = "11";
	    else if(dateString.split(" ")[1].equals("Dec"))
	        month = "12";
	    else
	        month = dateString.split(" ")[1];

	    time = dateString.split(" ")[3].replace(":", "");
	    date = addZeroForNum(dateString.split(" ")[2], 2);
	    
		return year + month + date +  time;
	}
	
	public static String addZeroForNum(String str, int strLength) {
	    int strLen = str.length();
	    if (strLen < strLength) {
	        while (strLen < strLength) {
	            StringBuffer sb = new StringBuffer();
	            sb.append("0").append(str);
	            str = sb.toString();
	            strLen = str.length();
	        }
	    }
	    return str;
	}

}
