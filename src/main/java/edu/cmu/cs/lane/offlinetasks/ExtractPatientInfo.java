package edu.cmu.cs.lane.offlinetasks;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

public class ExtractPatientInfo {
	Hashtable<String, String> rsidPos = new Hashtable<String, String>();
	DB db;
	DBCollection collection;

	public void instantiateMongo() throws UnknownHostException {

		// or//expression.ml.cmu.edu
		MongoClient mongoClient = new MongoClient("localhost", 27017);

		db = mongoClient.getDB("life");
		Set<String> colls = db.getCollectionNames();
		collection = db.getCollection("patients");

		for (String s : colls) {
			System.out.println(s);

		}
	}

	public void insertIntoMongo(String json) {
		DBObject dbObject = (DBObject) JSON.parse(json);
		// TODO: add update command if patient already exists
		collection.insert(dbObject);

	}

	public void readLookupTable(String location, int chromosome)
			throws IOException {
		BufferedReader bfr = new BufferedReader(new FileReader(location + "chr"
				+ chromosome + ".filtered.txt"));
		String str;
		while ((str = bfr.readLine()) != null) {
			String rec[] = str.trim().split("[\\t]");
			rsidPos.put(rec[1], rec[0]);
		}
		System.out.println(rsidPos.size());
		bfr.close();

	}

	public void dump(String location, int chromosome) throws IOException {
		BufferedReader bfr = new BufferedReader(new FileReader(location
				+ chromosome + ".txt"));
		HashMap<String, HashMap<String, String>> hs = new HashMap<String, HashMap<String, String>>();
		String str;
		str = bfr.readLine();
		String rec[] = str.trim().split("[\\t]");
		HashMap<String, String> p = new HashMap<String, String>();
		ArrayList<String> a = new ArrayList<String>();
		for (int i = 1; i < 10; i++) {
			hs.put(rec[i], new HashMap<String, String>());
			a.add(rec[i]);
		}
		String rsid;
		System.out.println(java.lang.Runtime.getRuntime().maxMemory());

		while ((str = bfr.readLine()) != null) {
			rec = str.trim().substring(0, 100).split("[\\t]");
			rsid = rec[0];
			rsid = rsidPos.get(rsid);
			if (rsid == null) {
				continue;
			}
			rsid = rsid.replace('.', '-');
			for (int i = 1; i < 9; i++) {

				if (rec[i].equals("0") || rec[i].equals("NaN")) {
					continue;
				}
				// p = hs.get(a.get(i-1));
				// p.put(rsid,rec[i]);
				hs.get(a.get(i - 1)).put(rsid, rec[i]);
				// hs.put(a.get(i-1), );

			}

		}

		bfr.close();
		for (int i = 0; i < 9; i++) {

			String json = "{'" + a.get(i) + "' : {'";
			// records' : 99, 'index' : 'vps_index1', 'active' : 'true'}}}";
			p = hs.get(a.get(i));
			if (p.isEmpty()) {
				continue;
			}
			Iterator<Entry<String, String>> it = p.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, String> pairs = (Map.Entry<String, String>) it
						.next();
				json += pairs.getKey() + "' : " + pairs.getValue() + ", '";
			}
			json = json.substring(0, json.length() - 3) + "}}";
			System.out.println(json);
			insertIntoMongo(json);

		}

	}

	public static void main(String args[]) throws IOException {
		ExtractPatientInfo ep = new ExtractPatientInfo();
		ep.instantiateMongo();
		ep.readLookupTable("", 1);
		ep.dump("", 1);

	}
}
