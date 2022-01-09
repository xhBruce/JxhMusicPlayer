package org.xhbruce.player.application;

import com.goxr3plus.streamplayer.stream.StreamPlayer;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.xhbruce.player.controllers.FXMLController;
import org.xhbruce.player.utils.Logger;
import org.xhbruce.player.utils.io.PathInfo;
import org.xhbruce.player.utils.JavaFXTool;


public class MainApp extends Application {


    // ------ START: 基础 Application  ------
    /*  window ：launch 主线窗口*/
    public static Stage window;
    public static String TITILE_TAG = " XhMusicPlayer ";
    public static Logger logger = Logger.getLogger("XhMusicPlayer-");
    // ------ END.

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        window = stage;
        /* Window */
        window.setTitle(TITILE_TAG);
        window.setWidth(JavaFXTool.getVisualScreenWidth() * 0.5);
        window.setMinWidth(JavaFXTool.getVisualScreenWidth() * 0.5);
        window.setHeight(JavaFXTool.getVisualScreenHeight() * 0.5);
        window.setMinHeight(JavaFXTool.getVisualScreenHeight() * 0.5);
        window.centerOnScreen();
        window.getIcons().add(PathInfo.getImage("logo-xhcode.png"));
        window.centerOnScreen();
        window.setOnCloseRequest(exit -> {
            MainAppExit.confirmApplicationExit();
            exit.consume();
        });

        Parent root = FXMLLoader.load(PathInfo.getfxml("scene.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(PathInfo.getCSS("styles.css"));
        stage.setScene(scene);
        stage.show();

    }

    @Override
    public void stop() throws Exception {
        super.stop();
        System.out.println("----- MainApp stop() -----");
        System.exit(0);
    }

}
