package org.xhbruce.player.controllers;

import javafx.fxml.Initializable;
import org.xhbruce.player.musicservice.XhPlayer;
import org.xhbruce.player.musicservice.XhPlayerListener;
import org.xhbruce.player.utils.LOG;
import org.xhbruce.player.utils.ResourceBundleUtil;

import java.net.URL;
import java.util.ResourceBundle;

public class BaseController extends XhPlayerListener implements Initializable {
    private static String TAG = "BaseController-";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOG.info(TAG, "addStreamPlayerListener ");
        XhPlayer.getInstance().addStreamPlayerListener(this);
    }

    public String getString(String key) {
        return ResourceBundleUtil.getString(key);
    }
}
