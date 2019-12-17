import javax.sound.sampled.*;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.Scanner;

public class Client2 {

    static int z = 0;
    private static AudioFormat getAudioFormat() {
        float sampleRate = 16000.0F;
        int sampleInbits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;
        return new AudioFormat(sampleRate, sampleInbits, channels, signed, bigEndian);
    }
    public static void main(String[] args) {
        boolean stopaudioCapture = false;
        ByteArrayOutputStream byteOutputStream;
        TargetDataLine targetDataLine;
        AudioInputStream InputStream;
        SourceDataLine sourceLine;


        String serverName = "localhost";
        int port = 2226;
        Scanner pm = new Scanner(System.in);

        try {
            System.out.println("Connecting to " + serverName + " on port " + port);
            Socket client = new Socket(serverName, port);

            System.out.println("Connected to " + client.getRemoteSocketAddress());

            while(true) {
                int norun = 0;
                z = 0;
                System.out.println("-----WELCOME to ALL INDIA RADIO STATION-----");
                System.out.println("-----Request a song from radio ??-----");
                DataOutputStream out = new DataOutputStream(client.getOutputStream());
                String msgToClient = pm.nextLine();
                if (msgToClient.equalsIgnoreCase(""))
                    continue;
                out.writeUTF("Client2: " + msgToClient);

                if (msgToClient.equalsIgnoreCase("no"))
                    continue;
                DataInputStream in = new DataInputStream(client.getInputStream());
                String inp = in.readUTF();
                System.out.println(inp);

                DatagramSocket serverSocket = new DatagramSocket(9788);
                //byte[] receiveData = new byte[10000];
                int packetsize=1024;
                FileOutputStream fos = null;
                fos = new FileOutputStream("ab2.wav");
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                double nosofpackets=Math.ceil(((int) (new File("sample1.wav")).length())/packetsize);
                byte[] mybytearray = new byte[packetsize];
                DatagramPacket receivePacket = new DatagramPacket(mybytearray,mybytearray.length);

                //System.out.println(nosofpackets+" "+mybytearray+" "+ packetsize);
                System.out.println("Waiting...");
                //serverSocket.receive(receivePacket);
                for(double i=0;i<nosofpackets+1;i++) {
                    serverSocket.receive(receivePacket);
                    byte audioData[] = receivePacket.getData();
                    InputStream byteInputStream = new ByteArrayInputStream(audioData);
                    AudioFormat adFormat = getAudioFormat();
                    InputStream = new AudioInputStream(byteInputStream, adFormat, audioData.length / adFormat.getFrameSize());
                    DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, adFormat);
                    sourceLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
                    sourceLine.open(adFormat);
                    sourceLine.start();
                    //System.out.println("Song got");
                    /*timerRun();
                    if(z >= 3){
                        System.out.println("Song got corrupted");
                        norun = 1;
                        break;
                    }*/

                    //System.out.println("Packet:"+(i+1)+" ");
                    bos.write(audioData, 0,audioData.length);
                }
                if(norun == 1)
                    continue;
                /*DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);*/
                System.out.println("RECEIVED: " + receivePacket.getAddress().getHostAddress() + " " + receivePacket.getPort());
                serverSocket.close();
                //client.close();

                System.out.println("Do you want to play the song?");
                String res = pm.next();
                Clip clip;
                if(res.equalsIgnoreCase("no")){
                    continue;
                }
                //if(res.equalsIgnoreCase("yes")) {
                    /*Thread t = Thread.currentThread();
                    try {
                        t.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/
                    /*File f = new File("ab2.wav");
                    song s = new song();
                    s.playSound(f);*/
                    try {
                        File yourFile = new File("ab2.wav");
                        AudioInputStream stream;
                        AudioFormat format;
                        DataLine.Info info;
                        stream = AudioSystem.getAudioInputStream(yourFile);
                        format = stream.getFormat();
                        info = new DataLine.Info(Clip.class, format);
                        clip = (Clip) AudioSystem.getLine(info);
                        clip.open(stream);
                        clip.start();

                        System.out.println("Do you want to stop the music?");//ask if want to stop
                        String res2 = pm.next();
                        if(res2.equalsIgnoreCase("yes")){
                            clip.stop();
                            clip.close();
                            System.out.println("Do you want to end the song- Y/N");
                            String res3 = pm.next();
                            if(res3.equalsIgnoreCase("Y")){
                                clip.stop();
                                clip.close();
                                continue;
                            }

                            //continue;
                        }

                        Thread.sleep(clip.getMicrosecondLength()/1000);

                    }
                    catch (Exception e) {
                        //whatevers
                    }
                /*}else{
                    continue;
                }*/

            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

}
