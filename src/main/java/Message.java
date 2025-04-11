import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class Message implements Serializable {
    public String id;
    public String sender;
    public String recipient;
    public int ttl;
    public Set<String> visited;

    public Message(String sender, String recipient, int ttl) {
        this.id = UUID.randomUUID().toString();
        this.sender = sender;
        this.recipient = recipient;
        this.ttl = ttl;
        this.visited = new HashSet<>();
    }
}

