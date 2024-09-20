package udesc.trabalho.consumer;

import com.rabbitmq.client.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.logging.Logger;
import java.util.logging.Level;
import udesc.trabalho.producer.Car;
import udesc.trabalho.Tag;

public class TagConsumer {
    private static final Logger LOGGER = Logger.getLogger(TagConsumer.class.getName());
    private static final String EXCHANGE_NAME = "car_exchange_mat";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Tag consumerTag;

    public TagConsumer(Tag consumerTag) {
        this.consumerTag = consumerTag;
    }

    public void start() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");

            String queueName = channel.queueDeclare(consumerTag.getRoutingKey(), false, true, true, null).getQueue();
            channel.queueBind(queueName, EXCHANGE_NAME, this.consumerTag.getRoutingKey());

            LOGGER.log(Level.INFO,  " [*] Waiting for messages with routing key: {0}", this.consumerTag.getRoutingKey());

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                Car car = objectMapper.readValue(message, Car.class);
                LOGGER.log(Level.INFO,  " [x] Received car: {0}", car);
                
                // Process the car object
                car.setPayed(true);  // Example modification
                
                // Send response back to reply-to queue
                String replyTo = delivery.getProperties().getReplyTo();
                if (replyTo != null) {
                    String responseMessage = objectMapper.writeValueAsString(car);
                    channel.basicPublish("", replyTo, null, responseMessage.getBytes("UTF-8"));
                    LOGGER.log(Level.INFO, "Sent modified car: {0}", responseMessage);
                }
            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE,  "An error occurred: {0}", e.getMessage());
        }
    }
}
