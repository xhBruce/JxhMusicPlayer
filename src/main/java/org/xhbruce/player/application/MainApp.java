package org.xhbruce.player.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.id3.ID3Tags;
import org.xhbruce.logger.Log;
import org.xhbruce.player.controllers.MainAppController;
import org.xhbruce.player.utils.ResourceBundleUtil;

import java.util.logging.Level;

public class MainApp extends Application {

    private static String TAG = "MainApp";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        super.init();
        AudioFile.logger.setLevel(Level.OFF);
        AudioFileIO.logger.setLevel(Level.OFF);
        ID3Tags.logger.setLevel(Level.OFF);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Log.d(TAG, "----- start() -----");
        FXMLLoader loader = new FXMLLoader(ResourceBundleUtil.getfxml("MainApp.fxml"));
        loader.setControllerFactory(c -> new MainAppController(primaryStage));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(ResourceBundleUtil.getCSS("MainApp.css"));
        scene.setFill(Color.TRANSPARENT);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        Log.i(TAG, "----- stop() -----");
    }

}
