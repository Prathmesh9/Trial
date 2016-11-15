package main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.RecModel;
import redis.clients.jedis.Jedis;

public class RedisImpl {
	/*
	 * public static final String REDIS_HOST="23.23.156.130"; public static
	 * final int REDIS_PORT=16379; public static final String
	 * REDIS_PASSWORD="p7mpbpvga2h96v60jdom3g07164";
	 */
	public static final String VISITOR_ID_VIEW_SET = "VISITOR_ID_VIEW_SET";
	public static final String VISITOR_ID_DOWNLOAD_SET = "VISITOR_ID_DOWNLOAD_SET";
	public static final String CONTENT_ID_SET = "CONTENT_ID_SET";
	/*
	 * public static final String VISITOR_SET = "VISITOR_SET"; public static
	 * final String VISITOR_ID_SET = "VISITOR_ID_SET"; public static final
	 * String CONTENT_SET = "CONTENT_SET";
	 */

	static Utility u = new Utility();
	final static Jedis redisConnect = new Jedis("localhost");
	final int visitor = 0, content = 1;
	final static String zero = "ZERO";

	public void redisStartup() {
		/*
		 * JedisShardInfo shardInfo = new JedisShardInfo(REDIS_HOST,
		 * REDIS_PORT); shardInfo.setPassword(REDIS_PASSWORD);
		 */
		// final Jedis subscriberJedis = new Jedis(shardInfo);

		FileReader fr;
		String[] entryData;
		String temp = "visitor_id";
		int i;
		boolean flagV = true, flagC = true;
		try {
			fr = new FileReader("/home/bridgeit/contentDb.csv");
			BufferedReader br = new BufferedReader(fr);
			String entry;
			entry = br.readLine();
			entryData = entry.split("\\,");

			for (i = 0; i < entryData.length; i++) {
				System.out.print(i + " " + entryData[i] + " ");
			}

			while (entry != null) {
				entryData = entry.split("\\,");
				System.out.println(entry);
				for (i = 0; i < entryData.length; i++) {
					entryData[i] = entryData[i].replace("\"", "");
				}

				if (!(entryData[0].equals(temp))) {
					RecModel rm = new RecModel(entryData[0], entryData[1], entryData[2], entryData[3], entryData[4],
							entryData[5]);
					System.out.println("add visitor");
					addVisitor(rm);
					System.out.println("add content");
					addContent(rm);

				}
				if (flagV && flagC)
					entry = br.readLine();
				else {
					// return error
					// System.out.println("Error");
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

	public static void addVisitor(RecModel rm) {
		redisConnect.select(1);

		System.out.println("Visitor_id : " + rm.getmVisitorID());
		System.out.print(rm.getmVisitorID() + " " + rm.getmVisitorID().substring(0, 3));
		System.out.println();
		System.out.println("Add Visitor view");
		addVisitorView(rm);
		System.out.println("Add Visitor download");
		addVisitorDownload(rm);
	}

	public static void addContent(RecModel rm) {
		redisConnect.select(1);
		String contentString = null;
		System.out.println("Content_id : " + rm.getmContentID());
		System.out.println();
		List<String> record;
		if (Integer.parseInt(rm.getmContentID()) > 100) {
			record = redisConnect.hmget(CONTENT_ID_SET + ":" + rm.getmContentID().substring(0, 3), rm.getmContentID());
		} else {
			record = redisConnect.hmget(CONTENT_ID_SET + ":" + zero, rm.getmContentID());
		}
		if (record.get(0) == null) {
			contentString = u.createContent(rm.getmContentName(), rm.getmCategoryName());
			if (Integer.parseInt(rm.getmContentID()) > 100) {
				redisConnect.hset(CONTENT_ID_SET + ":" + rm.getmContentID().substring(0, 3), rm.getmContentID(),
						contentString);
			} else {
				redisConnect.hset(CONTENT_ID_SET + ":" + rm.getmContentID(), rm.getmContentID(), contentString);
			}
		}
	}

	public static void addVisitorView(RecModel rm) {
		List<String> record = redisConnect.hmget(VISITOR_ID_VIEW_SET + ":" + rm.getmVisitorID().substring(0, 3),
				rm.getmVisitorID());

		Set<String> recordSet = new HashSet<String>();
		if (!(record.get(0) == null)) {
			System.out.println(record);
			if (Integer.parseInt(rm.getmView()) > 0) {
				recordSet = u.toSet(record.get(0));
				recordSet.forEach(x -> System.out.print(x + " "));
				addView(recordSet, rm.getmVisitorID(), rm.getmContentID());
			}

		} else {
			addView(recordSet, rm.getmVisitorID(), rm.getmContentID());
		}
	}

	public static void addView(Set<String> recordSet, String visitorID, String contentID) {
		String contentIDString = null;
		System.out.println("add view set vontent id : " + contentID);
		try {
			recordSet.add(contentID);
		} catch (NullPointerException e) {
			System.out.println(e);
		}
		contentIDString = u.toJson(recordSet);
		redisConnect.hset(VISITOR_ID_VIEW_SET + ":" + visitorID.substring(0, 3), visitorID, contentIDString);
	}

	public static void addVisitorDownload(RecModel rm) {
		List<String> record = redisConnect.hmget(VISITOR_ID_DOWNLOAD_SET + ":" + rm.getmVisitorID().substring(0, 3),
				rm.getmVisitorID());
		Set<String> recordSet = new HashSet<String>();
		if (!(record.get(0) == null)) {
			if (Integer.parseInt(rm.getmDownload()) > 0) {
				recordSet = u.toSet(record.get(0));
				addDownload(recordSet, rm.getmVisitorID(), rm.getmContentID());
			}
		} else {
			addDownload(recordSet, rm.getmVisitorID(), rm.getmContentID());
		}
	}

	public static void addDownload(Set<String> recordSet, String visitorID, String contentID) {
		String contentIDString = null;
		recordSet.add(contentID);
		contentIDString = u.toJson(recordSet);
		redisConnect.hset(VISITOR_ID_DOWNLOAD_SET + ":" + visitorID.substring(0, 3), visitorID, contentIDString);
	}

}