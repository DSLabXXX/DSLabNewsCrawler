import org.apache.commons.httpclient.Cookie;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import dslab.crawler.pack.CrawlerPack;
import dslab.crawler.pack.XTrustProvider;
import dslab.crawler.ptt.PttCrawlerPack;


/**
 * 雿輻 Apache Common VFS ����������
 */
public class Ch3Coz1 {


    public static void main(String[] args){

        // �����蝡��� API
        // @see http://taipeicity.github.io/traffic_realtime/
        // @see http://data.taipei/opendata/datalist/datasetMeta?oid=6556e1e8-c908-42d5-b984-b3f7337b139b
//		String api = "http://data.taipei/opendata/datalist/apiAccess?scope=resourceAquire&rid=55ec6d6e-dc5c-4268-a725-d04cc262172b";

		String url = "http://www.appledaily.com.tw/appledaily/article/headline/20120205/34002160/瘋啥?微軟新手機把PCXbox拉進來";
//		XTrustProvider.install();
		Document newsLinks = CrawlerPack.getFromXml(url);
		String[] newscontent = {"",""};
		
		for (Element elem : newsLinks.select("div")) {
			// 截取新聞標題、內容
			System.out.println(elem);
		}
        
        // call remote api
//		Cookie[] cookies = new Cookie[2];
//		cookies[0] = new Cookie("/","over18","1");
//		cookies[0].setDomain("www.ptt.cc");
//        System.out.print(PttCrawlerPack.getFromRemote(url));

    }
}
