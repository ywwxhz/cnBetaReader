package com.ywwxhz.crash;

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
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 崩溃日志处理
 */
public class CrashLogWriter {
    private Context mContext;
    private SimpleDateFormat dataFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);

    public CrashLogWriter(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 写入
     *
     * @param ex
     *            异常堆栈
     * @return
     */
    public String writeLogToFile(Throwable ex) {
        if (ex == null) {
            return null;
        }
        // 4.把所有的信息写入日志
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File f = Environment.getExternalStorageDirectory();// 获取SD卡目录
                File fileDir = new File(f,
                        String.format(Locale.CHINA, "%s-crash-%s.txt",
                                mContext.getApplicationInfo().loadLabel(mContext.getPackageManager()),
                                dataFormat.format(new Date())));

                StringBuilder stringBuilder = new StringBuilder();
                // 1.获取当前程序的版本号. 版本的id
                getVersionInfo(stringBuilder);

                // 2.获取手机的硬件信息.
                getMobileInfo(stringBuilder);

                // 3.把错误的堆栈信息 获取出来
                getErrorInfo(stringBuilder, ex);

                FileOutputStream os = new FileOutputStream(fileDir);
                os.write(stringBuilder.toString().getBytes());
                os.close();
                return fileDir.getAbsolutePath();
            }
            return null;
        } catch (Exception ignored) {
            return null;
        }
    }

    /**
     * 获取错误的信息
     * 
     * @param logBuilder
     *            StringBuilder
     * @param arg1
     *            异常堆栈
     */
    private void getErrorInfo(StringBuilder logBuilder, Throwable arg1) {
        // 获取错误堆栈
        logBuilder.append("=====================Tracert Info=========================\n");
        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        arg1.printStackTrace(pw);
        pw.close();
        logBuilder.append(writer.toString());
    }

    /**
     * 获取手机的硬件信息
     * 
     * @param logBuilder
     *            StringBuilder
     */
    private void getMobileInfo(StringBuilder logBuilder) {
        // 通过反射获取系统的硬件信息
        logBuilder.append("=====================Hardware Info=========================\n");
        try {
            Field[] fields = Build.class.getDeclaredFields();
            for (Field field : fields) {
                // 暴力反射 ,获取私有的信息
                field.setAccessible(true);
                String name = field.getName();
                String value = field.get(null).toString();
                logBuilder.append(name).append("=").append(value);
                logBuilder.append("\n");
            }
        } catch (Exception e) {
            logBuilder.append("获取硬件信息错误\n");
            logBuilder.append(e.getLocalizedMessage());
        }
    }

    /**
     * 获取软件版本号
     * 
     * @param logBuilder
     *            StringBuilder
     */
    private void getVersionInfo(StringBuilder logBuilder) {
        try {
            logBuilder.append("=====================Software Info=========================\n");
            PackageManager pm = mContext.getPackageManager();
            PackageInfo info = pm.getPackageInfo(mContext.getPackageName(), 0);
            logBuilder.append("Current Process = ");
            logBuilder.append(getCurProcessName(mContext));
            logBuilder.append("\n");
            Field[] fields = info.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    if (field.get(info) != null) {
                        logBuilder.append(field.getName());
                        logBuilder.append(" = ");
                        logBuilder.append(field.get(info));
                        logBuilder.append("\n");
                    }
                } catch (Exception ignored) {
                }
            }
        } catch (Exception e) {
            logBuilder.append("获取软件信息错误\n");
            logBuilder.append(e.getLocalizedMessage());
        }
    }

    /**
     * 获取当前线程名称
     * 
     * @param context
     *            上下文副i向
     * @return 当前线程名称
     */
    private String getCurProcessName(Context context) {
        int pid = Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }
}