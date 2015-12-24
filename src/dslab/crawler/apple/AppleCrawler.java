package dslab.crawler.apple;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import dslab.crawler.pack.CrawlerPack;

public class AppleCrawler {
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
	private static void saveNewsListText(List<String[]> newsList, String date) throws IOException{
		String[] newscontent = null;
		String dirPath = null;
		String filePath = null;
		String tag = null;
		String url = null;
		File dir;
		File f = null;
		OutputStream out = null;
		
		for (int i = 0; i < newsList.size(); i++) {

			//建路徑資料夾(時間/新聞分類)
			tag = newsList.get(i)[0];
			url = newsList.get(i)[1].toString();
			System.out.println(tag);	
			System.out.println(url);
			
			dirPath = "./蘋果日報" + date + "/" + tag;
			dir = new File(dirPath);
			dir.mkdirs();			
			
			Document contain = CrawlerPack.getFromXml(url);
			
			for (Element elem : contain.select("article#maincontent.vertebrae")) {
				//截取新聞標題、內容
				if(!tag.equals("地產焦點"))
					newscontent = commentNewsParseProcess(elem);
				else
					newscontent = houseNewsParseProcess(elem);
				
				//建檔案名稱(時間+新聞標題)
				filePath = date + newscontent[0] + ".txt";
				f = new File(dirPath + "/" + filePath.replaceAll("[\\\\/:*?\"<>|]", "-"));
				out = new FileOutputStream(f.getAbsolutePath());
				
				//寫入內容至檔案
				out.write(newscontent[0].getBytes());
				out.write("\n".getBytes());
				out.write(newscontent[1].getBytes());
				out.write("\n".getBytes());
				out.write(newscontent[2].getBytes());
				out.close();
			}
		}
	}
	
	/**
	 * 其他新聞爬蟲處理
	 * 
	 * @param elem
	 * @return
	 */
	private static String[] commentNewsParseProcess(Element elem){
		String[] paresResult = new String[3];
		paresResult[0] = elem.select("header").select("hgroup").text();
		paresResult[1] = elem.select("p#introid").text();
		paresResult[2] = elem.select("p#bcontent").text();
		return paresResult;
	}
	
	/**
	 * 房產新聞爬蟲特殊處理
	 * 
	 * @param elem
	 * @return
	 */
	private static String[] houseNewsParseProcess(Element elem){
		String[] paresResult = new String[3];
		paresResult[0] = elem.select("div.ncbox_cont").select("h1").text();
		paresResult[1] = elem.select("p#introid").text();
		paresResult[2] = elem.select("p#bcontent").text();
		return paresResult;
	}

	public static void main(String[] args) throws IOException {
		
		for (Integer date = 20040101; date < 20150000; date += 10000){
			String url = "http://www.appledaily.com.tw/appledaily/archive/" + date;
			String newsTag = null;

			Document newsLinks = CrawlerPack.getFromHtml(url);

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
			saveNewsListText(newsTagLinkList, date.toString());
		}
	}
}
