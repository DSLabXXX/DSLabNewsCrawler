package dslab.crawler.pack;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ScanFile {
	
	long lastProcessTime = 0;
	File s;
	Path path;
	String pathString;
	BasicFileAttributes attrs;
	Calendar c = Calendar.getInstance();	
	List<String> filePathList = new ArrayList<String>();
	
	public List<String> scan(String[] args) throws IOException{
		
		for(String arg: args){
			File rootFolder = new File(arg);
			if (!rootFolder.exists()) {
				System.out.println("Path err, it's not dir path..........");
				return null;
			} else {
				for (String i : rootFolder.list()) {
					pathString = arg + "/" + i;
					File f = new File(pathString);
					path = Paths.get(pathString);
					attrs = Files.readAttributes(path, BasicFileAttributes.class);
					c.setTimeInMillis(attrs.creationTime().toMillis());
					if (f.isFile()) {
						if (c.getTimeInMillis() > lastProcessTime || f.lastModified() > lastProcessTime) {
							filePathList.add(arg + "/" + i);
						}
					} else
						dir(new File(arg + "/" + i), filePathList);
				}
			}
		}
//		for(String filePath: filePathList){
//			System.out.println(filePath);
//		}
		return filePathList;
	}
	
	public List<String> scanDelete(String[] args) throws IOException{
		
		for(String arg: args){
			File rootFolder = new File(arg);
			if (!rootFolder.exists()) {
				System.out.println("Path err, it's not dir path..........");
				return null;
			} else {
				for (String i : rootFolder.list()) {
					pathString = arg + "/" + i;
					File f = new File(pathString);
					if (f.isFile()) {
						filePathList.add(arg + "/" + i);
					} else
						dir(new File(arg + "/" + i), filePathList);
				}
			}
		}
		return filePathList;
	}
	
	void dir(File son, List<String> fileNameList) throws IOException{

		String[] strs = son.list();
		for (int i = 0; i < strs.length; i++) {
			pathString = son + "/" + strs[i];
			s = new File(pathString);
			path = Paths.get(pathString);
			attrs = Files.readAttributes(path, BasicFileAttributes.class);
			c.setTimeInMillis(attrs.creationTime().toMillis());
			if (s.isDirectory()) {
				dir(s, fileNameList);
			} else {
				if (c.getTimeInMillis() > lastProcessTime || s.lastModified() > lastProcessTime) {
					fileNameList.add(son + "/" + strs[i]);
				}
			}
		}
	}
}
