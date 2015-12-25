package dslab.crawler.ltn;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import dslab.crawler.pack.CrawlerPack;

public class LtnCrawler {
	static List<String> newsLinkList = new ArrayList<String>();
	static List<String[]> newsTagLinkList = new ArrayList<String[]>();
	
	/**
	 * 加入新聞連結串列
	 * 
	 * @param url 新聞連結
	 * @param newsTag 新聞連結分類
	 */
	private static void addNewsLinkList(String url, String newsTag){
		String[] link = new String[2];
		link[0] = newsTag;
		link[1] = url;
		if (!newsLinkList.contains(link[1])){
			newsLinkList.add(link[1]);
			newsTagLinkList.add(link);
		}
	}
	
	/**
	 * 儲存新聞內容至txt檔，路徑：./日期/分類/日期+新聞名稱.txt
	 * 
	 * @param newsList 新聞連結列表
	 * @param date 日期
	 * @throws IOException
	 */
	private static void saveNewsListText(String date) throws IOException{
		String[] newscontent = null;
		String dirPath = null;
		String filePath = null;
		String tag = null;
		String url = null;
		File dir;
		File f = null;
		OutputStream out = null;
		
		for (int i = 0; i < newsTagLinkList.size(); i++) {

			//建路徑資料夾(時間/新聞分類)
			tag = newsTagLinkList.get(i)[0];
			url = newsTagLinkList.get(i)[1].toString();
			System.out.println(tag);	
			System.out.println(url);
			
			dirPath = "./自由時報" + date + "/" + tag;
			dir = new File(dirPath);
			dir.mkdirs();			
			
			Document contain = CrawlerPack.getFromXml(url);
			
			for (Element elem : contain.select("div.content")) {
				//截取新聞標題、內容
				newscontent = commentNewsParseProcess(elem);
				
				//建檔案名稱(時間+新聞標題)
				filePath = date + newscontent[0] + ".txt";
				f = new File(dirPath + "/" + filePath.replaceAll("[\\\\/:*?\"<>|]", "-"));
				out = new FileOutputStream(f.getAbsolutePath());
				
				//寫入內容至檔案
				out.write(newscontent[0].getBytes());
				out.write("\n".getBytes());
				out.write(newscontent[1].getBytes());
				out.close();
			}
		}
	}
	
	/**
	 * 新聞爬蟲處理
	 * 
	 * @param elem
	 * @return
	 */
	private static String[] commentNewsParseProcess(Element elem){
		String[] paresResult = new String[3];
		paresResult[0] = elem.select("h1").text();
		paresResult[1] = "";
		for (Element elem2 : elem.select("div#newstext.text.boxTitle")) {
			paresResult[1] += elem2.text();
			System.out.println(paresResult[1]);
		}
		return paresResult;
	}
		
	public static void main(String[] args) throws IOException {
		//新聞分類
		String[] category = {"focus", "politics", "society", "local", "life", "opinion", "world", "business", "sports", "entertainment", "consumer", "supplement"};
		String url = null;
		String newsTag = null;
		Document newsLinks;
		List<String> urlList = new ArrayList<String>();
		
		for (Integer date = 20151224; date < 20151226; date += 1) {
			
			for (int i = 0; i < category.length; i++) {
				urlList = new ArrayList<String>();
				url = "http://news.ltn.com.tw/newspaper/" + category[i] + "/" + date.toString();
				
				newsLinks = CrawlerPack.getFromXml(url);
				
				//取得須抓取新聞連結之網頁
				urlList.add(url);
				for (Element elem : newsLinks.select("div#page.boxTitle.boxText").select("a[href]")) {
					urlList.add("http://news.ltn.com.tw" + elem.attr("href"));
				}

				//抓取連結
				for (String urllink : urlList) {
					newsLinks = CrawlerPack.getFromXml(urllink);
					for (Element elem : newsLinks.select("ul#newslistul.boxTitle").select("li.lipic")) {
						newsTag = elem.select("span").text();
						addNewsLinkList("http://news.ltn.com.tw" + elem.select("a[href]").attr("href"), newsTag);
					}
					saveNewsListText(date.toString());
				}
			}
		}
	}
}
