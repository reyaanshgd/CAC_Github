package org.greenpath.app;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Minimal public-feed fetch helper for the current native-Java GreenPath prototype.
 * All calls run off the Android main thread.
 *
 * This returns raw JSON/ICS text. Parse and normalize it before showing records in UI.
 */
public final class PublicFeedClient {
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(2);

    private PublicFeedClient() {}

    public interface Callback {
        void onSuccess(String body);
        void onError(Exception error);
    }

    public static void get(String endpoint, Callback callback) {
        EXECUTOR.execute(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(endpoint);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(15000);
                connection.setRequestProperty("Accept", "*/*");
                connection.setRequestProperty("User-Agent", "GreenPath-Student-App/0.1");

                int status = connection.getResponseCode();
                if (status < 200 || status >= 300) {
                    throw new IllegalStateException("HTTP " + status + " from " + endpoint);
                }

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder body = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    body.append(line).append('\n');
                }
                reader.close();
                callback.onSuccess(body.toString());
            } catch (Exception e) {
                callback.onError(e);
            } finally {
                if (connection != null) connection.disconnect();
            }
        });
    }
}
