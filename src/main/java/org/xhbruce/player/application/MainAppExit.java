package org.xhbruce.player.application;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.xhbruce.player.musicservice.MusicPlayerService;
import org.xhbruce.player.utils.JavaFXTool;

/**
 * This class contains methods for exiting and restarting the application
 */

public class MainAppExit {

    private static void terminate(final boolean isSave) {
        if (isSave) {

        }
        MusicPlayerService.getInstance().stop();
        Platform.exit();
    }

    /**
     * This method is used to exit the application
     */
    public static void confirmApplicationExit(Stage stage) {
        final Alert alert = JavaFXTool.createAlert("Exit XhMusicPlayer ?",
                "Do you want to save the data?",
                null,
                Alert.AlertType.CONFIRMATION, StageStyle.UTILITY, stage, null);

        // Create Custom Buttons
        final ButtonType exit = new ButtonType("Exit", ButtonBar.ButtonData.OK_DONE);
        final ButtonType saveExit = new ButtonType("Save + Exit", ButtonBar.ButtonData.OK_DONE);
        final ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        ((Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL)).setDefaultButton(true);
        alert.getButtonTypes().setAll(exit, saveExit, cancel);

        // Pick the answer
        alert.showAndWait().ifPresent(answer -> {
            if (answer == exit)
                terminate(false);
            else if (answer == saveExit)
                terminate(true);
        });
    }
}
