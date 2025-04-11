import com.rabbitmq.client.*;
import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class User {
    private String name;
    private String currentAntenna;
    private Channel channel;
    private static final String EXCHANGE = "sms_network";
    String userQueue = "user_" + name;
    private final Map<String, Long> receivedMessages = new ConcurrentHashMap<>();
    private static final long MESSAGE_LIFETIME_MS = 2 * 60 * 1000; // 2 minutes

    public User(String name, String initialAntenna) throws Exception {
        this.name = name;
        this.currentAntenna = "";

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        this.channel = connection.createChannel();
        String userQueue = "user_" + name;
        channel.queueDeclare(userQueue, false, false, false, null);
        channel.queueBind(userQueue, EXCHANGE, userQueue);
        startListening(userQueue);
        startCleanupThread();

        // connect to initial antenna
        moveTo(initialAntenna);
    }

    // Method to simulate sending an SMS message
    public void sendSMS(String recipient, String content) throws Exception {
        SMSMessage msg = new SMSMessage(name, recipient, content, 5);
        send(msg);
    }


    private void startListening(String queueName) throws IOException {
        DeliverCallback callback = (consumerTag, delivery) -> {
            try (ByteArrayInputStream bis = new ByteArrayInputStream(delivery.getBody());
                 ObjectInput in = new ObjectInputStream(bis)) {
                Message msg = (Message) in.readObject();

                if (receivedMessages.containsKey(msg.id)) {
                    return; // Ignorer le doublon
                }

                // Mettre le message dans la map des messages reçus
                receivedMessages.put(msg.id, System.currentTimeMillis());

                if (msg instanceof SMSMessage) {
                    SMSMessage sms = (SMSMessage) msg;
                    System.out.println("\n Nouveau message de " + sms.sender + ": " + sms.content);
                    System.out.println("1. Send message");
                    System.out.println("2. Move to another antenna");
                    System.out.println("0. Exit");
                    System.out.print("Enter choice: "); // Keeping the display
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        channel.basicConsume(queueName, true, callback, consumerTag -> {});
    }

    /*Method to start a thread that cleans up the stored signatures of the received messages
     * we store the messages to avoid duplication of received msgs */
    private void startCleanupThread() {
        Thread cleaner = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(30 * 1000); // Nettoyage toutes les 30 secondes
                    long now = System.currentTimeMillis();
                    receivedMessages.entrySet().removeIf(entry ->
                            now - entry.getValue() > MESSAGE_LIFETIME_MS
                    );
                } catch (InterruptedException e) {
                    break;
                }
            }
        });

        cleaner.setDaemon(true); // Pour qu'il s'arrête à la fin du programme
        cleaner.start();
    }


    // Method to simulate the user physically moving to a new Antenna
    // We simulate this by sending a MOVE message to the new antenna
    public void moveTo(String newAntenna) throws Exception {
        MoveMessage moveMsg = new MoveMessage(name, newAntenna, currentAntenna, 5);
        currentAntenna = newAntenna;
        send(moveMsg);
    }

    // Method to simulate communication with the antenna
    private void send(Message msg) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(msg);
        out.flush();
        byte[] data = bos.toByteArray();
        channel.basicPublish(EXCHANGE, currentAntenna, null, data);
    }
}
