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
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author fno
 */
public class ServerProtocol {

    private String generatedString;
    private Thread th;
    public ServerProtocol(){
    }
    public boolean pokedByClient(DatagramSocket socket, String serverName, int serverPort, String wordToGuess) {
        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];
        DatagramSocket timerSocket = null;
        Timer sendDead = new Timer();
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        DatagramPacket sendPacket = null;
        try {
            
            socket.receive(receivePacket);
            timerSocket = new DatagramSocket(serverPort -2);
            createTimers(sendDead, timerSocket, InetAddress.getByName(serverName), serverPort);
            
            System.out.println("Poked by client");
            String sentence = new String(receivePacket.getData()).trim();
            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();
            //if there is already a thread handling a separate client
             if (th!=null){
                 if(th.isAlive()){
                     rejection(socket, receivePacket);
                     return false;
                 }
            }
            if (!sentence.equals("HELLO")) {
                if (sentence.equals("TIMEOUT"))
                    sendData = "TIMEOUT/Error at hello, TOO SLOW".getBytes();
                else
                    sendData = "ERROR/Error at hello".getBytes();
                sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                socket.send(sendPacket);
                sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort());
                socket.send(sendPacket);
                sendDead.cancel();
                if (timerSocket != null)
                    timerSocket.close();
                return false;
            } else {
                System.out.println("[From Client] > " + sentence);
                sendData = "OK".getBytes();
                sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                socket.send(sendPacket);
            }
            receivePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receivePacket);
            sentence = new String(receivePacket.getData()).trim();
            if ((!sentence.equals("START")) || (!receivePacket.getAddress().equals(IPAddress)) || !(receivePacket.getPort() == port)) {
                System.out.println(sentence);
                if (sentence.equals("TIMEOUT"))
                    sendData = "TIMEOUT/Error at START, TOO SLOW".getBytes();
                else
                    sendData = "ERROR/Error at START".getBytes();
                sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                socket.send(sendPacket);
                sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort());
                socket.send(sendPacket);
                sendDead.cancel();
                if (timerSocket != null)
                    timerSocket.close();
                return false;
            } else {
                System.out.println("[From Client] > " + sentence);
                String ready = "READY " + wordToGuess.length();
                sendData = ready.getBytes();
                sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                socket.send(sendPacket);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sendDead.cancel();
            if (timerSocket != null)
                timerSocket.close();
        }
        startClientThread(receivePacket, serverName, serverPort, wordToGuess);
        return true;
    }
    public void rejection(DatagramSocket socket, DatagramPacket receivePacket){
        System.out.println("Rejection");
        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];
        String rejectionMessage = new String(receivePacket.getData()).trim();
        try {
            
            System.out.println("(our client) " + rejectionMessage);
            String string = "BUSY/A client is already connected";
            sendData = string.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort());
            socket.send(sendPacket);
        } catch (IOException ex) {
            Logger.getLogger(ServerProtocol.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void startClientThread(DatagramPacket receivePacket, String serverName, int serverPort, String wordToGuess) {
        th = new Thread(new GameServer(receivePacket, serverName, serverPort, wordToGuess));
        th.start();
        System.out.println("returning from connectToClient");
    }
    public boolean getGameRunning(){
        return th.isAlive();
    }
   
    private void createTimers(Timer sendAlive, DatagramSocket timerSocket, 
                InetAddress serverIP, int serverPort) {
        // start in 8 seconds and then run every 8 seconds
        sendAlive.schedule(new CheckForAliveClient(timerSocket, serverIP, serverPort, "TIMEOUT"), 8000);
    }
    

}
