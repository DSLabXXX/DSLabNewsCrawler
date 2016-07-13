package dslab.crawler.pack;

import java.io.IOException;
import java.util.Calendar;

import org.json.JSONException;

import dslab.crawler.apple.AppleCrawler;
import dslab.crawler.chinatimes.BusinessTimesCrawlwe;
import dslab.crawler.chinatimes.ChinaElectronicsNewsCrawler;
import dslab.crawler.chinatimes.ChinatimesCrawler;
import dslab.crawler.chinatimes.DogNewsCrawler;
import dslab.crawler.ltn.LtnCrawler;
import dslab.crawler.ptt.PttGossipingCrawler;
import dslab.crawler.udn.UdnCrawler;

public class Launcher {
	
	public static String pastdayOfYear;
	public static String pastdayOfMonth;
	public static String pastdayOfdate;
	public static String pastday;
	public static String today;
	public static Calendar C;
	
	private static void apple(AppleCrawler apple, String[] dateAry) throws IOException, JSONException{
		apple.pastdayOfYear = dateAry[0];
		apple.pastdayOfMonth = dateAry[1];
		apple.pastdayOfdate = dateAry[2];
		apple.pastday = pastday;
		apple.today = today;
		apple.customerRunProcess();
		apple.processNewsList("/home/dslab_crawler/AppleDaily/");
	}
	
	private static void ltn(LtnCrawler ltn, String[] dateAry) throws IOException, JSONException{
		ltn.pastdayOfYear = dateAry[0];
		ltn.pastdayOfMonth = dateAry[1];
		ltn.pastdayOfdate = dateAry[2];
		ltn.pastday = pastday;
		ltn.today = today;
		ltn.customerRunProcess();
		ltn.processNewsList("/home/dslab_crawler/LTN/");
	}
	
	private static void chinatimes(ChinatimesCrawler chinatimes, String[] dateAry) throws IOException, JSONException{
		chinatimes.pastdayOfYear = dateAry[0];
		chinatimes.pastdayOfMonth = dateAry[1];
		chinatimes.pastdayOfdate = dateAry[2];
		chinatimes.pastday = pastday;
		chinatimes.today = today;
		chinatimes.customerRunProcess();
		chinatimes.processNewsList("/home/dslab_crawler/ChinaTimes/");
	}
	
	private static void businesstimes(BusinessTimesCrawlwe businesstimes, String[] dateAry) throws IOException, JSONException{
		businesstimes.pastdayOfYear = dateAry[0];
		businesstimes.pastdayOfMonth = dateAry[1];
		businesstimes.pastdayOfdate = dateAry[2];
		businesstimes.pastday = pastday;
		businesstimes.today = today;
		businesstimes.customerRunProcess();
		businesstimes.processNewsList("/home/dslab_crawler/BusinessTimes/");
	}
	
	private static void dogNews(DogNewsCrawler dogNews, String[] dateAry) throws IOException, JSONException{
		dogNews.pastdayOfYear = dateAry[0];
		dogNews.pastdayOfMonth = dateAry[1];
		dogNews.pastdayOfdate = dateAry[2];
		dogNews.pastday = pastday;
		dogNews.today = today;
		dogNews.customerRunProcess();
		dogNews.processNewsList("/home/dslab_crawler/DogNews/");
	}
	
	private static void chinaelectrontimes(ChinaElectronicsNewsCrawler chinaelectrontimes, String[] dateAry) throws IOException, JSONException{
		chinaelectrontimes.pastdayOfYear = dateAry[0];
		chinaelectrontimes.pastdayOfMonth = dateAry[1];
		chinaelectrontimes.pastdayOfdate = dateAry[2];
		chinaelectrontimes.pastday = pastday;
		chinaelectrontimes.today = today;
		chinaelectrontimes.customerRunProcess();
		chinaelectrontimes.processNewsList("/home/dslab_crawler/ChinaElectronicsNews/");
	}
	
	private static void udn(UdnCrawler udn, String[] dateAry) throws IOException, JSONException{
		udn.pastdayOfYear = dateAry[0];
		udn.pastdayOfMonth = dateAry[1];
		udn.pastdayOfdate = dateAry[2];
		udn.pastday = pastday;
		udn.today = today;
		udn.customerRunProcess();
		udn.processNewsList("/home/dslab_crawler/UDN/");
	}
	
	private static void pttgossiping(Integer startIdx) throws IOException, JSONException{
		PttGossipingCrawler ptt = new PttGossipingCrawler();
		ptt.startIdx = startIdx.toString();
		ptt.run();
		ptt.processNewsList("/home/dslab_crawler/PttGossiping/");
	}

	private static void dateProcess(String[] args) {
		pastdayOfYear = args[0];
		pastdayOfMonth = args[1];
		pastdayOfdate = args[2];
		today = args[3];
		pastday = pastdayOfYear + pastdayOfMonth + pastdayOfdate;
		C = Calendar.getInstance();
		C.set(Integer.parseInt(pastdayOfYear), Integer.parseInt(pastdayOfMonth) - 1, Integer.parseInt(pastdayOfdate));
	}
	
	public static void main(String[] args) throws IOException, InterruptedException, JSONException {
		
//		TcpLink tcp = new TcpLink();
//		tcp.setMode(TcpLink.DATA_MODE);
//		tcp.setHost("192.168.4.213");
//		tcp.setPort(9999);
//		tcp.connect();
//		tcp.start();
//		
//		int i = 0;
//		while(true){
//			tcp.writeBytes(("string\n").getBytes());
//			System.err.println("string" + i++);
//			Thread.sleep(500);
//		}
//		tcp.destroy();
		
		
//		AppleCrawler apple = new AppleCrawler();
//		LtnCrawler ltn = new LtnCrawler();
//		ChinatimesCrawler chinatimes = new ChinatimesCrawler();
//		BusinessTimesCrawlwe businesstimes = new BusinessTimesCrawlwe();
//		DogNewsCrawler dog = new DogNewsCrawler();
//		ChinaElectronicsNewsCrawler chinaelectrontimes = new ChinaElectronicsNewsCrawler();
////		UdnCrawler udn = new UdnCrawler();
//		
//		String[] dateAry = new String[4];
//		dateProcess(args);
//		
//		while (Integer.parseInt(pastday) < Integer.parseInt(today)) {
//			dateAry[0] = String.format("%04d", C.get(Calendar.YEAR));
//			dateAry[1] = String.format("%02d", C.get(Calendar.MONTH) + 1);
//			dateAry[2] = String.format("%02d", C.get(Calendar.DAY_OF_MONTH));
//			pastday = dateAry[0] + dateAry[1] + dateAry[2];
//			
//			apple(apple, dateAry);
////			udn(udn, dateAry, tcp);
//			ltn(ltn, dateAry);
//			chinatimes(chinatimes, dateAry);
//			businesstimes(businesstimes, dateAry);
//			dogNews(dog, dateAry);
//			chinaelectrontimes(chinaelectrontimes, dateAry);
//			
//			C.add(C.DATE, Integer.parseInt("1"));
//		}
		
		for(Integer i = Integer.parseInt(args[0]); i <= Integer.parseInt(args[1]); i++){
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			pttgossiping(i);
			System.out.println("Page:" + i);
		}
		
		System.out.println("Finish!!!!!!");
	}
}
