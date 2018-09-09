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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author fno
 */
public class ServerProtocol {

    private String generatedString;
    private Thread th;
    private boolean busy;
    public ServerProtocol(){
       //  th = new Thread(new GameServer(receivePacket, isBusy, 9876));
    }
    public boolean pokedByClient(DatagramSocket socket, Boolean isBusy) {
        this.busy = isBusy;
        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        DatagramPacket sendPacket = null;
        System.out.println("poked by client");
        try {
            socket.receive(receivePacket);
            String sentence = new String(receivePacket.getData());
            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();
            //if there is already a thread handling a separate client
             if (th!=null){
                 System.out.println("thread not null");
                 if(th.isAlive()){
                     System.out.println("Thread is alive");
                     rejection(socket, receivePacket);
                     return false;
                 }
            }
            if (!sentence.trim().equals("HELLO")) {
                sendData = "Error at hello".getBytes();
                sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                socket.send(sendPacket);
                return false;
            } else {
                System.out.println("[From Client] > " + sentence);
                sendData = "OK".getBytes();
                sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                socket.send(sendPacket);
            }
            receivePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receivePacket);
            sentence = new String(receivePacket.getData());
            if ((!sentence.trim().equals("START")) || (!receivePacket.getAddress().equals(IPAddress)) || !(receivePacket.getPort() == port)) {
                System.out.println(sentence);
                sendData = "Error at START".getBytes();
                sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                socket.send(sendPacket);
                return false;
            } else {
                generateString();
                System.out.println("[From Client] > " + sentence);
                String ready = "READY " + generatedString.length();
                sendData = ready.getBytes();
                sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                socket.send(sendPacket);
            }

        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        startClientThread(receivePacket, isBusy);
        return true;
    }
    public void rejection(DatagramSocket socket, DatagramPacket receivePacket){
        System.out.println("Rejection");
        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];
        String rejectionMessage = new String(receivePacket.getData());
        try {
            
            System.out.println("(our client) " + rejectionMessage);
            String string = "A client is already connected";
            sendData = string.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort());
            socket.send(sendPacket);
        } catch (IOException ex) {
            Logger.getLogger(ServerProtocol.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void generateString() {
        this.generatedString = "BROWN";
    }

    private void startClientThread(DatagramPacket receivePacket, Boolean isBusy) {
        th = new Thread(new GameServer(receivePacket, isBusy, 9876, "TESTWORD"));
        th.start();
        System.out.println("returning from connectToClient");
    }
    public boolean getGameRunning(){
        return th.isAlive();
    }
   
    /*
    public boolean getThreadStatus(){
       return this.th.isAlive();
    }*/

}
