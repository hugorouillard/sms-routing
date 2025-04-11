public class MoveMessage extends Message {
    public String oldAntenna;

    public MoveMessage(String sender, String recipient, String oldAntenna, int ttl) {
        super(sender, recipient, ttl);
        this.oldAntenna = oldAntenna;
    }
}
