package udesc.trabalho.consumer;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.logging.Logger;
import java.util.logging.Level;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class ExchangeMonitor {
    private static final Logger LOGGER = Logger.getLogger(ExchangeMonitor.class.getName());
    private String username = "guest"; // RabbitMQ username
    private String password = "guest"; // RabbitMQ password
    private String baseUrl = "http://localhost:15672/api/";

    // Get all queues bound to an exchange
    public Map<String, Integer> getQueuesBoundToExchange(String exchangeName) {
        Map<String, Integer> queueLoadMap = new HashMap<>();
        try {
            // Get all bindings for the exchange
            URL url = new URL(baseUrl + "exchanges/%2F/" + exchangeName + "/bindings/source");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            
            // Add basic authentication header
            String auth = username + ":" + password;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            conn.setRequestProperty("Authorization", "Basic " + encodedAuth);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder content = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            conn.disconnect();

            // Parse the JSON response to get the queue names
            JSONArray jsonResponse = new JSONArray(content.toString());
            for (int i = 0; i < jsonResponse.length(); i++) {
                JSONObject binding = jsonResponse.getJSONObject(i);
                String queueName = binding.getString("destination");
                // Get the queue length for each queue
                int queueLength = getQueueLength(queueName);
                queueLoadMap.put(queueName, queueLength);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to queues from exchange: {0}", e.getMessage());
        }
        LOGGER.log(Level.INFO, "Got the queues from exchange!");
        return queueLoadMap;
    }

    // Get the queue length using the RabbitMQ API
    private int getQueueLength(String queueName) {
        try {
            URL url = new URL(baseUrl + "queues/%2F/" + queueName);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            
            // Add basic authentication header
            String auth = username + ":" + password;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            conn.setRequestProperty("Authorization", "Basic " + encodedAuth);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder content = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            conn.disconnect();

            // Parse JSON response to get the message count
            JSONObject jsonResponse = new JSONObject(content.toString());
            LOGGER.log(Level.INFO, "Got queues length.");
            return jsonResponse.getInt("messages_ready");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to get queues length: {0}", e.getMessage());
        }
        return -1; // Return -1 to indicate an error
    }
}

