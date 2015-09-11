/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pandatest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Properties;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import server.PandaServeren;
import utils.Utils;

/**
 *
 * @author Bente
 */
public class PandaServerenJUnitTest {

//    static PandaServeren serveren;
    private static final Properties properties = Utils.initProperties("pandaProperty.properties");
    int port = Integer.parseInt(properties.getProperty("port"));
    String ip = properties.getProperty("serverIp");
//    PrintWriter output;
//    Socket socket;
//    BufferedReader in;
//    PrintWriter output2;
//    Socket socket2;
//    BufferedReader in2;
//    PrintWriter output3;
//    Socket socket3;
//    BufferedReader in3;
//    PrintWriter output4;
//    Socket socket4;
//    BufferedReader in4;
    
    ///// rettet i test
    
    public PandaServerenJUnitTest() {
    }

    @Before
    public void setUp() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
//                PandaServeren serveren = new PandaServeren();
//                serveren.Run();
                PandaServeren.main(null);
            }
        };
        new Thread(r).start();
    }

    @After
    public void tearDownClass() {
        PandaServeren.StopServer();
    }

    @Test
    public void testConnectionOfOneClientTrue() throws IOException, InterruptedException {

        Socket socket = new Socket(ip, port);
        PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        output.println("USER#Bubber");
        String msg = in.readLine();

        String serverResponse = "USERLIST#Bubber,";
        assertTrue(serverResponse.equals(msg));
         Thread.sleep(1000);
        output.println("STOP#");
                 Thread.sleep(1000);


    }

    @Test
    public void testConnectionOfTwoClientsTrue() throws IOException, InterruptedException {

        Socket socket = new Socket(ip, port);
        PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output.println("USER#Bubber");

        Socket socket2 = new Socket(ip, port);
        PrintWriter output2 = new PrintWriter(socket2.getOutputStream(), true);
        BufferedReader in2 = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
        output2.println("USER#Panda");

        String userlist2 = in2.readLine();
        output.println("STOP#");
        output2.println("STOP#");

        String serverResponse = "USERLIST#Bubber,Panda,";
        assertTrue(serverResponse.equals(userlist2));
         Thread.sleep(1000);
    }
    
    @Test
    public void testStopCommand() throws IOException, InterruptedException {

        Socket socket = new Socket(ip, port);
        PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output.println("USER#Mikkel");

        Socket socket2 = new Socket(ip, port);
        PrintWriter output2 = new PrintWriter(socket2.getOutputStream(), true);
        BufferedReader in2 = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
        output2.println("USER#Torben");
        
        String userlist1 = in2.readLine();
        output.println("STOP#");
                                  System.out.println("Første gang:"+userlist1);
        String userlist2 = in2.readLine();
                                  System.out.println("Anden gang: "+userlist2);
        output2.println("STOP#");

        String serverResponse = "USERLIST#Torben,";
        assertTrue(serverResponse.equals(userlist2));
         Thread.sleep(1000);
    }
    
    @Test
    public void testOfMSGCommandToAll() throws IOException, InterruptedException {
        Socket socket = new Socket(ip, port);
        PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output.println("USER#Bente");

        Socket socket2 = new Socket(ip, port);
        PrintWriter output2 = new PrintWriter(socket2.getOutputStream(), true);
        BufferedReader in2 = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
        output2.println("USER#Steffen");
        
        output.println("MSG#*#Hej alle");
        String bentesOutput = in.readLine();
        String steffensOutput = in2.readLine();
        assertTrue(bentesOutput.equals("MSG#Bente#Hej alle"));
        assertTrue(steffensOutput.equals("MSG#Bente#Hej alle"));
        output.println("STOP#");
        output2.println("STOP#");
        Thread.sleep(1000);
    }
//    
//    @Test
//    public void testOfMSGCommandToSpecificUser() throws IOException {
//        socket = new Socket(ip, port);
//        output = new PrintWriter(socket.getOutputStream(), true);
//        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//        output.println("USER#RainbowDash");
//
//        socket2 = new Socket(ip, port);
//        output2 = new PrintWriter(socket2.getOutputStream(), true);
//        in2 = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
//        output2.println("USER#PinkiePie");
//        
//        output.println("MSG#PinkiePie#Hold da helt hest!!");
//        String pinkiePiesOutput = in2.readLine();
//        assertTrue(pinkiePiesOutput.equals("MSG#RainbowDash#Hold da helt hest!!"));
//        output.println("STOP#");
//        output2.println("STOP#");
//    }
//    
//    @Test
//    public void testOfMSGCommandToTwoOutOfThreeUsers() throws IOException {
//        socket = new Socket(ip, port);
//        output = new PrintWriter(socket.getOutputStream(), true);
//        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//        output.println("USER#Tom Brady");
//
//        socket2 = new Socket(ip, port);
//        output2 = new PrintWriter(socket2.getOutputStream(), true);
//        in2 = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
//        output2.println("USER#Matthew Slater");
//        
//        socket3 = new Socket(ip, port);
//        output3 = new PrintWriter(socket3.getOutputStream(), true);
//        in3 = new BufferedReader(new InputStreamReader(socket3.getInputStream()));
//        output3.println("USER#Bill Belichick");
//        
//        socket4 = new Socket(ip, port);
//        output4 = new PrintWriter(socket4.getOutputStream(), true);
//        in4 = new BufferedReader(new InputStreamReader(socket4.getInputStream()));
//        output4.println("USER#Rob Gronkowski");
//        
//        output.println("MSG#Matthew Slater, Rob Gronkowski#Hi guys");
//        String MatthewsOutput = in2.readLine();
//        String BillsOutput = in3.readLine();
//        String RobsOutput = in4.readLine();
//        
//        assertTrue(MatthewsOutput.equals("MSG#Tom Brady#Hi guys"));
//        assertTrue(RobsOutput.equals("MSG#Tom Brady#Hi guys"));
//        assertFalse(BillsOutput.equals("MSG#Tom Brady#Hi guys"));
//        
//        output.println("STOP#");
//        output2.println("STOP#");
//        output3.println("STOP#");
//        output4.println("STOP#");
//
//    }
    
}
