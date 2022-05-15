package org.xhbruce.player.controllers;

import com.goxr3plus.streamplayer.stream.StreamPlayerEvent;
import com.goxr3plus.streamplayer.stream.StreamPlayerListener;
import io.github.palexdev.materialfx.beans.NumberRange;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXSlider;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.jaudiotagger.audio.AudioFile;
import org.xhbruce.player.musicservice.MusicPlayerService;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import static org.xhbruce.player.application.MainApp.logger;

public class PlayPaneController implements Initializable, StreamPlayerListener {

    private static String TAG = "PlayPaneController";
    private MusicPlayerService musicPlayerService = MusicPlayerService.getInstance();

    @FXML
    private GridPane gridPlayPane;
    @FXML
    private VBox playVBox;
    @FXML
    private MFXSlider musicProgress;

    @FXML
    private MFXButton lastSong;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        musicPlayerService.addStreamPlayerListener(this);
        musicProgress.prefWidthProperty().bind(playVBox.widthProperty());
        musicProgress.getRanges1().add(NumberRange.of(musicProgress.getMin(), 33.0));
        musicProgress.getRanges2().add(NumberRange.of(34.0, 66.0));
        musicProgress.getRanges3().add(NumberRange.of(67.0, musicProgress.getMax()));


        lastSong.setOnAction(event -> {
//            int selectIndex = musicList.getSelectionModel().getSelectedIndex();
            logger.info(TAG, " LastSong clicked;");
//            if (selectIndex > 0) {
//                musicList.getSelectionModel().select(selectIndex - 1);
//            } else {
//                musicList.getSelectionModel().select(musicList.getItems().size() - 1);
//            }

        });
    }

    @Override
    public void opened(Object dataSource, Map<String, Object> properties) {

    }

    @Override
    public void progress(int nEncodedBytes, long microsecondPosition, byte[] pcmData, Map<String, Object> properties) {
        AudioFile audioFile = musicPlayerService.getAudioFile();
        double trackLength = audioFile.getAudioHeader().getPreciseTrackLength();
        Platform.runLater(() -> {
            musicProgress.setValue(musicProgress.getMax() * (microsecondPosition / 1000000) / trackLength);
        });
    }

    @Override
    public void statusUpdated(StreamPlayerEvent event) {

    }
}
