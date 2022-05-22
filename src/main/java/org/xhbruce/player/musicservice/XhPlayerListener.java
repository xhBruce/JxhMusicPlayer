package org.xhbruce.player.musicservice;

import com.goxr3plus.streamplayer.stream.StreamPlayerEvent;
import com.goxr3plus.streamplayer.stream.StreamPlayerListener;
import org.xhbruce.player.utils.LOG;

import java.util.Map;

public class XhPlayerListener implements StreamPlayerListener {
    private static String TAG = "XhPlayerListener";

    @Override
    public void opened(Object dataSource, Map<String, Object> properties) {
        LOG.infoTag(TAG, "StreamPlayerListener opened");
    }

    @Override
    public void progress(int nEncodedBytes, long microsecondPosition, byte[] pcmData, Map<String, Object> properties) {
        LOG.infoTag(TAG, "StreamPlayerListener progress");
    }

    @Override
    public void statusUpdated(StreamPlayerEvent event) {
        LOG.infoTag(TAG, "StreamPlayerListener statusUpdated [" + event.getPlayerStatus() + "]");
    }
}
