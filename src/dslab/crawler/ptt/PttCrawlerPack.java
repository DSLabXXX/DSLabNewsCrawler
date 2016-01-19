package dslab.crawler.ptt;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.provider.http.HttpFileSystemConfigBuilder;
import dslab.crawler.pack.CrawlerPack;

public class PttCrawlerPack extends CrawlerPack{
	/**
	 * 透過 Apache Common VFS 套件 取回遠端的資源
	 *
	 * 能使用的協定參考：
	 * 
	 * @see "https://commons.apache.org/proper/commons-vfs/filesystems.html"
	 */
	public static String getFromRemote(String url) {
		try {
			// 透過 Apache VFS 取回指定的遠端資料
			FileSystemManager fsManager = VFS.getManager();
			FileSystemOptions fsOption = new FileSystemOptions();
			
			Cookie cookie = new Cookie("www.ptt.cc", "over18", "1", "/", null, false);
			
			HttpFileSystemConfigBuilder.getInstance().setCookies(fsOption, new Cookie[]{cookie});
			FileObject file = fsManager.resolveFile(url, fsOption);
			
			return IOUtils.toString(file.getContent().getInputStream(), "UTF-8");
			
		} catch (Exception ex) {
			if(ex.getMessage().contains("because it is a not a file.")){
				System.out.println(ex.getMessage());
				return null;
			}
			System.out.println(ex.getMessage());
//			ex.printStackTrace();
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return getFromRemote(url);
		}
	}
	/**
	 * 取得遠端格式為 XML 的資料
	 *
	 * @param url
	 *            required Apache Common VFS supported file systems
	 * @return Jsoup Document
	 */
	public static org.jsoup.nodes.Document getFromXml(String url) {
		// 取回資料，並轉化為XML格式
		String xml = getFromRemote(url);

		// Custom code here

		// 轉化為 Jsoup 物件
		return xmlToJsoupDoc(xml);
	}
}
