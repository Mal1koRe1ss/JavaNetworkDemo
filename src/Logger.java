import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private static java.util.logging.Logger logger;
    private static FileHandler fileHandler;
    private static final int FILE_SIZE = 1024 * 1024 * 10; // 1MB
    private static final int FILE_COUNT = 1; // Sadece 1 log dosyası tutulacak

    static {
        try {
            String appType = System.getProperty("app.type", "server");
            String logFileName;
            
            if(appType.equals("client")) {
                String clientId = System.getProperty("client.id", "0");
                logFileName = "client-" + clientId + ".log";
            } else {
                logFileName = "server.log";
            }

            fileHandler = new FileHandler(logFileName, FILE_SIZE, FILE_COUNT, true);

            // Log formatını ayarlayın (tarih, saat, seviye, mesaj)
            SimpleFormatter formatter = new SimpleFormatter() {
                private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

                @Override
                public String format(LogRecord record) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
                    Date date = new Date(record.getMillis());
                    return dateFormat.format(date) + " " +
                           "[" + record.getLevel() + "] " +
                           record.getMessage() + "\n";
                }
            };
            fileHandler.setFormatter(formatter);

            logger = java.util.logging.Logger.getLogger("ChatLogger");
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL); // Tüm seviyelerdeki logları kaydet
            logger.setUseParentHandlers(false); // Konsola yazdırmayı kapat
        } catch (IOException e) {
            e.printStackTrace();
        }

        String logPrefix = System.getProperty("app.type", "server");
        if(logPrefix.equals("client")) {
            logPrefix += "-" + System.getProperty("client.id", "0");
        }
    }

    public static void log(Level level, String message) {
        logger.log(level, message);
    }

    public static void close() {
        if (fileHandler != null) {
            fileHandler.close();
        }
    }
}
