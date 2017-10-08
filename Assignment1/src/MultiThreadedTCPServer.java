package com.tcp.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



/**
* The server program wwaits for a number of clients to connect to it. Once the 
*server receives a message from client it immediately generates a response message
*and sends it back to the client . The response message comprises of a "Welcome"  client_ID
*and a variable random size as payload (for simulation reasons we used a string and its size is between 
*300kb and 2mb.
@author Andreas Charalampous Danakis Christodoulides
 */
public class MultiThreadedTCPServer {
    public static final int timeinterval = 1 * 1000;
    public static int counter = 0;
    public static long start = System.currentTimeMillis();
    public static int sum = 0 ;
    public static int count=0;
    public static int end_count=0;
    public static int limit;
    
     /**This function is implemented to generated random numbers between 300 and 2000.
      *The random number is calculated by considering the current system time on the server
      * when generating the response message.
      *   
      * @return num the random number generated
      */ 
    public static int RandomNumber() {
	    
        long num;
        int max = 2000, min = 300;
        int range = (max - min) + 1;
    
        Random rand = new Random(System.nanoTime());
        num = rand.nextInt(range) + min;

        return (int) num;
    }

    /** This function is implemented to create the payload size according to the RandomNumber function. 
     * It is done by creating a stringbuilder and appending a hashtag(#) for randomNumber * 1024 (convert KB to B)    
     *
     * @return sb the string payload
     */
    public static String payload() {
        String pl = "";

        long size = RandomNumber() * 1024;
        StringBuilder sb = new StringBuilder();

        while (size > 0) {
            sb.append('#');

            size--;
        }
        return sb.toString();
    }

    private static class TCPWorker implements Runnable {

        private Socket client;
        private String clientbuffer;
        private int clientid;
        //private int limit;

        public TCPWorker(Socket client) {
            this.client = client;
            this.clientbuffer = "";
           // this.clientid = id;
            //this.limit = limit;
        }

        @Override
        public void run() {
            int requests;
            try {


                System.out.println("Client connected with: " + this.client.getInetAddress());

                DataOutputStream output = new DataOutputStream(client.getOutputStream());
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(this.client.getInputStream())
                );

             	this.clientid=reader.read(); //for the ID

                this.clientbuffer = reader.readLine();
                requests = 1;

                while (this.clientbuffer != null) {

                    System.out.println(this.clientbuffer);
                    

                    //System.out.println(counter);
                    output.writeBytes("Welcome " +  this.clientid +  " " + this.client.getInetAddress() + " " + payload() + System.lineSeparator());
		    counter++;
		    end_count++;
		    //System.out.println("The end_count is :"+ end_count);

                    if ((System.currentTimeMillis() - start) > timeinterval) {

                        System.out.println(counter);
			sum = sum + counter;
			count++;

                    // long  memory_utilization= Runtime.getRuntime().freeMemory()/Runtime.getRuntime().totalMemory();

            //Used for metrics
            System.out.println("The current sum is: "+sum);
			System.out.println("The current count is: "+count);
			System.out.println("The current throughput is: "+ (double) sum/count);

            System.out.println("The current memory utilization is:  " + (double)Runtime.getRuntime().freeMemory()/Runtime.getRuntime().totalMemory());
          //  System.out.println("The current memory utilization is: " +Runtime.getRuntime().totalMemory());
                        counter = 0;
                        start = System.currentTimeMillis();
                    }


                    this.clientbuffer = reader.readLine();
                    // System.out.println(requests);
                    if ( end_count >= limit ) {
			 //System.out.println("MPENW "+ end_count + " limit: "+limit);

                        output.writeBytes("end\n");
                        break;
                    }
                    requests++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public static ExecutorService TCP_WORKER_SERVICE = Executors.newFixedThreadPool(10);

    /**The main method starts the server and accepts the sockets created by clients
     *  To run the program execute java MultiThreadedTCPServer _Server's Port_ _Repetitions_
     *@param args arguments from command lines
     *
    */



    public static void main(String args[]) {
        try {


            int port = Integer.parseInt(args[0]);
             limit = Integer.parseInt(args[1]);
	       System.out.println("The Limit is :"+limit);
            ServerSocket socket = new ServerSocket(port);
            System.out.println("Server listening to: " + socket.getInetAddress() + ":" + socket.getLocalPort());
           

            long start = System.currentTimeMillis();
            while (true) {
                
                Socket client = socket.accept();

                // System.out.println(counter);

                TCP_WORKER_SERVICE.submit(
                        new TCPWorker(client)
                );
                //System.out.println(counter);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

