import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class Message implements Serializable {
    public String id;
    public String sender;
    public int ttl;
    public Set<String> visited;

    public Message(String sender, int ttl) {
        this.id = UUID.randomUUID().toString();
        this.sender = sender;
        this.ttl = ttl;
        this.visited = new HashSet<>();
    }
}

