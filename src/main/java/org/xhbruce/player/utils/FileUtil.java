package org.xhbruce.player.utils;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class FileUtil {

    private FileUtil() {
    }

    public static File openFileChooser(Stage stage) {
        return openFileChooser(stage, null);
    }

    public static File openFileChooser(Stage stage, String titleName) {
        String title = "Open Resource File";
        if (titleName != null) {
            title = titleName;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac", "*.flac"));
        // if (lastFolder != null) fileChooser.setInitialDirectory(lastFolder);
        return fileChooser.showOpenDialog(stage);
    }

    public static File openFolderChooser(Stage stage) {
        return openFolderChooser(stage, null);
    }

    public static File openFolderChooser(Stage stage, String titleName) {
        String title = "Open Folder";
        if (titleName != null) {
            title = titleName;
        }
        DirectoryChooser folderChooser = new DirectoryChooser();
        folderChooser.setTitle(title);
        // if (lastFolder != null) folderChooser.setInitialDirectory(lastFolder);
        return folderChooser.showDialog(stage);
    }

    public static String getExtension(File fileName) {
        return getExtension(fileName.getName());
    }

    public static String getExtension(String fileName) {
        return fileName.split("\\.(?=[^.]+$)")[1];
    }
}
