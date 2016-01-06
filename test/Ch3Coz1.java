import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import dslab.crawler.pack.CrawlerPack;


/**
 * 雿輻 Apache Common VFS ����������
 */
public class Ch3Coz1 {


    public static void main(String[] args){

        // �����蝡��� API
        // @see http://taipeicity.github.io/traffic_realtime/
        // @see http://data.taipei/opendata/datalist/datasetMeta?oid=6556e1e8-c908-42d5-b984-b3f7337b139b
//		String api = "http://data.taipei/opendata/datalist/apiAccess?scope=resourceAquire&rid=55ec6d6e-dc5c-4268-a725-d04cc262172b";

		String url = "http://www.chinatimes.com/history-by-date/2010-01-02-2601?page=5";
		Document newsLinks = CrawlerPack.getFromXml(url);
		for (Element elem : newsLinks.select("div.pagination.clear-fix").select("li")) {
			System.out.println(elem);
		}

		String url2 = "http://www.chinatimes.com/history-by-date/2010-01-03-2601?page=2";
		newsLinks = CrawlerPack.getFromXml(url2);

		for (Element elem : newsLinks.select("div.pagination.clear-fix").select("li")) {
			System.err.println(elem);
		}

		String url3 = "http://www.chinatimes.com/history-by-date/2010-01-02-2601?page=2";
		newsLinks = CrawlerPack.getFromXml(url3);

		for (Element elem : newsLinks.select("div.pagination.clear-fix").select("li")) {
			System.out.println(elem);
		}
        
        // call remote api
//        System.out.print(CrawlerPack.getFromRemote(api));

    }
}
