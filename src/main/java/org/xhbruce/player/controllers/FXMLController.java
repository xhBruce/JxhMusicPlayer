package org.xhbruce.player.controllers;

import com.goxr3plus.streamplayer.enums.Status;
import com.goxr3plus.streamplayer.stream.StreamPlayerEvent;
import com.goxr3plus.streamplayer.stream.StreamPlayerException;
import com.goxr3plus.streamplayer.stream.StreamPlayerListener;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileFilter;
import org.jaudiotagger.audio.AudioFileIO;
import org.kordamp.ikonli.javafx.FontIcon;
import org.xhbruce.player.application.MainApp;
import org.xhbruce.player.musicservice.MusicPlayerService;
import org.xhbruce.player.utils.io.FileUtil;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.xhbruce.player.application.MainApp.logger;

public class FXMLController implements Initializable, StreamPlayerListener {

    File rootDir = new File("F:\\Music");
    private static String TAG = "FXMLController";
    private MusicPlayerService musicPlayerService = MusicPlayerService.getInstance();
    private ExecutorService threadPool;

    @FXML
    private BorderPane mainWindow;
    @FXML
    private BorderPane mainBottom;

    @FXML
    private Button open_music_file;
    @FXML
    private Button open_music_folder;
    @FXML
    ListView musicList;
    private Boolean isMusicListFlush = false;
    @FXML
    private Label labelVersion;

    @FXML
    private Button lastSong;
    @FXML
    private Button musicResume;
    @FXML
    private Button nextSong;


    //
    @FXML
    private FontIcon antf_step_backward;
    @FXML
    private FontIcon antf_play_circle;
    @FXML
    private FontIcon antf_resume_circle;
    @FXML
    FontIcon antf_step_forward;

    @FXML
    private ProgressBar musicProgress;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        threadPool = Executors.newSingleThreadExecutor();
        musicPlayerService.addStreamPlayerListener(this);

        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        labelVersion.setText("Hello, JavaFX " + javafxVersion + " Running on Java " + javaVersion + ".");

        open_music_file.setOnAction(event -> {
            File filePath = FileUtil.openFileChooser(MainApp.window, open_music_file.getText());
            if (filePath.isFile()) {
                musicPlayerService.load(filePath);
                MainApp.window.setTitle(MainApp.TITILE_TAG + " : " + AudioFile.getBaseFilename(filePath));
            }
        });
        open_music_folder.setOnAction(event -> {
            File filePath = FileUtil.openFolderChooser(MainApp.window, open_music_folder.getText());
            if (filePath != null && filePath.isDirectory()) {
                logger.infoTag(TAG, " open_music_folder = " + filePath.getAbsolutePath());
                initMusicList(filePath);
            }
        });

        lastSong.setOnAction(event -> {
            int selectIndex = musicList.getSelectionModel().getSelectedIndex();
            logger.info(TAG, " LastSong clicked; cur MusicNum=" + selectIndex);
            if (selectIndex > 0) {
                musicList.getSelectionModel().select(selectIndex - 1);
            } else {
                musicList.getSelectionModel().select(musicList.getItems().size() - 1);
            }

        });

        musicResume.setOnAction(event -> {
            int selectIndex = musicList.getSelectionModel().getSelectedIndex();
            logger.infoTag(TAG, " musicResume clicked; cur MusicNum=" + selectIndex);
            if (selectIndex >= 0 && selectIndex < musicList.getItems().size()) {
                if (musicPlayerService.isPlaying()) {
                    musicPlayerService.pause();
                } else {
                    musicPlayerService.resume();
                }
            } else {
                musicList.getSelectionModel().select(0);
            }
        });

