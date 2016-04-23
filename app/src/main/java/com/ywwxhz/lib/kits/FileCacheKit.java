package com.ywwxhz.lib.kits;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by ywwxhz on 2014/10/17.
 */
public class FileCacheKit {
    private static final int MESSAGE_FINISH = 0x01;
    private static FileCacheKit fileCacheKit;
    private File cacheDir;

    FileCacheKit(File cacheDir) {
        this.cacheDir = cacheDir;
    }

    FileCacheKit(Context context) {
        this.cacheDir = context.getCacheDir();
    }

    public static FileCacheKit getInstance(File cacheDir) {
        if (fileCacheKit == null) {
            fileCacheKit = new FileCacheKit(cacheDir);
        }
        return fileCacheKit;
    }

    public static FileCacheKit getInstance() {
        if (fileCacheKit == null)
            throw new NullPointerException(
                    "must getInstance(File cacheDir) before getInstance()");
        return fileCacheKit;
    }

    public static FileCacheKit getInstance(Context context) {
        if (fileCacheKit == null) {
            fileCacheKit = new FileCacheKit(context);
        }
        return fileCacheKit;
    }

    public void put(String filename, String value) {
        put(filename, value, "json");
    }

    public void put(String filename, String value, String type) {
        FileKit.writeFile(cacheDir, filename + "." + type, value);

    }

    public void putAsync(String key, String value, FileCacheListener listener) {
        putAsync(key, value, "json", listener);
    }

    public void putAsync(String key, String value, String type, FileCacheListener listener) {
        FileCacheHandler handler = null;
        if (listener != null) {
            handler = new FileCacheHandler(listener);
        }
        new FileCacheSaveThread(key, value, type, handler).start();
    }

    public void putObject(String filename, Object value) {
        Gson gson = new Gson();
        put(filename, gson.toJson(value));
    }

    public void getAsync(String key, FileCacheListener listener) {
        FileCacheHandler handler = null;
        if (listener != null) {
            handler = new FileCacheHandler(listener);
        }
        new FileCacheGetThread(key, handler).start();
    }

    public String getAsString(String filename) {
        return getAsString(filename, "json");
    }

    public String getAsString(String filename, String type) {
        return FileKit.getFileContent(cacheDir, filename + "." + type);
    }

    public <T> T getAsObject(String key, Class<T> clazz) {
        return getAsObject(key, "json", clazz);
    }

    public <T> T getAsObject(String key, String type, Class<T> clazz) {
        Gson gson = new Gson();
        String tmp = getAsString(key, type);
        if (tmp == null) {
            return null;
        } else {
            try {
                return gson.fromJson(tmp, clazz);
            } catch (Exception e) {
                return null;
            }
        }
    }

    public boolean isCached(String key, String type){
        return FileKit.isExit(cacheDir, key + "." + type);
    }

    public <T> T getAsObject(String key, TypeToken<T> typeToken) {
        return getAsObject(key, "json", typeToken);
    }

    public <T> T getAsObject(String key, String type, TypeToken<T> typeToken) {
        Gson gson = new Gson();
        String tmp = getAsString(key, type);
        if (tmp == null) {
            return null;
        } else {
            try {
                return gson.fromJson(tmp, typeToken.getType());
            } catch (Exception e) {
                return null;
            }
        }
    }

    public void cleanCache() {
        File[] files = cacheDir.listFiles();
        if (files != null) {
            for (File f : files) {
                f.delete();
            }
        }
    }

    public long getCacheSize() {
        try {
            return FileKit.getFolderSize(cacheDir);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public File getCacheDir() {
        return cacheDir;
    }

    public interface FileCacheListener {
        public void onFinish(String obj);
    }

    private class FileCacheSaveThread extends Thread {
        private String key;
        private String value;
        private String type;
        private FileCacheHandler handler;

        protected FileCacheSaveThread(String key, String value, String type, FileCacheHandler handler) {
            this.key = key;
            this.type = type;
            this.value = value;
            this.handler = handler;
        }

        @Override
        public void run() {
            put(key, value, type);
            if (handler != null) {
                Message msg = new Message();
                msg.what = MESSAGE_FINISH;
                msg.obj = "";
                handler.sendMessage(msg);
            }
        }
    }

    private class FileCacheGetThread extends Thread {
        private String key;
        private FileCacheHandler handler;

        protected FileCacheGetThread(String key, FileCacheHandler handler) {
            this.key = key;
            this.handler = handler;
        }

        @Override
        public void run() {
            if (handler != null) {
                Message msg = new Message();
                msg.what = MESSAGE_FINISH;
                msg.obj = getAsString(key);
                handler.sendMessage(msg);
            }
        }
    }

    private static class FileCacheHandler extends Handler {
        private WeakReference<FileCacheListener> listener;

        private FileCacheHandler(FileCacheListener listener) {
            super(Looper.getMainLooper());
            this.listener =  new WeakReference<>(listener);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_FINISH:
                    if (listener != null) {
                        listener.get().onFinish((String) msg.obj);
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    }

}
