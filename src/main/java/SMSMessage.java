public class SMSMessage extends Message {
    public String recipient;
    public String content;

    public SMSMessage(String sender, String recipient, String content, int ttl) {
        super(sender, ttl);
        this.recipient = recipient;
        this.content = content;
    }
}
