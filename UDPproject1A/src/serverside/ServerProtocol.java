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
    
    public boolean pokedByClient( DatagramSocket socket){
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
            if(!sentence.trim().equals("HELLO")){
                sendData = "Error at hello".getBytes();
                sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                socket.send(sendPacket);
                return false;
            }
            else{
                System.out.println("[From Client] > " + sentence);
                sendData = "OK".getBytes();
                sendPacket =new DatagramPacket(sendData, sendData.length, IPAddress, port);
                socket.send(sendPacket);
            }
            receivePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receivePacket);
            sentence = new String(receivePacket.getData());
            if((!sentence.trim().equals("START")) || (!receivePacket.getAddress().equals(IPAddress)) || !(receivePacket.getPort()==port)){
                  System.out.println(sentence);
                 sendData = "Error at START".getBytes();
                 sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                 socket.send(sendPacket);
                 return false;
            }
            else{
                generateString();
                System.out.println("[From Client] > " + sentence);
                String ready = "READY " +generatedString.length();
                sendData = ready.getBytes();
                sendPacket =new DatagramPacket(sendData, sendData.length, IPAddress, port);
                socket.send(sendPacket);
            }
            
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
       return true;
    }

    private void generateString() {
                this.generatedString = "BROWN";
    }
}
