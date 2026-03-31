package it.frafol.cleanping.hytale;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.frafol.cleanping.hytale.enums.HytaleConfig;
import it.frafol.cleanping.hytale.enums.HytaleMessages;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class UpdateCheck {

    private static boolean updated = false;

    public static void checkForUpdates(CleanPing plugin, String currentVersion, String resourceId) {
        CompletableFuture.runAsync(() -> {
            try {
                URI uri = new URI("https://api.orbis.place/resources/" + resourceId + "/versions");
                URL url = uri.toURL();
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
                        plugin.getLogger().at(Level.WARNING).log("An update is found! (" + latestVersion + ")");
                        if (Boolean.TRUE.equals(HytaleConfig.AUTO_UPDATE.get(Boolean.class)) && !updated) {
                            boolean success = downloadUpdate(plugin);
                            if (success) {
                                plugin.getLogger().at(Level.INFO).log("Update downloaded successfully. Please restart the server to apply the update.");
                            } else {
                                plugin.getLogger().at(Level.WARNING).log("Failed to download the update. Please download manually from Orbis.planet");
                            }
                        }
                    }
                }
            } catch (Exception ignored) {}
        });
    }

    private static boolean isNewer(String current, String remote) {
        current = extractCleanVersion(current);
        remote = extractCleanVersion(remote);
        if (current.isEmpty() || remote.isEmpty()) return false;
        String[] currentParts = current.split("\\.");
        String[] remoteParts = remote.split("\\.");
        int length = Math.max(currentParts.length, remoteParts.length);
        for (int i = 0; i < length; i++) {
            int c = i < currentParts.length ? parseIntSafe(currentParts[i]) : 0;
            int r = i < remoteParts.length ? parseIntSafe(remoteParts[i]) : 0;
            if (r > c) return true;
            if (c > r) return false;
        }
        return false;
    }

    private static String extractCleanVersion(String version) {
        if (version == null) return "";
        StringBuilder cleanVersion = new StringBuilder();
        for (char c : version.toCharArray()) {
            if ((c >= '0' && c <= '9') || c == '.') {
                cleanVersion.append(c);
            } else {
                break;
            }
        }
        return cleanVersion.toString();
    }


    private static int parseIntSafe(String s) {
        try {
            return Integer.parseInt(s.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static boolean downloadUpdate(CleanPing plugin) {
        try {
            URI uri = new URI("https://github.com/frafol/CleanPing/releases/download/release/CleanPing-Hytale.jar");
            URL url = uri.toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/octet-stream");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);
            if (connection.getResponseCode() == 200) {
                InputStream inputStream = getInputStream(plugin, connection);
                inputStream.close();
                updated = true;
                return true;
            } else {
                plugin.getLogger().at(Level.SEVERE).log("Failed to download update: HTTP " + connection.getResponseCode());
            }
        } catch (Exception e) {
            plugin.getLogger().at(Level.SEVERE).log("Exception while downloading update: " + e.getMessage());
        }
        return false;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static InputStream getInputStream(CleanPing plugin, HttpURLConnection connection) throws IOException {
        InputStream inputStream = connection.getInputStream();
        File modsFolder = new File(plugin.getDataDirectory().getParent().toFile().getParentFile(), "mods");
        if (!modsFolder.exists()) modsFolder.mkdirs();
        File outputFile = new File(modsFolder, "CleanPing.jar");
        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
        return inputStream;
    }
}