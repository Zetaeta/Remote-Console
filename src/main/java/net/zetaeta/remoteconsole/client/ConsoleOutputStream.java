package net.zetaeta.remoteconsole.client;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import jline.console.ConsoleReader;

public class ConsoleOutputStream extends PrintStream {

    private ConsoleReader reader;
    
    public ConsoleOutputStream(ConsoleReader reader, OutputStream out, boolean autoFlush) {
        super(out, autoFlush);
        this.reader = reader;
    }
    
    @Override
    public void flush() {
        try {
            reader.print(String.valueOf(ConsoleReader.RESET_LINE));
            reader.flush();
            super.flush();
            reader.drawLine();
            reader.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
