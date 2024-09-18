package udesc.trabalho.consumer;

import java.util.logging.Logger;

import udesc.trabalho.producer.ProducerLogConfig;

public class Server {
    public static void main(String[] args) {

        ProducerLogConfig.setupGlobalFileLogger();

        Logger logger = Logger.getLogger(Server.class.getName());
        logger.info("Application started.");        

        ProcessBalancer balancer = new ProcessBalancer();
        balancer.startBalancing();
    }
}
