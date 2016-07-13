package dslab.crawler.pack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
	public String newsCategory;
	public String elemString;
	public String dirPath;
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
	
	public void processNewsContain(String[] newscontent) throws IOException, JSONException {
		String filePath =  newscontent[1] + newscontent[4];
		if (newscontent[4].equals("")) {
			filePath =  newscontent[1] + "---------No title---------" + new Random().nextInt(10000000) + ".txt";
			saveNewsFile(createJsonFile(newscontent), dirPath + "/" + filePath);
		} else {
			filePath.replaceAll("[\\\\/:*?\"<>| ]", "-");
			try {
				saveNewsFile(createJsonFile(newscontent), dirPath + "/" + filePath.replace("/", "-"));
			} catch (IOException e) {
				filePath = newscontent[1] + "---------Get Title Error---------" + new Random().nextInt(10000000) + ".txt";
				saveNewsFile(createJsonFile(newscontent), dirPath + "/" + filePath.replace("/", "-"));
				e.printStackTrace();
			}
		}
	}
	
	private void ImgUrlTransferJson(JSONObject jsonObj, String cnt7) throws JSONException{
		if(cnt7 != null){
			JSONArray imgList = new JSONArray();
			for(String img: cnt7.split("====")){
				imgList.put(img);
			}
			jsonObj.put("ImgUrl", imgList);
		}
		else{
			jsonObj.put("ImgUrl", "");
		}
	}
	
	private void PushTransferJson(JSONObject jsonObj, String cnt17) throws JSONException{
		JSONArray pushList = new JSONArray();
		if(cnt17 != null){
			String tmp = "";
			for(String p: cnt17.split("\n")){
				JSONObject pushObj = new JSONObject();
				if (p.split("hl push-taghl push-taghl push-tag").length < 2)
					break;
				pushObj.put("pushTag", p.split("hl push-taghl push-taghl push-tag")[0]);
				tmp = p.split("hl push-taghl push-taghl push-tag")[1];
				pushObj.put("userId", tmp.split("f3 hl push-useridf3 hl push-useridf3 hl push-userid")[0]);
				tmp = tmp.split("f3 hl push-useridf3 hl push-useridf3 hl push-userid")[1];
				pushObj.put("pushContent", tmp.split("f3 push-contentf3 push-contentf3 push-content")[0]);
				pushObj.put("ipDatetime", tmp.split("f3 push-contentf3 push-contentf3 push-content")[1]);
				pushList.put(pushObj);
			}
			jsonObj.put("Push", pushList);
		}
		else{
			jsonObj.put("Push", "");
		}
	}
	
	protected JSONObject createJsonFile(String[] cnt) throws JSONException, IOException{
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("URL", cnt[0]);
		jsonObj.put("Date", cnt[1]);
		jsonObj.put("Source", cnt[2]);
		jsonObj.put("Category", cnt[3]);
		jsonObj.put("Title", cnt[4]);
		jsonObj.put("Text", cnt[5]);
		jsonObj.put("KeyWord", cnt[6]);
		ImgUrlTransferJson(jsonObj, cnt[7]);
		jsonObj.put("HDFSurl", cnt[8]);
		jsonObj.put("SplitText", cnt[9]);
		jsonObj.put("Location", cnt[10]);
		jsonObj.put("People", cnt[11]);
		jsonObj.put("Org", cnt[12]);
		jsonObj.put("Event", cnt[13]);
		jsonObj.put("Value", cnt[14]);
		jsonObj.put("Author", cnt[15]);
		jsonObj.put("AuthorIp", cnt[16]);
		PushTransferJson(jsonObj, cnt[17]);
		jsonObj.put("LinkUrl", cnt[18]);
		
		return jsonObj;
	}
	
	protected void saveNewsFile(JSONObject jsonObj, String path){
		 
		try (FileWriter file = new FileWriter(path)) {
			file.write(jsonObj.toString());
			System.out.println("Successfully copied JSON object to file: " + path);
		} catch (IOException e) {
			System.err.println("Fail to save JSON file: " + path);
		}
	}

	public void transferFail(String dirPath, int num, String url) throws IOException {
		File f = null;
		OutputStream out = null;
		System.err.println("Tranfer Fail");
		f = new File(dirPath + "/Tranfer Fail" + num);
		out = new FileOutputStream(f.getAbsolutePath());
		out.write(url.getBytes());
		out.close();
	}

	public void processNewsList(String newsName) throws IOException, JSONException {
		String category = null;
		String url = null;
		String date = null;
		File dir;

		for (int i = 0; i < newsTagLinkList.size(); i++) {

			category = newsTagLinkList.get(i)[0];
			url = newsTagLinkList.get(i)[1].toString();
			date = newsTagLinkList.get(i)[2].toString();

			dirPath = newsName + category;
			dir = new File(dirPath);
			dir.mkdirs();

			Document contain = CrawlerPack.start().getFromXml(url);

			for (int j = 0; j < 5; j++) {
				if (contain != null) {
					customerProcessNewsList(category, url, date, dirPath, contain);
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
			throws IOException, JSONException{

	}

	public void customerRunProcess() {

	}

}
