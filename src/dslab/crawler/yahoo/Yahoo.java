package dslab.crawler.yahoo;

import dslab.crawler.pack.CrawlerPack;

public class Yahoo {

	public static void main(String[] args) {
		String api = "http://data.taipei/opendata/datalist/apiAccess?scope=resourceAquire&rid=55ec6d6e-dc5c-4268-a725-d04cc262172b";
		
		System.out.print(CrawlerPack.getFromRemote(api));
	}
}
