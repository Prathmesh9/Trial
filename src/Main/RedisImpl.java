package Main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import model.RecModel;
import redis.clients.jedis.Jedis;

public class RedisImpl {
	/*
	 * public static final String REDIS_HOST="23.23.156.130"; public static
	 * final int REDIS_PORT=16379; public static final String
	 * REDIS_PASSWORD="p7mpbpvga2h96v60jdom3g07164";
	 */
	public static final String VISITOR_SET = "VISITOR_SET";
	public static final String VISITOR_ID_SET = "VISITOR_ID_SET";
	public static final String CONTENT_SET = "CONTENT_SET";
	public static final String CONTENT_ID_SET = "CONTENT_ID_SET";
	final Jedis redisConnect = new Jedis("localhost");

	public void redisStartup() {
		/*
		 * JedisShardInfo shardInfo = new JedisShardInfo(REDIS_HOST,
		 * REDIS_PORT); shardInfo.setPassword(REDIS_PASSWORD);
		 */
		// final Jedis subscriberJedis = new Jedis(shardInfo);

		FileReader fr;
		String[] entryData;
		String temp="visitor_id";
		int i;
		boolean flagV = true,flagC=true;
		try {
			fr = new FileReader("/home/bridgeit/Prathmesh/PrathmeshC/contentTrenddata.csv");
			BufferedReader br = new BufferedReader(fr);
			String entry;
			entry = br.readLine();
			entryData = entry.split("\\,");

			for (i = 0; i < entryData.length; i++) {
				System.out.print(i + " " + entryData[i] + " ");
			}
	
			while (entry != null) {
				// System.out.println("*");
				entryData = entry.split("\\,");
				for (i = 0; i < entryData.length; i++) {
					entryData[i] = entryData[i].replace("\"", "");
				}
				
				if(!(entryData[0].equals(temp))) {
					RecModel rm = new RecModel(entryData[0], entryData[1], entryData[2], entryData[3],
							entryData[4],entryData[5]);
					flagV = addVisitor(rm);
					flagC= addContent(rm);
					/*if (flagV && flagC)
						entry = br.readLine();
					else {
						// return error
						//System.out.println("Error");
					}*/
				}
				if (flagV && flagC)
					entry = br.readLine();
				else {
					// return error
					//System.out.println("Error");
				}
			}
			System.out.println();
			System.out.println("Exit");
			br.close();
			fr.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException ie) {
			// TODO Auto-generated catch block
			ie.printStackTrace();
		}

	}

	public boolean addVisitor(RecModel rm) {
		redisConnect.select(1);

		System.out.print(rm.getmVisitorID()+" "+rm.getmVisitorID().substring(0, 3));
		System.out.println();
		
		/*To create VISITOR_SET which has the keys for visitor_id*/
		redisConnect.sadd(VISITOR_SET, rm.getmVisitorID().substring(0, 3));
		
		/*To create set of VISITOR_SET keys and visitor_id as value*/
		redisConnect.sadd(VISITOR_ID_SET+":"+rm.getmVisitorID().substring(0, 3),rm.getmVisitorID());
		
		
		System.out.print(rm.getmVisitorID()+" "+rm.getmContentID());
		System.out.println();
		
		/*To create set with visitor_id as key and content_id as values*/
		redisConnect.sadd(rm.getmVisitorID(),rm.getmContentID());

		/*To create hash for visitor_id*/
		Map<String, String> user = new HashMap<String, String>();
		//user.put("content_id", rm.getmContentID());
		user.put("visitor_id_view", String.valueOf(rm.getmView()));
		user.put("visitor_id_download", String.valueOf(rm.getmDownload()));
		String res =redisConnect.hmset(rm.getmVisitorID()+":" + rm.getmContentID()+":"+rm.getmCategoryName(), user);
		String temp="OK";
		if(res.equals(temp))return true;
		else{System.out.println("Error Visitor"); return false;}
	}
	
	public boolean addContent(RecModel rm) {
		redisConnect.select(1);

		System.out.print(rm.getmContentID()+" "+rm.getmContentID().substring(0, 3));
		System.out.println();
		
		/*To create VISITOR_SET which has the keys for visitor_id*/
		redisConnect.sadd(CONTENT_SET, rm.getmContentID().substring(0, 3));
		
		/*To create set of VISITOR_SET keys and visitor_id as value*/
		redisConnect.sadd(CONTENT_ID_SET+":"+rm.getmContentID().substring(0, 3),rm.getmContentID());
		
		
		System.out.print(rm.getmContentID());
		System.out.println();
		
		/*To create set with visitor_id as key and content_id as values*/
		//redisConnect.sadd(rm.getmVisitorID(),rm.getmContentID());

		/*To create hash for visitor_id*/
		Map<String, String> user = new HashMap<String, String>();
		//user.put("content_id", rm.getmContentID());
		user.put("content_name", String.valueOf(rm.getmContentName()));
		user.put("content_categoryName", String.valueOf(rm.getmCategoryName()));
		String res=redisConnect.hmset(rm.getmContentID()+":"+rm.getmCategoryName(), user);
		String temp="OK";
		if(res.equals(temp))return true;
		else{System.out.println("Error Visitor"); return false;}
	}

}
