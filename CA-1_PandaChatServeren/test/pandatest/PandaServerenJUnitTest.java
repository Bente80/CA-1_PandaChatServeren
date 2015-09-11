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
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import server.PandaServeren;
import sharedprotocol.PandaProtocol;
import utils.Utils;

/**
 *
 * @author Bente
 */
public class PandaServerenJUnitTest {
    
    int port = PandaProtocol.port;
    String ip = PandaProtocol.serverIp;
    PrintWriter output;
    Socket socket;
    BufferedReader in;
    PrintWriter output2;
    Socket socket2;
    BufferedReader in2;
    PrintWriter output3;
    Socket socket3;
    BufferedReader in3;
    PrintWriter output4;
    Socket socket4;
    BufferedReader in4;
    
    public PandaServerenJUnitTest() {
    }

    @BeforeClass
    public static void setUp() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                //PandaServeren serveren = new PandaServeren();
                //serveren.Run();
                PandaServeren.main(null);
            }
        };
        new Thread(r).start();
    }

    @AfterClass
    public static void tearDownClass() {
        PandaServeren.StopServer();
    }

    @Test
    public void testConnectionOfOneClientTrue() throws IOException {

        socket = new Socket(ip, port);
        output = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        output.println("USER#Bubber");
        String msg = in.readLine();
        output.println("STOP#");

        String serverResponse = "USERLIST#Bubber,";
        assertTrue(serverResponse.equals(msg));
    }

    @Test
    public void testConnectionOfTwoClientsTrue() throws IOException, InterruptedException {

        Thread.sleep(1000);
        socket = new Socket(ip, port);
        output = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output.println("USER#Bubber");
        in.readLine();
       
        socket2 = new Socket(ip, port);
        output2 = new PrintWriter(socket2.getOutputStream(), true);
        in2 = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
        output2.println("USER#Panda");

        String userlist2 = in2.readLine();
        output.println("STOP#");
        output2.println("STOP#");
        System.out.println("fra test 2, skal indeholde bubber,panda"+userlist2);

        String serverResponse = "USERLIST#Bubber,Panda,";
        assertTrue(serverResponse.equals(userlist2));
    }
    
    @Test
    public void testStopCommand() throws IOException, InterruptedException {

        Thread.sleep(4000);
        socket = new Socket(ip, port);
        output = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output.println("USER#Mikkel");
        in.readLine();

        socket2 = new Socket(ip, port);
        output2 = new PrintWriter(socket2.getOutputStream(), true);
        in2 = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
        output2.println("USER#Torben");
        
        String userlist1 = in2.readLine();
        output.println("STOP#");
        String userlist2 = in2.readLine();
        output2.println("STOP#");

        String serverResponse = "USERLIST#Torben,";
        assertTrue(serverResponse.equals(userlist2));
    }
    
    @Test
    public void testOfMSGCommandToAll() throws IOException, InterruptedException {
        Thread.sleep(10000);
        socket = new Socket(ip, port);
        output = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output.println("USER#Bente");
        in.readLine();

        socket2 = new Socket(ip, port);
        output2 = new PrintWriter(socket2.getOutputStream(), true);
        in2 = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
        output2.println("USER#Steffen");
        in.readLine();
        String steffensUserlist = in2.readLine();
        output.println("MSG#*#Hej alle");
        String bentesOutput = in.readLine();
        
        String steffensOutput = in2.readLine();
        
        assertTrue(bentesOutput.equals("MSG#Bente#Hej alle"));
        
        assertTrue(steffensOutput.equals("MSG#Bente#Hej alle"));
        output.println("STOP#");
        output2.println("STOP#");
    }
    
    @Test
    public void testOfMSGCommandToSpecificUser() throws IOException, InterruptedException {
        Thread.sleep(16000);
        socket = new Socket(ip, port);
        output = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output.println("USER#RainbowDash");
        in.readLine();

        socket2 = new Socket(ip, port);
        output2 = new PrintWriter(socket2.getOutputStream(), true);
        in2 = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
        output2.println("USER#PinkiePie");
        in2.readLine();
        in.readLine();
        
        output.println("MSG#PinkiePie#Hold da helt hest!!");
        String pinkiePiesOutput = in2.readLine();
        assertTrue(pinkiePiesOutput.equals("MSG#RainbowDash#Hold da helt hest!!"));
        output.println("STOP#");
        output2.println("STOP#");
    }
    
    @Test
    public void testOfMSGCommandToTwoOutOfThreeUsers() throws IOException, InterruptedException {
        Thread.sleep(25000);
        socket = new Socket(ip, port);
        output = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output.println("USER#Tom Brady");
        in.readLine();

        socket2 = new Socket(ip, port);
        output2 = new PrintWriter(socket2.getOutputStream(), true);
        in2 = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
        output2.println("USER#Matthew Slater");
        in2.readLine();
        in.readLine();
        
        socket3 = new Socket(ip, port);
        output3 = new PrintWriter(socket3.getOutputStream(), true);
        in3 = new BufferedReader(new InputStreamReader(socket3.getInputStream()));
        output3.println("USER#Bill Belichick");
        
        in.readLine();
        
        socket4 = new Socket(ip, port);
        output4 = new PrintWriter(socket4.getOutputStream(), true);
        in4 = new BufferedReader(new InputStreamReader(socket4.getInputStream()));
        output4.println("USER#Rob Gronkowski");
        
        in.readLine();
        
        output.println("MSG#Matthew Slater,Rob Gronkowski#Hi guys");
        Thread.sleep(5000);
        String førsteBesked = in2.readLine();
        String andenBesked = in2.readLine();
        String MatthewsOutput = in2.readLine();
        String RobsOutput = in4.readLine();
        String BillsOutput = in3.readLine();
        
        assertTrue(MatthewsOutput.equals("MSG#Tom Brady#Hi guys"));
        assertTrue(RobsOutput.equals("USERLIST#Matthew Slater,Bill Belichick,Rob Gronkowski,Tom Brady,"));
        assertFalse(BillsOutput.equals("MSG#Tom Brady#Hi guys"));
        
        output.println("STOP#");
        output2.println("STOP#");
        output3.println("STOP#");
        output4.println("STOP#");
    }   
}