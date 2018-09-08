package serverside;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameServer implements Runnable {

    private DatagramSocket socket;
    private DatagramPacket packet;
    private String correctWord = "TESTWORD";
    private String currentGuess = "********";
    private int port;
    private Boolean isBusy;
    private boolean isServingClient;
    private static final int maxbuff = 1024;

    public GameServer(DatagramPacket packet, Boolean isBusy, int port) {
        this.packet = packet;
        this.isBusy = isBusy;
        isServingClient = true;
        try {
            this.socket = new DatagramSocket(port + 1);
            this.port = port + 1;
            isBusy = true;
        } catch (SocketException e) {
            isBusy = false;
            isServingClient = false;
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("hello in thread");
        if (!isServingClient) {
            isBusy = false;
            // send bye to client
            return;
        }
        InetAddress clientIPAddress = packet.getAddress();
        int port = packet.getPort();
        String replyString = "USEPORTQ" + this.port;
        byte[] replyData = new byte[maxbuff];
        replyData = replyString.getBytes();
        sendData(replyData, clientIPAddress, port);

        
            try {
                while (isServingClient) {
                    System.out.println("Thread waiting...");
                packet = new DatagramPacket(new byte[maxbuff], maxbuff);
                socket.receive(packet);
                String sentence = new String(packet.getData()).toUpperCase();
                System.out.println("THREAD RECEIVED: " + sentence);
                String[] receive = sentence.trim().split("/");
                System.out.println("0: " + receive[0]);
                System.out.println("1: " + receive[1]);
                switch (receive[0]) {
                    case "BYE":
                        isServingClient = false;
                        isBusy = false;
                        break;
                    case "GUESS":
                        if (receive[1].length()>1) {
                            sendData("Guess only one letter".getBytes(), clientIPAddress, port);
                            break;
                        }
                        replyString = handleGuess(receive[1]);
                        sendData(replyString.getBytes(), clientIPAddress, port);
                        break;
                    default:
                        replyString = "thread reply " + sentence;
                        sendData(replyString.getBytes(), clientIPAddress, port);
                }
                }
                

            } catch (IOException e) {
                System.out.println("Thread could not receive");
                e.printStackTrace();
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("protocol was probably broken");
                replyString = " protocol was probably broken";
                sendData(replyString.getBytes(), clientIPAddress, port);
                e.printStackTrace();
            }
            finally{
                 System.out.println("thread ending");
                 this.socket.close();
            }
      
        
       
      
    }

    
    
    private String handleGuess(String g) {
        String[] word = correctWord.split("");
        String[] guess = currentGuess.split("");
        String toReturn = "";
        for (int i = 0; i < correctWord.length(); i++) {
            if (word[i].equals(g)) {
                guess[i] = g;
            }
            toReturn = toReturn + guess[i];
        }
        System.out.println("toReturn " + toReturn);
        currentGuess = toReturn;
        return toReturn;
    }

    public void sendData(byte[] data, InetAddress address, int port) {
        DatagramPacket sendPacket = null;
        try {
            sendPacket = new DatagramPacket(data, data.length, address, port);
            System.out.println("sending: " + new String(sendPacket.getData()));
            socket.send(sendPacket);
        } catch (IOException e) {
            System.out.println("thread could not send ");
            e.printStackTrace();
        }
    }
    
}
