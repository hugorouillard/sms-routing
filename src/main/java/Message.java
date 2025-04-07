import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Message implements Serializable {
    public enum Type { SMS, MOVE }

    public String id;
    public String sender;
    public String recipient;
    public String content;
    public int ttl;
    public Set<String> visited;
    public Type type;

    public Message(String sender, String recipient, String content, int ttl, Type type) {
        this.id = UUID.randomUUID().toString();
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.ttl = ttl;
        this.visited = new HashSet<>();
        this.type = type;
    }
}

