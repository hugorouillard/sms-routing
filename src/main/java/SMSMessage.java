public class SMSMessage extends Message {
    public String content;

    public SMSMessage(String sender, String recipient, String content, int ttl) {
        super(sender, recipient, ttl);
        this.content = content;
    }
}
