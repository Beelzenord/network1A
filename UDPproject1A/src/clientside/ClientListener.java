/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package clientside;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 *
 * @author fno
 */
public class ClientListener extends Thread{
    private static final String BYE = "BYE";
    private static final String USEPORT = "USEPORT";
    private static final String ALIVE = "ALIVE";
    private static final String OK = "OK";
    private static final String GUESSRESPONSE = "GUESSRESPONSE";
    private static final int MAXBUFF = 1024;
    
    private DatagramSocket socket;
    private DatagramPacket receivePacket;
    private UserInfo userinfo;
    ClientListener(DatagramSocket clientSocket, UserInfo userInfo) {
       this.socket = clientSocket;
       this.userinfo = userInfo;
   }

    @Override
    public void run() {
        
        System.out.println("Client receiving thread is running");
        
        String sentence = "";
        try {
            while (sentence != null) {
                this.receivePacket = new DatagramPacket(new byte[MAXBUFF], MAXBUFF);
                this.socket.receive(receivePacket);
                String modifiedSentence = new String(receivePacket.getData());
                String[] receive = modifiedSentence.trim().split("/");
                switch (receive[0].trim()) {
                    case BYE:
                        if (receive.length > 1)
                            System.out.println(receive[1]);
                        sentence = null;
                        break;
                    case USEPORT:
                        userinfo.setPortAddress(Integer.parseInt(receive[1]));
                        break;
                    case ALIVE:
                        DatagramPacket sendPacket = new DatagramPacket(ALIVE.getBytes(), ALIVE.getBytes().length, userinfo.getIPAddress(), userinfo.getPortAddress());
                        socket.send(sendPacket); //change*/
                        break;
                    case OK:
                        break;
                    case GUESSRESPONSE:
                        System.out.println(receive[1]);
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
