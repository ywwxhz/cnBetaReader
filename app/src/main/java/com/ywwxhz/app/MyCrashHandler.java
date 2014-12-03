package com.ywwxhz.app;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 自定义的 异常处理类 , 实现了 UncaughtExceptionHandler接口
 *
 * @author Administrator
 */
public class MyCrashHandler implements UncaughtExceptionHandler {
    private static final String TAG = "MyCrashHandler";
    // 需求是 整个应用程序 只有一个 MyCrash-Handler
    private static MyCrashHandler myCrashHandler;
    //系统默认的UncaughtException处理类
    private UncaughtExceptionHandler mDefaultHandler;
    private Context mContext;
    private SimpleDateFormat dataFormat = new SimpleDateFormat(
            "yyyyMMddHHmmss");
    private File fileDir;

    // 1.私有化构造方法
    private MyCrashHandler() {

    }

    public static synchronized MyCrashHandler getInstance() {
        if (myCrashHandler != null) {
            return myCrashHandler;
        } else {
            myCrashHandler = new MyCrashHandler();
            return myCrashHandler;
        }
    }

    public void init(Context context) {
        this.mContext = context;
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(thread, ex) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            //退出程序
            Process.killProcess(Process.myPid());
        }
    }

    private boolean handleException(Thread thread, Throwable ex) {
        if (ex == null) {
            return false;
        }
        // 4.把所有的信息写入日志
        try {
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                File f = mContext.getExternalFilesDir("Logs");// 获取SD卡目录
                fileDir = new File(f, "crash-"
                        + dataFormat.format(new Date()) + ".txt");

                StringBuilder stringBuilder = new StringBuilder();
                // 1.获取当前程序的版本号. 版本的id
                getVersionInfo(stringBuilder);

                // 2.获取手机的硬件信息.
                getMobileInfo(stringBuilder);

                // 3.把错误的堆栈信息 获取出来
                getErrorInfo(stringBuilder, thread, ex);

                FileOutputStream os = new FileOutputStream(fileDir);
                os.write(stringBuilder.toString()
                        .getBytes());
                os.close();
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    /**
     * 获取错误的信息
     */
    private void getErrorInfo(StringBuilder stringBuilder, Thread thread, Throwable arg1) {
        // 获取错误堆栈
        stringBuilder.append("=====================Tracert Info=========================\n");
        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        arg1.printStackTrace(pw);
        pw.close();
        stringBuilder.append(writer.toString());
    }

    /**
     * 获取手机的硬件信息
     */
    private void getMobileInfo(StringBuilder stringBuilder) {
        // 通过反射获取系统的硬件信息
        stringBuilder.append("=====================Hardware Info=========================\n");
        try {
            Field[] fields = Build.class.getDeclaredFields();
            for (Field field : fields) {
                // 暴力反射 ,获取私有的信息
                field.setAccessible(true);
                String name = field.getName();
                String value = field.get(null).toString();
                stringBuilder.append(name).append("=").append(value);
                stringBuilder.append("\n");
            }
        } catch (Exception e) {
            stringBuilder.append("获取硬件信息错误\n");
            stringBuilder.append(e.getLocalizedMessage());
        }
    }

    /**
     * 软件版本号
     */
    private void getVersionInfo(StringBuilder stringBuilder) {
        try {
            stringBuilder.append("=====================Software Info=========================\n");
            PackageManager pm = mContext.getPackageManager();
            PackageInfo info = pm.getPackageInfo(mContext.getPackageName(), 0);
            stringBuilder.append("Current Process = ");
            stringBuilder.append(getCurProcessName(mContext));
            stringBuilder.append("\n");
            Field[] fields = info.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    if (field.get(info) != null) {
                        stringBuilder.append(field.getName());
                        stringBuilder.append(" = ");
                        stringBuilder.append(field.get(info));
                        stringBuilder.append("\n");
                    }
                } catch (Exception ignored) {
                }
            }
        } catch (Exception e) {
            stringBuilder.append("获取软件信息错误\n");
            stringBuilder.append(e.getLocalizedMessage());
        }
    }

    private String getCurProcessName(Context context) {
        int pid = Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }
}