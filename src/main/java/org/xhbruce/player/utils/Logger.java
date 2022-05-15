package org.xhbruce.player.utils;

public class Logger {
    private static Logger logger = null;

    private String name = "XhMusicPlayer-";

    public static Logger getLogger() {
        if (logger == null) {
            synchronized (Logger.class) {
                if (logger == null) {
                    logger = new Logger();
                    return logger;
                }
            }
        }
        return logger;
    }

    // =====
    public void info(String... values) {
        String msg = name;
        for (String value : values) {
            msg += value;
        }
        System.out.println(msg);
    }

    public void infoTag(String tag, String... values) {
        String msg = name + tag + ":";
        for (String value : values) {
            msg += value;
        }
        System.out.println(msg);
    }

}
