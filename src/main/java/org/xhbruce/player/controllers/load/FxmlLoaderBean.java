package org.xhbruce.player.controllers.load;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.util.Callback;

import java.net.URL;
import java.util.function.Supplier;

public class FxmlLoaderBean {
    //================================================================================
    // Properties
    //================================================================================
    private final String viewName;
    private final URL fxmlFile;
    private Parent root;
    private Callback<Class<?>, Object> controllerFactory;
    private boolean defaultView = false;
    private boolean loaded = false;
    private Supplier<Node> beanToNodeMapper;

    //================================================================================
    // Constructors
    //================================================================================
    public FxmlLoaderBean(String viewName, URL fxmlFile) {
        this.viewName = viewName;
        this.fxmlFile = fxmlFile;
    }

    public FxmlLoaderBean(String viewName, URL fxmlFile, Callback<Class<?>, Object> controllerFactory, boolean defaultView, Supplier<Node> beanToNodeMapper) {
        this.viewName = viewName;
        this.fxmlFile = fxmlFile;
        this.controllerFactory = controllerFactory;
        this.defaultView = defaultView;
        this.beanToNodeMapper = beanToNodeMapper;
    }

    //================================================================================
    // Static Methods
    //================================================================================
    public static Builder of(String viewName, URL fxmlFile) {
        return new Builder(viewName, fxmlFile);
    }

    //================================================================================
    // Getters/Setters
    //================================================================================

    /**
     * @return the view's identifier
     */
    public String getViewName() {
        return viewName;
    }

    /**
     * @return the view's FXML file
     */
    public URL getFxmlFile() {
        return fxmlFile;
    }

    /**
     * @return the FXML file's root node
     */
    public Parent getRoot() {
        return root;
    }

    /**
     * Sets the view's root node.
     * <p>
     * Package private, handled by the loader.
     */
    FxmlLoaderBean setRoot(Parent root) {
        this.root = root;
        return this;
    }

    /**
     * @return the callback used to produce the view's controller
     */
    public Callback<Class<?>, Object> getControllerFactory() {
        return controllerFactory;
    }

    /**
     * Sets the callback used to produce the view's controller.
     */
    public FxmlLoaderBean setControllerFactory(Callback<Class<?>, Object> controllerFactory) {
        this.controllerFactory = controllerFactory;
        return this;
    }

    /**
     * @return whether this view should be considered the default view
     */
    public boolean isDefaultView() {
        return defaultView;
    }

    /**
     * Sets whether this view should be considered the default view.
     */
    public FxmlLoaderBean setDefaultView(boolean defaultView) {
        this.defaultView = defaultView;
        return this;
    }

    /**
     * @return whether the view has been loaded by the loader
     */
    public boolean isLoaded() {
        return loaded;
    }

    /**
     * Sets whether the view has been loaded by the loader.
     * <p>
     * Package private, handled by the loader.
     */
    FxmlLoaderBean setLoaded(boolean loaded) {
        this.loaded = loaded;
        return this;
    }

    /**
     * @return the supplier used to convert this view into a {@code Node}
     */
    public Supplier<Node> getBeanToNodeMapper() {
        return beanToNodeMapper;
    }

    /**
     * Sets the supplier used to convert this view into a {@code Node}.
     */
    public FxmlLoaderBean setBeanToNodeMapper(Supplier<Node> beanToNodeMapper) {
        this.beanToNodeMapper = beanToNodeMapper;
        return this;
    }

    //================================================================================
    // Builder
    //================================================================================
    public static class Builder {
        private final FxmlLoaderBean bean;

        public Builder(String viewName, URL fxmlFile) {
            this.bean = new FxmlLoaderBean(viewName, fxmlFile);
        }

        public Builder setControllerFactory(Callback<Class<?>, Object> controllerFactory) {
            bean.setControllerFactory(controllerFactory);
            return this;
        }

        public Builder setDefaultRoot(boolean defaultRoot) {
            bean.setDefaultView(defaultRoot);
            return this;
        }

        public Builder setBeanToNodeMapper(Supplier<Node> beanToNodeMapper) {
            bean.setBeanToNodeMapper(beanToNodeMapper);
            return this;
        }

        public FxmlLoaderBean get() {
            return bean;
        }
    }
}
