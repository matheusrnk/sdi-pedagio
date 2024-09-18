package udesc.trabalho.producer;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.nio.charset.StandardCharsets;

public class ProducerLogConfig {

    public static void setupGlobalFileLogger() {
        Logger rootLogger = Logger.getLogger("");

        LogManager.getLogManager().reset();

        rootLogger.setLevel(Level.ALL);
        
        try {
            FileHandler fileHandler = new FileHandler("producer.log", 1024 * 1024, 3, true);
            fileHandler.setEncoding(StandardCharsets.UTF_8.name());
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new SimpleFormatter());

            rootLogger.addHandler(fileHandler);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

