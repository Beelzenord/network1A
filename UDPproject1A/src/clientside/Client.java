/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientside;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author fno
 */
public class Client {
    
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
        byte[] sendData = new byte[1024];
        byte[] receiveData = new byte[1024];
        boolean linkedWithServer = false;
        System.out.println("user port: " + userInfo.getPortAddress());
        if (pokeServer(userInfo, clientSocket)) {
            //
            try {
                ClientListener clientListener = new ClientListener(clientSocket,userInfo);
                clientListener.start();
    //            String sentence = inFromUser.readLine();
                String sentence = "";
                while (sentence != null && clientListener.isAlive()) {

                    sentence = inFromUser.readLine();
                    sendData = new byte[1024];
                    sendData = sentence.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, userInfo.getIPAddress(), userInfo.getPortAddress());
                    if (clientListener.isAlive())
                        clientSocket.send(sendPacket);

                }
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            } finally {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            }
        }

    }

    /**
     * This is the protocol that initiates contact with the server if the
     * protocol is fulfilled then the server accepts the client. if it fails
     * then there is not connection
     */
    public static boolean pokeServer(UserInfo userInfo, DatagramSocket clientSocket) {
        byte[] sendData = new byte[1024];
        byte[] receiveData = new byte[1024];
        BufferedReader inFromUser
            = new BufferedReader(new InputStreamReader(System.in));
        String hello = "HELLO";
        String ok = "OK";
        String start = "START";
        String ready = "READY";
        String receivedString;
        /**
         * Send Hello
         */
        System.out.println("send" + userInfo.getPortAddress());
        sendData = hello.getBytes();
        DatagramPacket sendPacket = null;
        try {
            
            System.out.println("Type HELLO");
            sendData = inFromUser.readLine().toUpperCase().getBytes();
            sendPacket = new DatagramPacket(sendData, sendData.length, userInfo.getIPAddress(), userInfo.getPortAddress());
            clientSocket.send(sendPacket); // sends hello
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket); // receives ok
            receivedString = new String(receivePacket.getData()).trim();
            System.out.println("[From Server] > " + receivedString);
            if (!receivedString.trim().equals(ok)) {
                System.out.println("Unexpected message from server, shuting down");
                return false;
            }
            System.out.println("Type START");
            sendData = inFromUser.readLine().toUpperCase().getBytes();
            sendPacket = new DatagramPacket(sendData, sendData.length, userInfo.getIPAddress(), userInfo.getPortAddress());
            clientSocket.send(sendPacket); // sends start
            receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            receivedString = new String(receivePacket.getData()).trim();
            String[] received = receivedString.split(" ");
            if (!received[0].equals("READY")) {
                System.out.println("Unexpected message from server, shuting down");
                return false;
            }
            System.out.println("[From Server] > " + receivedString);

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }
        return true;
    }
}
