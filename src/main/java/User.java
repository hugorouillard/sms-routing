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
        channel.exchangeDeclare(EXCHANGE, BuiltinExchangeType.FANOUT);
    }

    public void sendSMS(String recipient, String content) throws Exception {
        Message msg = new Message(name, recipient, content, 5, Message.Type.SMS);
        send(msg);
    }

    public void moveTo(String newAntenna) throws Exception {
        System.out.println("[User] " + name + " moving to " + newAntenna);
        Message moveMsg = new Message(name, newAntenna, "MOVE", 5, Message.Type.MOVE);
        send(moveMsg);
        currentAntenna = newAntenna;
    }

    private void send(Message msg) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(msg);
        out.flush();
        byte[] data = bos.toByteArray();
        channel.basicPublish(EXCHANGE, "", null, data);
    }
}
