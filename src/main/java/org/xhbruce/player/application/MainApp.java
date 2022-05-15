package org.xhbruce.player.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.xhbruce.player.controllers.MainAppController;
import org.xhbruce.player.utils.Logger;
import org.xhbruce.player.utils.io.PathInfo;

public class MainApp extends Application {


    // ------ START: Application  ------
    public static String TITILE_TAG = " XhMusicPlayer ";
    public static Logger logger = Logger.getLogger();
    // ------ END.

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
//        primaryStage.setTitle(TITILE_TAG);
//        primaryStage.getIcons().add(PathInfo.getImage("logo-xhcode.png"));

        FXMLLoader loader = new FXMLLoader(PathInfo.getfxml("MainApp.fxml"));
        loader.setControllerFactory(c -> new MainAppController(primaryStage));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(PathInfo.getCSS("MainApp.css"));
        scene.setFill(Color.TRANSPARENT);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();

    }

    @Override
    public void stop() throws Exception {
        super.stop();
        System.out.println("----- MainApp stop() -----");
        System.exit(0);
    }

}
