package dslab.crawler.pack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
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
	
	public void clearList(){
		newsLinkList = new ArrayList<String>();
		newsTagLinkList = new ArrayList<String[]>();
	}


	public void processNewsContain(String[] newscontent, String date, String dirPath) throws IOException {
		saveNewsToFile(newscontent, date, dirPath);
//		sentNewsToStream(newscontent);
	}
	
	private void saveNewsToFile(String[] newscontent, String date, String dirPath) throws IOException{
		
		File f = null;
		String filePath = null;
		OutputStreamWriter out = null;

		if (newscontent[0].equals("")) {
			newscontent[0] = "---------抓取標題為空---------" + new Random().nextInt(10000000);
			filePath = newscontent[0] + ".txt";
		} else {
			filePath = date + newscontent[0] + ".txt";
			f = new File(dirPath + "/" + filePath.replaceAll("[\\\\/:*?\"<>| ]", "-"));
			try {
				out = new OutputStreamWriter(new FileOutputStream(f.getAbsolutePath()), "UTF-8");
				System.out.println(f.getAbsolutePath());
			} catch (Exception e) {
				newscontent[0] = "---------抓取標題錯誤~---------" + new Random().nextInt(10000000);
				filePath = newscontent[0] + ".txt";
				f = new File(dirPath + "/" + filePath);
				out = new OutputStreamWriter(new FileOutputStream(f.getAbsolutePath()), "UTF-8");
			}

			System.out.println(date + newscontent[0]);

			out.write(newscontent[0]);
			out.write("\n");
			out.write(newscontent[1]);
			out.close();
		}
	}

	public void transferFail(String dirPath, int num, String url) throws IOException {
		File f = null;
		OutputStream out = null;
		System.err.println("轉換失敗");
		f = new File(dirPath + "/轉換失敗" + num);
		out = new FileOutputStream(f.getAbsolutePath());
		out.write(url.getBytes());
		out.close();
	}

	public void processNewsList(String newsName) throws IOException {

		String dirPath = null;
		String tag = null;
		String url = null;
		String date = null;
		File dir;

		for (int i = 0; i < newsTagLinkList.size(); i++) {

			tag = newsTagLinkList.get(i)[0];
			url = newsTagLinkList.get(i)[1].toString();
			date = newsTagLinkList.get(i)[2].toString();

			dirPath = newsName + tag;
			dir = new File(dirPath);
			dir.mkdirs();

			Document contain = CrawlerPack.start().getFromXml(url);

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
				contain = CrawlerPack.start().getFromXml(url);
			}
		}
		clearList();
	}

	public void customerProcessNewsList(String tag, String url, String date, String dirPath, Document contain)
			throws IOException {

	}

	public void customerRunProcess() {

	}

}
