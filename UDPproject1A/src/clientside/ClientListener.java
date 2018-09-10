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
        
        System.out.println("Client receiving thread is running");
        
        String sentence = "";
        try {
            while (sentence != null) {
                this.receivePacket = new DatagramPacket(new byte[1024], 1024);
                this.socket.receive(receivePacket);
                String modifiedSentence = new String(receivePacket.getData());
                String[] receive = modifiedSentence.trim().split("/");
                switch (receive[0]) {
                    case "BYE":
                        if (receive.length > 1)
                            System.out.println(receive[1]);
                        sentence = null;
                        break;
                    case "USEPORT":
                        userinfo.setPortAddress(Integer.parseInt(receive[1]));
                        break;
                   case "ALIVE":
                        DatagramPacket sendPacket = new DatagramPacket("ALIVE".getBytes(), "ALIVE".getBytes().length, userinfo.getIPAddress(), userinfo.getPortAddress());
                        socket.send(sendPacket); //change*/
                        break;
                        
                    default:
                        System.out.println("FROM SERVER:" + modifiedSentence);
                        break;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }
        finally {
            if (socket != null)
                socket.close();
            System.exit(0);
        }
               
    }
    
    
    
    
}
