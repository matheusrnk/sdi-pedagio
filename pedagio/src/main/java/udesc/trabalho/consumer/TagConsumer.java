package udesc.trabalho.consumer;

import java.util.logging.Logger;
import java.util.logging.Level;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import udesc.trabalho.Tag;

public class TagConsumer {
    private static final Logger LOGGER = Logger.getLogger(ExchangeMonitor.class.getName());
    private static final String EXCHANGE_NAME = "car_exchange_mat";
    private Tag consumerTag;

    public TagConsumer(Tag consumerTag) {
        this.consumerTag = consumerTag;
    }

    public void start() {
        try {
            // remover isto daqui tambem, pq deve haver somente um exchange.
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");

            String queueName = channel.queueDeclare("", false, true, true, null).getQueue();
            channel.queueBind(queueName, EXCHANGE_NAME, this.consumerTag.getRoutingKey());

            LOGGER.log(Level.INFO,  " [*] Waiting for messages with routing key: {0}", this.consumerTag.getRoutingKey());

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                LOGGER.log(Level.INFO,  " [x] Received '{0}'", message);
                System.out.println(message);
            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE,  "An error occurred: {0}", e.getMessage());
        }
    }
}

