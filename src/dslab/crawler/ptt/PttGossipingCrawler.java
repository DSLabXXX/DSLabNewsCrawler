package dslab.crawler.ptt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import dslab.crawler.pack.Crawler;
import dslab.crawler.pack.XTrustProvider;

public class PttGossipingCrawler extends Crawler{
	
	@Override
	public void customerProcessNewsList(String tag, String url, String date, String dirPath, Document contain) throws IOException{
		saveNewsToFile(commentNewsParseProcess(contain), date, dirPath);
	}
	
	/**
	 * 儲存新聞內容至txt檔，路徑：./日期/分類/日期+新聞名稱.txt
	 * 
	 * @param newscontent
	 *            新聞內容
	 * @param date
	 *            新聞日期
	 * @param dirPath
	 *            儲存路徑
	 * @throws IOException
	 */
	@Override
	public void saveNewsToFile(String[] newscontent, String date, String dirPath) throws IOException {

		File f = null;
		String filePath = null;
		OutputStream out = null;
		
		// 建檔案名稱(時間+新聞標題)
		if(newscontent[1].equals("")){
			newscontent[1] = "---------無法抓取標題---------" + new Random().nextInt(10000000);
			filePath = newscontent[1] + ".txt";
		}
		else{
			filePath = newscontent[2] + "_" + newscontent[1] + "_" + newscontent[0] + ".txt";
			f = new File(dirPath + "/" + filePath.replaceAll("[\\\\/:*?\"<>| ]", "-"));
			out = new FileOutputStream(f.getAbsolutePath());

			// 寫入內容至檔案
			for (int i = 0; i < newscontent.length; i++) {
				out.write(newscontent[i].getBytes());
				out.write("\n".getBytes());
			}
			out.close();
		}
		System.out.println(filePath);
	}
	
	public void run(){

		XTrustProvider.install();

		url = "https://www.ptt.cc/bbs/Gossiping/index" + startIdx + ".html";
		newsLinks = PttCrawlerPack.getFromXml(url);
		for (Element elem : newsLinks.select("div.r-ent")) {
			addNewsLinkList("https://www.ptt.cc" + elem.select("a[href]").attr("href"));
		}
	}
	
	/**
	 * 處理新聞連結清單
	 * 
	 * @param newsName
	 *            新聞名稱
	 * @throws IOException
	 */
	@Override
	public void processNewsList(String newsName) throws IOException {

		String dirPath = null;
		String url = null;
		String tag = null;
		String date = null;
		File dir;

		for (int i = 0; i < newsLinkList.size(); i++) {

			// 建路徑資料夾(時間/新聞分類)
			url = newsLinkList.get(i);

			dirPath = newsName;
			dir = new File(dirPath);
			dir.mkdirs();

			Document contain = PttCrawlerPack.getFromXml(url);

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
				contain = PttCrawlerPack.getFromXml(url);
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
		String[] newscontent = {"","","","","",""};
		String[] tmp;
		
		for (Element elem : contain.select("div#main-container").select("div#main-content.bbs-screen.bbs-content")) {
			// 截取作者、標題、日期
			for(Element elem2 : elem.select("div.article-metaline").select("span.article-meta-tag")){
				if(elem2.text().equals("作者"))
					newscontent[0] = elem2.parent().select("span.article-meta-value").text();
				else if(elem2.text().equals("標題"))
					newscontent[1] = elem2.parent().select("span.article-meta-value").text();
				else if(elem2.text().equals("時間")){
					newscontent[2] = dateProcess(elem2.parent().select("span.article-meta-value").text());
				}
			}
			// 截取IP
			try {
				newscontent[3] = elem.ownText().split("來自: ")[1];
			} catch (Exception e) {
//				e.printStackTrace();
				try {
					tmp = elem.html().split("</div>")[4].split("<span class=\"f2\">");
					newscontent[3] = tmp[tmp.length - 1].split(": ")[2].split("<div")[0].split("</span>")[0];
				} catch (Exception e1) {
//					e1.printStackTrace();
					try {
						newscontent[3] = elem.ownText().split("◆ From: ")[1];						
					} catch (Exception e2) {
//						e2.printStackTrace();
						newscontent[3] = "無法截取IP";
					}
				}
			}
			
			// 截取內文
			try {
				newscontent[4] = elem.html().split("</div>")[4].split("<div")[0] + "\n";
			} catch (Exception e) {
//				e.printStackTrace();
				newscontent[4] = "";
			}
			
			// 截取推文
			for(Element elem2 : elem.select("div.push")){
				for(Element elem3 : elem2.select("span")){
					if(elem3.className().equals("push-ipdatetime"))
						newscontent[5] += "\n" + elem3.text();
					else
						newscontent[5] += elem3.text() + " ";
				}
				newscontent[5] += "\n";
			}
		}
		
//		System.out.println(newscontent[0].toString());
//		System.out.println(newscontent[1].toString());
//		System.out.println(newscontent[2].toString());
//		System.out.println(newscontent[3].toString());
//		System.out.println(newscontent[4].toString());
//		System.out.println(newscontent[5].toString());
		
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
	            sb.append("0").append(str);// 左補0
	            // sb.append(str).append("0");//右補0
	            str = sb.toString();
	            strLen = str.length();
	        }
	    }
	    return str;
	}

}
