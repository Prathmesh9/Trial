package main.java;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;

public class Main3 {

	public static void main(String[] args) {
		final Jedis redisConnect = new Jedis("localhost");
		Utility u = new Utility();
		Set<String> ex = new HashSet<String>();
		ex.add("a");
		ex.add("b");
		ex.add("c");
		ex.add("a");
		ex.add("d");
		ex.add("b");

		String str = u.toJson(ex);
		// System.out.println(str);

		redisConnect.hset("visitorid:100", "100123", str);
		redisConnect.hset("visitorid:100", "100124", str);
		redisConnect.hset("visitorid:100", "100125", str);

		/*
		 * Map<String,String> ex1=redisConnect.hgetAll("visitorid:100");
		 * Set<Map.Entry<String, String>> ex2= ex1.entrySet();
		 * //obj.fromJson(str,new TypeToken<Set<String>>(){}.getType());
		 */
		// ex2.forEach(x -> System.out.println(x));

		/*
		 * List<String> ex3=redisConnect.hmget("visitorid:100", "100123");
		 * ex3.add("e"); ex3.add("f"); ex3.add("a"); ex3.add("z"); ex3.forEach(x
		 * -> System.out.print(x+" "));
		 */

		/*
		 * Set<String> alphaSet = new HashSet<String>(ex3); Set<String> tp=new
		 * Hashset<String>(ex3); alphaSet.add("e"); alphaSet.add("f");
		 * alphaSet.add("a"); alphaSet.add("z"); alphaSet.forEach(x ->
		 * System.out.println(x));
		 */

		/*
		 * List<String> ex2=redisConnect.hmget("visitorid:100", "100123");
		 * System.out.println("Hget :"); ex2.forEach(x ->
		 * System.out.println(x)); //System.out.println(ex2.size());
		 * //System.out.println(ex2.get(0)==null);
		 * Set<String>tp=u.toSet(ex2.get(0)); System.out.println(tp.size());
		 * tp.forEach(x -> System.out.println(x)); Set<String> alphaSet = new
		 * HashSet<String>(ex2); Set<String> ex3 =new HashSet<String>();
		 * tp.add("e"); tp.add("f"); tp.add("a"); tp.add("z");
		 * //alphaSet.addAll(tp); tp.forEach(x -> System.out.print(x+ " "));
		 */
		/*
		 * JsonObject jo =new JsonObject(); jo.addProperty("Name", "123456789");
		 * jo.addProperty("Category", "987456321");
		 * System.out.println(jo.toString());
		 */

		redisConnect.select(1);
		String contentString = null;
		Set<String> temp = redisConnect.smembers("VISITOR_SET");

		String[] arr = (String[]) temp.toArray(new String[temp.size()]);
		// for (int i = 0; i < arr.length; i++) {
		Map<String, String> tmpMap = redisConnect.hgetAll("VISITOR_ID_VIEW_SET" + ":" + arr[0]);
		Set<String> viewSet = tmpMap.keySet();
		viewSet.forEach(x -> System.out.print(x + " "));
		System.out.println();
		Iterator<String> viewSetIterator = viewSet.iterator();
		while (viewSetIterator.hasNext()) {
			List<String> ex2 = redisConnect.hmget("VISITOR_ID_VIEW_SET" + ":" +arr[0], viewSetIterator.next());
			System.out.println(ex2.get(0));
			Set<String> visitorIDViewSet = u.toSet(ex2.get(0));
			//System.out.println(visitorIDViewSet.size());
			String[] visitorIDViewArr = (String[]) visitorIDViewSet.toArray(new String[visitorIDViewSet.size()]);
			Arrays.sort(visitorIDViewArr);
			if ((visitorIDViewSet.size()) > 1) {
				for (int i = 0; i < visitorIDViewArr.length; i++) {
					for (int j = 0; j < visitorIDViewArr.length; j++) {
						if (visitorIDViewArr[i] != visitorIDViewArr[j]) {

							contentString = redisConnect.hget("contentHash:" + visitorIDViewArr[i].substring(0, 3),
									visitorIDViewArr[i] + ":" + visitorIDViewArr[j]);
							if (contentString != null) {
								contentString = String.valueOf((Integer.parseInt(contentString) + 1));
								redisConnect.hset("contentHash:" + visitorIDViewArr[i].substring(0, 3),
										visitorIDViewArr[i] + ":" + visitorIDViewArr[j], contentString);
							} else{
								redisConnect.hset("contentHash:" + visitorIDViewArr[i].substring(0, 3),
										visitorIDViewArr[i] + ":" + visitorIDViewArr[j], "1");
							}
						}
					}
				}
			}
		}

		// }
		// System.out.println("Size : "+temp.size());
		System.out.println("Exit");
		redisConnect.disconnect();
		redisConnect.close();

	}
}
