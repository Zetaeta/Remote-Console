package net.zetaeta.remoteconsole.client;

import static java.util.Arrays.asList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import jline.console.ConsoleReader;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.zetaeta.util.debug.DebugThread;
import net.zetaeta.util.io.LoggerOutputStream;

public class Main {
    public static void main(String[] args) throws IOException {
//        new DebugThread(10000).start();
        ConsoleReader reader = new ConsoleReader(System.in, System.out);
        reader.setExpandEvents(false);
        initLogger(reader);
//        PrintStream out = System.out;
//        PrintStream err = System.err;
//        System.setOut(new ConsoleOutputStream(reader, System.out, true));
//        System.setErr(new ConsoleOutputStream(reader, System.err, true));
//        reader.setPrompt(">");
        OptionParser optionParser = new OptionParser() {
            {
                acceptsAll(asList("username", "u"), "Username to connect with").withRequiredArg();
                acceptsAll(asList("password", "p", "ps", "pw"), "Password to connect with").withRequiredArg();
                acceptsAll(asList("server", "s", "host", "h"), "Host to connect to").withRequiredArg();
                acceptsAll(asList("port", "pt"), "Port to connect to").withRequiredArg();
            }
        };
        OptionSet optionSet = optionParser.parse(args);
        List<String> nonOpts = optionSet.nonOptionArguments();
        String server = null, port = null;
        if (nonOpts.size() > 0) {
            String host = nonOpts.get(0);
            String[] serverPort = host.split(":");
            server = serverPort[0];
            if (serverPort.length > 1) {
                port = serverPort[1];
            }
        }
        if (optionSet.has("server")) {
            server = (String) optionSet.valueOf("server");
        }
        if (optionSet.has("port")) {
            port = (String) optionSet.valueOf("server");
        }
        if (server == null) {
            server = "localhost";
        }
        if (port == null) {
            port = RemoteConsole.DEFAULT_PORT;
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String username, password;
        if (optionSet.has("username")) {
            username = (String) optionSet.valueOf("username");
        }
        else {
//            reader.setPrompt("Username: ");
//            System.out.print("Username:");
            username = reader.readLine("Username: ");
        }
        if (optionSet.has("password")) {
            password = (String) optionSet.valueOf("password");
        }
        else {
//            System.out.print("Password:");
//            reader.setPrompt("Password: ");
            password = reader.readLine("Password: ");
        }
//        reader.setPrompt(">");
        RemoteConsole.main(server, port, username, password, reader);
    }
    
    public static void initLogger(ConsoleReader reader) {
        System.out.println("Initialising logger!");
        Logger logger = Logger.getLogger("RemoteConsole");
        logger.setUseParentHandlers(false);
        ConsoleHandler handler = new TerminalConsoleHandler(reader);
        handler.setFormatter(new Formatter() {
            
            @Override
            public String format(LogRecord record) {
                if (record.getMessage().endsWith("\n")) {
                    return record.getMessage();
                }
                else {
                    return record.getMessage() + '\n';
                }
            }
            
        });
        for (Handler h : logger.getHandlers()) {
            logger.removeHandler(h);
        }
        logger.addHandler(handler);
        System.out.println("About to set sysout...");
        System.setOut(new PrintStream(new LoggerOutputStream(logger, Level.INFO), true));
        System.setErr(new PrintStream(new LoggerOutputStream(logger, Level.SEVERE), true));
        System.out.println("Initialised logger!");
        System.out.println("Meow!");
    }
}
