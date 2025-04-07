import com.rabbitmq.client.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;

public class Antenna {
    private static final Logger logger = Logger.getLogger(Antenna.class.getName());

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
                System.out.println("Received message: " + msg.type + " from " + msg.sender + " to " + msg.recipient);

                if (msg.visited.contains(name) || msg.ttl <= 0) return;
                msg.visited.add(name);

                switch (msg.type) {
                    case SMS:
                        handleSms(msg);
                        break;
                    case MOVE:
                        handleMove(msg);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        channel.basicConsume(name, true, callback, consumerTag -> {});
    }

    private void handleSms(Message msg) throws IOException {
        System.out.println(getUsers());
        if (users.contains(msg.recipient)) {
            System.out.println("Delivered to " + msg.recipient + ": " + msg.content);
        } else {
            System.out.println("User " + msg.recipient + " not found. Forwarding...");
            msg.ttl--;
            forward(msg);
        }
    }

    private void handleMove(Message msg) {
        String user = msg.sender;
        String target = msg.recipient;

        if (name.equals(target)) {
            users.add(user);
            System.out.println("User " + user + " arrived.");
        } else if (users.contains(user)) {
            users.remove(user);
            System.out.println("User " + user + " departed.");
        } else {
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
