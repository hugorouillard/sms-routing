import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/* utility class to maintain a single connection to rabbitMQ shared by all antennas */
public class RabbitMQConnection {
    private static Connection connection;

    public Connection getConnection(String hostAddress) throws IOException, TimeoutException {
        if (connection == null || !connection.isOpen()) {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(hostAddress);
            connection = factory.newConnection();
            Channel channel = connection.createChannel();
        }
        return connection;
    }
}
