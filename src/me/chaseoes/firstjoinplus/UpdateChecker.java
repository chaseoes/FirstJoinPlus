package me.chaseoes.firstjoinplus;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class UpdateChecker {

    public String getLatestVersion() {
        String version = "";
        BufferedReader in = null;

        try {
            URL data = new URL("http://emeraldsmc.com/fjp/");
            String inputLine = "";
            in = new BufferedReader(new InputStreamReader(data.openStream()));
            while ((inputLine = in.readLine()) != null) {
                version = inputLine;
            }
        } catch (Exception e) {
            // Failed to check for updates!
        }

        return version;
    }

}
