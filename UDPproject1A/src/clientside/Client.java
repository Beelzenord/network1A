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
    public static void main(String args[]) throws Exception
   {
      BufferedReader inFromUser =
         new BufferedReader(new InputStreamReader(System.in));
      DatagramSocket clientSocket = new DatagramSocket();
      InetAddress IPAddress = InetAddress.getByName("localhost");
      byte[] sendData = new byte[1024];
      byte[] receiveData = new byte[1024];
      boolean linkedWithServer = false;
      if(pokeServer(IPAddress,clientSocket)){
          String sentence = inFromUser.readLine();
          while(sentence!=null){
              sendData = sentence.getBytes();
              DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
              clientSocket.send(sendPacket);
              DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
              clientSocket.receive(receivePacket); 
              String modifiedSentence = new String(receivePacket.getData());
              System.out.println("FROM SERVER:" + modifiedSentence);
              sentence = new String(inFromUser.readLine());
              System.out.println("sentence " +sentence);
          }
           clientSocket.close();
      }
      
      
      
      
      
     
   }
    /**
     *This is the protocol that initiates contact with the server if the protocol
     * is fulfilled then the server accepts the client. if it fails then there is not connection
     */
    public static boolean pokeServer(InetAddress IPAddress, DatagramSocket clientSocket){
        byte[] sendData = new byte[1024];
        byte[] receiveData = new byte[1024];
        String hello = "HELLO";
        String ok = "OK";
        String start = "START";
        String ready = "READY";
        String receivedString;
        /**
         *Send Hello
         */
        sendData = hello.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
        try {
            clientSocket.send(sendPacket); // sends hello
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket); // receives ok
            receivedString = new String(receivePacket.getData());
            System.out.println("[From Server] > " + receivedString );
            if(!receivedString.trim().equals(ok)){
                System.out.println("not ok");
                return false;
            } 
            sendData = start.getBytes();
            sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
            clientSocket.send(sendPacket); // sends start
            receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            receivedString = new String(receivePacket.getData());
            System.out.println("[From Server] > " + receivedString );
            
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
}
