package com.ywwxhz.activitys;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.melnykov.fab.FloatingActionButton;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.lib.kits.FileCacheKit;
import com.ywwxhz.lib.kits.FileKit;
import com.ywwxhz.lib.kits.NetKit;
import com.ywwxhz.lib.kits.PrefKit;
import com.ywwxhz.widget.TranslucentStatus.TranslucentStatusHelper;

import org.apache.http.Header;

import java.io.File;
import java.util.Locale;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * cnBetaReader
 *
 * Created by 远望の无限(ywwxhz) on 14-4-15 17:51.
 */
public class ImageViewActivity extends SwipeBackActivity {
    public static final String IMAGE_URL = "image_url";

    private PhotoView photoView;
    private TranslucentStatusHelper helper;
    private ProgressWheel progressWheel;
    private PhotoViewAttacher attacher;
    private com.melnykov.fab.FloatingActionButton action;
    private File image;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getExtras().containsKey(IMAGE_URL)) {
            helper = TranslucentStatusHelper.from(this)
                    .setTranslucentProxy(TranslucentStatusHelper.TranslucentProxy.STATUS_BAR)
                    .builder();
            setContentView(R.layout.activity_imageview);
            this.action = (FloatingActionButton) findViewById(R.id.action);
            this.progressWheel = (ProgressWheel) findViewById(R.id.progressWheel);
            this.photoView = (PhotoView) findViewById(R.id.photoView);
            this.attacher = new PhotoViewAttacher(photoView);
            this.image = new File(FileCacheKit.getInstance().getCacheDir().getAbsolutePath() + "/" + getIntent().getExtras().getString(IMAGE_URL).hashCode());
            this.action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "cnBetaPlus").toString()
                            + "/" + Uri.parse(getIntent().getExtras().getString(IMAGE_URL)).getLastPathSegment();
                    FileKit.copyFile(image.getAbsolutePath(),
                            new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "cnBetaPlus"),
                            Uri.parse(getIntent().getExtras().getString(IMAGE_URL)).getLastPathSegment());
                    Toast.makeText(ImageViewActivity.this, String.format(Locale.CHINA, "保存成功 文件路径：%s", path), Toast.LENGTH_LONG).show();
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
                }
            });

            if (image.exists()) {
                loadImage(image);
            } else {
                makeRequest();
            }

        }
        setSwipeBackEnable(PrefKit.getBoolean(this, R.string.pref_swipeback_key, true));
    }

    private void makeRequest() {
        final File temp = new File(FileCacheKit.getInstance().getCacheDir().getAbsolutePath() + "/" + System.currentTimeMillis());
        NetKit.getInstance().getClient().get(getIntent().getExtras().getString(IMAGE_URL), new FileAsyncHttpResponseHandler(temp) {
            @Override
            public void onStart() {
                photoView.setImageDrawable(null);
                photoView.setOnClickListener(null);
                attacher.setOnViewTapListener(null);
                attacher.setZoomable(true);
                attacher.update();
                progressWheel.setProgress(0);
                progressWheel.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                file.delete();
                photoView.setImageResource(R.drawable.imagehoder_error);
                attacher.setZoomable(false);
                attacher.update();
                photoView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        makeRequest();
                    }
                });
                progressWheel.setVisibility(View.GONE);
                makeText("图片下载失败",0xFF98473E);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                file.renameTo(image);
                loadImage(image);
            }

            @Override
            public void onProgress(int bytesWritten, int totalSize) {
                progressWheel.setProgress((float) bytesWritten / totalSize);
            }

            @Override
            public void onCancel() {
                temp.delete();
            }
        });
    }

    private void makeText(String message,int color) {
        LinearLayout infoHoder = (LinearLayout) getLayoutInflater().inflate(R.layout.infolayout, (ViewGroup) getWindow().getDecorView(),false);
        TextView text1 = (TextView) infoHoder.findViewById(R.id.message);
        int[] ints = helper.getInsertPixs(false);
        infoHoder.setPadding(0,  ints[1],
                ints[2], 0);
        text1.setText(message);
        infoHoder.setBackgroundColor(color);
        Crouton.make(ImageViewActivity.this, infoHoder).show();
    }

    private void loadImage(File file) {
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheOnDisk(false)
                .considerExifParams(true).build();
        ImageLoader.getInstance().displayImage(Uri.fromFile(file).toString()
                , photoView, options, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                progressWheel.setProgress(1);
                progressWheel.setVisibility(View.GONE);
                attacher.update();
                action.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        action.setVisibility(View.VISIBLE);
                        action.animate().scaleX(1).scaleY(1).setDuration(500).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                    }
                }, 200);
                makeText(getResources().getString(R.string.message_load_success), 0xFF11659A);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                loadFail();
                makeText("图片加载失败",0xFF98473E);
            }
        });
    }

    private void loadFail() {
        photoView.setImageResource(R.drawable.imagehoder_error);
        attacher.setZoomable(false);
        attacher.update();
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeRequest();
            }
        });
        progressWheel.setVisibility(View.GONE);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    protected void onDestroy() {
        NetKit.getInstance().getClient().cancelAllRequests(true);
        Crouton.clearCroutonsForActivity(this);
        super.onDestroy();
    }
}
