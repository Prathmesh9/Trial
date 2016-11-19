package main.java;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

public class Utility {
	
	Gson obj = new Gson();
	public String toJson(Set<String> contentIDSet){
		return obj.toJson(contentIDSet);
	}
	
	public Set<String> toSet(String contentIDString){
		return obj.fromJson(contentIDString,new TypeToken<Set<String>>(){}.getType());
	}
	
	public List<String> toList(String contentIDString){
		return obj.fromJson(contentIDString,new TypeToken<List<String>>(){}.getType());
	}
	
	public String createContent(String contentName, String categoryName){
		JsonObject jObj = new JsonObject();
		jObj.addProperty("ContentName", contentName);
		jObj.addProperty("CategoryName", categoryName);
		return jObj.toString();
	}
	
	public Properties getProperties() {

		Properties prop = new Properties();
		try {

			String propFileName = "/home/bridgeit/Prathmesh/PrathmeshC/Trial/src/main/resource/resource.properties";
			// passing propFileName to the FileInputStream class object
			FileInputStream fis;

			fis = new FileInputStream(propFileName);
			if (fis != null) {
				// calling load method of Properties class
				prop.load(fis);
			} 
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return prop;
	}
}
