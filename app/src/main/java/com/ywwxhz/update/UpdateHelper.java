package com.ywwxhz.update;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.request.BaseRequest;
import com.ywwxhz.lib.handler.BaseCallback;
import com.ywwxhz.lib.kits.FileKit;
import com.ywwxhz.lib.kits.NetKit;
import com.ywwxhz.lib.kits.PrefKit;
import com.ywwxhz.update.listener.OnUpdateListener;
import com.ywwxhz.update.pojo.UpdateInfo;

import java.io.File;

import okhttp3.Call;
import okhttp3.Response;

public class UpdateHelper {

    private File apkFile;
    private Options options;
    private String url = "";
    private Context mContext;
    private UpdateInfo updateInfo;
    private boolean running = false;
    private OnUpdateListener updateListener;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder ntfBuilder;

    private static UpdateHelper instance;
    private static UpdateReceiver receiver;
    private static String installPackageAction;
    private static final int DOWNLOAD_NOTIFICATION_ID = 0x3;
    private static final String ACTION_PREFIX = ".update.InstallPackage";

    private static void init(Context context) {
        if (receiver == null) {
            receiver = new UpdateReceiver();
            IntentFilter filter = new IntentFilter();
            installPackageAction = context.getPackageName() + ACTION_PREFIX;
            filter.addAction(installPackageAction);
            context.registerReceiver(receiver, filter);
        }
    }

    private static void destroy(Context context) {
        if (receiver != null) {
            context.unregisterReceiver(receiver);
        }
        receiver = null;
    }

    private UpdateHelper(Context context, String url, Options options) {
        this.mContext = context;
        this.url = url;
        notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        init(context.getApplicationContext());
        setOptions(options);
    }

    private UpdateHelper(Context context, String url) {
        this(context, url, null);
    }

    public void setOptions(Options options) {
        this.options = options == null ? new Options() : options;
        if (this.options.savePath == null) {
            this.options.savePath = FileKit.isExternalStorageAvalible()
                    ? new File(mContext.getExternalCacheDir(), "update") :
                    new File(mContext.getCacheDir(), "update");
        }
    }

    public void check() {
        check(null);
    }

    public void check(OnUpdateListener listener) {
        if (listener != null) {
            this.updateListener = listener;
        }
        if (mContext == null) {
            Log.e("NullPointerException", "The context must not be null.");
            return;
        }
        if (!running) {
            running = true;
            OkGo.get(url).execute(versionCheckHandler);
        }
    }

    private BaseCallback<String> versionCheckHandler = new BaseCallback<String>() {

        @Override
        public void onBefore(BaseRequest request) {
            if (updateListener != null) {
                updateListener.onStartCheck();
            }
        }

        @Override
        public String convertSuccess(Response response) throws Exception {
            try {
                return response.body().string();
            }finally {
                response.body().close();
            }
        }

        @Override
        protected void onError(int httpCode, Response response, Exception cause) {
            running = false;
            if (options.hintVersion && mContext != null) {
                Toast.makeText(mContext, "当前已是最新版", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onResponse(String responseString) {
            System.out.println(responseString);
            updateInfo = new Gson().fromJson(responseString, UpdateInfo.class);
            if (mContext != null && updateInfo != null) {
                int ignoreVersionCode = PrefKit.getInt(mContext, "ignoreVersionCode", 0);
                if (updateInfo.getVersionCode() > getPackageInfo().versionCode &&
                        (options.showIgnoreVersion || ignoreVersionCode != updateInfo.getVersionCode())) {
                    showUpdateUI();
                } else {
                    running = false;
                    if (options.hintVersion) {
                        Toast.makeText(mContext, "当前已是最新版", Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                running = false;
                if (options.hintVersion && mContext != null) {
                    Toast.makeText(mContext, "当前已是最新版", Toast.LENGTH_LONG).show();
                }
            }
            if (UpdateHelper.this.updateListener != null) {
                UpdateHelper.this.updateListener.onFinishCheck(updateInfo);
            }
        }
    };

    private void showNetDialog() {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == AlertDialog.BUTTON_POSITIVE) {
                    startDownload();
                }
                dialog.dismiss();
            }
        };
        new AlertDialog.Builder(mContext)
                .setTitle("下载提示")
                .setMessage("您在目前的网络环境下继续下载将可能会消耗手机流量，请确认是否继续下载？")
                .setNegativeButton("取消下载", listener)
                .setPositiveButton("继续下载", listener)
                .create().show();
    }

    /**
     * 弹出提示更新窗口
     */
    private void showUpdateUI() {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case AlertDialog.BUTTON_POSITIVE:
                        int type = NetKit.getNetType(mContext);
                        if (type != 1) {
                            showNetDialog();
                        } else {
                            startDownload();
                        }
                        break;
                    case AlertDialog.BUTTON_NEGATIVE:
                        running = false;
                        break;
                    case AlertDialog.BUTTON_NEUTRAL:
                        running = false;
                        PrefKit.writeInt(mContext, "ignoreVersionCode", updateInfo.getVersionCode());
                        break;
                }
                dialog.dismiss();
            }
        };
        AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setTitle(updateInfo.getUpdateTips())
                .setMessage(updateInfo.buildUpdateMessage(mContext))
                .setNegativeButton("下次再说", listener)
                .setNeutralButton("忽略该版本", listener)
                .setPositiveButton("立即更新", listener)
                .show();
        TextView textView = (TextView) dialog.findViewById(android.R.id.message);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
    }

