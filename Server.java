package java11lab;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

class Main {

    private static DatagramSocket socket;
    private static InetAddress address;
    private static int port = 4444;
    private static String name = "server";
    private static Boolean work = true;

    public static void main(final String[] args) throws SocketException, UnknownHostException, InterruptedException {
        socket = new DatagramSocket(4444);
        address = InetAddress.getByName("localhost");

        final Thread t1 = new Thread() {
            private byte[] buf = new byte[256];

            public void run() {
                while (work) {
                    final DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    try {
                        socket.receive(packet);
                        address = packet.getAddress();
                        port = packet.getPort();
                        String received = new String(packet.getData(), 0, packet.getLength());
                        System.out.println(received);
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        
        final Thread t2 = new Thread() {
            private byte[] buf = new byte[256];

            public void run() {
                Scanner sc = new Scanner(System.in);
                while(work){
                    String str = sc.nextLine();
                    if (str.codePointAt(0) == '@') {
                        if (str.indexOf("name") == 1) {
                            name = str.substring(6);
                        } else if (str.indexOf("quit") == 1) {
                            work = false;
                        }
                    } else {
                        str = "@" + name + ": " + str;
                        buf = str.getBytes();
                        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
                        try {
                            socket.send(packet);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                sc.close();
            }
        };
        t1.start();
        t2.start();
        
        t2.join();
        t1.stop();
        
        socket.close();
   } 
}