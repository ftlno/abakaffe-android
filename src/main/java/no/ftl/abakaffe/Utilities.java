package no.ftl.abakaffe;

import android.content.Context;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created by fredrik on 29.11.13.
 */

public class Utilities {
	public static JSONObject getJsonFromInputStream(final InputStream inputStream, String type) {

		if (inputStream != null) {
			Writer writer = new StringWriter();
			int buffer_size = 1024;
			char[] buffer = new char[buffer_size];

			try {
				Reader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), buffer_size);
				int n;

				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}

				inputStream.close();
				reader.close();
				writer.close();

				return new JSONObject(writer.toString()).getJSONObject(type);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static String formatStatus(long hours, long mins, Context context) {

		if (hours >= 24) {
			return "Sist skrudd på for mer enn et døgn siden.";
		} else if (hours > 0) {
			return "Sist skrudd på for mer enn en time siden.";
		} else if (mins == 1) {
			return "Sist skrudd på for " + mins + " minutt siden.";
		} else {
			return "Sist skrudd på for " + mins + " minutter siden.";
		}
	}
}
