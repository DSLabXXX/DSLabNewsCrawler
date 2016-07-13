package dslab.crawler.chinatimes;

public class BusinessTimesCrawlwe extends ChinatimesCrawler{
	
	@Override
	public void setUrl(){
		url = "http://www.chinatimes.com/history-by-date/" + pastdayOfYear + "-" + pastdayOfMonth + "-" + pastdayOfdate + "-2602";
		setSource("BusinessTimes");
	}
}
