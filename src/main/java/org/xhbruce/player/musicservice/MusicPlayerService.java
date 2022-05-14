package org.xhbruce.player.musicservice;

import com.goxr3plus.streamplayer.stream.StreamPlayer;
import com.goxr3plus.streamplayer.stream.StreamPlayerException;
import com.goxr3plus.streamplayer.stream.StreamPlayerListener;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;

import java.io.File;
import java.util.ArrayList;

import static org.xhbruce.player.application.MainApp.logger;

public class MusicPlayerService extends StreamPlayer {

    private static MusicPlayerService instance;

    // private String audioFileName = "Logic - Ballin [Bass Boosted].mp3";
    AudioFile audioFile = new AudioFile();
    private final ArrayList<StreamPlayerListener> listeners;

    private MusicPlayerService() {
        listeners = new ArrayList<>();
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
        if (isPausedOrPlaying()) {
            stop();
        }
        try {
            this.audioFile = audioFile;
            open(this.audioFile.getFile());
            play();
        } catch (Exception e) {
            logger.info(" Open failed : " + getStatus());
//            listeners.forEach(listener -> listener.opened("OPENFAILED", null));
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

    @Override
    public void play() {
        try {
            super.play();
        } catch (StreamPlayerException e) {
            e.printStackTrace();
        }
    }

    public AudioFile getAudioFile() {
        return audioFile;
    }

    @Override
    public void addStreamPlayerListener(StreamPlayerListener streamPlayerListener) {
        super.addStreamPlayerListener(streamPlayerListener);
        listeners.add(streamPlayerListener);
    }
}
