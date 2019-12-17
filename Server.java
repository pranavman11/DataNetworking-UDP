//Server Transfer songs using UDP
//Broadcast songs

import javax.sound.sampled.*;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Scanner;

import static javax.sound.sampled.AudioSystem.*;

class ServerThr extends Thread{

    static int port;
    static int mainPort;
    static boolean stopaudioCapture = false;
    static ByteArrayOutputStream byteOutputStream;
    static AudioFormat adFormat;
    static TargetDataLine targetDataLine;
    static AudioInputStream InputStream;
    static SourceDataLine sourceLine;

    ServerSocket serverSocket;
    ServerSocket serverSocket2;
    Scanner pm = new Scanner(System.in);
    public ServerThr(int port,int port2) throws IOException {
        serverSocket = new ServerSocket(port);
        serverSocket2 = new ServerSocket(port2);
    }
    @Override
    public void run(){
        try{
            System.out.println("Waiting for client 1 on port " +serverSocket.getLocalPort() + "...");
            System.out.println("Waiting for client 2 on port " +serverSocket2.getLocalPort() + "...");
            Socket server = serverSocket.accept();
            Socket server2 = serverSocket2.accept();
            System.out.println("Connected to " + server.getRemoteSocketAddress());
            System.out.println("Connected to " + server2.getRemoteSocketAddress());
            //DataOutputStream out = new DataOutputStream(server.getOutputStream());

            while(true){
                int run = 0;
                String inp ="",inp2 = "";

                DataInputStream in = new DataInputStream(server.getInputStream());//From client 1
                inp = in.readUTF();
                System.out.println(inp);
                if(inp.equalsIgnoreCase("Client1: ")){
                    continue;
                }

                DataInputStream in2 = new DataInputStream(server2.getInputStream());//From client 2
                inp2 = in2.readUTF();
                System.out.println(inp2);

                if(inp2.equalsIgnoreCase("Client2: ")){
                    continue;
                }
                if(inp.equalsIgnoreCase("Client1: yes") && inp2.equalsIgnoreCase("Client2: no")){
                    System.out.println("Streaming songs to client 1");
                    DataOutputStream out = new DataOutputStream(server.getOutputStream());
                    String msgToClient = "Streaming Padhoge Likhoge song from movie MS Dhoni";
                    out.writeUTF("Server: "+msgToClient);
                    port = 9786;
                    mainPort = 8786;
                    //sending song to client 1
                    captureAudio();
                }
                else if(inp.equalsIgnoreCase("Client1: no") && inp2.equalsIgnoreCase("Client2: yes")){
                    System.out.println("Streaming songs to client 2");
                    DataOutputStream out2 = new DataOutputStream(server2.getOutputStream());
                    String msgToClient = "Streaming Padhoge Likhoge song from movie MS Dhoni";
                    out2.writeUTF("Server: "+msgToClient);
                    port = 9788;
                    mainPort = 8788;
                    //sending song to client 2
                    captureAudio();

                }
                else if(inp.equalsIgnoreCase("Client1: yes") && inp2.equalsIgnoreCase("Client2: yes")){
                    System.out.println("Streaming songs to both the clients");
                    DataOutputStream out = new DataOutputStream(server.getOutputStream());
                    String msgToClient = "Streaming padhoge likhoge song from MS Dhoni";
                    out.writeUTF("Server: "+msgToClient);//control data to 1
                    DataOutputStream out2 = new DataOutputStream(server2.getOutputStream());
                    out2.writeUTF("Server: "+msgToClient);//control data to 2
                    //sending song to both
                    port = 9786;
                    mainPort = 8786;
                    captureAudio();
                    port = 9788;
                    mainPort = 8788;
                    //sending song to client 2
                    captureAudio();
                }
                if(inp.equalsIgnoreCase("Client1: bye")){
                    DataOutputStream out1 = new DataOutputStream(server.getOutputStream());
                    out1.writeUTF("Server: Thank you,Happy to help you");
                    server.close();
                    break;
                }
                else if(inp2.equalsIgnoreCase("Client2: bye")){
                    DataOutputStream out2 = new DataOutputStream(server2.getOutputStream());
                    out2.writeUTF("Server: Thank you,Happy to help you");
                    server2.close();
                    break;
                }


            }

        }catch(SocketTimeoutException e){
        }catch(IOException e){
        }


    }
    private static void captureAudio() {
        File myFile = new File("sample1.wav");
        DatagramSocket ds = null;
        BufferedInputStream bis = null;
        try {
            ds = new DatagramSocket(mainPort);
            DatagramPacket dp;
            int packetsize = 1024;
            double nosofpackets;
            nosofpackets = Math.ceil(((int) myFile.length()) / packetsize);

            bis = new BufferedInputStream(new FileInputStream(myFile));
            for (double i = 0; i < nosofpackets + 1; i++) {
                byte[] mybytearray = new byte[packetsize];
                bis.read(mybytearray, 0, mybytearray.length);
                //System.out.println("Packet:" + (i + 1));
                dp = new DatagramPacket(mybytearray, mybytearray.length, InetAddress.getByName("127.0.0.1"), port);
                ds.send(dp);
                try{
                    Thread.sleep(10L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(bis!=null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

                ds.close();
        }
    }

    private static AudioFormat getAudioFormat() {
        float sampleRate = 16000.0F;
        int sampleInbits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;
        return new AudioFormat(sampleRate, sampleInbits, channels, signed, bigEndian);
    }

}
public class Server {

    public static void main(String argv[]) throws Exception
    {
        try{
            Thread t = new ServerThr(2225,2226);
            t.start();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

}
