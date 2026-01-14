package it.frafol.cleanping.hytale;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class UpdateCheck {

    public static void checkForUpdates(CleanPing plugin, String currentVersion, String resourceId) {
        CompletableFuture.runAsync(() -> {
            try {
                URL url = new URL("https://api.orbis.place/resources/" + resourceId + "/versions");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");
                if (connection.getResponseCode() == 200) {
                    InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                    JsonObject response = JsonParser.parseReader(reader).getAsJsonObject();
                    JsonArray versions = response.getAsJsonArray("versions");

                    boolean updateFound = false;
                    String latestVersion = currentVersion;

                    for (JsonElement element : versions) {
                        JsonObject versionObj = element.getAsJsonObject();
                        String remoteVersion = versionObj.get("versionNumber").getAsString();
                        if (isNewer(currentVersion, remoteVersion)) {
                            updateFound = true;
                            latestVersion = remoteVersion;
                            break;
                        }
                    }

                    if (updateFound) {
                        plugin.getLogger().at(Level.WARNING).log("An update is found! " + latestVersion);
                    }
                }
            } catch (Exception ignored) {}
        });
    }

    private static boolean isNewer(String current, String remote) {
        if (current == null || remote == null) return false;
        String[] currentParts = current.split("\\.");
        String[] remoteParts = remote.split("\\.");
        int length = Math.max(currentParts.length, remoteParts.length);

        for (int i = 0; i < length; i++) {
            int c = i < currentParts.length ? Integer.parseInt(currentParts[i].replaceAll("[^0-9]", "")) : 0;
            int r = i < remoteParts.length ? Integer.parseInt(remoteParts[i].replaceAll("[^0-9]", "")) : 0;
            if (r > c) return true;
            if (c > r) return false;
        }
        return false;
    }
}
