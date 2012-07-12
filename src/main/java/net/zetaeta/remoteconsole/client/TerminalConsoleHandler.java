package net.zetaeta.remoteconsole.client;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import jline.console.ConsoleReader;

public class TerminalConsoleHandler extends ConsoleHandler {
    private ConsoleReader reader;
    
    public TerminalConsoleHandler(ConsoleReader reader) {
        this.reader = reader;
    }
    
    @Override
    public synchronized void flush() {
        try {
            reader.print(ConsoleReader.RESET_LINE + "");
            reader.flush();
            super.flush();
            try {
                reader.drawLine();
            } catch (Throwable ex) {
               reader.getCursorBuffer().clear();
            }
            reader.flush();
        } catch (IOException ex) {
            Logger.getLogger(TerminalConsoleHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
