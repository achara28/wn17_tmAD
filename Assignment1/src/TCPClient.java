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



/**
 *This class simulates at minimum of 10 tcp clients who establishes a connection 
 *with a server hosted on a virtual machine and requesting data from him. It is implemented using 
 *multithreading code. Every tcp client is a thread and clients are running concurrently and every
 *client generate a series of requests towards the server.The client sends a hello message which comprises of
 *a "hello" string the client's ip and the port and the client id. 
 *
 * @author Andreas Charalampous Danakis Christodoulides
 *
 */


public class TCPClient {

    public static long id = 1;
    static String serverIp;
    static int port;

/**Inner class Client which extends threads and implements runnable interface for multithreading*/
    static class Client extends Thread implements Runnable {

/**When an object implementing interface Runnable is used to create a thread, starting the 
 *thread causes the object's run method to be called in that separately executing thread.
 *The general contract of the method run is that it may take any action whatsoever.
 */
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


                // int test = server.read();

                final Lock _mutex = new ReentrantLock(true);

                _mutex.lock();

               /**protect code using a mutex because of multithreading (protect clients id variable)*/ 
                output.write((int) id);
                long prID = id;
                id++;

                _mutex.unlock();


                while (true) {


                    message = "HELLO " + socket.getInetAddress() + " " + socket.getPort() + " " + prID + System.lineSeparator();
                
                    senttime = System.nanoTime();
                    output.writeBytes(message);

                    response = server.readLine();
                    receivetime = System.nanoTime();
                    // latencytable[count]=receivetime-senttime;

                    if (response.equals("end")) {
                        
                        socket.close();
                        System.out.println(Arrays.toString(latencytable));
                        int x = 0;
                        long sum = 0;
                        while (latencytable[x] != 0) {
                            sum = sum + latencytable[x];
                            x++;
                            if (x == 300)
                                break;
                        }
                        
                        System.out.println("The Sum Latency is: " + sum / 1000000);
                        System.out.println("The Average Latency is: " + (sum / x) / 1000000);

                        fout.write(String.valueOf((sum / x) / 1000000) + "\n");
                        fout.close();

                        break;
                    }
                    System.out.println(response);
                    latencytable[count] = receivetime - senttime;
                    count++;
                }


            } catch (IOException e) {
                e.printStackTrace();
            }


        }


    }

/**Main method reading and parsing from command line and starts the simulation
  * To run the program execute java TCPClient _server's IP_ _Port_
  *
  * @param args arguments from command lines
  *
  *	
  */
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
