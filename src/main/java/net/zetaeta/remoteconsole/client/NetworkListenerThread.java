package net.zetaeta.remoteconsole.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class NetworkListenerThread extends Thread {
    
    private RemoteConsole remoteConsole;
    private BufferedReader reader;
    private boolean running = true;
    private PrintWriter out = new PrintWriter(System.out);
    
    public NetworkListenerThread(RemoteConsole rc) {
        this.remoteConsole = rc;
        try {
            reader = new BufferedReader(new InputStreamReader(rc.getSocket().getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void run() {
        String line;
        try {
            System.out.println("Listening for input...");
            while (running && (line = reader.readLine()) != null) {
                if (!line.isEmpty()) {
                    out.println(line);
                    out.flush();
                }
                if (interrupted()) {
                    return;
                }
            }
        } catch (IOException e) {
            remoteConsole.shutdown();
        }
        System.out.println("Finished listening to network");
        remoteConsole.shutdown();
    }
    
    public void shutdown() {
        running = false;
        interrupt();
    }
}
