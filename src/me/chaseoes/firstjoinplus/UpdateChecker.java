package me.chaseoes.firstjoinplus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

public class UpdateChecker {

	public static String fetch(String url) {

		URL data;
		String html = "";

		BufferedReader in = null;
		try {
			data = new URL(url);

			try {
				in = new BufferedReader(
						new InputStreamReader(data.openStream()));
			} catch (UnknownHostException tr) {
				System.out.println("NO NETWORK CONNECTION");
				return "No Network " + tr.getMessage();
			}

			catch (IOException ex) {
				return "Exception" + ex.getMessage();
			}
			String inputLine = "";

			try {
				while ((inputLine = in.readLine()) != null) {
					html += inputLine + "";

				}

			} catch (IOException ex) {
				return "Exception" + ex.getMessage();

			}

		} catch (MalformedURLException u) {
			return "Exception" + u.getMessage();

		}

		return html;

	}

}
