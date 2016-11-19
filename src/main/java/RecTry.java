package main.java;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import model.RecModel;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class RecTry {

	final static Jedis redisConnect = new Jedis("localhost");
	static Utility u = new Utility();
	Properties prop = u.getProperties();
	static Logger log =Logger.getLogger(RecTry.class); 

	public void addVisitor(RecModel rm) {
		addVisitorView(rm);
		addVisitorDownload(rm);
		addContentID(rm);
	}

	public void createContentIDMap() {
		System.out.println("view set");
		createContentIDMapView("VISITOR_ID_VIEW_SET","one");
		System.out.println("download set");
		createContentIDMapView("VISITOR_ID_DOWNLOAD_SET","two");
		//createContentIDMapDownload();
	}

	/*
	 * To check fetch visitor view set from redis. If set exist add new visitor
	 * to set otherwise create new set.
	 */
	public void addVisitorView(RecModel rm) {

		redisConnect.sadd(prop.getProperty("VISITOR_SET"), rm.getmVisitorID().substring(0, 3));

		List<String> record = redisConnect.hmget(
				prop.getProperty("VISITOR_ID_VIEW_SET") + ":" + rm.getmVisitorID().substring(
						Integer.parseInt(prop.getProperty("low")), Integer.parseInt(prop.getProperty("high"))),
				rm.getmVisitorID());

		Set<String> recordSet = new HashSet<String>();
		if (!(record.get(Integer.parseInt(prop.getProperty("low"))) == null)) {
			System.out.println(record);
			if (Integer.parseInt(rm.getmView()) > 0) {
				recordSet = u.toSet(record.get(Integer.parseInt(prop.getProperty("low"))));
				addToViewSet(recordSet, rm.getmVisitorID(), rm.getmContentID());
			}

		} else {
			addToViewSet(recordSet, rm.getmVisitorID(), rm.getmContentID());
		}
	}

	/* Add to visitor_view_set */
	private void addToViewSet(Set<String> recordSet, String visitorID, String contentID) {
		String contentIDString = null;
		try {
			recordSet.add(contentID);
		} catch (NullPointerException e) {
			log.debug(e);
			log.info(e);
		}
		contentIDString = u.toJson(recordSet);
		redisConnect.hset(
				prop.getProperty("VISITOR_ID_VIEW_SET") + ":" + visitorID.substring(
						Integer.parseInt(prop.getProperty("low")), Integer.parseInt(prop.getProperty("high"))),
				visitorID, contentIDString);
	}

	/*
	 * To check fetch visitor download set from redis. If set exist add new
	 * visitor to set otherwise create new set.
	 */
	public void addVisitorDownload(RecModel rm) {

		redisConnect.sadd(prop.getProperty("VISITOR_SET"), rm.getmVisitorID().substring(0, 3));

		List<String> record = redisConnect.hmget(
				prop.getProperty("VISITOR_ID_DOWNLOAD_SET") + ":" + rm.getmVisitorID().substring(
						Integer.parseInt(prop.getProperty("low")), Integer.parseInt(prop.getProperty("high"))),
				rm.getmVisitorID());
		Set<String> recordSet = new HashSet<String>();
		if (!(record.get(Integer.parseInt(prop.getProperty("low"))) == null)) {
			if (Integer.parseInt(rm.getmDownload()) > 0) {
				recordSet = u.toSet(record.get(Integer.parseInt(prop.getProperty("low"))));
				addToDownloadSet(recordSet, rm.getmVisitorID(), rm.getmContentID());
			}
		} else {
			addToDownloadSet(recordSet, rm.getmVisitorID(), rm.getmContentID());
		}
	}

	/* Add to visitor_download_set */
	private void addToDownloadSet(Set<String> recordSet, String visitorID, String contentID) {
		String contentIDString = null;
		try {
			recordSet.add(contentID);
		} catch (NullPointerException e) {
			log.debug(e);
			log.info(e);
		}
		contentIDString = u.toJson(recordSet);
		redisConnect.hset(
				prop.getProperty("VISITOR_ID_DOWNLOAD_SET") + ":" + visitorID.substring(
						Integer.parseInt(prop.getProperty("low")), Integer.parseInt(prop.getProperty("high"))),
				visitorID, contentIDString);
	}

	/* To create content_id_set for */
	public void addContentID(RecModel rm) {
		// redisConnect.(1);
		String contentString = null;
		System.out.println("Content_id : " + rm.getmContentID());
		System.out.println();
		List<String> record;
		if (Integer.parseInt(rm.getmContentID()) > 100) {
			record = redisConnect.hmget(
					prop.getProperty("CONTENT_ID_SET") + ":" + rm.getmContentID().substring(
							Integer.parseInt(prop.getProperty("low")), Integer.parseInt(prop.getProperty("high"))),
					rm.getmContentID());
		} else {
			record = redisConnect.hmget(prop.getProperty("CONTENT_ID_SET") + ":" + prop.getProperty("zero"),
					rm.getmContentID());
		}
		if (record.get(Integer.parseInt(prop.getProperty("low"))) == null) {
			contentString = u.createContent(rm.getmContentName(), rm.getmCategoryName());
			if (Integer.parseInt(rm.getmContentID()) > 100) {
				redisConnect.hset(
						prop.getProperty("CONTENT_ID_SET") + ":" + rm.getmContentID().substring(
								Integer.parseInt(prop.getProperty("low")), Integer.parseInt(prop.getProperty("high"))),
						rm.getmContentID(), contentString);
			} else {
				redisConnect.hset(prop.getProperty("CONTENT_ID_SET") + ":" + rm.getmContentID(), rm.getmContentID(),
						contentString);
			}
		}
	}

	/* Create content map for recommendation using visitor_views_set */
	public void createContentIDMapView(String setName,String value) {

		try {
			/* To import the visitor_id hash keys */
			Set<String> visitorSet = redisConnect.smembers(prop.getProperty("VISITOR_SET"));
			String contentString = null;
			String[] visitorSetArray = (String[]) visitorSet.toArray(new String[visitorSet.size()]);
			String[] contentIDSetArr = null;
			Arrays.sort(visitorSetArray);

			for (int i = 0; i < visitorSetArray.length; i++) {

				/* To import the map of each visitor_id key */
				Map<String, String> visitorMap = redisConnect
						.hgetAll(prop.getProperty(setName) + ":" + visitorSetArray[i]);
				Set<String> mapKeySet = visitorMap.keySet();
				// downloadSet.forEach(x -> System.out.print(x + " "));
				System.out.println();
				Iterator<String> mapKeySetIterator = mapKeySet.iterator();

				while (mapKeySetIterator.hasNext()) {

					/*
					 * To import json of content_id of view_set for particular
					 * visitor_id
					 */
					List<String> contentIDList = redisConnect.hmget(
							prop.getProperty(setName) + ":" + visitorSetArray[i],
							mapKeySetIterator.next());

					/*
					 * To convert json containing content_id of view_set for
					 * particular visitor_id to set
					 */
					Set<String> contentIDSet = u.toSet(contentIDList.get(Integer.parseInt(prop.getProperty("low"))));

					/* Converting set to array and sorting it */
					contentIDSetArr = (String[]) contentIDSet.toArray(new String[contentIDSet.size()]);
					Arrays.sort(contentIDSetArr);
					
					/*Creating content_id-content_id map. If present update the current values else create new entry*/
					if ((contentIDSetArr.length) > 1) {
						for (int k = 0; k < contentIDSetArr.length; k++) {
							for (int j = 0; j < contentIDSetArr.length; j++) {
								if (contentIDSetArr[k] != contentIDSetArr[j]) {
									contentString = getContentMap(contentIDSetArr[k], contentIDSetArr[j]);
									if (contentString != null) {
										contentString = String.valueOf((Integer.parseInt(contentString) + 1));
										setContentMap(contentIDSetArr[k], contentIDSetArr[j], contentString);
									} else {
										setContentMap(contentIDSetArr[k], contentIDSetArr[j], prop.getProperty(value));
									}

								}
							}
						}
					} else {
						// Code for set with single content id
					}

				}
			}
		} catch (NullPointerException e) {
			log.debug(e);
			log.info(e);
		} catch (JedisConnectionException e) {
			log.debug(e);
			log.info(e);
		}
	}

	/* Create content map for recommendation using visitor_download_set */
	public void createContentIDMapDownload() {

		/*
		 * Fetching view_set or download_set from redis and converting set to
		 * array.
		 */

		/* To import the visitor_id hash keys */
		try {
			Set<String> visitorSet = redisConnect.smembers(prop.getProperty("VISITOR_SET"));
			String[] visitorSetArray = (String[]) visitorSet.toArray(new String[visitorSet.size()]);
			String[] contentIDSetArr = null;
			String contentString = null;
			Arrays.sort(visitorSetArray);

			for (int i = 0; i < visitorSetArray.length; i++) {

				/* To import the map of each visitor_id key */
				Map<String, String> visitorMap = redisConnect
						.hgetAll(prop.getProperty("VISITOR_ID_DOWNLOAD_SET") + ":" + visitorSetArray[i]);
				Set<String> mapKeySet = visitorMap.keySet();
				Iterator<String> mapKeySetIterator = mapKeySet.iterator();

				while (mapKeySetIterator.hasNext()) {

					/*
					 * To import json of content_id of view_set for particular
					 * visitor_id
					 */
					List<String> contentIDList = redisConnect.hmget(
							prop.getProperty("VISITOR_ID_DOWNLOAD_SET") + ":" + visitorSetArray[i],
							mapKeySetIterator.next());

					/*
					 * To convert json containing content_id of view_set for
					 * particular visitor_id to set
					 */
					Set<String> contentIDSet = u.toSet(contentIDList.get(Integer.parseInt(prop.getProperty("low"))));
					
					/* Converting set to array and sorting it */
					contentIDSetArr = (String[]) contentIDSet.toArray(new String[contentIDSet.size()]);
					Arrays.sort(contentIDSetArr);
					
					/*Creating content_id-content_id map. If present update the current values else create new entry*/
					if ((contentIDSetArr.length) > 1) {
						for (int k = 0; k < contentIDSetArr.length; k++) {
							for (int j = 0; j < contentIDSetArr.length; j++) {
								if (contentIDSetArr[k] != contentIDSetArr[j]) {
									contentString = getContentMap(contentIDSetArr[k], contentIDSetArr[j]);
									if (contentString != null) {
										contentString = String.valueOf((Integer.parseInt(contentString) + 2));
										setContentMap(contentIDSetArr[k], contentIDSetArr[j], contentString);

									} else {
										setContentMap(contentIDSetArr[k], contentIDSetArr[j], prop.getProperty("two"));
									}
								}
							}
						}
					} else {
						// Code for set with single content id
					}
				}
			}
		} catch (NullPointerException e) {
			log.debug(e);
			log.info(e);
		} catch (JedisConnectionException e) {
			log.debug(e);
			log.info(e);
		}
	}

	/* Add to content map */
	private void setContentMap(String contentID1, String contentID2, String contentString) {
		if (Integer.parseInt(contentID1) > 100) {
			redisConnect.hset(
					prop.getProperty("CONTENT_MAP") + ":"
							+ contentID1.substring(Integer.parseInt(prop.getProperty("low")),
									Integer.parseInt(prop.getProperty("high"))),
					contentID1 + ":" + contentID2, contentString);
		} else {
			redisConnect.hset(prop.getProperty("CONTENT_MAP") + ":" + contentID1, contentID1 + ":" + contentID2,
					contentString);
		}

	}

	/* Fetch from content map */
	private String getContentMap(String contentID1, String contentID2) {
		String contentString;
		if (Integer.parseInt(contentID1) > 100) {
			contentString = redisConnect.hget(
					prop.getProperty("CONTENT_MAP") + ":" + contentID1.substring(
							Integer.parseInt(prop.getProperty("low")), Integer.parseInt(prop.getProperty("high"))),
					contentID1 + ":" + contentID2);
		} else {
			contentString = redisConnect.hget(prop.getProperty("CONTENT_MAP") + ":" + contentID1,
					contentID1 + ":" + contentID2);
		}
		return contentString;
	}

}
