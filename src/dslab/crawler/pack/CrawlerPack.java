/**
 * Copyright 2015-2016 Abola Lee
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dslab.crawler.pack;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.*;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.http.HttpFileSystemConfigBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.parser.PrefixXmlTreeBuilder;
import org.mozilla.universalchardet.UniversalDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class CrawlerPack {

    static Logger log = LoggerFactory.getLogger(CrawlerPack.class);

    static StandardFileSystemManager fileSystem ;

    static{
        // create a Self-signed Server Certificates
        // for pass SSL
        XTrustProvider.install();

        try {
            fileSystem = new StandardFileSystemManager();
            fileSystem.setCacheStrategy(CacheStrategy.ON_CALL);
            fileSystem.init();
        }catch(FileSystemException fse){
            // ignore
        }
    }

    static CrawlerPack defaultCrawler ;

    /**
     * Create a CrawlerPack instance
     *
     * @return CrawlerPack
     */
    public static CrawlerPack start(){
        if (null == defaultCrawler)
            defaultCrawler = new CrawlerPack();
        return defaultCrawler;
    }

    private List<Cookie> cookies = new ArrayList<>();


    /**
     * Creates a cookie with the given name and value.
     *
     * @param name    the cookie name
     * @param value   the cookie value
     * @return CrawlerPack
     */
    public CrawlerPack addCookie(String name, String value){
        if( null == name ) {
            log.warn("Cookie name null.");
            return this;
        }

        cookies.add( new Cookie("", name, value) );

        return this;
    }

    /**
     * Creates a cookie with the given name, value, domain attribute,
     * path attribute, expiration attribute, and secure attribute
     *
     * @param name    the cookie name
     * @param value   the cookie value
     * @param domain  the domain this cookie can be sent to
     * @param path    the path prefix for which this cookie can be sent
     * @param expires the {@link Date} at which this cookie expires,
     *                or <tt>null</tt> if the cookie expires at the end
     *                of the session
     * @param secure if true this cookie can only be sent over secure
     * connections
     *
     */
    public CrawlerPack addCookie(String domain, String name, String value,
                  String path, Date expires, boolean secure) {
        if( null == name ) {
            log.warn("Cookie name null.");
            return this;
        }

        cookies.add(new Cookie(domain, name, value, path, expires, secure));
        return this;
    }

    /**
     * Return a Cookie array
     * and auto importing domain and path when domain was empty.
     *
     * @param uri required Apache Common VFS supported file systems and response JSON format content.
     * @return Cookie[]
     */
    Cookie[] getCookies(String uri){
        if( null == cookies || 0 == cookies.size()) return null;

        for(Cookie cookie: cookies){

            if("".equals(cookie.getDomain())){
                String domain = uri.replaceAll("^.*:\\/\\/([^\\/]+)[\\/]?.*$", "$1");
//                System.out.println(domain);
                cookie.setDomain(domain);
                cookie.setPath("/");
                cookie.setExpiryDate(null);
                cookie.setSecure(false);
            }
        }

        return cookies.toArray(new Cookie[cookies.size()]);
    }

    /**
     * Clear all cookies
     */
    void clearCookies(){
        cookies = new ArrayList<>();
    }

    public org.jsoup.nodes.Document getFromJson(String uri) throws JSONException{

        String json = getFromRemote(uri);

        String xml  = jsonToXml(json);

        return xmlToJsoupDoc(xml);
    }
    
    public org.jsoup.nodes.Document getFromHtml(String uri){
        String html = getFromRemote(uri);

        return htmlToJsoupDoc(html);
    }

    public org.jsoup.nodes.Document getFromXml(String uri){
    	
        String xml = getFromRemote(uri);

        return xmlToJsoupDoc(xml);
    }

    public String jsonToXml(String json) throws JSONException{
        String xml = "";
        if ( "[".equals( json.substring(0,1) ) ){
            xml = XML.toString(new JSONArray(json), "row");
        }else{
            xml = XML.toString(new JSONObject(json));
        }

        return xml;
    }
    public String getFromRemote(String uri){

        // clear cache
        fileSystem.getFilesCache().close();

        String remoteContent ;
        String remoteEncoding = "utf-8";

        log.debug("Loading remote URI:" + uri);
        FileContent fileContent ;

        try {
            // set cookie if cookies set
            if (0 < this.cookies.size()) {
                FileSystemOptions fsOptions = new FileSystemOptions();
                HttpFileSystemConfigBuilder.getInstance().setCookies(fsOptions, getCookies(uri));
                fileContent = fileSystem.resolveFile(uri, fsOptions).getContent();
            } else
                fileContent = fileSystem.resolveFile(uri).getContent();

            // 2016-03-22 only pure http/https auto detect encoding
            if ("http".equalsIgnoreCase(uri.substring(0, 4))) {
                fileContent.getSize();  // pass a bug {@link https://issues.apache.org/jira/browse/VFS-427}
                remoteEncoding = fileContent.getContentInfo().getContentEncoding();
            }


            if (null == remoteEncoding) remoteEncoding = "utf-8";

            if (!"utf".equalsIgnoreCase(remoteEncoding.substring(0, 3))) {
                log.debug("remote content encoding: " + remoteEncoding);

                // force charset encoding if setRemoteEncoding set
                if (!"utf".equalsIgnoreCase(encoding.substring(0, 3))) {
                    remoteEncoding = encoding;
                } else {
                    // auto detecting encoding
                    remoteEncoding = detectCharset(IOUtils.toByteArray(fileContent.getInputStream()));
                    log.info("real encoding: " + remoteEncoding);
                }
            }

            // 2016-02-29 fixed
            remoteContent = IOUtils.toString(fileContent.getInputStream(), remoteEncoding);

        } catch(FileSystemException fse){
            log.warn(fse.getMessage());
            remoteContent =null;
        }catch(IOException ioe){
            // return empty
            log.warn(ioe.getMessage());
            remoteContent =null;
        }catch(StringIndexOutOfBoundsException stre){
            log.warn("uri: " + uri );
            log.warn(stre.getMessage());
            remoteContent =null;
        }

        clearCookies();

        // any exception will return "null"
        return remoteContent;
    }

    public org.jsoup.nodes.Document htmlToJsoupDoc(String html){

        Document jsoupDoc = Jsoup.parse(html, "UTF-8", Parser.htmlParser() );
        jsoupDoc.charset(StandardCharsets.UTF_8);

        return jsoupDoc;
    }

    final static String prefix = "all-lower-case-prefix";

    public org.jsoup.nodes.Document xmlToJsoupDoc(String xml){
		if (xml != null) {
			xml = xml.replaceAll("<([^A-Za-z\\/! ][^\\/>]*)>", "<" + prefix.toLowerCase() + "$1>")
					.replaceAll("<\\/([^A-Za-z\\/ ][^\\/>]*)>", "</" + prefix.toLowerCase() + "$1>");

			Document jsoupDoc = Jsoup.parse(xml, "", new Parser(new PrefixXmlTreeBuilder(prefix.toLowerCase())));
			jsoupDoc.charset(StandardCharsets.UTF_8);

			return jsoupDoc;
		} else
			return null;
    }

    private String encoding = "utf-8";

    public CrawlerPack setRemoteEncoding(String encoding){
        this.encoding = encoding;
        return this;
    }

    private String detectCharset(byte[] content){
        return detectCharset(content, 0);
    }

    final Integer detectBuffer = 1000;

    /**
     * Detecting real content encoding
     * @param content
     * @param offset
     * @return real charset encoding
     */
    private String detectCharset(byte[] content, Integer offset){
        log.debug("offset: " + offset);

        // detect failed
        if( offset > content.length ) return null;

        UniversalDetector detector = new UniversalDetector(null);
        detector.handleData(content, offset, content.length - offset > detectBuffer ? detectBuffer : content.length - offset);
        detector.dataEnd();

        String detectEncoding = detector.getDetectedCharset();

        return null==detectEncoding?detectCharset(content,offset+detectBuffer):detectEncoding;
    }

}
