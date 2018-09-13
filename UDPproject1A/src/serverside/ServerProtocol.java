/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package serverside;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Timer;

/**
 *
 * @author fno
 */
public class ServerProtocol {
    private static final int MAXBUFF = 1024;
    private Thread th;
    public ServerProtocol(){
    }
    public boolean pokedByClient(DatagramSocket socket, String serverName, int serverPort, String wordToGuess) throws IOException {
        byte[] receiveData = new byte[MAXBUFF];
        byte[] sendData = new byte[MAXBUFF];
        DatagramSocket timerSocket = null;
        Timer sendDead = new Timer();
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        DatagramPacket sendPacket = null;
        try {
            socket.receive(receivePacket);
            System.out.println("Poked by client");
            //if there is already a thread handling a separate client
            if (th!=null){
                 if(th.isAlive()){
                     rejection(socket, receivePacket);
                     return false;
                 }
            }
            
            timerSocket = new DatagramSocket(serverPort -2);
            createTimers(sendDead, timerSocket, InetAddress.getByName(serverName), serverPort);
            String sentence = new String(receivePacket.getData()).trim();
            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();
            
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
            sendDead.cancel();
            if (timerSocket != null)
                timerSocket.close();
            throw new IOException();
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
        byte[] sendData = new byte[MAXBUFF];
        String rejectionMessage = new String(receivePacket.getData()).trim();
        try {
            
            System.out.println("(our client) " + rejectionMessage);
            String string = "BUSY/A client is already connected";
            sendData = string.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort());
            socket.send(sendPacket);
        } catch (IOException ex) {
            System.out.println("Could not send rejection message to client");
        }
    }

    private void startClientThread(DatagramPacket receivePacket, String serverName, int serverPort, String wordToGuess) {
        System.out.println("STARTING CLIENT TRHEAD");
        th = new Thread(new GameServer(receivePacket, serverName, serverPort, wordToGuess));
        th.start();
    }
    public boolean getGameRunning(){
        return th.isAlive();
    }
   
    private void createTimers(Timer sendAlive, DatagramSocket timerSocket, 
                InetAddress serverIP, int serverPort) {
        // start in 8 seconds, i.e client has 8 seconds to complete the handshake
        sendAlive.schedule(new CheckForAliveClient(timerSocket, serverIP, serverPort, "TIMEOUT"), 8000);
    }
    

}
