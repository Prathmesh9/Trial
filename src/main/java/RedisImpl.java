package main.java;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
	public static final String VISITOR_SET = "VISITOR_SET";
	public static final String CONTENT_MAP = "CONTENT_MAP";

	static Utility u = new Utility();
	final static Jedis redisConnect = new Jedis("localhost");
	final int visitor = 0, content = 1;
	final static String zero = "ZERO";
	final static String one = "1";
	final static String two = "2";

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
					addVisitorID(rm);
					System.out.println("add content");
					addContentID(rm);

				}

				if (flagV && flagC)
					entry = br.readLine();
				else {
					// return error
					// System.out.println("Error");
				}
			}
			System.out.println("Start map");
			createContentIDMap();
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

	public static void addVisitorID(RecModel rm) {
		redisConnect.select(1);

		System.out.println("Visitor_id : " + rm.getmVisitorID());
		System.out.print(rm.getmVisitorID() + " " + rm.getmVisitorID().substring(0, 3));
		System.out.println();
		System.out.println("Add Visitor view");
		redisConnect.sadd(VISITOR_SET, rm.getmVisitorID().substring(0, 3));
		addVisitorView(rm);
		System.out.println("Add Visitor download");
		addVisitorDownload(rm);
	}

	public static void addContentID(RecModel rm) {
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
		
		redisConnect.sadd(VISITOR_SET, rm.getmVisitorID().substring(0, 3));
		List<String> record = redisConnect.hmget(VISITOR_ID_VIEW_SET + ":" + rm.getmVisitorID().substring(0, 3),
				rm.getmVisitorID());

		Set<String> recordSet = new HashSet<String>();
		if (!(record.get(0) == null)) {
			System.out.println(record);
			if (Integer.parseInt(rm.getmView()) > 0) {
				recordSet = u.toSet(record.get(0));
				recordSet.forEach(x -> System.out.print(x + " "));
				addToViewSet(recordSet, rm.getmVisitorID(), rm.getmContentID());
			}

		} else {
			addToViewSet(recordSet, rm.getmVisitorID(), rm.getmContentID());
		}
	}

	public static void addToViewSet(Set<String> recordSet, String visitorID, String contentID) {
		String contentIDString = null;
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
				addToDownloadSet(recordSet, rm.getmVisitorID(), rm.getmContentID());
			}
		} else {
			addToDownloadSet(recordSet, rm.getmVisitorID(), rm.getmContentID());
		}
	}

	public static void addToDownloadSet(Set<String> recordSet, String visitorID, String contentID) {
		String contentIDString = null;
		try {
			recordSet.add(contentID);
		} catch (NullPointerException e) {
			System.out.println(e);
		}
		contentIDString = u.toJson(recordSet);
		redisConnect.hset(VISITOR_ID_DOWNLOAD_SET + ":" + visitorID.substring(0, 3), visitorID, contentIDString);
	}

	public static void createContentIDMap() {

		// redisConnect.select(1);

		createContentIDViewMap();
		createContentIDDownloadMap();

	}

	public static String[] getVisitorSet() {

		/* To import the visitor_id hash keys */
		Set<String> visitorSet = redisConnect.smembers(VISITOR_SET);
		String[] visitorSetArray = (String[]) visitorSet.toArray(new String[visitorSet.size()]);
		Arrays.sort(visitorSetArray);
		return visitorSetArray;
	}

	public static void createContentIDViewMap() {

		String contentString = null;

		String[] visitorSetArray = getVisitorSet();

		for (int i = 0; i < visitorSetArray.length; i++) {

			/* To import the map of each visitor_id key */
			Map<String, String> visitorViewMap = redisConnect.hgetAll("VISITOR_ID_VIEW_SET" + ":" + visitorSetArray[i]);
			Set<String> viewSet = visitorViewMap.keySet();
			viewSet.forEach(x -> System.out.print(x + " "));
			System.out.println();
			Iterator<String> viewSetIterator = viewSet.iterator();

			while (viewSetIterator.hasNext()) {

				/*
				 * To import json of content_id of view_set for particular
				 * visitor_id
				 */
				List<String> visitorIDViewList = redisConnect.hmget(VISITOR_ID_VIEW_SET + ":" + visitorSetArray[i],
						viewSetIterator.next());
				System.out.println("Visitor map" + visitorIDViewList.get(0));

				/*
				 * To convert json containing content_id of view_set for
				 * particular visitor_id to set
				 */
				Set<String> visitorIDViewSet = u.toSet(visitorIDViewList.get(0));
				System.out.println(visitorIDViewSet.size());

				/* Converting set to array and sorting it */
				String[] visitorIDViewArr = (String[]) visitorIDViewSet.toArray(new String[visitorIDViewSet.size()]);
				Arrays.sort(visitorIDViewArr);

				if ((visitorIDViewSet.size()) > 1) {
					for (int k = 0; k < visitorIDViewArr.length; k++) {
						for (int j = 0; j < visitorIDViewArr.length; j++) {
							if (visitorIDViewArr[k] != visitorIDViewArr[j]) {
								if (Integer.parseInt(visitorIDViewArr[k]) > 100) {
									contentString = redisConnect.hget(
											CONTENT_MAP + ":" + visitorIDViewArr[k].substring(0, 3),
											visitorIDViewArr[k] + ":" + visitorIDViewArr[j]);
								} else {
									contentString = redisConnect.hget(CONTENT_MAP + ":" + visitorIDViewArr[k],
											visitorIDViewArr[k] + ":" + visitorIDViewArr[j]);
								}
								if (contentString != null) {
									contentString = String.valueOf((Integer.parseInt(contentString) + 1));
									if (Integer.parseInt(visitorIDViewArr[k]) > 100) {
										redisConnect.hset(CONTENT_MAP + ":" + visitorIDViewArr[k].substring(0, 3),
												visitorIDViewArr[k] + ":" + visitorIDViewArr[j], contentString);
									} else {
										redisConnect.hset(CONTENT_MAP + ":" + visitorIDViewArr[k],
												visitorIDViewArr[k] + ":" + visitorIDViewArr[j], contentString);
									}
								} else {
									if (Integer.parseInt(visitorIDViewArr[k]) > 100) {
										redisConnect.hset(CONTENT_MAP + ":" + visitorIDViewArr[k].substring(0, 3),
												visitorIDViewArr[k] + ":" + visitorIDViewArr[j], one);
									} else {
										redisConnect.hset(CONTENT_MAP + ":" + visitorIDViewArr[k],
												visitorIDViewArr[k] + ":" + visitorIDViewArr[j], one);
									}
								}

							}
						}
					}
				} else {
					// Code for set with single content id
				}
			}
		}

	}

	public static void createContentIDDownloadMap() {

		String contentString = null;

		String[] visitorSetArray = getVisitorSet();

		for (int i = 0; i < visitorSetArray.length; i++) {

			/* To import the map of each visitor_id key */
			Map<String, String> visitorDownloadMap = redisConnect
					.hgetAll(VISITOR_ID_DOWNLOAD_SET + ":" + visitorSetArray[i]);
			Set<String> downloadSet = visitorDownloadMap.keySet();
			downloadSet.forEach(x -> System.out.print(x + " "));
			System.out.println();
			Iterator<String> downloadSetIterator = downloadSet.iterator();

			while (downloadSetIterator.hasNext()) {

				/*
				 * To import json of content_id of view_set for particular
				 * visitor_id
				 */
				List<String> visitorIDDownloadList = redisConnect
						.hmget(VISITOR_ID_DOWNLOAD_SET + ":" + visitorSetArray[i], downloadSetIterator.next());
				System.out.println("DOWNLOAD map : " + visitorIDDownloadList.get(0));

				/*
				 * To convert json containing content_id of view_set for
				 * particular visitor_id to set
				 */
				Set<String> visitorIDDownloadSet = u.toSet(visitorIDDownloadList.get(0));
				System.out.println(visitorIDDownloadSet.size());

				/* Converting set to array and sorting it */
				String[] visitorIDDownloadArr = (String[]) visitorIDDownloadSet
						.toArray(new String[visitorIDDownloadSet.size()]);
				Arrays.sort(visitorIDDownloadArr);

				if ((visitorIDDownloadSet.size()) > 1) {
					for (int k = 0; k < visitorIDDownloadArr.length; k++) {
						for (int j = 0; j < visitorIDDownloadArr.length; j++) {
							if (visitorIDDownloadArr[k] != visitorIDDownloadArr[j]) {
								if (Integer.parseInt(visitorIDDownloadArr[k]) > 100) {
									contentString = redisConnect.hget(
											CONTENT_MAP + ":" + visitorIDDownloadArr[k].substring(0, 3),
											visitorIDDownloadArr[k] + ":" + visitorIDDownloadArr[j]);
								} else {
									contentString = redisConnect.hget(CONTENT_MAP + ":" + visitorIDDownloadArr[k],
											visitorIDDownloadArr[k] + ":" + visitorIDDownloadArr[j]);
								}
								if (contentString != null) {
									contentString = String.valueOf((Integer.parseInt(contentString) + 2));
									if (Integer.parseInt(visitorIDDownloadArr[k]) > 100) {
										redisConnect.hset(CONTENT_MAP + ":" + visitorIDDownloadArr[k].substring(0, 3),
												visitorIDDownloadArr[k] + ":" + visitorIDDownloadArr[j], contentString);
									} else {
										redisConnect.hset(CONTENT_MAP + ":" + visitorIDDownloadArr[k],
												visitorIDDownloadArr[k] + ":" + visitorIDDownloadArr[j], contentString);
									}
								} else {
									if (Integer.parseInt(visitorIDDownloadArr[k]) > 100) {
										redisConnect.hset(CONTENT_MAP + ":" + visitorIDDownloadArr[k].substring(0, 3),
												visitorIDDownloadArr[k] + ":" + visitorIDDownloadArr[j], two);
									} else {
										redisConnect.hset(CONTENT_MAP + ":" + visitorIDDownloadArr[k],
												visitorIDDownloadArr[k] + ":" + visitorIDDownloadArr[j], two);
									}
								}
							}
						}
					}
				} else {
					// Code for set with single content id
				}
			}
		}

	}

}