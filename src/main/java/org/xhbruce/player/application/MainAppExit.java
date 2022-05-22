package org.xhbruce.player.application;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.xhbruce.player.musicservice.XhPlayer;
import org.xhbruce.player.utils.JavaFXTool;
import org.xhbruce.player.utils.ResourceBundleUtil;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * This class contains methods for exiting and restarting the application
 */

public class MainAppExit {

    private MainAppExit() {
    }

    private static void terminate(final boolean isSave) {
        if (isSave) {

        }
        XhPlayer.getInstance().stop();
        Platform.exit();
        System.exit(1);
    }

    /**
     * This method is used to exit the application
     */
    public static void confirmApplicationExit(final Stage stage) {
        final Alert alert = JavaFXTool.createAlert(ResourceBundleUtil.getString("exit.app"),
                null,
                null,
                Alert.AlertType.NONE, StageStyle.UNIFIED, stage, null);

        // Create Custom Buttons
        final ButtonType exit = new ButtonType(ResourceBundleUtil.getString("exit"), ButtonBar.ButtonData.OK_DONE);
        final ButtonType minTray = new ButtonType(ResourceBundleUtil.getString("min.tray"), ButtonBar.ButtonData.OK_DONE);
        final ButtonType cancel = new ButtonType(ResourceBundleUtil.getString("cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(minTray, exit,cancel);
//        alert.getButtonTypes().setAll(minTray, exit);

        // Pick the answer
        alert.showAndWait().ifPresent(answer -> {
            if (answer == exit) {
                terminate(false);
            } else if (answer == minTray) {
                Platform.setImplicitExit(false);
                stage.hide();

                SystemTray systemTray = SystemTray.getSystemTray();
                Image image = Toolkit.getDefaultToolkit().getImage(ResourceBundleUtil.getURL("img/xhbruce_csdn.jpg"));
                String notifyStr = "JxhMusicPlayer";
                PopupMenu menu = new PopupMenu();
                MenuItem itemShow = new MenuItem(ResourceBundleUtil.getString("tray.show"));
                MenuItem itemExit = new MenuItem(ResourceBundleUtil.getString("tray.exit"));

                menu.add(itemShow);
                menu.add(itemExit);
                TrayIcon trayIcon = new TrayIcon(image, notifyStr, menu);

                itemShow.addActionListener(e -> Platform.runLater(() -> {
                    systemTray.remove(trayIcon);
                    Platform.setImplicitExit(true);
                    stage.show();
                }));
                itemExit.addActionListener(e -> Platform.runLater(() -> {
                    systemTray.remove(trayIcon);
                    Platform.setImplicitExit(true);
                    terminate(false);
                }));
                trayIcon.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                            System.out.println(e.getButton());
                            Platform.runLater(() -> {
                                systemTray.remove(trayIcon);
                                stage.show();
                            });
                        }
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {

                    }
                    @Override
                    public void mouseReleased(MouseEvent e) {

                    }
                    @Override
                    public void mouseEntered(MouseEvent e) {

                    }
                    @Override
                    public void mouseExited(MouseEvent e) {

                    }
                });

                try {
                    systemTray.add(trayIcon);
                } catch (AWTException e) {
                }
            }
        });
    }
}
