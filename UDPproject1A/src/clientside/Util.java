/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package clientside;


/**
 *
 * @author fno
 */
public class Util {
    
    public static int assignPort(String[] args, int currentPort) {
       for(int i = 0 ; i < args.length ; i++){
           String tmp = args[i];
           if(tmp.chars().allMatch(Character::isDigit)){
               return Integer.parseInt(tmp);
           }
       }
       return currentPort;
    }
}
