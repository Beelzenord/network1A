/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package clientside;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

/**
 *
 * @author fno
 */
public class Client {
    private static final String HELLO = "HELLO";
    private static final String OK = "OK";
    private static final String START = "START";
    private static final String READY = "READY";
    private static final String ERROR = "ERROR";
    private static final String TIMEOUT = "TIMEOUT";
    private static final String BUSY = "BUSY";
    
    private static final int MAXBUFF = 1024;
    
    public static void main(String args[]) throws Exception {
        BufferedReader inFromUser
                = new BufferedReader(new InputStreamReader(System.in));
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName("localhost");
        
        int port = 9876;
        if(args.length>=1){
            port = Util.assignPort(args, 9876);
        }
        
        UserInfo userInfo = new UserInfo(IPAddress,port);
        byte[] sendData = new byte[MAXBUFF];
        if (pokeServer(userInfo, clientSocket)) {
            try {
                ClientListener clientListener = new ClientListener(clientSocket,userInfo);
                clientListener.start();
                String sentence = "";
                while (sentence != null && clientListener.isAlive()) {
                    sentence = inFromUser.readLine();
                    sendData = new byte[MAXBUFF];
                    System.out.println("sentence: " + sentence);
                    sendData = sentence.getBytes();
                    System.out.println(new String(sendData));
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, userInfo.getIPAddress(), userInfo.getPortAddress());
                    if (clientListener.isAlive())
                        clientSocket.send(sendPacket);
                }
            } catch (NullPointerException ex) {
            } catch (IOException ex) {
                System.out.println("Could not send to server");
            } finally {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            }
        }
        else {
            System.out.println("Unsuccessful poking of the server");
            System.exit(0);
        }
    }

    /**
     * This is the protocol that initiates contact with the server if the
     * protocol is fulfilled then the server accepts the client. if it fails
     * then there is not connection
     */
    public static boolean pokeServer(UserInfo userInfo, DatagramSocket clientSocket) {
        byte[] sendData = new byte[MAXBUFF];
        byte[] receiveData = new byte[MAXBUFF];
        BufferedReader inFromUser
            = new BufferedReader(new InputStreamReader(System.in));

        String receivedString;
        sendData = HELLO.getBytes();
        DatagramPacket sendPacket = null;
        try {
            clientSocket.setSoTimeout(4000);
            System.out.println("Type HELLO");
            sendData = inFromUser.readLine().toUpperCase().getBytes();
            sendPacket = new DatagramPacket(sendData, sendData.length, userInfo.getIPAddress(), userInfo.getPortAddress());
            clientSocket.send(sendPacket); // sends hello
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket); // receives ok
            receivedString = new String(receivePacket.getData()).trim();
            String[] received = receivedString.split("/");
            System.out.println("[From Server] > " + receivedString);
            if (!received[0].equals(OK)) {
                switch(received[0]) {
                    case BUSY:
                        System.out.println(received[1]);
                        break;
                    case ERROR:
                        System.out.println(received[1]);
                        break;
                    case TIMEOUT:
                        System.out.println(received[1]);
                        break;
                    default:
                        System.out.println("Unexpected message from server, shuting down");
                        break;
                }
                return false;
            }
            System.out.println("Type START");
            sendData = inFromUser.readLine().toUpperCase().getBytes();
            sendPacket = new DatagramPacket(sendData, sendData.length, userInfo.getIPAddress(), userInfo.getPortAddress());
            clientSocket.send(sendPacket); // sends start
            receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            receivedString = new String(receivePacket.getData()).trim();
            received = receivedString.split(" ");
            if (!received[0].equals(READY)) {
                switch(received[0]) {
                    case BUSY:
                        System.out.println(received[1]);
                        break;
                    case ERROR:
                        System.out.println(received[1]);
                        break;
                    case TIMEOUT:
                        System.out.println(received[1]);
                        break;
                    default:
                        System.out.println("Unexpected message from server, shuting down");
                        break;
                }
                return false;
            }
            clientSocket.setSoTimeout(15000);
            System.out.println("[From Server] > " + receivedString);

        } catch (SocketTimeoutException ex) {
            System.out.println("Server is unresponsive");
            if (clientSocket != null) {
                clientSocket.close();
            }
            return false;
        } catch (IOException ex) {
        } catch (ArrayIndexOutOfBoundsException ex) {
        } 
        
        return true;
    }
}
