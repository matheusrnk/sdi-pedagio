package udesc.trabalho.producer;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import udesc.trabalho.Tag;

public class CarProducer implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(CarProducer.class.getName());
    private static final String ALPHANUMERIC = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final Random RANDOM = new Random();
    private final Cabin CABIN;
    private int startProdNumber = 0;
    private int carsToProduce = 0;

    public CarProducer(int startProdNumber, int carsToProduce, Cabin cabin) {
        this.CABIN = cabin;
        this.startProdNumber = startProdNumber;
        this.carsToProduce = carsToProduce;
    }

    @Override
    public void run() {
        int producedCars = 0;
        while(producedCars < carsToProduce) {
            Car c = makeCar(this.startProdNumber + producedCars);
            LOGGER.log(Level.INFO, "Thread CarProducer {0} produced {1}", new Object[]{Thread.currentThread().getId(), c});
            CABIN.addCar(c);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                LOGGER.log(Level.SEVERE, "Thread CarProducer {0} interrupted", Thread.currentThread().getId());
                e.printStackTrace();
            }
            producedCars++;
        }
    }

    public static Car makeCar(int number) {
        try {
            String plate = hashString("" + number);
            boolean payed = false;
            Tag tag = getRandomEnum(Tag.class);

            return new Car(plate, payed, tag);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.log(Level.SEVERE, "Error in hashing algorithm: {0}", e.getMessage());
            System.exit(1);
        }
        return null;
    }

    private static <T extends Enum<T>> T getRandomEnum(Class<T> clazz) {
        T[] values = clazz.getEnumConstants();
        
        int randomIndex = RANDOM.nextInt(values.length);
        
        return values[randomIndex];
    }

    private static String hashString(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashBytes = md.digest(input.getBytes());

        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            hexString.append(String.format("%02x", b));
        }

        String alphanumericString = convertHexToAlphaNumeric(hexString.toString());

        return alphanumericString.substring(0, 7);
    }

    private static String convertHexToAlphaNumeric(String hex) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < hex.length(); i += 2) {
            int value = Integer.parseInt(hex.substring(i, i + 2), 16);
            result.append(ALPHANUMERIC.charAt(value % ALPHANUMERIC.length()));
        }
        return result.toString();
    }

}
