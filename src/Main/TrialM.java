package Main;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import model.RecModel;
import redis.clients.jedis.Jedis;

public class TrialM {

	public static void main(String[] args) {
		Jedis jed= new Jedis("localhost");
		System.out.println("Server runnig : "+jed.ping());
		RecModel rm = new RecModel("20067658","1002349338","Ultimate Pony Smash World","Adventure",1,0);	
		Map<String,String> user=new HashMap<String,String>();
		user.put("visitor_id", rm.getmVisitorID());
		user.put("content_id", rm.getmContentID());
		user.put("visitor_id_view", String.valueOf(rm.getmView()));
		user.put("visitor_id_download", String.valueOf(rm.getmDownload()));
		jed.hmset("visitor_id:"+rm.getmVisitorID(), user);
		Map<String,String> user1=jed.hgetAll("visitor_id:"+rm.getmVisitorID());
		System.out.println(user1.get("visitor_id"));
		int a =Integer.parseInt("100947ff64a88cda");
		System.out.println("Value = "+a);
		System.out.println("a = "+String.valueOf(a));
		jed.close();
	}

	public void CsvTest() {
		// TODO Auto-generated method stub
		String contentId = "20003503";
		FileReader fr;
		String[] entryData;
		int i;
		try {
			fr = new FileReader("contentTrenddata.csv");
			BufferedReader br = new BufferedReader(fr);
			boolean fl = false;
			String entry;
			entry = br.readLine();
			entryData = entry.split("\\,");

			for (i = 0; i < entryData.length; i++) {
				System.out.println(i + " " + entryData[i] + " ");
			}

			while (entry != null && fl == false) {
				System.out.println("*");
				entryData = entry.split("\\,");
				for (i = 0; i < entryData.length; i++) {
					entryData[i] = entryData[i].replace("\"", "");
				}
				/*
				 * for ( i = 0; i < entryData.length; i++) {
				 * System.out.print(entryData[i] + " "); }
				 */
				if (entryData[1].equals(contentId)) {
					for (i = 0; i < entryData.length; i++) {
						System.out.print(entryData[i] + " ");
						fl = true;
					}
				}
				entry = br.readLine();
				// System.out.println(entry);
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
}
