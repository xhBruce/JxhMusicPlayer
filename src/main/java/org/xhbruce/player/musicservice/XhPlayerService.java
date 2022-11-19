package org.xhbruce.player.musicservice;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.EventDispatchChain;
import org.jaudiotagger.audio.AudioFileFilter;
import org.xhbruce.logger.Log;

import java.io.File;

public class XhPlayerService extends Service<Boolean> {

    private static String TAG ="XhPlayerService";
    private static XhPlayerService xhPlayerService;
    private XhPlayer xhPlayer = XhPlayer.getInstance();

    @Override
    protected void ready() {
        super.ready();
        System.out.println("Service<> ready");
    }

    @Override
    protected void scheduled() {
        super.scheduled();
        System.out.println("Service<> scheduled");
    }

    @Override
    protected void running() {
        super.running();
        System.out.println("Service<> running");
    }

    @Override
    protected void succeeded() {
        super.succeeded();
        System.out.println("Service<> succeeded");
    }

    @Override
    protected void cancelled() {
        super.cancelled();
        System.out.println("Service<> cancelled");
    }

    @Override
    protected void failed() {
        super.failed();
        System.out.println("Service<> failed");
    }

    @Override
    public boolean cancel() {
        System.out.println("Service<> cancel");
        return super.cancel();
    }

    @Override
    public void restart() {
        super.restart();
        System.out.println("Service<> restart");
    }

    @Override
    public void reset() {
        super.reset();
        System.out.println("Service<> reset");
    }

    @Override
    public void start() {
        super.start();
        System.out.println("Service<> start");
    }

    @Override
    protected void executeTask(Task<Boolean> task) {
        super.executeTask(task);
    }

    @Override
    public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
        return super.buildEventDispatchChain(tail);
    }
    private XhPlayerService() {
    }

    public static XhPlayerService getInstance() {
        if (xhPlayerService == null) {
            synchronized (XhPlayerService.class) {
                if (xhPlayerService == null) {
                    xhPlayerService = new XhPlayerService();
                }
            }
        }
        return xhPlayerService;
    }

    @Override
    protected Task<Boolean> createTask() {
        return new Task<Boolean>() {
            @Override
            protected Boolean call() {
                System.out.println(" xhbruce tast ");
                File rootDir = new File("F:/Music/音乐/");
                File[] audioFiles = rootDir.listFiles(new AudioFileFilter(false));
                Log.i(TAG, rootDir.getName() + ".length = " + audioFiles.length);
                return true;
            }
        };
    }
}
