package org.xhbruce.player;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileFilter;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.test.TestAudioTagger;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.Date;

public class Test {
    public static void main(String[] args) throws UnsupportedEncodingException {
        Test test = new Test();
        String path = new String("F:/Music/音乐/".getBytes(), "UTF-8");
//        String path = "F:/Music";
        File rootDir = new File(path);
        if (!rootDir.isDirectory()) {
            System.err.println(" Directory could not be found");
            System.exit(1);
        }
        Date start = new Date();
        System.out.println("Started to read from: " + rootDir.getPath() + " at " + start.toString());
        test.scanSingleDir(rootDir);
        Date finish = new Date();
        System.out.println("Finished to read from: " + rootDir.getPath() + " at " + finish.toString());
        System.out.println("Attempted  to read:" + count);
        System.out.println("Successful to read:" + (count - failed));
        System.out.println("Failed     to read:" + failed);
    }

    private static int count = 0;
    private static int failed = 0;
    private static ObservableList<AudioFile> musicObsList = FXCollections.observableArrayList();

    private void scanSingleDir(final File dir) {

        final File[] audioFiles = dir.listFiles(new AudioFileFilter(false));
        if (audioFiles.length > 0) {
            for (File audioFile : audioFiles) {
                count++;
                try {
                    AudioFile musicFile = AudioFileIO.read(audioFile);
                    musicObsList.add(musicFile);
                    System.out.println(AudioFile.getBaseFilename(musicFile.getFile()));
                } catch (Throwable t) {
                    failed++;
                }
            }
        }
    }
}
