import com.rabbitmq.client.*;
import java.io.*;
import java.util.*;

public class Antenna {
    private String name;
    private Set<String> users;
    private List<String> neighbors;
    private Connection connection;
    private Channel channel;
    private static final String EXCHANGE = "sms_network";

    public Antenna(String name, List<String> initialUsers, List<String> neighbors) throws Exception {

        this.name = name;
        this.users = new HashSet<>(initialUsers);
        this.neighbors = neighbors;
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        this.connection = factory.newConnection();
        this.channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE, BuiltinExchangeType.DIRECT);
        channel.queueDeclare(name, false, false, false, null);
        channel.queueBind(name, EXCHANGE, name);

        System.out.println(name + " ready. Users: " + users + " Neighbors: " + neighbors);
        startListening();
    }

    private void startListening() throws IOException {
        DeliverCallback callback = (consumerTag, delivery) -> {
            try (ByteArrayInputStream bis = new ByteArrayInputStream(delivery.getBody());
                 ObjectInput in = new ObjectInputStream(bis)) {
                Message msg = (Message) in.readObject();
                System.out.println("Received message from " + msg.sender + " to " + msg.recipient);

                if (msg.visited.contains(name) || msg.ttl <= 0) return;
                msg.visited.add(name);
                System.out.println(msg.visited);

                if (msg instanceof SMSMessage) {
                    handleSms((SMSMessage) msg);
                } else if (msg instanceof MoveMessage) {
                    handleMove((MoveMessage) msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        channel.basicConsume(name, true, callback, consumerTag -> {});
    }

    private void handleSms(SMSMessage msg) throws IOException {
        System.out.println(getUsers());
        if (users.contains(msg.recipient)) {
            System.out.println("Delivered to " + msg.recipient + ": " + msg.content);
        } else {
            System.out.println("User " + msg.recipient + " not found. Forwarding...");
            msg.ttl--;
            forward(msg);
        }
    }

    private void handleMove(MoveMessage msg) {
        String user = msg.sender;
        String target = msg.recipient;
        String oldAntenna = msg.oldAntenna;

        if (name.equals(target)) {
            users.add(user);
            System.out.println("User " + user + " connected to " + name);

            // Notify the old antenna to remove the user
            MoveMessage notifyOldAntenna = new MoveMessage(user, oldAntenna, name, 5);
            try {
                forward(notifyOldAntenna);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (name.equals(oldAntenna)) {
            users.remove(user);
            System.out.println("User " + user + " disconnected from " + name);
        } else {
            // Forward the message if it's not for a user in this antenna
            msg.ttl--;
            try {
                forward(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void forward(Message msg) throws IOException {
        for (String neighbor : neighbors) {
            if (!msg.visited.contains(neighbor)) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutput out = new ObjectOutputStream(bos);
                out.writeObject(msg);
                out.flush();
                byte[] data = bos.toByteArray();
                channel.basicPublish(EXCHANGE, neighbor, null, data);
                System.out.println("Forwarded to " + neighbor);
            }
        }
    }

    public Set<String> getUsers() {
        return users;
    }
}
