package manetmobsys;


/**
 * @author dane
 */
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Multicast implements Runnable{
    private final MulticastSocket socket;
    private final InetAddress inetAddress;
    private final AtomicBoolean running;
    private final Collection<MulticastMessage> sendBuffer;
    private final int port;
    private final ReciveListener reciveListener;
    private int probability;
    
    
    
    public Multicast(final String ip, final int pport, final int socketTimeout, ReciveListener pReciveListener) throws IOException {
        this.port = pport;
        this.socket = new MulticastSocket(port);
        this.inetAddress = InetAddress.getByName(ip);
        this.socket.joinGroup(inetAddress);
        this.socket.setSoTimeout(socketTimeout);
        this.running = new AtomicBoolean(true);
        this.sendBuffer = new LinkedBlockingDeque<>();
        this.reciveListener = pReciveListener;
        
    }

    @Override
    public void run() {
        while (this.running.get()) {
            synchronized (this) {
                this.sendBuffer.forEach(cnsmr -> {
                    final String msg = cnsmr.toString();
                    System.out.println(msg);
                    DatagramPacket toSend = new DatagramPacket(cnsmr.getTelegram(), 
                            msg.length(), this.inetAddress, this.port);
                    try {
                        this.socket.send(toSend);
                    } catch (IOException ex) {
                        Logger.getLogger(Multicast.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
                this.sendBuffer.clear();
            }
            
            
            byte[] buf = new byte[1000];
            DatagramPacket recv = new DatagramPacket(buf, buf.length);
            try {
                this.socket.receive(recv);
                MulticastMessage mg = new MulticastMessage(buf);
                System.out.println(mg.toString());
                this.reciveListener.messageRecived(mg);
                float r = new Random().nextInt(100);
                if(this.probability > r) {
                    this.sendMessage(mg);
                    mg.setRetransmitted(true);
                }
            } catch (IOException ex) {
                //Logger.getLogger(Multicast.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.socket.close();
    }
    
    public void close() {
        this.running.set(false);
    }
    
    public void sendMessage(MulticastMessage messageToSend) {
        this.sendBuffer.add(messageToSend);
    }

    public synchronized void setProbability(int probability) {
        this.probability = probability;
    }

}
