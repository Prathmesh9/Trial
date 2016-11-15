package main;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import redis.clients.jedis.Jedis;





public class Main3 {

	public static void main(String[] args) {
		final Jedis redisConnect = new Jedis("localhost");
		Utility u =new Utility();
		Set<String> ex =new HashSet<String>();
			ex.add("a");
			ex.add("b");
			ex.add("c");
			ex.add("a");
			ex.add("d");
			ex.add("b");

			String str=u.toJson(ex);
			//System.out.println(str);
			
			redisConnect.hset("visitorid:100","100123", str);
			redisConnect.hset("visitorid:100","100124", str);
			redisConnect.hset("visitorid:100","100125", str);
			
			/*Map<String,String> ex1=redisConnect.hgetAll("visitorid:100");
			Set<Map.Entry<String, String>> ex2= ex1.entrySet();
			//obj.fromJson(str,new TypeToken<Set<String>>(){}.getType());
	
			ex2.forEach(x -> System.out.println(x));*/
			
			
			/*List<String> ex3=redisConnect.hmget("visitorid:100", "100123");
			ex3.add("e");
			ex3.add("f");
			ex3.add("a");
			ex3.add("z");
			ex3.forEach(x -> System.out.print(x+" "));*/
			
			/*Set<String> alphaSet = new HashSet<String>(ex3);
			Set<String> tp=new Hashset<String>(ex3);
			alphaSet.add("e");
			alphaSet.add("f");
			alphaSet.add("a");
			alphaSet.add("z");
			alphaSet.forEach(x -> System.out.println(x));*/
			
			
			
			List<String> ex2=redisConnect.hmget("visitorid:100", "1001281");
			ex2.forEach(x -> System.out.println(x));
			System.out.println(ex2.size());
			System.out.println(ex2.get(0)==null);
			Set<String>tp=u.toSet(ex2.get(0));
			System.out.println(tp.size());
			tp.forEach(x -> System.out.println(x));
			/*Set<String> alphaSet = new HashSet<String>(ex2);
			Set<String> ex3 =new HashSet<String>();
			tp.add("e");
			tp.add("f");
			tp.add("a");
			tp.add("z");*/
			//alphaSet.addAll(tp);
			tp.forEach(x -> System.out.print(x+ " "));
			
			/*JsonObject jo =new JsonObject();
			jo.addProperty("Name", "123456789");
			jo.addProperty("Category", "987456321");
			System.out.println(jo.toString());*/
			
			
	
	}

}
