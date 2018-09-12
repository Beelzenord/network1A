package serverside;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Timer;

public class GameServer implements Runnable {
    private static final String BYE = "BYE";
    private static final String OK = "OK";
    private static final String USEPORT = "USEPORT";
    private static final String GUESS = "GUESS";
    private static final String ALIVE = "ALIVE";
    private static final String DEADCLIENT = "DEADCLIENT";
    private static final String GUESSRESPONSE = "GUESSRESPONSE";

    private DatagramSocket socket;
    private DatagramPacket packet;
    private String correctWord;
    private String currentGuess = "";
    private String serverName;
    private int nrOfGuesses;
    private int port;
    private boolean isServingClient;
    private static final int maxbuff = 1024;

    public GameServer(DatagramPacket packet, String serverName, int port, String correctWord) {
        try {
            this.socket = new DatagramSocket(port + 1);
            this.packet = packet;
            this.isServingClient = true;
            this.correctWord = correctWord.toUpperCase();
            for (int i = 0 ;i < correctWord.length(); i++) 
                currentGuess = currentGuess + "*";
            nrOfGuesses = correctWord.length() + 3;
            this.port = port + 1;
            this.serverName = serverName;
        } catch (SocketException e) {
            isServingClient = false;
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        InetAddress clientIPAddress = packet.getAddress();
        int clientPort = packet.getPort();
        
        if (!isServingClient) {
            sendData(BYE.getBytes(), clientIPAddress, clientPort);
            return;
        }
        
        String replyString = USEPORT+"/" + this.port;
        byte[] replyData = new byte[maxbuff];
        replyData = replyString.getBytes();
        
        long timeout = System.currentTimeMillis();
        DatagramSocket timerSocket = null;
        Timer sendAlive = new Timer();
        Timer sendDead = new Timer();
        
        try {
            timerSocket = new DatagramSocket(clientPort -2);
            createTimers(sendAlive, sendDead, timerSocket, InetAddress.getByName(serverName), port, clientIPAddress, clientPort);
            sendData(replyData, clientIPAddress, clientPort);
            
            
            while (isServingClient) {
                System.out.println("Client server waiting...");
                packet = new DatagramPacket(new byte[maxbuff], maxbuff);
                socket.receive(packet);
                String sentence = new String(packet.getData()).toUpperCase();
                String[] receive = sentence.trim().split("/");
                switch (receive[0]) {
                    case BYE:
                        sendData(BYE.getBytes(), clientIPAddress, clientPort);
                        isServingClient = false;
                        break;
                    case GUESS:
                        timeout = System.currentTimeMillis();;
                        if (receive[1].length()>1) {
                            sendData("GUESSRESPONSE/Guess only one letter".getBytes(), clientIPAddress, clientPort);
                            break;
                        }
                        replyString = handleGuess(receive[1]);
                        if (correctWord.equals(currentGuess)) {
                            replyString = GUESSRESPONSE+"/You guessed the correct word: " + replyString;
                            sendData(replyString.getBytes(), clientIPAddress, clientPort);
                            sendData(BYE.getBytes(), clientIPAddress, clientPort);
                        }
                        else {
                            if (nrOfGuesses == 0) {
                                replyString = BYE+"/"+"You ran out of guesses. \n The correct word was: " + correctWord;
                                sendData(replyString.getBytes(), clientIPAddress, clientPort);
                            }
                            else {
                                nrOfGuesses--;
                                replyString = GUESSRESPONSE+"/"+replyString;
                                sendData(replyString.getBytes(), clientIPAddress, clientPort);
                            }

                        }
                        break;
                    case ALIVE:
                        timeout = System.currentTimeMillis();
                        sendData(OK.getBytes(), clientIPAddress, clientPort);
                        break;
                    case DEADCLIENT:
                        if (System.currentTimeMillis() - timeout > 11000) {
                            sendData(BYE.getBytes(), clientIPAddress, clientPort);
                            isServingClient = false;
                        }
                        break;
                        
                    default:
                        replyString = BYE+"/Please follow the protocol";
                        sendData(replyString.getBytes(), clientIPAddress, clientPort);
                        isServingClient = false;
                        break;
                }
            }

        } catch (IOException e) {
            System.out.println("Thread could not receive");
            replyString = BYE+"/Unexpected error";
            sendData(replyString.getBytes(), clientIPAddress, clientPort);
            e.printStackTrace();
            e.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("protocol was probably broken");
            replyString = BYE+"/protocol was probably broken";
            sendData(replyString.getBytes(), clientIPAddress, clientPort);
            e.printStackTrace();
        }
        finally{
             System.out.println("Client server ending");
             sendAlive.cancel();
             sendDead.cancel();
             if (this.socket != null)
                this.socket.close();
             if (timerSocket != null)
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
        currentGuess = toReturn;
        return toReturn;
    }

    public void sendData(byte[] data, InetAddress address, int port) {
        DatagramPacket sendPacket = null;
        try {
            sendPacket = new DatagramPacket(data, data.length, address, port);
            socket.send(sendPacket);
        } catch (IOException e) {
            System.out.println("Client server could not send to Client");
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
