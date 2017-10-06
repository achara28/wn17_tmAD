
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadedTCPServer {
    public static final int timeinterval = 1 * 1000;
    public static int counter = 0;
    public static long start = System.currentTimeMillis();
    public static int sum = 0 ;
    public static int count=0;
    public static int RandomNumber() {
        long num;
        int max = 2000, min = 300;
        int range = (max - min) + 1;
        /* do {
            rand = (System.currentTimeMillis() % 2000) + 1;
        }
        while (rand < 300);
*/

        Random rand = new Random(System.nanoTime());
        num = rand.nextInt(range) + min;


        return (int) num;
    }

    public static String payload() {
        String pl = "";

        long size = RandomNumber() * 1;
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
        private int limit;

        public TCPWorker(Socket client, int id, int limit) {
            this.client = client;
            this.clientbuffer = "";
           // this.clientid = id;
            this.limit = limit;
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

                    if ((System.currentTimeMillis() - start) > timeinterval) {

                        System.out.println(counter);
			sum = sum + counter;
			count++;
			System.out.println("The current sum is:"+sum);
			System.out.println("The current count is"+count);
			System.out.println("The current throughput is :"+ (double) sum/count);
                        counter = 0;
                        start = System.currentTimeMillis();
                    }


                    this.clientbuffer = reader.readLine();
                    // System.out.println(requests);
                    if (requests == limit || requests == 300) {
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

    public static void main(String args[]) {
        try {


            int port = Integer.parseInt(args[0]);
            int limit = Integer.parseInt(args[1]);
            ServerSocket socket = new ServerSocket(port);
            System.out.println("Server listening to: " + socket.getInetAddress() + ":" + socket.getLocalPort());
            int id = 1;

            long start = System.currentTimeMillis();
            while (true) {
                
                Socket client = socket.accept();

                // System.out.println(counter);

                TCP_WORKER_SERVICE.submit(
                        new TCPWorker(client, id++, limit)
                );
                //System.out.println(counter);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

