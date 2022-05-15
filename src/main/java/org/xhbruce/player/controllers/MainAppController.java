package org.xhbruce.player.controllers;

import com.goxr3plus.streamplayer.stream.StreamPlayerEvent;
import com.goxr3plus.streamplayer.stream.StreamPlayerListener;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import io.github.palexdev.materialfx.controls.MFXRectangleToggleNode;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.utils.others.loader.MFXLoader;
import io.github.palexdev.materialfx.utils.others.loader.MFXLoaderBean;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileFilter;
import org.jaudiotagger.audio.AudioFileIO;
import org.kordamp.ikonli.javafx.FontIcon;
import org.xhbruce.player.application.MainApp;
import org.xhbruce.player.application.MainAppExit;
import org.xhbruce.player.controllers.load.FxmlLoader;
import org.xhbruce.player.controllers.load.FxmlLoaderBean;
import org.xhbruce.player.musicservice.MusicPlayerService;
import org.xhbruce.player.utils.io.FileUtil;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.xhbruce.player.application.MainApp.logger;
import static org.xhbruce.player.utils.io.PathInfo.getImage;
import static org.xhbruce.player.utils.io.PathInfo.getfxml;

public class MainAppController implements Initializable{
    private final Stage stage;

    @FXML
    private HBox windowHeader;
    private double xOffset;
    private double yOffset;
    @FXML
    private MFXFontIcon closeIcon;
    @FXML
    private MFXFontIcon minimizeIcon;
    @FXML
    private MFXFontIcon alwaysOnTopIcon;

    @FXML
    private MFXScrollPane scrollPane;
    @FXML
    private VBox navBar;

    @FXML
    private StackPane contentPane;
    private final ToggleGroup toggleGroup;

    File rootDir = new File("F:\\Music");
    private static String TAG = "MainAppController";

    @FXML
    private AnchorPane rootPane;

    @FXML
    private MFXButton open_music_file;
    @FXML
    private MFXButton open_music_folder;
    @FXML
    ListView musicList;
    private Boolean isMusicListFlush = false;
    @FXML
    private Label labelVersion;

    public MainAppController(Stage stage) {
        this.stage = stage;
        this.toggleGroup = new ToggleGroup();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        windowHeader.setOnMousePressed(event -> {
            xOffset = stage.getX() - event.getScreenX();
            yOffset = stage.getY() - event.getScreenY();
        });
        windowHeader.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() + xOffset);
            stage.setY(event.getScreenY() + yOffset);
        });

        closeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            MainAppExit.confirmApplicationExit(stage);
        });
        minimizeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> ((Stage) rootPane.getScene().getWindow()).setIconified(true));
        alwaysOnTopIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            boolean newVal = !stage.isAlwaysOnTop();
            alwaysOnTopIcon.pseudoClassStateChanged(PseudoClass.getPseudoClass("always-on-top"), newVal);
            stage.setAlwaysOnTop(newVal);
        });

        initializeLoader();

        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        labelVersion.setText("Hello, JavaFX " + javafxVersion + " Running on Java " + javaVersion + ".");

        open_music_file.setOnAction(event -> {
            File filePath = FileUtil.openFileChooser(stage, open_music_file.getText());
            if (filePath.isFile()) {
                stage.setTitle(MainApp.TITILE_TAG + " : " + AudioFile.getBaseFilename(filePath));
            }
        });
        open_music_folder.setOnAction(event -> {
            File filePath = FileUtil.openFolderChooser(stage, open_music_folder.getText());
            if (filePath != null && filePath.isDirectory()) {
                logger.infoTag(TAG, " open_music_folder = " + filePath.getAbsolutePath());
            }
        });
    }
    private void initializeLoader() {
        FxmlLoader loader = new FxmlLoader();
        loader.addView(FxmlLoaderBean.of("BUTTONS", getfxml("scene.fxml")).setBeanToNodeMapper(() -> createToggle("DEFALUT")).setDefaultRoot(true).get());
        loader.addView(FxmlLoaderBean.of("LISTS", getfxml("ListViews.fxml")).setBeanToNodeMapper(() -> createToggle("mfx-square-list", "Lists")).get());
        loader.addView(FxmlLoaderBean.of("PLAYPANE", getfxml("PlayPane.fxml")).setBeanToNodeMapper(() -> createToggle("mfx-square-list", "PlayPane")).get());
        loader.setOnLoadedAction(beans -> {
            List<ToggleButton> nodes = beans.stream()
                    .map(bean -> {
                        ToggleButton toggle = (ToggleButton) bean.getBeanToNodeMapper().get();
                        toggle.setOnAction(event -> contentPane.getChildren().setAll(bean.getRoot()));
                        if (bean.isDefaultView()) {
                            contentPane.getChildren().setAll(bean.getRoot());
                            toggle.setSelected(true);
                        }
                        return toggle;
                    })
                    .toList();
            navBar.getChildren().setAll(nodes);
        });
        loader.start();
    }

    private ToggleButton createToggle(String text){
        return createToggle(null,text);
    }
    private ToggleButton createToggle(String icon, String text) {
        MFXIconWrapper wrapper = new MFXIconWrapper(icon, 24, 32);

        MFXRectangleToggleNode toggleNode = new MFXRectangleToggleNode(text, wrapper);
        toggleNode.setAlignment(Pos.CENTER_LEFT);
        toggleNode.setMaxWidth(Double.MAX_VALUE);
        toggleNode.setToggleGroup(toggleGroup);

        return toggleNode;
    }

}
