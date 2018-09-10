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
       
        try {
            serverSocket = new DatagramSocket(SERVERPORT);
            String serverName = "localhost";
            int serverPort = 9876;
            String wordToGuess = "TESTWORD";

            byte[] receiveData = new byte[1024];
            byte[] sendData = new byte[1024];
            ServerProtocol serverProtocol = new ServerProtocol();
            System.out.println("Awaiting client activity... ");
            boolean serverOccupied = serverProtocol.pokedByClient(serverSocket, serverName, serverPort, wordToGuess);
            
            while(true){
                serverProtocol.pokedByClient(serverSocket, serverName, serverPort, wordToGuess);
            }
           

            /*
             */
        } catch (SocketException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    
}
