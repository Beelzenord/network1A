/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverside;

import java.net.DatagramSocket;
import java.net.SocketException;
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
        
        DatagramSocket serverSocket = null;
        String secretWord="test";
        int port= SERVERPORT;
        try {
             if(args.length>=1){
                port = Util.assignPort(args,SERVERPORT);
                secretWord = Util.initializeWord(args);
            }
            serverSocket = new DatagramSocket(port);
            System.out.print ("Opened port " + port + " ");
            
            String serverName = "localhost";
            ServerProtocol serverProtocol = new ServerProtocol();
            
            while(true){
                System.out.println("Awaiting client activity... ");
                serverProtocol.pokedByClient(serverSocket, serverName, port, secretWord);
            }
        } catch (SocketException ex) {
            System.out.println("Could not start server on port: " + port);
        } finally {
            if (serverSocket != null) {
                serverSocket.close();
            }
        }

    }
    
    
}
