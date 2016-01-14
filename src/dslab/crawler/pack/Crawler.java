package dslab.crawler.pack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import org.jsoup.nodes.Document;

public class Crawler {

	public ArrayList<String> newsLinkList = new ArrayList<String>();
	public ArrayList<String[]> newsTagLinkList = new ArrayList<String[]>();
	public String today;
	public String startIdx;
	public String endIdx;
	public String pastdayOfYear;
	public String pastdayOfMonth;
	public String pastdayOfdate;
	public String pastday;
	public String url;
	public String newsTag;
	public String elemString;
	public Document newsLinks;
	public Calendar C;

	/**
	 * 日期變數建立
	 */
	public void dateProcess() {
		pastday = pastdayOfYear + pastdayOfMonth + pastdayOfdate;
		C = Calendar.getInstance();
		C.set(Integer.parseInt(pastdayOfYear), Integer.parseInt(pastdayOfMonth) - 1, Integer.parseInt(pastdayOfdate));
	}

	/**
	 * 加入新聞連結串列
	 * 
	 * @param url
	 *            新聞網址
	 * @param newsTag
	 *            新聞分類
	 * @param pastday
	 *            新聞日期
	 */
	public void addNewsLinkList(String url, String newsTag, String pastday) {
		String[] link = new String[3];
		link[0] = newsTag;
		link[1] = url;
		link[2] = pastday;
		if (!newsLinkList.contains(link[1])) {
			newsLinkList.add(link[1]);
			newsTagLinkList.add(link);
			System.out.println(link[1]);
		}
	}
	
	public void addNewsLinkList(String url) {
		if (!newsLinkList.contains(url)) {
			newsLinkList.add(url);
			System.out.println(url);
		}
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
	public void saveNewsToFile(String[] newscontent, String date, String dirPath) throws IOException {

		File f = null;
		String filePath = null;
		OutputStream out = null;
		
		if(newscontent[0].equals(""))
			newscontent[0] = "---------抓取標題錯誤---------" + new Random().nextInt(10000000);

		// 建檔案名稱(時間+新聞標題)
		filePath = date + newscontent[0] + ".txt";
		f = new File(dirPath + "/" + filePath.replaceAll("[\\\\/:*?\"<>| ]", "-"));
		out = new FileOutputStream(f.getAbsolutePath());

		System.out.println(date);
		System.out.println(newscontent[0]);

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
	 *            儲存路徑
	 * @param num
	 *            錯誤編號
	 * @param url
	 *            新聞網址
	 * @throws IOException
	 */
	public void transferFail(String dirPath, int num, String url) throws IOException {
		File f = null;
		OutputStream out = null;
		System.err.println("轉換失敗");
		f = new File(dirPath + "/轉換失敗" + num);
		out = new FileOutputStream(f.getAbsolutePath());
		out.write(url.getBytes());
		out.close();
	}

	/**
	 * 處理新聞連結清單
	 * 
	 * @param newsName
	 *            新聞名稱
	 * @throws IOException
	 */
	public void processNewsList(String newsName) throws IOException {

		String dirPath = null;
		String tag = null;
		String url = null;
		String date = null;
		File dir;

		for (int i = 0; i < newsTagLinkList.size(); i++) {

			// 建路徑資料夾(時間/新聞分類)
			tag = newsTagLinkList.get(i)[0];
			url = newsTagLinkList.get(i)[1].toString();
			date = newsTagLinkList.get(i)[2].toString();

			dirPath = newsName + tag;
			dir = new File(dirPath);
			dir.mkdirs();

			Document contain = CrawlerPack.getFromXml(url);

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
				contain = CrawlerPack.getFromXml(url);
			}
		}
	}

	/**
	 * 客製化處理連結
	 * 
	 * @param tag
	 * @param url
	 * @param date
	 * @param dirPath
	 * @param tmp
	 * @throws IOException
	 */
	public void customerProcessNewsList(String tag, String url, String date, String dirPath, Document contain)
			throws IOException {

	}

	@SuppressWarnings("static-access")
	public void run() throws IOException {

		dateProcess();

		while (Integer.parseInt(pastday) < Integer.parseInt(today)) {
			pastdayOfYear = String.format("%04d", C.get(Calendar.YEAR));
			pastdayOfMonth = String.format("%02d", C.get(Calendar.MONTH) + 1);
			pastdayOfdate = String.format("%02d", C.get(Calendar.DAY_OF_MONTH));
			pastday = pastdayOfYear + pastdayOfMonth + pastdayOfdate;

			customerRunProcess();

			C.add(C.DATE, Integer.parseInt("1"));
		}
	}

	/**
	 * 客製化執行
	 */
	public void customerRunProcess() {

	}

}
