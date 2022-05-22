package org.xhbruce.player;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.IOException;

public class Test {
    public static void main(String[] args) {
        System.out.println("=====Test Start=====");
        String audioFileName = "F:\\Music\\Let Me Down Slowly.mp3";
        File audioFilePath = new File(audioFileName);

        try {
            AudioFile audioFile = AudioFileIO.read(audioFilePath);
            MP3File mp3File = new MP3File(audioFilePath, MP3File.LOAD_IDV1TAG | MP3File.LOAD_IDV2TAG, true);
//            Tag tag = audioFileName
//            System.out.println(audioFile.toString());
            System.out.println("=====AudioFile=====");
            System.out.println(audioFile.getFile());
            System.out.println(audioFile.getExt());
            System.out.println("=====TAG FieldKey=====");
            Tag tag = audioFile.getTag();
            System.out.println(tag.toString());
            System.out.println(tag.getFirst(FieldKey.ARTIST));
            System.out.println(tag.getFirst(FieldKey.ALBUM));
            System.out.println(tag.getFirst(FieldKey.TITLE));
            System.out.println(tag.getFirst(FieldKey.TRACK));
            System.out.println(tag.getFirst(FieldKey.YEAR));
            System.out.println(tag.getFirst(FieldKey.GENRE));
            System.out.println(tag.getFirst(FieldKey.COMMENT));
        } catch ( CannotReadException | IOException | TagException | ReadOnlyFileException |
                 InvalidAudioFrameException e) {
        }

        System.out.println("=====Test END=====");
    }
}
