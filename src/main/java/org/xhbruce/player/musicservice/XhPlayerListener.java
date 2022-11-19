package org.xhbruce.player.musicservice;

import com.goxr3plus.streamplayer.stream.StreamPlayerEvent;
import com.goxr3plus.streamplayer.stream.StreamPlayerListener;
import org.xhbruce.logger.Log;

import java.util.Map;

public class XhPlayerListener implements StreamPlayerListener {
    private static String TAG = "XhPlayerListener";

    @Override
    public void opened(Object dataSource, Map<String, Object> properties) {
        Log.i(TAG, "StreamPlayerListener opened");
    }

    @Override
    public void progress(int nEncodedBytes, long microsecondPosition, byte[] pcmData, Map<String, Object> properties) {
        Log.i(TAG, "StreamPlayerListener progress");
    }

    @Override
    public void statusUpdated(StreamPlayerEvent event) {
        Log.i(TAG, "StreamPlayerListener statusUpdated [" + event.getPlayerStatus() + "]");
    }
}
