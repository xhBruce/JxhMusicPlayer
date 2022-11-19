package org.xhbruce.logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class Log {
    private static final String TAG = "XhMusicPlayer";
    private static final Logger logger = Logger.getLogger(TAG);
    private static Level curlevel = Level.ALL;

    static {
        logger.setLevel(Level.FINE);
        logger.setUseParentHandlers(false);
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new LogFormatter());
        consoleHandler.setLevel(Level.FINE);
        logger.addHandler(consoleHandler);
    }

    private Log() {
    }

    // ===Level Start===
    public static void setLevel(Level level) {
        logger.setLevel(level);
    }

    private static String formatterMessage(String tag, String message) {
        String msg = "[" + tag + "] " + message;
        return msg;
    }

    public static void e(String tag, String message) {
        curlevel = Level.SEVERE;
        logger.severe(formatterMessage(tag, message));
    }

    public static void w(String tag, String message) {
        curlevel = Level.WARNING;
        logger.warning(formatterMessage(tag, message));
    }

    public static void i(String tag, String message) {
        curlevel = Level.INFO;
        logger.info(formatterMessage(tag, message));
    }

    public static void d(String tag, String message) {
        curlevel = Level.CONFIG;
        logger.config(formatterMessage(tag, message));
    }

    public static void v(String tag, String message) {
        curlevel = Level.FINE;
        logger.fine(formatterMessage(tag, message));
    }
    // ===Level End===

    private static class LogFormatter extends Formatter {

        // ANSI escape code
        public static final String ANSI_RESET = "\u001B[0m";
        public static final String ANSI_BLACK = "\u001B[30m";
        public static final String ANSI_RED = "\u001B[31m";
        public static final String ANSI_GREEN = "\u001B[32m";
        public static final String ANSI_YELLOW = "\u001B[33m";
        public static final String ANSI_BLUE = "\u001B[34m";
        public static final String ANSI_PURPLE = "\u001B[35m";
        public static final String ANSI_CYAN = "\u001B[36m";
        public static final String ANSI_WHITE = "\u001B[37m";

        private SimpleDateFormat mDateFormat;

        public LogFormatter() {
            mDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        }

        @Override
        public String format(LogRecord record) {
            Date now = new Date();
            String dateStr = mDateFormat.format(now);

            String name = record.getLoggerName();
            String message = record.getMessage();

            //Thread currentThread = Thread.currentThread();
            //StackTraceElement stackTrace = currentThread.getStackTrace()[8];

            String throwable = "";
            if (record.getThrown() != null) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                pw.println();
                record.getThrown().printStackTrace(pw);
                pw.close();
                throwable = sw.toString();
            }

            String color = ANSI_RED;
            if (curlevel == Level.SEVERE) {
                color = ANSI_RED;
            } else if (curlevel == Level.WARNING) {
                color = ANSI_PURPLE;
            } else if (curlevel == Level.INFO) {
                color = ANSI_GREEN;
            } else if (curlevel == Level.CONFIG) {
                color = ANSI_BLUE;
            } else if (curlevel == Level.FINE) {
                color = ANSI_BLACK;
//                color = ANSI_WHITE;
            } else {
//                return String.format("%s(%s:%d)%s : %s%s\n",
//                        dateStr, stackTrace.getFileName(), stackTrace.getLineNumber(),
//                        name, message, throwable);
                return String.format("%s\t%s : %s%s\n", dateStr, name, message, throwable);
            }

            return String.format("%s%s\t%s : %s%s%s\n", color, dateStr, name, message, throwable, ANSI_RESET);
        }
    }
}
