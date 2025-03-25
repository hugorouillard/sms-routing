import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;

public class Antenna {
    private final String id;
    private final String queueName;
    private Channel channel;

    public Antenna(String id) {
        this.id = id;
        this.queueName = "antenna_" + id;
        try {
            Connection connection = RabbitMQConnection.getConnection("localhost");
            this.channel = connection.createChannel();
            channel.queueDeclare(queueName, false, false, false, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void listen() {
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println("[" + id + "] received SMS: " + message);
        };
        try {
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendSMS(String targetAntennaQueue, String smsContent) {
        try {
            channel.basicPublish("", targetAntennaQueue, null, smsContent.getBytes("UTF-8"));
            System.out.println("[" + id + "] Sent SMS to " + targetAntennaQueue + ": " + smsContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getQueueName() {
        return this.queueName;
    }
}
