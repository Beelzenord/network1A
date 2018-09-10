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
import utilities.Util;

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
        String secretWord="";
        int port= SERVERPORT;
        try {
            

             if(args.length>=1){
              //  port = Integer.parseInt(args[0]);
                port = Util.assignPort(args,SERVERPORT);
                secretWord = Util.initializeWord(args);
            }
            serverSocket = new DatagramSocket(port);
            System.out.print ("Opened port " + port + " ");
            
            String serverName = "localhost";
            //int serverPort = 9876;
           

            byte[] receiveData = new byte[1024];
            byte[] sendData = new byte[1024];
            ServerProtocol serverProtocol = new ServerProtocol();
            System.out.println("Awaiting client activity... ");
            boolean serverOccupied = serverProtocol.pokedByClient(serverSocket, serverName, port, secretWord);
            
            while(true){
                serverProtocol.pokedByClient(serverSocket, serverName, port, secretWord);
            }
           

            /*
             */
        } catch (SocketException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    
}
