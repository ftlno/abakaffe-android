package no.ftl.abakaffe;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.InputStream;

/**
 * Created by fredrik on 29.11.13.
 */

public class NetworkOperations {

	public static String STATUS_URL = "http://kaffe.abakus.no/api/status";

	public static JSONObject updateStatus() {
		return Utilities.getJsonFromInputStream(getRequest(STATUS_URL), "coffee");
	}

	private static InputStream getRequest(String url) {

		InputStream content = null;
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response = httpclient.execute(new HttpGet(url));
			content = response.getEntity().getContent();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return content;

	}
}
