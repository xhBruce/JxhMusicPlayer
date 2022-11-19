package org.xhbruce.player.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import io.github.palexdev.materialfx.controls.MFXRectangleToggleNode;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.utils.others.loader.MFXLoader;
import io.github.palexdev.materialfx.utils.others.loader.MFXLoaderBean;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.xhbruce.player.application.MainAppExit;
import org.xhbruce.player.musicservice.XhPlayer;
import org.xhbruce.player.utils.FileUtil;
import org.xhbruce.logger.Log;
import org.xhbruce.player.utils.ResourceBundleUtil;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainAppController extends BaseController {

    private static String TAG = "MainAppController";
    private XhPlayer xhPlayer = XhPlayer.getInstance();
    private final Stage stage;

    @FXML
    private SplitPane rootPane;
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
    private VBox navBar;

    @FXML
    private StackPane contentPane;
    private final ToggleGroup toggleGroup;


    @FXML
    private MFXButton open_music_file;
    @FXML
    private MFXButton open_music_folder;
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
            if (filePath != null && filePath.isFile()) {
                xhPlayer.play(filePath);
            }
        });
        open_music_folder.setOnAction(event -> {
            File filePath = FileUtil.openFolderChooser(stage, open_music_folder.getText());
            if (filePath != null && filePath.isDirectory()) {
                Log.i(TAG, " open_music_folder = " + filePath.getAbsolutePath());
            }
        });
    }

    private void initializeLoader() {
        MFXLoader loader = new MFXLoader();
        loader.addView(MFXLoaderBean.of("BUTTONS", ResourceBundleUtil.getfxml("scene.fxml")).setBeanToNodeMapper(() -> createToggle("DEFALUT")).get());
        loader.addView(MFXLoaderBean.of("LISTS", ResourceBundleUtil.getfxml("ListViews.fxml")).setBeanToNodeMapper(() -> createToggle("mfx-square-list", "Lists")).get());
        loader.addView(MFXLoaderBean.of("PLAYPANE", ResourceBundleUtil.getfxml("PlayPane.fxml")).setBeanToNodeMapper(() -> createToggle("mfx-square-list", "PlayPane")).setDefaultRoot(true).get());
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

    private ToggleButton createToggle(String text) {
        return createToggle(null, text);
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
