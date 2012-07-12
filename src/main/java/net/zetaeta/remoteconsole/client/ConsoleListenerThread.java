package net.zetaeta.remoteconsole.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import jline.console.ConsoleReader;

public class ConsoleListenerThread extends Thread {
    
    private RemoteConsole remoteConsole;
    private ConsoleReader reader;
    private boolean running = true;
    
    public ConsoleListenerThread(ConsoleReader reader, RemoteConsole rc) {
        this.remoteConsole = rc;
        this.reader = reader;// new BufferedReader(new InputStreamReader(System.in));
    }
    
    @Override
    public void run() {
        System.out.println("Started reading input!");
        String line;
        try {
            while (running && (line = reader.readLine(">")) != null) {
                if (line.equals("!exit")) {
                    remoteConsole.shutdown();
                }
                remoteConsole.dispatchMessage(line);
                if (interrupted()) {
                    return;
                }
            }
        } catch (IOException e) {
            remoteConsole.shutdown();
        }
    }
    
    public void shutdown() {
        running = false;
        interrupt();
    }
}
