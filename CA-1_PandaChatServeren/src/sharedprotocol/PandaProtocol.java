/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sharedprotocol;

/**
 *
 * @author Mikkel, Steffen & Bente
 */
public class PandaProtocol {
    
    public static final String STOP = "STOP#"; 
    public static final String messageCommand = "MSG";
    public static final String delimiter = "#";
    public static final String userDelimiter = ",";
    public static final String sendToAllUsers = "*";
    public static final String userlistCommand ="USERLIST";
    public static final String userCommand ="USER";
    public static final String ErrorMessage ="The server is unable to process your command, please try again";
    public static final String loginErrorMessage ="Login failed";
     public static final String serverIp = "localhost";
//    public static final String serverIp = "10.146.84.83";
    public static final int port = 9090;
    public static final String logFile = "PandaLogFile.txt";
}