package org.xhbruce.player.musicservice;

import com.goxr3plus.streamplayer.stream.StreamPlayer;
import com.goxr3plus.streamplayer.stream.StreamPlayerException;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.xhbruce.player.utils.LOG;

import java.io.File;
import java.io.IOException;

public class XhPlayer extends StreamPlayer {

    private static String TAG = "XhPlayer";
    private static XhPlayer xhPlayer;
    private AudioFile curAudioFile;

    private XhPlayer() {
        super();
    }

    public static XhPlayer getInstance() {
        if (xhPlayer == null) {
            synchronized (XhPlayer.class) {
                if (xhPlayer == null) {
                    xhPlayer = new XhPlayer();
                }
            }
        }
        return xhPlayer;
    }

    public void setCurAudioFile(Object dataSource) {
        if (dataSource instanceof File) {
            try {
                curAudioFile = AudioFileIO.read((File) dataSource);
            } catch (CannotReadException | IOException | TagException | ReadOnlyFileException |
                     InvalidAudioFrameException e) {
            }
        }
    }

    public AudioFile getCurAudioFile() {
        return curAudioFile;
    }

    public Boolean play(AudioFile audioFile) {
        return play(audioFile.getFile());
    }

    public Boolean play(Object audioFile) {
        if (audioFile == null) {
            LOG.infoTag(TAG, "audioFile is null");
            return false;
        }

        if (isPausedOrPlaying()) {
            stop();
        }

        try {
            // Audio resources from file||URL||inputStream.
            open(audioFile);
            play();
            setCurAudioFile(audioFile);
        } catch (StreamPlayerException e) {
            LOG.infoTag(TAG, e.getCause().toString() + ";" + getCurAudioFile().getFile().getName());
            return false;
        }
        return true;
    }

    public void resumeOrPuase() {
        if (isPlaying()) {
            pause();
        } else if (isPaused()) {
            resume();
        }
    }
}
