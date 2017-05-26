package manetmobsys;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author dane
 */
public class MessageBuffer {
    private final LinkedBlockingDeque<MessageType> recivedMessages = new LinkedBlockingDeque<>();
    public MessageBuffer() {
    }
    
    public void addMessage(MessageType newMessage) {
        for (MessageType cnsmr : this.recivedMessages) {
            if (cnsmr.equals(newMessage)) {
                 cnsmr.recived();
                 return;
            }
        }
        this.recivedMessages.add(newMessage);
    }
    
    public Collection<MessageType> getAllMessages() {
        return this.recivedMessages;
    }
}
