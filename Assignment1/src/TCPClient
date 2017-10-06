package com.tcp.client;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.Scanner;
import java.io.FileWriter;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class TCPClient {

    public static long id = 1;
    static String serverIp;
    static int port;


    static class Client extends Thread implements Runnable {


        @Override
        public void run() {
            try {
                FileWriter fout = new FileWriter("filename.txt", true);
                //PrintWriter pw = new PrintWriter(fout);

                String response, pac;
                String message;
                long[] latencytable;
                latencytable = new long[300];
                long senttime;
                long receivetime;
                int count = 0;

                // CSVWriter writer = new CSVWriter(new FileWriter("yourfile.csv"), '\t');


                Socket socket = new Socket(serverIp, port);
                int i = 0;

                DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                BufferedReader server = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );



                while (true) {


                    message = "HELLO " + socket.getInetAddress() + " " + socket.getPort() + " " + System.lineSeparator();
                    //id++;
                    senttime = System.nanoTime();
                    output.writeBytes(message);

                    response = server.readLine();
                    receivetime = System.nanoTime();
                    // latencytable[count]=receivetime-senttime;

                    System.out.println(response);
                    latencytable[count] = receivetime - senttime;
                    count++;
                }


            } catch (IOException e) {
                e.printStackTrace();
            }


        }


    }


    public static void main(String args[]) {

        serverIp = args[0];
        port = Integer.parseInt(args[1]);
        int i;


        for (i = 0; i < 10; i++) {
            Client client = new Client();
            client.start();

        }


    }

}
