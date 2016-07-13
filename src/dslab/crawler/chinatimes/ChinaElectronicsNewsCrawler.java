package dslab.crawler.chinatimes;

public class ChinaElectronicsNewsCrawler extends ChinatimesCrawler{
	
	@Override
	public void setUrl(){
		url = "http://www.chinatimes.com/history-by-date/" + pastdayOfYear + "-" + pastdayOfMonth + "-" + pastdayOfdate + "-2604";
		setSource("ChinaElectronicsNews");
	}
	
	@Override
	public void setSelectElement(){
		elemString = "div.page_container.stack.clear-fix.realtimenews_ad";
	}
}
