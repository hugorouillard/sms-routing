public class MoveMessage extends Message {
    public String newAntenna;
    public String oldAntenna;

    public MoveMessage(String sender, String newAntenna, String oldAntenna, int ttl) {
        super(sender, ttl);
        this.newAntenna = newAntenna;
        this.oldAntenna = oldAntenna;
    }
}
