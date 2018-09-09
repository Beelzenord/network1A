/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientside;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author fno
 */
public class ClientListener extends Thread{
    private DatagramSocket socket;
    private DatagramPacket receivePacket;
    private int port;
    private UserInfo userinfo;
    ClientListener(DatagramSocket clientSocket, UserInfo userInfo) {
       this.socket = clientSocket;
       this.userinfo = userInfo;
   }

    ClientListener(DatagramSocket clientSocket, Client aThis) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
        
        System.out.println("Client thread is running");
        
        String sentence = "";
            while (sentence != null) {
            try {
                this.receivePacket = new DatagramPacket(new byte[1024], 1024);
                this.socket.receive(receivePacket);
                String modifiedSentence = new String(receivePacket.getData());
                System.out.println("got : " + modifiedSentence);
                String[] receive = modifiedSentence.trim().split("Q");
                switch (receive[0]) {
                    case "BYE":
                        System.exit(0);
                    case "USEPORT":
                      //  port = Integer.parseInt(receive[1]);
                        userinfo.setPortAddress(Integer.parseInt(receive[1]));
                        System.out.println("port: " + port);
                        break;
                   case "ALIVE":
                        System.out.println("responding to ALIVE");
                        DatagramPacket sendPacket = new DatagramPacket("ALIVE".getBytes(), "ALIVE".getBytes().length, userinfo.getIPAddress(), userinfo.getPortAddress());
                        socket.send(sendPacket); //change*/
                        break;
                        
                    default:
                        System.out.println("FROM SERVER:" + modifiedSentence);
                }
            } catch (IOException ex) {
                Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, null, ex);
            }
          }
               
    }
    
    
    
    
}
