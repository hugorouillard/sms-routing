import com.rabbitmq.client.*;
import java.io.*;

public class User {
    private String name;
    private String currentAntenna;
    private Channel channel;
    private static final String EXCHANGE = "sms_network";

    public User(String name, String initialAntenna) throws Exception {
        this.name = name;
        this.currentAntenna = initialAntenna;

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        this.channel = connection.createChannel();
    }

    // Method to simulate sending an SMS message
    public void sendSMS(String recipient, String content) throws Exception {
        Message msg = new Message(name, recipient, content, 5, Message.Type.SMS);
        send(msg);
    }

    // Method to simulate the user physically moving to a new Antenna
    // we simulate that by sending a MOVE message to the old antenna
    public void moveTo(String newAntenna) throws Exception {
//        System.out.println("[User] " + name + " moving to " + newAntenna);
        Message moveMsg = new Message(name, newAntenna, "MOVE", 5, Message.Type.MOVE);
        send(moveMsg);
        currentAntenna = newAntenna;
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
