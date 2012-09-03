package javaBot.tools;

// ~--- JDK imports ------------------------------------------------------------

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class htmlParser {
	public static String[] readWebsite(String urlString) {
		final ArrayList<String> array = new ArrayList<String>();

		try {
			final URL url = new URL(urlString);
			final BufferedReader in = new BufferedReader(new InputStreamReader(
			        url.openStream()));
			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				inputLine = inputLine.trim();
				array.add(inputLine);
			}

			in.close();
		}
		catch (final Exception e) {
			e.printStackTrace();
		}

		return array.toArray(new String[array.size()]);
	}
}

// ~ Formatted by Jindent --- http://www.jindent.com
