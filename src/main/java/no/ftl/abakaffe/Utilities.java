package no.ftl.abakaffe;

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
}
