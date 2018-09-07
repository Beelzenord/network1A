/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverside;

import java.io.IOException;
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
public class Server {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
          DatagramSocket serverSocket;
        try {
            serverSocket = new DatagramSocket(9876);
            
            
            byte[] receiveData = new byte[1024];
            byte[] sendData = new byte[1024];
            ServerProtocol serverProtocol = new ServerProtocol();
            System.out.println("Awaiting client activity... ");
            
            if(serverProtocol.pokedByClient(serverSocket)){
                
                while(true)
               {
                   // https://www.baeldung.com/udp-in-java
                  DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                  receivePacket = new DatagramPacket(receiveData, receiveData.length);
                  serverSocket.receive(receivePacket);
                  //= new String(packet.getData(), 0, packet.getLength());
                  String sentence = new String( receivePacket.getData(), 0 ,receivePacket.getLength() );
                  System.out.println("RECEIVED: " + sentence);
                  InetAddress IPAddress = receivePacket.getAddress();
                  int port = receivePacket.getPort();
                  String capitalizedSentence = sentence.toUpperCase();
                  sendData = capitalizedSentence.getBytes();
                  DatagramPacket sendPacket =
                  new DatagramPacket(sendData, sendData.length, IPAddress, port);
                  serverSocket.send(sendPacket);
                  
               }
                
            }
            
            /*
            */
            
        } catch (SocketException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } 
            
    }
    
    
    
}
