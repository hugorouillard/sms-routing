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

        channel.exchangeDeclare(EXCHANGE, BuiltinExchangeType.FANOUT);
        channel.queueDeclare(name, false, false, false, null);
        channel.queueBind(name, EXCHANGE, "");

        System.out.println(name + " ready. Users: " + users);
        startListening();
    }

    private void startListening() throws IOException {
        DeliverCallback callback = (consumerTag, delivery) -> {
            try (ByteArrayInputStream bis = new ByteArrayInputStream(delivery.getBody());
                 ObjectInput in = new ObjectInputStream(bis)) {
                Message msg = (Message) in.readObject();

                if (msg.visited.contains(name) || msg.ttl <= 0) return;
                msg.visited.add(name);

                switch (msg.type) {
                    case SMS:
                        if (users.contains(msg.recipient)) {
                            System.out.println("[" + name + "] Delivered to " + msg.recipient + ": " + msg.content);
                        } else {
                            msg.ttl--;
                            forward(msg);
                        }
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

    private void handleMove(Message msg) {
        String user = msg.sender;
        String target = msg.recipient;

        if (name.equals(target)) {
            users.add(user);
            System.out.println("[" + name + "] User " + user + " arrived.");
        } else if (users.contains(user)) {
            users.remove(user);
            System.out.println("[" + name + "] User " + user + " departed.");
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
                channel.basicPublish(EXCHANGE, "", null, data);
                System.out.println("[" + name + "] Forwarded to " + neighbor + ": " + msg.content);
            }
        }
    }

    public Set<String> getUsers() {
        return users;
    }
}
