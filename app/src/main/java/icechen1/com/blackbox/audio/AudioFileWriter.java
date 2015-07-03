package icechen1.com.blackbox.audio;

import android.media.AudioRecord;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * Created by YuChen on 2015-03-30.
 */
public class AudioFileWriter {
    private final File file;
    private final FileOutputStream os;
    private final BufferedOutputStream bos;
    private final DataOutputStream dos;

    AudioFileWriter(String url) throws FileNotFoundException {
        Date date = new Date();
        if(url == null){
            url = String.valueOf(date.getTime());
        }
        final File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/BlackBox/");
        dir.mkdirs(); //create folders where write files
        file = new File(dir, url+".wav");
        os = new FileOutputStream(file);
        bos = new BufferedOutputStream(os);
        dos = new DataOutputStream(bos);
    }

    public String getPath(){
        return file.getPath();
    }


    void write(byte[] buffer, long size) throws IOException {
        for(int i = 0; i < size;i++) {
            dos.writeByte(buffer[i]);
        }
    }

    void setupHeader(AudioRecord rec, int size) throws IOException {
        long byteRate = RECORDER_BPP * rec.getSampleRate() * rec.getChannelCount()/8;

        long totalDataLen = size + 36;

        writeWaveFileHeader(os, size, totalDataLen,
                rec.getSampleRate(), rec.getChannelCount(), byteRate);
    }
    void close() throws IOException {
        dos.flush();
        dos.close();
        Log.i("BlackBox", "Saved File at " + file.toURI());
    }

    private static final int RECORDER_BPP = 16;


    private void writeWaveFileHeader(
            FileOutputStream out, long totalAudioLen,
            long totalDataLen, long longSampleRate, int channels,
            long byteRate) throws IOException {

        byte[] header = new byte[44];

        header[0] = 'R';  // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f';  // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16;  // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1;  // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8);  // block align
        header[33] = 0;
        header[34] = RECORDER_BPP;  // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

        out.write(header, 0, 44);
    }

}