    public void cleanDownload() {
        FileKit.deleteDir(options.savePath);
    }

    private void startDownload() {
        Toast.makeText(mContext, "开始下载更新", Toast.LENGTH_SHORT).show();
        String apkName = updateInfo.getAppName() + "-"
                + updateInfo.getVersionCode() + ".apk";
        if (!options.savePath.exists())
            options.savePath.mkdirs();
        apkFile = new File(options.savePath, apkName);
        if (apkFile.exists() && apkFile.length() == updateInfo.getSize()) {
            downloadComplate();
            if (UpdateHelper.this.updateListener != null) {
                UpdateHelper.this.updateListener.onFinshDownload();
            }
            return;
        }
        FileCallback downLoadFileHandler = new FileCallback(options.savePath.getAbsolutePath(), apkName) {

            @Override
            public void onSuccess(File file, Call call, Response response) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (FileKit.valifyFileMd5(apkFile, updateInfo.getMd5())) {
                            downloadComplate();
                        } else {
                            apkFile.delete();
                            NotificationCompat.Builder ntfBuilder = new NotificationCompat.Builder(mContext)
                                    .setSmallIcon(mContext.getApplicationInfo().icon)
                                    .setContentTitle("更新失败")
                                    .setContentText("文件校验失败")
                                    .setTicker("文件校验失败");
                            notificationManager.notify(DOWNLOAD_NOTIFICATION_ID,
                                    ntfBuilder.build());
                            running = false;
                        }
                    }
                }).start();
            }

            @Override
            public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                int progress1 = (int) ((currentSize / (float) totalSize) * 100);
                if (progress1 != oldProgress) {
                    showDownloadNotificationUI(progress1, currentSize, totalSize);
                    if (UpdateHelper.this.updateListener != null) {
                        UpdateHelper.this.updateListener.onDownloading(progress1);
                    }
                    oldProgress = progress1;
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                NotificationCompat.Builder ntfBuilder = new NotificationCompat.Builder(mContext)
                        .setSmallIcon(mContext.getApplicationInfo().icon)
                        .setContentTitle("更新失败")
                        .setContentText("更新下载失败")
                        .setTicker("更新下载失败");
                notificationManager.notify(DOWNLOAD_NOTIFICATION_ID,
                        ntfBuilder.build());
                running = false;
            }

            int oldProgress;
        };
        ntfBuilder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(mContext.getApplicationInfo().icon)
                .setTicker("开始下载...")
                .setOngoing(true)
                .setContentTitle("下载更新中...")
                .setProgress(100, 0, true);
        notificationManager.notify(DOWNLOAD_NOTIFICATION_ID,
                ntfBuilder.build());
        OkGo.get(updateInfo.getApkUrl()).execute(downLoadFileHandler);
    }

    /**
     * 通知栏弹出下载提示进度
     *
     * @param progress
     */
    private void showDownloadNotificationUI(int progress, long bytesWritten, long totalSize) {
        if (mContext != null) {
            if (progress == 100) {
                ntfBuilder.setContentText("文件校验中");
                ntfBuilder.setProgress(100, 0, true);
            } else {
                ntfBuilder.setContentText(FileKit.formatFileSize(mContext, bytesWritten)
                        + " / " + FileKit.formatFileSize(mContext, totalSize));
                ntfBuilder.setProgress(100, progress, false);
            }
            notificationManager.notify(DOWNLOAD_NOTIFICATION_ID,
                    ntfBuilder.build());
        }
    }

    /**
     * 获取当前app版本
     *
     * @return PackageInfo
     */
    private PackageInfo getPackageInfo() {
        PackageInfo pinfo = null;
        if (mContext != null) {
            try {
                pinfo = mContext.getPackageManager().getPackageInfo(
                        mContext.getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return pinfo;
    }

    private void installApk(Uri data) {
        if (mContext != null) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setDataAndType(data, "application/vnd.android.package-archive");
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(i);
            notificationManager.cancel(DOWNLOAD_NOTIFICATION_ID);
        } else {
            Log.e("NullPointerException", "The context must not be null.");
        }

    }

    private void downloadComplate() {
        Toast.makeText(mContext, "更新下载完毕", Toast.LENGTH_SHORT).show();
        if (options.autoInstall) {
            installApk(Uri.fromFile(apkFile));
        } else {
            NotificationCompat.Builder ntfBuilder = new NotificationCompat.Builder(mContext)
                    .setSmallIcon(mContext.getApplicationInfo().icon)
                    .setContentTitle("下载完成")
                    .setContentText("点击安装").setTicker("下载完成");
            Intent intent = new Intent(mContext, UpdateReceiver.class);
            intent.setAction(installPackageAction);
            intent.setData(Uri.fromFile(apkFile));
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            ntfBuilder.setContentIntent(pendingIntent);
            notificationManager.notify(DOWNLOAD_NOTIFICATION_ID,
                    ntfBuilder.build());
        }
        running = false;
    }

    public static UpdateHelper build(Context context, String url, Options options) {
        if (instance == null) {
            instance = new UpdateHelper(context, url, options);
        } else {
            instance.setOptions(options);
            instance.update(context, url, options);
        }
        return instance;
    }

    private void update(Context context, String url, Options options) {
        if (context == null) {
            throw new IllegalArgumentException("context can't be null");
        }
        if (TextUtils.isEmpty(url)) {
            throw new IllegalArgumentException("url can't be null or empty");
        }
        mContext = context;
        this.url = url;
        setOptions(options);
    }

    public static UpdateHelper build(Context context, String url) {
        if (instance == null) {
            instance = new UpdateHelper(context, url);
        } else {
            instance.update(context, url, null);
        }
        return instance;
    }

    public static class UpdateReceiver extends BroadcastReceiver {

        public UpdateReceiver() {
            super();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            UpdateHelper.destroy(context.getApplicationContext());
            Log.v("UpdateReceiver", intent.getData().toString());
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setDataAndType(intent.getData(), "application/vnd.android.package-archive");
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
            NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(DOWNLOAD_NOTIFICATION_ID);
        }
    }

    public static class Options {
        private File savePath;
        private boolean autoInstall = false;
        private boolean hintVersion = false;
        private boolean showIgnoreVersion = false;

        public Options() {
        }

        public Options setAutoInstall(boolean autoInstall) {
            this.autoInstall = autoInstall;
            return this;
        }

        public Options setHintVersion(boolean hintVersion) {
            this.hintVersion = hintVersion;
            return this;
        }

        public Options setSavePath(File savePath) {
            this.savePath = savePath;
            return this;
        }

        public Options setShowIgnoreVersion(boolean showIgnoreVersion) {
            this.showIgnoreVersion = showIgnoreVersion;
            return this;
        }

    }
}