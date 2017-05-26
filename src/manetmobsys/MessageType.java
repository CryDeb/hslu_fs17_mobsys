package manetmobsys;

/**
 * @author dane
 */
public class MessageType {
    final private String message;
    final private int uid;
    final private String ident;
    private boolean retransmitted;
    private int cntRecived;
    
    public MessageType(String message,int uid,String ident) {
        this.message = message;
        this.uid = uid;
        this.ident = ident;
        this.retransmitted = false;
        this.cntRecived = 0;
    }
    public void recived() {
        this.cntRecived++;
    }
    public void transmitted() {
        this.retransmitted = true;
    }
}
