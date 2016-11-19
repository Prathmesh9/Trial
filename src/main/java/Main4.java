package main.java;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;

import model.RecModel;

public class Main4 {

	 /* Get actual class name to be printed on */
	   static Logger loger = Logger.getLogger(Main4.class.getName());
	public static void main(String[] args) {
		RecTry rt = new RecTry();

		
		FileReader fr;
		String[] entryData;
		String temp = "visitor_id";
		int i;
		// boolean flagV = true, flagC = true;
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
					rt.addVisitor(rm);

				}

				// if (flagV && flagC)
				entry = br.readLine();
				// else {
				// return error
				// System.out.println("Error");
				// }
			}
			
			rt.createContentIDMap();
			loger.debug("hdsajihdihasiredsi");
			loger.info("Chalu hoja");

			// rt.addVisitor(rm);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException ie) {
			// TODO Auto-generated catch block
			ie.printStackTrace();
		}

	}
}
