package udesc.trabalho.producer;

import java.util.logging.Logger;

public class Client 
{
    public static void main( String[] args )
    {

        ProducerLogConfig.setupGlobalFileLogger();

        Logger logger = Logger.getLogger(Client.class.getName());
        logger.info("Application started.");

        int numberOfCabins = 5;
        int numberOfCars = 500;
        int carsToProduce = numberOfCars/numberOfCabins;
        int queueCapacity = 10;
        int processingInterval = 500;

        Cabin[] cabins = new Cabin[numberOfCabins];
        Thread[] cabinThreads = new Thread[numberOfCabins];

        for (int i = 0; i < numberOfCabins; i++) {
            try {
				cabins[i] = new Cabin(queueCapacity, processingInterval);
			} catch (Exception e) {
				e.printStackTrace();
			}
            cabinThreads[i] = new Thread(cabins[i]);
            cabinThreads[i].start();
        }

        int startProdNumber = 0;
        for (int i = 0; i < numberOfCabins; i++) {
            
            Runnable producer = new CarProducer(startProdNumber, carsToProduce, cabins[i]);

            new Thread(producer).start();

            startProdNumber += carsToProduce;
        }

        try {
            Thread.sleep(50000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (Cabin cabin : cabins) {
            cabin.stop();
        }

        System.out.println("All tasks are complete.");
    }
}
