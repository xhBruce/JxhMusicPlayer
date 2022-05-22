package org.xhbruce.player.utils;

import java.io.InputStream;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class ResourceBundleUtil {

    private static final ResourceBundle resource;

    static {
        //resource = ResourceBundle.getBundle("language.String", Locale.ENGLISH);
        resource = ResourceBundle.getBundle("language.String", Locale.getDefault());
    }

    // fxml\css\img
    private static String FXMLS = "/fxml/";
    private static String CSSS = "/css/";
    private static String IMAGES = "/img/";
    //

    private ResourceBundleUtil() {
    }

    public static ResourceBundle getResource() {
        return resource;
    }

    public static String getString(String key) {
        try {
            return resource.getString(key);
        } catch (Exception exception) {
            return "";
        }
    }

    // ResourceBundleUtil.class.getResource start
    public static URL getfxml(String fxmlName) {
        return ResourceBundleUtil.class.getResource(FXMLS + fxmlName);
    }

    public static String getCSS(String cssName) {
        return ResourceBundleUtil.class.getResource(CSSS + cssName).toExternalForm();
    }

    public static InputStream getImage(String imageName) {
        return ResourceBundleUtil.class.getResourceAsStream(IMAGES + imageName);
    }
    public static URL getURL(String str){
        return ResourceBundleUtil.class.getResource("/" + str);
    }

    // ResourceBundleUtil.class.getResource end

}
