package org.xhbruce.player.controllers;

import com.goxr3plus.streamplayer.enums.Status;
import com.goxr3plus.streamplayer.stream.StreamPlayerEvent;
import io.github.palexdev.materialfx.beans.NumberRange;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXSlider;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.jaudiotagger.audio.AudioFile;
import org.kordamp.ikonli.javafx.FontIcon;
import org.xhbruce.player.musicservice.XhPlayer;
import org.xhbruce.player.utils.JavaFXTool;
import org.xhbruce.player.utils.LOG;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class PlayPaneController extends BaseController {

    private static String TAG = "PlayPaneController";
    private XhPlayer xhPlayer = XhPlayer.getInstance();

    @FXML
    private GridPane gridPlayPane;
    @FXML
    private VBox playVBox;
    @FXML
    private MFXButton lastSong;
    @FXML
    private MFXButton nextSong;
    @FXML
    private MFXButton musicResume;
    private FontIcon playIcon = JavaFXTool.getFontIcon("antf-play-circle", Color.RED, 24);
    private FontIcon pauseIcon = JavaFXTool.getFontIcon("antf-play-circle", Color.RED, 24);
    @FXML
    private MFXSlider musicProgress;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        musicProgress.prefWidthProperty().bind(playVBox.widthProperty());
        musicProgress.getRanges1().add(NumberRange.of(musicProgress.getMin(), 33.0));
        musicProgress.getRanges2().add(NumberRange.of(34.0, 66.0));
        musicProgress.getRanges3().add(NumberRange.of(67.0, musicProgress.getMax()));

        lastSong.setOnAction(event -> {
            LOG.info(TAG, " lastSong clicked;");
        });
        nextSong.setOnAction(event -> {
            LOG.info(TAG, " nextSong clicked;");
        });
        musicResume.setOnAction(event -> {
            LOG.info(TAG, " musicResume clicked;");
            xhPlayer.resumeOrPuase();
        });


    }

    @Override
    public void opened(Object dataSource, Map<String, Object> properties) {
        LOG.infoTag(TAG, "opened , dataSource = " + dataSource.toString());
    }

    @Override
    public void progress(int nEncodedBytes, long microsecondPosition, byte[] pcmData, Map<String, Object> properties) {
        AudioFile audioFile = xhPlayer.getCurAudioFile();
        double trackLength = audioFile.getAudioHeader().getPreciseTrackLength() * 1000000;
        double progress = (microsecondPosition > 0 && trackLength > 0) ? (microsecondPosition / trackLength) : 0f;

//        System.out.println("Seconds  : " + (int) (microsecondPosition / 1000000) + " s " + "Progress: [ " + progress * 100 + " ] %");

        Platform.runLater(() -> {
            musicProgress.setValue(musicProgress.getMax() * progress);
        });
    }

    @Override
    public void statusUpdated(StreamPlayerEvent event) {
        final Status status = event.getPlayerStatus();
        switch (status) {
            case OPENING: {
                break;
            }
            case OPENED: {
//                if(){
                LOG.infoTag(TAG, "OPENED , isPausedOrPlaying = " + xhPlayer.isPausedOrPlaying());
//                }
                break;
            }
            case PLAYING:
            case PAUSED:
            case RESUMED:
            case STOPPED: {
                Platform.runLater(() -> {
                    musicResume.setGraphic(xhPlayer.isPlaying() ? pauseIcon : playIcon);
                });
            }
        }
    }
}
