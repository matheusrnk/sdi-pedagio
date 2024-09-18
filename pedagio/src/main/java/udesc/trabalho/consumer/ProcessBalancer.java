package udesc.trabalho.consumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.logging.Logger;
import java.util.logging.Level;

import java.util.HashMap;

import udesc.trabalho.Tag;

public class ProcessBalancer {
    private static final Logger LOGGER = Logger.getLogger(ProcessBalancer.class.getName());
    private Map<Tag, List<Thread>> consumers = new HashMap<>();
    private ExecutorService executorService = Executors.newCachedThreadPool(); // For dynamic consumer management
    private ExchangeMonitor exchangeMonitor = new ExchangeMonitor();
    
    // Thresholds for scaling
    private final int HIGH_LOAD_THRESHOLD = 100;
    private final String EXCHANGE_NAME = "car_exchange_mat";

    public ProcessBalancer() {
        Tag[] tags = Tag.values();
        for(Tag t: tags) {
            consumers.put(t, new ArrayList<>());
        }
    }

    public void startBalancing() {
        // Initial setup: Start monitoring and adjusting consumers
        // first, add the consumers.
        Tag[] tags = Tag.values();
        for(Tag t: tags) {
            addConsumer(t);
        }
        monitorAndBalance();
    }

    // Monitor queues and adjust consumers dynamically
    private void monitorAndBalance() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5000); // Check every 5 seconds

                    // Get queues bound to the exchange and their loads
                    Map<String, Integer> queueLoadMap = exchangeMonitor.getQueuesBoundToExchange(EXCHANGE_NAME);

                    queueLoadMap.forEach((key, value) -> {
                        Tag tag = null;
                        try {
                            tag = findAssociatedTag(key);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if(value > HIGH_LOAD_THRESHOLD) {
                            LOGGER.log(Level.INFO, "High load detected on queue {0}, adding more consumers.", key);
                            addConsumer(tag);
                        } else if(value < HIGH_LOAD_THRESHOLD && consumers.get(tag).size() > 1) {
                            LOGGER.log(Level.INFO, "Low load detected on queue {0}, removing one consumer.", key);
                            removeConsumer(tag);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private Tag findAssociatedTag(String queueName) throws Exception {
        Tag[] tags = Tag.values();
        int pos = 0;

        for(Tag t : tags) {
            if(queueName == t.getRoutingKey()) {break;}
            pos++;
        }

        if(pos == tags.length) {
            LOGGER.log(Level.SEVERE, "It was not possible to find the associated tag to the queue!");
            throw new Exception("It was not possible to find the associated tag to the queue!");
        }

        return tags[pos];
    }

    // Add a consumer to the specified queue
    private void addConsumer(Tag consumerTag) {
        TagConsumer tagConsumer = new TagConsumer(consumerTag);
        Runnable consumerTask = () -> tagConsumer.start();
        Thread consumerThread = new Thread(consumerTask);
        consumers.get(consumerTag).add(consumerThread);
        executorService.execute(consumerThread);
        LOGGER.log(Level.INFO, "Consumer {0} added for queue: {1}", new Object[]{consumerTag.getName(), consumerTag.getRoutingKey()});
    }

    // Remove a consumer from the list (scaling down)
    private void removeConsumer(Tag consumerTag) {
        if (!consumers.get(consumerTag).isEmpty()) {
            Thread consumerThread = consumers.get(consumerTag).remove(consumers.size() - 1);
            consumerThread.interrupt(); // Signal the thread to stop
            LOGGER.log(Level.INFO, "Consumer {0} removed.", consumerTag.getName());
        }
    }
}


