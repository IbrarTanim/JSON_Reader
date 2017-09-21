package com.educareapps.jsonreader.utilitis;

import android.content.Context;
import android.os.Environment;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import com.educareapps.jsonreader.manager.DatabaseManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileProcessing {
    String TAG = DatabaseManager.class.getCanonicalName();
    private String appOutputSoundFullPath = null;
    private String appOutputSoundPath = null;
    private String appSoundPath = null;
    Context ctx;

    public FileProcessing(Context ctx) {
        this.ctx = ctx;
        this.appOutputSoundPath = StaticAccess.ANDROID_DATA + ctx.getPackageName() + StaticAccess.ANDROID_DATA_PACKAGE_SOUND;
    }

    public int fileSize(String path) {
        return Integer.parseInt(String.valueOf(new File(path).length() / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID));
    }

    public String createSoundFile(String inputPath) {
        FileNotFoundException fnfe1;
        InputStream inputStream;
        IOException e;
        OutputStream outputStream;
        try {
            OutputStream out;
            File sdCardDirectory = new File(Environment.getExternalStorageDirectory() + this.appOutputSoundPath);
            if (!sdCardDirectory.exists()) {
                sdCardDirectory.mkdirs();
            }
            this.appSoundPath = StaticAccess.FILE_FORMAT_NAME + System.currentTimeMillis() + StaticAccess.DOT_SOUND_FORMAT;
            File soundFile = new File(sdCardDirectory, this.appSoundPath);
            InputStream in = new FileInputStream(inputPath);
            try {
                out = new FileOutputStream(soundFile);
            } catch (FileNotFoundException e2) {
                fnfe1 = e2;
                inputStream = in;
                Log.e(this.TAG, fnfe1.getMessage());
                return this.appSoundPath;
            } catch (IOException e3) {
                e = e3;
                inputStream = in;
                e.printStackTrace();
                return this.appSoundPath;
            }
            try {
                byte[] buffer = new byte[1024];
                while (true) {
                    int read = in.read(buffer);
                    if (read == -1) {
                        break;
                    }
                    out.write(buffer, 0, read);
                }
                in.close();
                try {
                    out.flush();
                    out.close();
                } catch (FileNotFoundException e4) {
                    fnfe1 = e4;
                    outputStream = out;
                    Log.e(this.TAG, fnfe1.getMessage());
                    return this.appSoundPath;
                } catch (IOException e5) {
                    e = e5;
                    outputStream = out;
                    e.printStackTrace();
                    return this.appSoundPath;
                }
            } catch (FileNotFoundException e6) {
                fnfe1 = e6;
                outputStream = out;
                inputStream = in;
                Log.e(this.TAG, fnfe1.getMessage());
                return this.appSoundPath;
            } catch (IOException e7) {
                e = e7;
                outputStream = out;
                inputStream = in;
                e.printStackTrace();
                return this.appSoundPath;
            }
        } catch (FileNotFoundException e8) {
            fnfe1 = e8;
            Log.e(this.TAG, fnfe1.getMessage());
            return this.appSoundPath;
        } catch (IOException e9) {
            e = e9;
            e.printStackTrace();
            return this.appSoundPath;
        }
        return this.appSoundPath;
    }

    public boolean deleteSound(String soundName) {
        File sound = new File(getAbsolutepath_Of_Sound(soundName));
        if (sound.exists()) {
            return sound.delete();
        }
        return false;
    }

    public String getAbsolutepath_Of_Sound(String soundName) {
        return Environment.getExternalStorageDirectory() + this.appOutputSoundPath + soundName;
    }
}
