package org.xhbruce.player.musicservice;

import com.goxr3plus.streamplayer.stream.StreamPlayer;
import com.goxr3plus.streamplayer.stream.StreamPlayerException;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.IOException;

public class MusicPlayerService extends StreamPlayer {

    private static MusicPlayerService instance;

    // private String audioFileName = "Logic - Ballin [Bass Boosted].mp3";
    AudioFile audioFile = new AudioFile();

    private MusicPlayerService() {

    }

    public static MusicPlayerService getInstance() {
        if (instance == null) {
            synchronized (MusicPlayerService.class) {
                if (instance == null) {
                    instance = new MusicPlayerService();
                }
            }
        }
        return instance;
    }


    public void load(AudioFile audioFile) {
        stop();
        try {
            this.audioFile = audioFile;
            open(this.audioFile.getFile());
            play();
        } catch (Exception e) {
            System.out.println("Open failed : " + getStatus());
            reset();
        }
    }

    public void load(File audioFile) {
        try {
            load(AudioFileIO.read(audioFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load(String audioFile) {
        load(new File(audioFile));
    }

    public AudioFile getAudioFile() {
        return audioFile;
    }
}
