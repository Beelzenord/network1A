/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package serverside;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

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
        String secretWord="TESTWORD";
        int port= SERVERPORT;
        try {
             if(args.length>=1){
                port = UtilServerside.assignPort(args,SERVERPORT);
                secretWord = UtilServerside.initializeWord(args);
            } 
             if (args.length > 2) {
                 throw new IndexOutOfBoundsException();
             }
            String serverName = "localhost";

            serverSocket = new DatagramSocket(port, InetAddress.getByName(serverName));
            ServerProtocol serverProtocol = new ServerProtocol();
            
            System.out.println("Server listening at: " + serverName + ". On port: " + port);
            while(true){
                System.out.println("Awaiting client activity... ");
                serverProtocol.pokedByClient(serverSocket, serverName, port, secretWord);
            }
        } catch (SocketException ex) {
            System.out.println("Could not start server on port: " + port);
        } catch (UnknownHostException ex) {
            System.out.println("Servername is not known");
        } catch (IOException ex) {
            System.out.println("Could not handle clients, shuting down");
        } catch (IllegalArgumentException ex) {
            System.out.println("Valid port is between 1025 and 65535");
        } catch (IndexOutOfBoundsException ex) {
            System.out.println("Maximum 2 arguments, guessing word and port number");
        }  
        finally {
            if (serverSocket != null) {
                serverSocket.close();
            }
        }

    }
    
    
}
