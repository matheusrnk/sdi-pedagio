package udesc.trabalho.producer;

import com.rabbitmq.client.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cabin implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(Cabin.class.getName());
    private static final String EXCHANGE_NAME = "car_exchange_mat";
    private final BlockingQueue<Car> CAR_QUEUE;
    private final int PROCESSING_INTERVAL;
    private volatile boolean running = true;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Cabin(int capacity, int processingInterval) {
        this.CAR_QUEUE = new LinkedBlockingQueue<>(capacity);
        this.PROCESSING_INTERVAL = processingInterval;
    }

    @Override
    public void run() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");

            String replyQueueName = channel.queueDeclare().getQueue();
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String response = new String(delivery.getBody(), "UTF-8");
                LOGGER.log(Level.INFO, "Received response: {0}", response);
            };
            channel.basicConsume(replyQueueName, true, deliverCallback, consumerTag -> {});

            while (running) {
                Car car = CAR_QUEUE.take();
                LOGGER.log(Level.INFO, "Processing car: {0}", car);
                processCar(channel, car, replyQueueName);
                Thread.sleep(PROCESSING_INTERVAL);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in Cabin run: {0}", e.getMessage());
        }
    }

    public void addCar(Car car) {
        try {
            CAR_QUEUE.put(car);
            LOGGER.log(Level.INFO, "Car added to queue: {0}", car);
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Failed to add car to queue: {0}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private void processCar(Channel channel, Car car, String replyQueueName) {
        try {
            String carJson = objectMapper.writeValueAsString(car);
            AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().replyTo(replyQueueName).build();
            channel.basicPublish(EXCHANGE_NAME, car.getTag().getRoutingKey(), props, carJson.getBytes("UTF-8"));
            LOGGER.log(Level.INFO, "Sent car: {0}", carJson);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error sending car message: {0}", e.getMessage());
        }
    }

    public void stop() {
        running = false;
    }
}