        nextSong.setOnAction(event -> {
            int selectIndex = musicList.getSelectionModel().getSelectedIndex();
            logger.infoTag(TAG, " NextSong clicked; cur MusicNum=" + selectIndex);
            if (selectIndex < musicList.getItems().size() - 1) {
                musicList.getSelectionModel().select(selectIndex + 1);
            } else {
                musicList.getSelectionModel().select(0);
            }
        });

//        mainWindow.prefHeightProperty().bind(MainApp.window.heightProperty());
        mainWindow.widthProperty().addListener((observable, oldValue, newValue) -> {
            logger.infoTag(TAG, "当前窗口宽度 " + newValue.doubleValue());
            musicProgress.setPrefWidth(newValue.doubleValue());
//            musicList.setPrefWidth(newValue.doubleValue() - 20);
            musicList.setPrefWidth(newValue.doubleValue());
            musicList.refresh();
        });
        mainWindow.heightProperty().addListener((observable, oldValue, newValue) -> {
            logger.infoTag(TAG, "当前窗口高度 " + newValue.doubleValue());
//            double musicListHeight = newValue.doubleValue() - (mainBottom.getHeight() + open_music_file.getHeight() + 20);
//            musicList.setPrefHeight(musicListHeight);
            musicList.setPrefHeight(newValue.doubleValue());
            musicList.refresh();
        });

        musicProgress.setProgress(0);

        initMusicList();
        musicList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (isMusicListFlush) {
                return;
            }
            int selectIndex = musicList.getSelectionModel().getSelectedIndex();
            musicList.scrollTo(selectIndex);
            musicPlayerService.load((AudioFile) newValue);
            MainApp.window.setTitle(MainApp.TITILE_TAG + " : " + AudioFile.getBaseFilename(((AudioFile) newValue).getFile()));
        });
    }

    private void initMusicList() {
        String path = "";
        try {
            path = new String("F:/Music/音乐/".getBytes(), "UTF-8");
            initMusicList(new File(path));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    private void initMusicList(File rootDir) {
        if (!rootDir.exists() || !rootDir.isDirectory()) {
            logger.infoTag(TAG, " Directory could not be found");
            return;
        }
        threadPool.execute(() -> {
            File[] audioFiles = rootDir.listFiles(new AudioFileFilter(false));
            logger.infoTag(TAG, rootDir.getName() + ".length = " + audioFiles.length);
            ObservableList<AudioFile> musicObsList = FXCollections.observableArrayList();
            if (audioFiles.length > 0) {
                for (File audioFile : audioFiles) {
                    try {
                        AudioFile musicFile = AudioFileIO.read(audioFile);
                        musicObsList.add(musicFile);
                    } catch (Throwable t) {
                        logger.infoTag(TAG, "Unable to read record :" + audioFile.getPath());
                    }
                }
            }
            musicObsList.sorted();
            Platform.runLater(() -> {
                isMusicListFlush = true;
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
                isMusicListFlush = false;
            });
        });
    }

    @Override
    public void opened(Object dataSource, Map<String, Object> properties) {
        logger.infoTag(TAG, "opened");
        if ("OPENFAILED".equals(String.valueOf(dataSource))) {
            logger.infoTag(TAG, "opened failed!");
//            musicPlayerService.reset();
//            Platform.runLater(() -> {
//                nextMusic();
//            });
        }
    }

    @Override
    public void progress(int nEncodedBytes, long microsecondPosition, byte[] pcmData, Map<String, Object> properties) {
        AudioFile audioFile = musicPlayerService.getAudioFile();
        double trackLength = audioFile.getAudioHeader().getPreciseTrackLength();
        musicProgress.setProgress((int) (microsecondPosition / 1000000) / trackLength);
    }

    @Override
    public void statusUpdated(StreamPlayerEvent event) {
        switch (event.getPlayerStatus()) {
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
                Platform.runLater(() -> {
                    nextMusic();
                });
                logger.infoTag(TAG, "EOM: END OF MEDIA");
                break;
        }

    }

    private void nextMusic() {
        int selectIndex = musicList.getSelectionModel().getSelectedIndex();
        logger.infoTag(TAG, " musicStepForward clicked: SelectedIndex = " + selectIndex);
        if (selectIndex < musicList.getItems().size() - 1) {
            musicList.getSelectionModel().select(selectIndex + 1);
        } else {
            musicList.getSelectionModel().select(0);
        }
        AudioFile audioFile = (AudioFile) musicList.getSelectionModel().getSelectedItem();
        musicPlayerService.load(audioFile);
        MainApp.window.setTitle(MainApp.TITILE_TAG + " : " + AudioFile.getBaseFilename(audioFile.getFile()));
    }

}
