package org.xhbruce.player.controllers;

import com.goxr3plus.streamplayer.enums.Status;
import com.goxr3plus.streamplayer.stream.StreamPlayerEvent;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileFilter;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.id3.ID3Tags;
import org.kordamp.ikonli.javafx.FontIcon;
import org.xhbruce.player.musicservice.XhPlayer;
import org.xhbruce.player.utils.LOG;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class FXMLController extends BaseController {

    File rootDir = new File("F:\\Music");
    private static String TAG = "FXMLController";
    private XhPlayer playerService = XhPlayer.getInstance();
    private ExecutorService threadPool;

    @FXML
    ListView musicList;

    @FXML
    private MFXButton lastSong;
    @FXML
    private Button musicResume;
    @FXML
    private Button nextSong;


    //
    @FXML
    private FontIcon antf_resume_circle;
    @FXML
    FontIcon antf_step_forward;

    @FXML
    private ProgressBar musicProgress;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
        threadPool = Executors.newSingleThreadExecutor();

        lastSong.setOnAction(event -> {
            int selectIndex = musicList.getSelectionModel().getSelectedIndex();
            LOG.info(TAG, " LastSong clicked; cur MusicNum=" + selectIndex);
            if (selectIndex > 0) {
                musicList.getSelectionModel().select(selectIndex - 1);
            } else {
                musicList.getSelectionModel().select(musicList.getItems().size() - 1);
            }

        });

        musicResume.setOnAction(event -> {
            LOG.infoTag(TAG, " musicResume clicked;");
            playerService.resumeOrPuase();
        });

        nextSong.setOnAction(event -> {
            int selectIndex = musicList.getSelectionModel().getSelectedIndex();
            LOG.infoTag(TAG, " NextSong clicked; cur MusicNum=" + selectIndex);
            if (selectIndex < musicList.getItems().size() - 1) {
                musicList.getSelectionModel().select(selectIndex + 1);
            } else {
                musicList.getSelectionModel().select(0);
            }
        });

        musicProgress.setProgress(0);

        initMusicList();
        musicList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            int selectIndex = musicList.getSelectionModel().getSelectedIndex();
            musicList.scrollTo(selectIndex);
            System.out.println(selectIndex);
            playerService.play((AudioFile) newValue);
        });
    }

    private void initMusicList() {
        String path = "";
        try {
            path = new String("F:/Music/音乐/".getBytes(), "UTF-8");
            initMusicList(new File(path));
        } catch (UnsupportedEncodingException e) {
        }

    }

    private void initMusicList(File rootDir) {
        if (!rootDir.exists() || !rootDir.isDirectory()) {
            LOG.infoTag(TAG, " Directory could not be found");
            return;
        }
        threadPool.execute(() -> {
            File[] audioFiles = rootDir.listFiles(new AudioFileFilter(false));
            LOG.infoTag(TAG, rootDir.getName() + ".length = " + audioFiles.length);
            ObservableList<AudioFile> musicObsList = FXCollections.observableArrayList();
            if (audioFiles.length > 0) {
                for (File audioFile : audioFiles) {
                    try {
                        AudioFile musicFile = AudioFileIO.read(audioFile);
                        musicObsList.add(musicFile);
                    } catch (Throwable t) {
                        //logger.infoTag(TAG, "Unable to read record :" + audioFile.getPath());
                    }
                }
                LOG.infoTag(TAG, "musicObsList.size = " + musicObsList.size());
            }
            musicObsList.sorted();
            Platform.runLater(() -> {
                musicList.setItems(musicObsList);
                musicList.setCellFactory(param -> new ListCell<AudioFile>() {
                    @Override
                    protected void updateItem(AudioFile item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null || item.getFile() == null) {
                            setText(null);
                        } else {
                            setText(AudioFile.getBaseFilename(item.getFile()));
                        }
                    }
                });
            });
        });
    }

    @Override
    public void progress(int nEncodedBytes, long microsecondPosition, byte[] pcmData, Map<String, Object> properties) {
        AudioFile audioFile = playerService.getCurAudioFile();
        double trackLength = audioFile.getAudioHeader().getPreciseTrackLength();
        musicProgress.setProgress((int) (microsecondPosition / 1000000) / trackLength);
    }

    @Override
    public void statusUpdated(StreamPlayerEvent event) {
        final Status status = event.getPlayerStatus();
        switch (status) {
            case OPENING:
            case OPENED:
                break;
            case PLAYING:
            case RESUMED:
                antf_resume_circle.setIconLiteral("antf-pause-circle");
                break;
            case PAUSED:
            case STOPPED:
                antf_resume_circle.setIconLiteral("antf-play-circle");
                break;
            case EOM:
//                Platform.runLater(() -> {
//                    nextMusic();
//                });
//                LOG.infoTag(TAG, "EOM: END OF MEDIA");
                break;
        }

    }

    private void nextMusic() {
        int selectIndex = musicList.getSelectionModel().getSelectedIndex();
        LOG.infoTag(TAG, " musicStepForward clicked: SelectedIndex = " + selectIndex);
        if (selectIndex < musicList.getItems().size() - 1) {
            musicList.getSelectionModel().select(selectIndex + 1);
        } else {
            musicList.getSelectionModel().select(0);
        }
        AudioFile audioFile = (AudioFile) musicList.getSelectionModel().getSelectedItem();
        playerService.play(audioFile);
    }
}
