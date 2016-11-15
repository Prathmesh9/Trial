package main;

import java.util.List;
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
}
