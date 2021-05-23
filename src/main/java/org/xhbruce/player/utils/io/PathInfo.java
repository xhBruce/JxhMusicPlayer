package org.xhbruce.player.utils.io;

import javafx.scene.image.Image;

import java.net.URL;

public class PathInfo {

    public static String FXMLS = "/fxml/";
    public static String CSSS = "/css/";
    public static String IMAGES = "/img/";

    private PathInfo() {
    }

    public static URL getfxml(String fxmlName) {
        return PathInfo.class.getResource(PathInfo.FXMLS + fxmlName);
    }

    public static String getCSS(String cssName) {
        return PathInfo.class.getResource(PathInfo.CSSS + cssName).toExternalForm();
    }

    public static Image getImage(String imageName) {
        return new Image(PathInfo.class.getResourceAsStream(IMAGES + imageName));
    }
}
