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

    public static final int SERVERPORT = 9876;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        DatagramSocket serverSocket;
        Boolean isBusy = false;
       
        try {
            serverSocket = new DatagramSocket(SERVERPORT);

            byte[] receiveData = new byte[1024];
            byte[] sendData = new byte[1024];
            ServerProtocol serverProtocol = new ServerProtocol();
            System.out.println("Awaiting client activity... ");
            boolean serverOccupied = serverProtocol.pokedByClient(serverSocket, isBusy);
            
            while(true){
                serverProtocol.pokedByClient(serverSocket, isBusy);
            }
           

            /*
             */
        } catch (SocketException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    
}
