package org.xhbruce.player.controllers.load;

import io.github.palexdev.materialfx.beans.properties.functional.SupplierProperty;
import io.github.palexdev.materialfx.enums.LoaderCacheLevel;
import io.github.palexdev.materialfx.utils.LoaderUtils;
import io.github.palexdev.materialfx.utils.others.loader.MFXLoader;
import io.github.palexdev.materialfx.utils.others.loader.MFXLoaderBean;
import javafx.fxml.FXMLLoader;
import javafx.scene.CacheHint;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class FxmlLoader {
    //================================================================================
    // Properties
    //================================================================================
    private final Map<String, FxmlLoaderBean> viewMap = new LinkedHashMap<>();
    private final SupplierProperty<FXMLLoader> fxmlLoaderSupplier = new SupplierProperty<>(FXMLLoader::new) {
        @Override
        public void set(Supplier<FXMLLoader> newValue) {
            if (newValue == null) {
                super.set(FXMLLoader::new);
                return;
            }
            super.set(newValue);
        }
    };
    private final AtomicInteger loadedCount = new AtomicInteger(0);
    private Consumer<List<FxmlLoaderBean>> onLoadedAction;
    private LoaderCacheLevel cacheLevel = LoaderCacheLevel.SCENE_CACHE;

    //================================================================================
    // Constructors
    //================================================================================
    public FxmlLoader() {
        this(null);
    }

    public FxmlLoader(Consumer<List<FxmlLoaderBean>> onLoadedAction) {
        this.onLoadedAction = onLoadedAction;
    }

    //================================================================================
    // Methods
    //================================================================================
    public void start() {
        List<FxmlLoaderBean> toLoad = viewMap.values().stream()
                .filter(bean -> !bean.isLoaded())
                .collect(Collectors.toList());
        for (FxmlLoaderBean bean : toLoad) {
            try {
                Callable<Parent> task = buildTask(bean);
                Parent loaded = LoaderUtils.submit(task).get();
                cacheParent(loaded);
                bean.setLoaded(true);
                loadedCount.addAndGet(1);
            } catch (InterruptedException | ExecutionException ex) {
                ex.printStackTrace();
            } finally {
                loadedCount.addAndGet(1);
            }
        }
        onLoaded(new ArrayList<>(viewMap.values()));
    }

    /**
     * Same as {@link #start()} but the load tasks are submitted to the given
     * {@link ExecutorService}.
     */
    public void startWith(ExecutorService executorService) {
        List<FxmlLoaderBean> toLoad = viewMap.values().stream()
                .filter(bean -> !bean.isLoaded())
                .collect(Collectors.toList());
        for (FxmlLoaderBean bean : toLoad) {
            try {
                Callable<Parent> task = buildTask(bean);
                Parent loaded = executorService.submit(task).get();
                cacheParent(loaded);
                bean.setLoaded(true);
                loadedCount.addAndGet(1);
            } catch (InterruptedException | ExecutionException ex) {
                ex.printStackTrace();
            } finally {
                loadedCount.addAndGet(1);
            }
        }
        onLoaded(new ArrayList<>(viewMap.values()));
    }

    /**
     * Adds the given view to the views map.
     */
    public FxmlLoader addView(FxmlLoaderBean bean) {
        LoaderUtils.checkFxmlFile(bean.getFxmlFile());
        viewMap.put(bean.getViewName(), bean);
        return this;
    }

    /**
     * Builds a new {@link MFXLoaderBean} with the given identifier and FXML file,
     * then adds it to the views map.
     */
    public FxmlLoader addView(String viewName, URL fxmlFile) {
        LoaderUtils.checkFxmlFile(fxmlFile);
        viewMap.put(viewName, new FxmlLoaderBean(viewName, fxmlFile));
        return this;
    }

    /**
     * Builds a new {@link MFXLoaderBean} with the given identifier, FXML file and
     * controller factory, then adds it to the views map.
     */
    public FxmlLoader addView(String viewName, URL fxmlFile, Callback<Class<?>, Object> controllerFactory) {
        LoaderUtils.checkFxmlFile(fxmlFile);
        viewMap.put(viewName, new FxmlLoaderBean(viewName, fxmlFile, controllerFactory, false, null));
        return this;
    }

    /**
     * @return a view for the given identifier, or null if no view is found
     */
    public FxmlLoaderBean getView(String viewName) {
        return viewMap.getOrDefault(viewName, null);
    }

    /**
     * This method is called once all the views have been loaded by {@link #start()} or
     * {@link #startWith(ExecutorService)}.
     * <p>
     * This simple methods is just responsible for executing the action specified by the user, {@link #setOnLoadedAction(Consumer)},
     * if not null.
     */
    protected void onLoaded(List<FxmlLoaderBean> beans) {
        if (onLoadedAction != null) {
            onLoadedAction.accept(beans);
        }
    }

    /**
     * Responsible for building the {@link Callable} which will load the given view.
     * <p>
     * First a {@link FXMLLoader} is created by using the specified supplier, {@link #fxmlLoaderSupplierProperty()},
     * then the location and controller factory are set on it.
     * <p>
     * {@link FXMLLoader#load()} is invoked and then the loaded {@link Parent} is set in the bean.
     */
    private Callable<Parent> buildTask(FxmlLoaderBean bean) {
        List<Callable<Parent>> tasks = new ArrayList<>();
        return () -> {
            FXMLLoader loader = getFxmlLoaderSupplier().get();
            URL fxmlFile = bean.getFxmlFile();
            Callback<Class<?>, Object> controllerFactory = bean.getControllerFactory();
            loader.setLocation(fxmlFile);
            loader.setControllerFactory(controllerFactory);

            Parent root = loader.load();
            bean.setRoot(root);
            return root;
        };
    }

    /**
     * This method is responsible for caching/preloading the loaded views to make them
     * ready for switching.
     * For a description of the various cache levels, see {@link LoaderCacheLevel}.
     * <p>
     * If the cache level is set to {@link LoaderCacheLevel#NONE} exits immediately.
     */
    private void cacheParent(Parent parent) {
        if (cacheLevel == LoaderCacheLevel.NONE) return;

        if (cacheLevel == LoaderCacheLevel.SCENE_JAVAFX_CACHE) {
            parent.setCache(true);
            parent.setCacheHint(CacheHint.SPEED);
        }

        StackPane pane = new StackPane();
        pane.getChildren().setAll(parent);
        Scene scene = new Scene(pane);
        pane.applyCss();
        pane.layout();
    }

    //================================================================================
    // Getters/Setters
    //================================================================================
    public Supplier<FXMLLoader> getFxmlLoaderSupplier() {
        return fxmlLoaderSupplier.get();
    }

    /**
     * Specifies the {@link Supplier} used to build a new {@link FXMLLoader} each time a view has to be loaded.
     */
    public SupplierProperty<FXMLLoader> fxmlLoaderSupplierProperty() {
        return fxmlLoaderSupplier;
    }

    public FxmlLoader setFxmlLoaderSupplier(Supplier<FXMLLoader> fxmlLoaderSupplier) {
        this.fxmlLoaderSupplier.set(fxmlLoaderSupplier);
        return this;
    }

    public Consumer<List<FxmlLoaderBean>> getOnLoadedAction() {
        return onLoadedAction;
    }

    /**
     * Sets the action to perform once all the views have been loaded.
     * <p>
     * The action is a {@link Consumer} which carries the list fo loaded views.
     */
    public FxmlLoader setOnLoadedAction(Consumer<List<FxmlLoaderBean>> onLoadedAction) {
        this.onLoadedAction = onLoadedAction;
        return this;
    }

    public LoaderCacheLevel getCacheLevel() {
        return cacheLevel;
    }

    /**
     * Sets the {@link LoaderCacheLevel} for this loader.
     */
    public FxmlLoader setCacheLevel(LoaderCacheLevel cacheLevel) {
        this.cacheLevel = cacheLevel;
        return this;
    }
}
