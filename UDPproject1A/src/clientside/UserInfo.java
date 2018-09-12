/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package clientside;
import java.net.InetAddress;
/**
 *
 * @author fno
 */
public class UserInfo {
    private InetAddress IPAddress;
    private int portAddress;
    

    public UserInfo(InetAddress IPAddress, int portAddress) {
        this.IPAddress = IPAddress;
        this.portAddress = portAddress;
    }

    public InetAddress getIPAddress() {
        return IPAddress;
    }
    
    public int getPortAddress() {
        return portAddress;
    }

    public void setPortAddress(int portAddress) {
        this.portAddress = portAddress;
    }
    
    
}
