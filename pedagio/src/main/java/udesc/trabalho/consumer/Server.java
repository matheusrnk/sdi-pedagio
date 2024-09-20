package udesc.trabalho.consumer;

import java.util.logging.Logger;

public class Server {
    public static void main(String[] args) {

        ConsumerLogConfig.setupGlobalFileLogger();

        Logger logger = Logger.getLogger(Server.class.getName());
        logger.info("Application started.");        

        ProcessBalancer balancer = new ProcessBalancer();
        balancer.startBalancing();
    }
}
