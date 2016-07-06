package dslab.crawler.chinatimes;

public class DogNewsCrawler extends ChinatimesCrawler{
	
	@Override
	public void setUrl(){
		url = "http://www.chinatimes.com/history-by-date/" + pastdayOfYear + "-" + pastdayOfMonth + "-" + pastdayOfdate + "-2603";
	}
}
