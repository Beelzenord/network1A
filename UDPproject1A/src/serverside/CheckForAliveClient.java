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
import java.util.TimerTask;

/**
 *
 * @author Niklas
 */
public class CheckForAliveClient extends TimerTask {
    private DatagramSocket socket;
    private InetAddress address;
    private int port;
    private String message;
    
    public CheckForAliveClient(DatagramSocket socket, InetAddress address, int port, String message) {
        this.socket = socket;
        this.address = address;
        this.port = port;
        this.message = message;
    }
    @Override
    public void run() {
        DatagramPacket sendPacket = null;
        try {
            sendPacket = new DatagramPacket(message.getBytes(), message.getBytes().length, address, port);
            socket.send(sendPacket);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
}
