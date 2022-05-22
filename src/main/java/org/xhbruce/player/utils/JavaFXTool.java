package org.xhbruce.player.utils;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * This class has some functions that are not there by default in JavaFX 8
 *
 * @author GOXR3PLUS
 */
public final class JavaFXTool {

    private JavaFXTool() {
    }

    // -----------------------------------------------------------------------------------------------------------
    public static Alert createAlert(final String title, final String headerText, final String contentText,
                                    final Alert.AlertType alertType, final StageStyle stageStyle, final Stage owner, final Node graphic) {

        // Show Alert
        final Alert alert = new Alert(alertType);
        if (title != null)
            alert.setTitle(title);
        if (headerText != null)
            alert.setHeaderText(headerText);
        if (contentText != null)
            alert.setContentText(contentText);
        if (stageStyle != null)
            alert.initStyle(stageStyle);
        if (owner != null)
            alert.initOwner(owner);
        if (graphic != null)
            alert.setGraphic(graphic);

        // Make sure alert is not outside the screen so app becomes unresponsible
        alert.heightProperty().addListener(l -> {

            // Width and Height of the Alert
            final double alertWidth = alert.getWidth();
            final double alertHeight = alert.getHeight();
            double alertScreenX = alert.getX();
            double alertScreenY = alert.getY();

            // Check if Alert goes out of the Screen on X Axis
            if (alertScreenX + alertWidth > JavaFXTool.getVisualScreenWidth())
                alertScreenX = (int) (JavaFXTool.getVisualScreenWidth() - alertWidth);
            else if (alertScreenX < 0)
                alertScreenX = 0;

            // Check if Alert goes out of the Screen on Y AXIS
            if (alertScreenY + alertHeight > JavaFXTool.getVisualScreenHeight())
                alertScreenY = (int) (JavaFXTool.getVisualScreenHeight() - alertHeight);
            else if (alertScreenY < 0)
                alertScreenY = 0;

            // Set the X and Y of the Alert
            alert.setX(alertScreenX);
            alert.setY(alertScreenY);
        });

        return alert;
    }

    // -----------------------------------------------------------------------------------------------------------
    public static double getVisualScreenHeight() {
        return Screen.getPrimary().getVisualBounds().getHeight();
    }

    public static double getVisualScreenWidth() {
        return Screen.getPrimary().getVisualBounds().getWidth();
    }

    public static double getScreenHeight() {
        return Screen.getPrimary().getBounds().getHeight();
    }

    public static double getScreenWidth() {
        return Screen.getPrimary().getBounds().getWidth();
    }

    // -----------------------------------------------------------------------------------------------------------
    public static FontIcon getFontIcon(String iconLiteral, Color color, int size) {
        FontIcon icon = new FontIcon(iconLiteral);
        icon.setIconColor(color);
        if (size != 0) {
            icon.setIconSize(size);
        }
        return icon;
    }

}
