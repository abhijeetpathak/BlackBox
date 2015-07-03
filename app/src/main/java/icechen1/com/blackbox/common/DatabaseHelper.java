package icechen1.com.blackbox.common;

import android.content.Context;

import icechen1.com.blackbox.provider.recording.RecordingColumns;
import icechen1.com.blackbox.provider.recording.RecordingContentValues;
import icechen1.com.blackbox.provider.recording.RecordingModel;

/**
 * Created by yuchen.hou on 15-07-02.
 */
public class DatabaseHelper {
    public static void saveRecording(Context cxt, String name, String filename, long duration, long timestamp){
        RecordingContentValues values = new RecordingContentValues();
        values.putName(name).putFilename(filename).putDuration(duration).putTimestamp(timestamp);
        cxt.getContentResolver().insert(RecordingColumns.CONTENT_URI, values.values());
    }
}
