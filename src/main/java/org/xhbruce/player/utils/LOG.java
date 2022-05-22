package org.xhbruce.player.utils;

import com.goxr3plus.streamplayer.stream.StreamPlayer;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.id3.ID3Tags;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LOG {
    private static String name = "XhMusicPlayer-";
//    private static Logger logger = Logger.getGlobal();

    static {
//        logger.setResourceBundle(ResourceBundleUtil.getResource());
        // Disable loggers
//        java.util.logging.Logger.getLogger("com.goxr3plus.streamplayer.stream.StreamPlayer").setLevel(Level.OFF);
//        java.util.logging.Logger.getLogger(StreamPlayer.class.getName()).setLevel(Level.OFF);
//        AudioFileIO.logger.setLevel(Level.OFF);
//        AudioFile.logger.setLevel(Level.OFF);
//        ID3Tags.logger.setLevel(Level.OFF);
    }

    private LOG() {
    }

    // =====
    public static void info(String... values) {
        String msg = name;
        for (String value : values) {
            msg += value;
        }
//        logger.info(msg);
        System.out.println(msg);
    }

    public static void infoTag(String tag, String... values) {
        String msg = name + tag + ":";
        for (String value : values) {
            msg += value;
        }
//        logger.info(msg);
        System.out.println(msg);
    }

}
