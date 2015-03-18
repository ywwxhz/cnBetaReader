package com.ywwxhz.activitys;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.widget.TranslucentStatus.TranslucentStatusHelper;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * Created by Sam on 14-4-15.
 */
public class ImageViewActivity extends Activity {
    public static final String IMAGE_URL = "image_url";

    private PhotoView photoView;

    private ProgressWheel progressWheel;
    private PhotoViewAttacher attacher;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getExtras().containsKey(IMAGE_URL)) {
            setContentView(R.layout.activity_imageview);
            TranslucentStatusHelper.setTranslucentStatus(this, TranslucentStatusHelper.TranslucentProxy.STATUS_BAR);
            this.progressWheel = (ProgressWheel) findViewById(R.id.progressWheel);
            this.photoView = (PhotoView) findViewById(R.id.photoView);
            this.attacher = new PhotoViewAttacher(photoView);

            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheOnDisk(true)
                    .considerExifParams(true).build();
            ImageLoader.getInstance().displayImage(getIntent().getExtras().getString(IMAGE_URL)
                    , photoView, options, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    progressWheel.setVisibility(View.GONE);
                    attacher.update();
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    Toast.makeText(ImageViewActivity.this,failReason.getCause().getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            }, new ImageLoadingProgressListener() {
                @Override
                public void onProgressUpdate(String imageUri, View view, int current, int total) {
                    progressWheel.setProgress((float) current / total);
                }
            });
        }
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

}
