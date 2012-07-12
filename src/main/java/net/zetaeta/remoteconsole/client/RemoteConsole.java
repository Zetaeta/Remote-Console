package net.zetaeta.remoteconsole.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import jline.console.ConsoleReader;

public class RemoteConsole {
    public static final String DEFAULT_PORT = "2222";
    public static final int MAGIC_HANDSHAKE = 0xCAFEBABE; // I suck at thinking up numbers
    public static final int ACCEPTED = 0x11;
    public static final int DENIED = 0x00;
    
    public static void main(String server, String port, String username, String password, ConsoleReader sysin) {
//        // Keystore
        System.setProperty("javax.net.ssl.trustStore", "sslKeystore.ks");
        System.setProperty("javax.net.ssl.trustStorePassword", "Heliocentric");
        // Debug stuff
//        System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
//        System.setProperty("javax.net.debug", "ssl");
        
        int portNum;
        try {
            portNum = Integer.parseInt(port);
        } catch (NumberFormatException e) {
            System.err.println("Invalid port number: " + port);
            return;
        }
        SSLSocket socket;
        try {
//            socket = new SSLSocket(server, portNum);
            System.out.println(SSLSocketFactory.getDefault().getClass().getName());
            socket = (SSLSocket) SSLSocketFactory.getDefault().createSocket(server, portNum);
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + server);
            return;
        } catch (IOException e) {
            System.err.println("IOException occurred trying to connect to server!");
            return;
        }
        System.out.println("Connected!");
        DataInputStream in = null;
        DataOutputStream out = null;
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            int i;
            System.out.println("About to run handshake...");
            sysin.setPrompt(">");
//            socket.setSoTimeout(5000);
            socket.getSession();
            System.out.println("Run handshake!");
            if ((i = in.readInt()) != MAGIC_HANDSHAKE) {
                
                System.err.println("Invalid server handshake code: 0x" + Integer.toHexString(i));
                int b;
                System.out.println("Printing bytes:");
                while ((b = in.read()) > 0) {
                    System.out.println(Integer.toHexString(b));
                }
                in.close();
                out.close();
                socket.close();
                return;
            }
            System.out.println("Recieved handshake!");
            out.writeUTF(username);
            out.writeUTF(password);
            System.out.println("Authenticating...");
            if (in.readInt() == 0) {
                System.err.println("Invalid username and password!");
                in.close();
                out.close();
                return;
            }
            System.out.println("Authenticated!");
            RemoteConsole rc = new RemoteConsole(sysin, socket);
            rc.start();
        } catch (IOException e) {
            System.err.println("IOException occurred trying to connect to server!");
            return;
        }
    }
    
    private ConsoleReader reader;
    private SSLSocket socket;
    private NetworkListenerThread network;
    private ConsoleListenerThread console;
    private PrintWriter socketWriter;
    
    public RemoteConsole(ConsoleReader reader, SSLSocket socket) {
        this.reader = reader;
        this.socket = socket;
        try {
            socketWriter = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            System.err.println("Error getting socket output stream!");
            System.exit(1);
        }
    }
    
    public void start() {
        System.out.println("Starting!");
        network = new NetworkListenerThread(this);
        console = new ConsoleListenerThread(reader, this);
        System.out.println("Starting network listener!");
        network.start();
        System.out.println("Starting console listener!");
        console.start();
    }
    
    public SSLSocket getSocket() {
        return socket;
    }
    
    public void shutdown() {
        network.shutdown();
        console.shutdown();
    }
    
    public void dispatchMessage(String message) throws IOException {
//        System.out.println("Dispatching message " + message);
        socketWriter.println(message);
        socketWriter.flush();
    }
}
