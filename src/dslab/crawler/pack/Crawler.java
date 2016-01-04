package dslab.crawler.pack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.jsoup.nodes.Document;

public class Crawler {
	
	public ArrayList<String> newsLinkList = new ArrayList<String>();
	public ArrayList<String[]> newsTagLinkList = new ArrayList<String[]>();
	public String today;
	public String pastdayOfYear;
	public String pastdayOfMonth;
	public String pastdayOfdate;
	public String pastday;
	public String url;
	public String newsTag;
	public Document newsLinks;
	public Calendar C;
	
	public void dateProcess(){
		pastday = pastdayOfYear + pastdayOfMonth + pastdayOfdate;
		Date todayDate = Calendar.getInstance().getTime();
		DateFormat dateFormate = new SimpleDateFormat("yyyyMMdd");
		today = dateFormate.format(todayDate).toString();
		C = Calendar.getInstance();
		C.set(Integer.parseInt(pastdayOfYear), Integer.parseInt(pastdayOfMonth) - 1, Integer.parseInt(pastdayOfdate));
	}
	
	/**
	 * 加入新聞連結串列
	 * 
	 * @param url 新聞連結
	 * @param newsTag 新聞連結分類
	 */
	
	public void addNewsLinkList(String url, String newsTag){
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
	 * @param newscontent
	 * @param date
	 * @param dirPath
	 * @throws IOException
	 */
	public void saveNewsToFile(String[] newscontent, String date, String dirPath) throws IOException{
		
		File f = null;
		String filePath = null;
		OutputStream out = null;
		
		// 建檔案名稱(時間+新聞標題)
		filePath = date + newscontent[0] + ".txt";
		f = new File(dirPath + "/" + filePath.replaceAll("[\\\\/:*?\"<>| ]", "-"));
		out = new FileOutputStream(f.getAbsolutePath());

		// 寫入內容至檔案
		out.write(newscontent[0].getBytes());
		out.write("\n".getBytes());
		out.write(newscontent[1].getBytes());
		out.close();
	}
	
	/**
	 * 轉換失敗處理
	 * 
	 * @param dirPath
	 * @param num
	 * @param url
	 * @throws IOException
	 */
	public void transferFail(String dirPath, int num, String url) throws IOException{
		File f = null;
		OutputStream out = null;
		System.err.println("轉換失敗");
		f = new File(dirPath + "/轉換失敗" + num);
		out = new FileOutputStream(f.getAbsolutePath());
		out.write(url.getBytes());
		out.close();
	}

}
