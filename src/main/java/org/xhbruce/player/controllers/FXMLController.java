package org.xhbruce.player.controllers;

import com.goxr3plus.streamplayer.stream.StreamPlayerEvent;
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

public class FXMLController implements Initializable, StreamPlayerListener {

    File rootDir = new File("F:\\Music");
    private static String TAG = "FXMLController: ";
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
    private Button musicStepBackward;
    @FXML
    private Button musicResume;
    @FXML
    private Button musicStepForward;


    //
    @FXML
    private FontIcon antf_step_backward;
    @FXML
    private FontIcon antf_play_circle;
    @FXML
    private FontIcon antf_resume_circle;
    @FXML
    FontIcon antf_step_forward;
    //
    private Boolean isNextMusic = false;

    @FXML
    private ProgressBar musicProgress;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        threadPool = Executors.newSingleThreadExecutor();
        MusicPlayerService.getInstance().addStreamPlayerListener(this);

        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        labelVersion.setText("Hello, JavaFX " + javafxVersion + " Running on Java " + javaVersion + ".");

        open_music_file.setOnAction(event -> {
            File filePath = FileUtil.openFileChooser(MainApp.window, open_music_file.getText());
            if (filePath.isFile()) {
                MusicPlayerService.getInstance().load(filePath);
                MainApp.window.setTitle(MainApp.TITILE_TAG + " : " + AudioFile.getBaseFilename(filePath));
            }
        });
        open_music_folder.setOnAction(event -> {
            File filePath = FileUtil.openFolderChooser(MainApp.window, open_music_folder.getText());
            if (filePath.isDirectory()) {
                System.out.println(TAG + " open_music_folder = " + filePath.getAbsolutePath());
                initMusicList(filePath);
            }
        });

        musicStepBackward.setOnAction(event -> {
            System.out.println(TAG + " musicStepBackward clicked ");
            int selectIndex = musicList.getSelectionModel().getSelectedIndex();
            if (selectIndex > 0) {
                musicList.getSelectionModel().select(selectIndex - 1);
            }

        });
        musicResume.setOnAction(event -> {
            System.out.println(TAG + " musicResume clicked");
            if (MusicPlayerService.getInstance().isPlaying()) {
                MusicPlayerService.getInstance().pause();
            } else {
                MusicPlayerService.getInstance().resume();
            }
        });

        musicStepForward.setOnAction(event -> {
            System.out.println(TAG + " musicStepForward clicked ");
            int selectIndex = musicList.getSelectionModel().getSelectedIndex();
            if (selectIndex < musicList.getItems().size() - 1) {
                musicList.getSelectionModel().select(selectIndex + 1);
            }
        });

//        mainWindow.prefHeightProperty().bind(MainApp.window.heightProperty());
        mainWindow.widthProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println(TAG + "当前窗口宽度 " + newValue.doubleValue());
            musicProgress.setPrefWidth(newValue.doubleValue());
//            musicList.setPrefWidth(newValue.doubleValue() - 20);
            musicList.setPrefWidth(newValue.doubleValue());
            musicList.refresh();
        });
        mainWindow.heightProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println(TAG + "当前窗口高度 " + newValue.doubleValue());
//            double musicListHeight = newValue.doubleValue() - (mainBottom.getHeight() + open_music_file.getHeight() + 20);
//            musicList.setPrefHeight(musicListHeight);
            musicList.setPrefHeight(newValue.doubleValue());
            musicList.refresh();
        });

        musicProgress.setProgress(0);

        initMusicList();
        musicList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (isMusicListFlush) {
                isMusicListFlush = false;
                return;
            }
            isNextMusic = false;
            int selectIndex = musicList.getSelectionModel().getSelectedIndex();
            musicList.scrollTo(selectIndex);
            MusicPlayerService.getInstance().load((AudioFile) newValue);
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
            System.err.println(" Directory could not be found");
            return;
        }
        threadPool.execute(() -> {
            File[] audioFiles = rootDir.listFiles(new AudioFileFilter(false));
            System.out.println(rootDir.getName() + ".length = " + audioFiles.length);
            ObservableList<AudioFile> musicObsList = FXCollections.observableArrayList();
            if (audioFiles.length > 0) {
                for (File audioFile : audioFiles) {
                    try {
                        AudioFile musicFile = AudioFileIO.read(audioFile);
                        musicObsList.add(musicFile);
//                        musicObsList.add(AudioFile.getBaseFilename(audioFile));

                    } catch (Throwable t) {
                        System.err.println("Unable to read record :" + audioFile.getPath());
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
//        new Thread(() -> {
//
//        }).start();
    }

    @Override
    public void opened(Object dataSource, Map<String, Object> properties) {
        System.out.println(TAG + "opened");
    }

    @Override
    public void progress(int nEncodedBytes, long microsecondPosition, byte[] pcmData, Map<String, Object> properties) {
        AudioFile audioFile = MusicPlayerService.getInstance().getAudioFile();
        double trackLength = audioFile.getAudioHeader().getPreciseTrackLength();
        musicProgress.setProgress((int) (microsecondPosition / 1000000) / trackLength);
    }

    @Override
    public void statusUpdated(StreamPlayerEvent event) {
        switch (event.getPlayerStatus()) {
            case OPENED:
                musicProgress.setProgress(0);
                Platform.runLater(() -> {
                    MusicPlayerService.getInstance().play();
                });
                break;
            case PLAYING:
                isNextMusic = true;
            case RESUMED:
                antf_resume_circle.setIconLiteral("antf-pause-circle");
                break;
            case PAUSED:
                antf_resume_circle.setIconLiteral("antf-play-circle");
                break;
            case STOPPED:
                musicProgress.setProgress(1);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                antf_resume_circle.setIconLiteral("antf-play-circle");
                musicProgress.setProgress(0);
                Platform.runLater(() -> {
                    nextMusic();
                });
                break;
        }

    }

    private void nextMusic() {
        if (!isNextMusic) {
            return;
        }
        System.out.println(TAG + " musicStepForward clicked: SelectedIndex = " + musicList.getSelectionModel().getSelectedIndex());
        int selectIndex = musicList.getSelectionModel().getSelectedIndex();
        if (selectIndex < musicList.getItems().size() - 1) {
            musicList.getSelectionModel().select(selectIndex + 1);
        } else {
            musicList.getSelectionModel().select(0);
        }
        AudioFile audioFile = (AudioFile) musicList.getSelectionModel().getSelectedItem();
        MusicPlayerService.getInstance().load(audioFile);
        MainApp.window.setTitle(MainApp.TITILE_TAG + " : " + AudioFile.getBaseFilename(audioFile.getFile()));
    }

}
