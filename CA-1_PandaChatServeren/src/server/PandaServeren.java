/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import sharedprotocol.PandaProtocol;
import utils.Utils;

/**
 *
 * @author steffen, Mikkel & Bente
 */
public class PandaServeren
{
    private static ServerSocket serverSocket;
    private static boolean KeepRunning = true;
    PandaServeren server = this;
    public ConcurrentMap<String, ClientHandler> clientMap = new ConcurrentHashMap();
    PrintWriter out;
    BufferedReader in;
    String usernamestream;

    public static void StopServer()
    {
        KeepRunning = false;
    }

    public void send(String username, String msg)
    {
        String[] parts = msg.split(PandaProtocol.delimiter);
        if (parts.length == 3)
        {
            String part1 = parts[0]; // selve kommandoen
            String part2 = parts[1]; //brugerne
            String part3 = parts[2]; // selve beskeden

            if (part1.equals(PandaProtocol.messageCommand))
            {
                if (part2.equals(PandaProtocol.sendToAllUsers))
                {
                    // her skal alle i listen have beskeden
                    for (ClientHandler value : clientMap.values())
                    {
                        String messageToUser = PandaProtocol.messageCommand + PandaProtocol.delimiter + username + PandaProtocol.delimiter + part3;
                        value.send(messageToUser);
                    }
                } else
                {
                    String[] userString = part2.split(PandaProtocol.userDelimiter); // ved flere brugere splittes disse op, og fÃ¥r hver deres index i userString arrayet.
                    try
                    {
                        for (String users : userString)
                        {
                            ClientHandler value = clientMap.get(users);
                            String messageToUser = PandaProtocol.messageCommand + PandaProtocol.delimiter + username + PandaProtocol.delimiter + part3;
                            value.send(messageToUser);
                        }
                    } catch (Exception e)
                    {
                        ClientHandler thisClient = clientMap.get(username);
                        thisClient.send(PandaProtocol.ErrorMessage);
                    }
                }
            } else
            {
                ClientHandler thisClient = clientMap.get(username);
                thisClient.send(PandaProtocol.ErrorMessage);
            }
        } else
        {
            ClientHandler thisClient = clientMap.get(username);
            thisClient.send(PandaProtocol.ErrorMessage);
        }
    }

    public void removeHandler(ClientHandler client)
    {
        clientMap.remove(client.getUsername(), client);
        // denne blok tilfÃ¸jer users til stringWithUsers og udskriver til alle klienter
        String stringWithUsers = PandaProtocol.userlistCommand + PandaProtocol.delimiter;
        for (ClientHandler value : clientMap.values())
        {
            stringWithUsers += String.valueOf(value.getUsername() + PandaProtocol.userDelimiter);
        }
        for (ClientHandler value : clientMap.values())
        {
            value.send(stringWithUsers.substring(0,stringWithUsers.length() - 1));
        }
    }

    public void Run()
    {
        // henter vÃ¦rdierene af serverIp og port fra vores property fil
        int port = PandaProtocol.port;
        String ip = PandaProtocol.serverIp;
        // Server startede besked med specifik ip og port tilfÃ¸jet i log
        Logger.getLogger(PandaServeren.class.getName()).log(Level.INFO, "Sever started. Listening on: " + port + ", bound to: " + ip);
        try
        {
            // Serversocket instantieres
            serverSocket = new ServerSocket();
            // serverSocket bindes til en specifik adresse, vÃ¦rdien af InetSocketAddress = predefinerede ip og port
            serverSocket.bind(new InetSocketAddress(ip, port));
            do
            {
                // serversocket venter pÃ¥ at nogen prÃ¸ver at oprette forbindelse, og accepterer nÃ¥r det sker, og blockerer indtil da 
                Thread t;
                            Socket socket = serverSocket.accept();
                class HelperThread implements Runnable {
                    Socket socket;
                    public HelperThread(Socket soc) {
                    socket=soc;
                    }
                    @Override
                    public void run() {
                        try {
                            
                            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            usernamestream = in.readLine();
                            String[] part = usernamestream.split(PandaProtocol.delimiter);
                            String username = part[1];
                            
                            if (part[0].equals(PandaProtocol.userCommand))
                            {
                                // bruger den accepterede socket til at oprette en ny client
                                ClientHandler client = new ClientHandler(username, socket, server);
                                // starter en ny trÃ¥d for hver ny client
                                client.start();
                                // tilfÃ¸jere den nye client til det trÃ¥dsikre concurrenthashmap med username som key
                                clientMap.put(username, client);
                                // denne blok tilfÃ¸jer users til stringWithUsers og udskriver til alle klienter
                                String stringWithUsers = PandaProtocol.userlistCommand + PandaProtocol.delimiter;
                                for (ClientHandler value : clientMap.values())
                                {
                                    stringWithUsers += String.valueOf(value.getUsername() + PandaProtocol.userDelimiter);
                                }
                                for (ClientHandler value : clientMap.values())
                                {
                                    value.send(stringWithUsers.substring(0,stringWithUsers.length() - 1));
                                }
                            } else
                            {
                                out = new PrintWriter(socket.getOutputStream(), true);
                                out.println(PandaProtocol.ErrorMessage);
                                socket.close();
                            }       } catch (IOException ex) {
                            Logger.getLogger(PandaServeren.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                };
                t=new Thread(new HelperThread(socket));
                t.start();
                // besked om ny klient, tilfÃ¸jet til log
                Logger.getLogger(PandaServeren.class.getName()).log(Level.INFO, "Connected to a client");

                // en boolean som kan sÃ¦ttes til false med stopserver metoden 
            } while (KeepRunning);
        } catch (IOException ex)
        {
            Logger.getLogger(PandaServeren.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args)
    {

        PandaServeren panda = new PandaServeren();
        panda.Run();

        try
        {
            String logFile = PandaProtocol.logFile;
            Utils.setLogFile(logFile, PandaServeren.class.getName());

        } finally
        {
            Utils.closeLogger(PandaServeren.class.getName());
        }
    }
}
