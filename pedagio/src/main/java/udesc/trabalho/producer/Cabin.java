package udesc.trabalho.producer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

public class Cabin implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(Cabin.class.getName());
    private static final String EXCHANGE_NAME = "car_exchange_mat";
    private final BlockingQueue<Car> CAR_QUEUE;
    private final int PROCESSING_INTERVAL;
    private volatile boolean running = true;

    public Cabin(int capacity, int processingInterval) {
        this.CAR_QUEUE = new LinkedBlockingQueue<>(capacity);
        this.PROCESSING_INTERVAL = processingInterval;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Car car = CAR_QUEUE.take();
                LOGGER.log(Level.INFO, "Processing car: {0}", car);
                processCar(car);
                Thread.sleep(PROCESSING_INTERVAL);
            } catch (InterruptedException e) {
                LOGGER.log(Level.SEVERE, "Processing interrupted: {0}", e.getMessage());
                Thread.currentThread().interrupt();
            }
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

    private void processCar(Car car) {
        try {
            // arrumar isso aqui depois, pq a conexao nao deve ficar sendo feita toda hora
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
                channel.exchangeDeclare(EXCHANGE_NAME, "direct");
                channel.basicPublish(EXCHANGE_NAME, car.getTag().getName(), null, car.toString().getBytes());
                System.out.println(" [x] Sent '" + car + "' with routing key '" + car.getTag().getName() + "'");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOGGER.log(Level.INFO, "Finished processing car: {0}", car);
    }

    public void stop() {
        running = false;
    }

}
