package java11lab;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

class Server {

    private static DatagramSocket socket;
    private static InetAddress address;
    private static int port = 4445;
    private static String name;
    private static Boolean work = true;
    private static Scanner sc;

    public static void main(final String[] args) throws SocketException, UnknownHostException, InterruptedException {
        sc = new Scanner(System.in);
        if(args.length > 0){
            if(args[0] == "client"){
                name = "client";
                socket = new DatagramSocket();
                address = InetAddress.getByName(args[1]);
                port = Integer.parseInt(args[2]);
            }else{
                name = "server";
                socket = new DatagramSocket(Integer.parseInt(args[1]));
                address = InetAddress.getByName("localhost");
            }
        }else{
            System.out.print("client or server? ");
            if(sc.nextLine().equals("client")){
                name = "client";
                socket = new DatagramSocket();
                System.out.print("adress: ");
                address = InetAddress.getByName(sc.nextLine());
                System.out.print("port: ");
                port = sc.nextInt();
            }else{
                name = "server";
                System.out.print("port: ");
                socket = new DatagramSocket(sc.nextInt());
                address = InetAddress.getByName("localhost");
            }
        }


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
                sc = new Scanner(System.in);
                while(work){
                    if(!sc.hasNext()) continue;
                    String str = sc.nextLine();
                    if( str.length() == 0) continue;
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
            }
        };
        
        System.out.println("start");
        t1.start();
        t2.start();
        
        t2.join();
        t1.stop();
        
        socket.close();
        sc.close();
   } 
}