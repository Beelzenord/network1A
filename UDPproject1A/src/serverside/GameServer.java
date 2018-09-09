package serverside;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Timer;
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
        
        long timeout = System.currentTimeMillis();
        DatagramSocket timerSocket = null;
        InetAddress clientIPAddress = packet.getAddress();
        int clientPort = packet.getPort();
        String replyString = "USEPORTQ" + this.port;
        byte[] replyData = new byte[maxbuff];
        replyData = replyString.getBytes();
        Timer sendAlive = new Timer();
        Timer sendDead = new Timer();
        
        try {
            timerSocket = new DatagramSocket(clientPort -2);
            createTimers(sendAlive, sendDead, timerSocket, InetAddress.getByName("localhost"), port, clientIPAddress, clientPort);
            sendData(replyData, clientIPAddress, clientPort);
            
            
            while (isServingClient) {
                System.out.println("Thread waiting...");
                packet = new DatagramPacket(new byte[maxbuff], maxbuff);
                socket.receive(packet);
                String sentence = new String(packet.getData()).toUpperCase();
                String[] receive = sentence.trim().split("/");
                switch (receive[0]) {
                    case "BYE":
                        isServingClient = false;
                        isBusy = false;
                        break;
                    case "GUESS":
                        timeout = System.currentTimeMillis();;
                        if (receive[1].length()>1) {
                            sendData("Guess only one letter".getBytes(), clientIPAddress, clientPort);
                            break;
                        }
                        replyString = handleGuess(receive[1]);
                        sendData(replyString.getBytes(), clientIPAddress, clientPort);
                        break;
                    case "ALIVE":
                        timeout = System.currentTimeMillis();
//                        sendData("OK".getBytes(), clientIPAddress, clientPort);
                        break;
                    case "DEADCLIENT":
                        if (System.currentTimeMillis() - timeout > 11000) {
                            sendData("BYE".getBytes(), clientIPAddress, clientPort);
                            isServingClient = false;
                        }
                        break;
                        
                    default:
                        replyString = "thread reply " + sentence;
                        sendData(replyString.getBytes(), clientIPAddress, clientPort);
                }
            }

        } catch (IOException e) {
            System.out.println("Thread could not receive");
            e.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("protocol was probably broken");
            replyString = " protocol was probably broken";
            sendData(replyString.getBytes(), clientIPAddress, clientPort);
            e.printStackTrace();
        }
        finally{
             System.out.println("thread ending");
             sendAlive.cancel();
             sendDead.cancel();
             this.socket.close();
             timerSocket.close();
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
            socket.send(sendPacket);
        } catch (IOException e) {
            System.out.println("thread could not send ");
            e.printStackTrace();
        }
    }
    
    private void createTimers(Timer sendAlive, Timer sendDead, DatagramSocket timerSocket, 
                InetAddress serverIP, int serverPort, InetAddress clientIP, int clientPort) {
        // start in 8 seconds and then run every 8 seconds
        sendAlive.schedule(new CheckForAliveClient(timerSocket, clientIP, clientPort, "ALIVE"), 8000, 8000);
        // start in 9 seconds and then run every 8 seconds
        sendDead.schedule(new CheckForAliveClient(timerSocket, serverIP, serverPort, "DEADCLIENT"), 9000, 8000);
    }
    
}
