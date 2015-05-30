package com.ywwxhz.activitys;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.linroid.filtermenu.library.FilterMenu;
import com.linroid.filtermenu.library.FilterMenuLayout;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.ywwxhz.MyApplication;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.lib.ThemeManger;
import com.ywwxhz.lib.kits.FileKit;
import com.ywwxhz.lib.kits.PrefKit;
import com.ywwxhz.lib.kits.Toolkit;
import com.ywwxhz.lib.kits.UIKit;
import com.ywwxhz.widget.FixViewPager;
import com.ywwxhz.widget.TranslucentStatus.TranslucentStatusHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


/**
 * cnBetaReader
 * <p>
 * Created by 远望の无限(ywwxhz) on 14-4-15 17:51.
 */
public class ImageViewActivity extends FragmentActivity implements ViewPager.OnPageChangeListener {
    public static final String IMAGE_URLS = "image_urls";
    public static final String CURRENT_POS = "current";
    private static final String imageNumFormate = " %d / %d ";
    private FixViewPager pager;
    private TextView imagenum;
    private String[] imageSrcs;
    private int pos;
    private List<View> views;
    private List<ImageItem> imageItems;
    private int screenHeight;
    private int screenWidth;
    private boolean debug;
    private boolean preload_image;

    public void onCreate(Bundle savedInstanceState) {
        ThemeManger.onActivityCreateSetTheme(this);
        getWindow().setBackgroundDrawableResource(R.color.gray_80);
        super.onCreate(savedInstanceState);
        if (getIntent().getExtras().containsKey(IMAGE_URLS) && getIntent().getExtras().containsKey(CURRENT_POS)) {
            debug = MyApplication.getInstance().getDebug();
            preload_image = PrefKit.getBoolean(this,R.string.pref_preload_image_key,true);
            screenHeight = getResources().getDisplayMetrics().heightPixels;
            screenWidth = getResources().getDisplayMetrics().widthPixels;
            this.imageSrcs = getIntent().getStringArrayExtra(IMAGE_URLS);
            this.pos = getIntent().getIntExtra(CURRENT_POS, 0);
            if (imageSrcs.length == 0) {
                this.finish();
                return;
            }
            TranslucentStatusHelper.from(this)
                    .setTranslucentProxy(TranslucentStatusHelper.TranslucentProxy.STATUS_BAR)
                    .builder();
            setContentView(R.layout.activity_imageview);
            initView();
            loadAndShowPos(pos);
        } else {
            this.finish();
        }
    }

    private void initView() {
        FilterMenuLayout filtermenu = (FilterMenuLayout) findViewById(R.id.filter_menu);
        this.imagenum = (TextView) findViewById(R.id.image_num);
        this.pager = (FixViewPager) findViewById(R.id.pager);
        attachMenu(filtermenu);
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheOnDisk(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .considerExifParams(true).build();
        views = new ArrayList<>(imageSrcs.length);
        imageItems = new ArrayList<>(imageSrcs.length);
        int width = UIKit.dip2px(this, 4);
        int progressWidth = UIKit.dip2px(this, 80);
        for (String imageSrc : imageSrcs) {
            FrameLayout view = new FrameLayout(this);
            //View view = LayoutInflater.from(this).inflate(R.layout.image_item, pager, false);
            View imageView;
            FrameLayout.LayoutParams pvparams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            if (!imageSrc.endsWith(".gif")) {
                imageView = new SubsamplingScaleImageView(this);
                ((SubsamplingScaleImageView)imageView).setDebug(debug);
                ((SubsamplingScaleImageView)imageView).setMinimumDpi(50);
            } else {
                imageView = new GifImageView(this);
            }
            imageView.setLayoutParams(pvparams);
            view.addView(imageView);
            ProgressWheel progress = new ProgressWheel(this);
            progress.setRimWidth(width);
            progress.setBarWidth(width);
            progress.setBarColor(Color.parseColor("#fff0f4e2"));
            progress.setRimColor(Color.parseColor("#44000000"));
            FrameLayout.LayoutParams pgparams = new FrameLayout.LayoutParams(progressWidth, progressWidth);
            pgparams.gravity = Gravity.CENTER;
            progress.setLayoutParams(pgparams);
            progress.spin();
            view.addView(progress);
            imageItems.add(new ImageItem(imageSrc, imageView, progress, options));
            views.add(view);
        }
        PagerAdapter mPagerAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return imageSrcs.length;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(views.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(views.get(position));
                return views.get(position);
            }
        };
        pager.setAdapter(mPagerAdapter);
        pager.setOnPageChangeListener(this);
        pager.setPageTransformer(true, new MyPageTransformer());
        pager.setCurrentItem(pos);
    }

    private FilterMenu attachMenu(FilterMenuLayout layout) {
        return new FilterMenu.Builder(this)
                .addItem(R.drawable.ic_save)
                .addItem(R.drawable.ic_share)
                .addItem(R.drawable.ic_reflush)
                .withListener(new FilterMenu.OnMenuChangeListener() {
                    @Override
                    public void onMenuItemClick(View view, int position) {
                        if (position != 2) {
                            String image_url = imageSrcs[pager.getCurrentItem()];
                            File imageFile = ImageLoader.getInstance().getDiskCache().get(image_url);
                            if (imageFile != null) {
                                switch (position) {
                                    case 0:
                                        saveImage(image_url, imageFile);
                                        break;
                                    case 1:
                                        shareImage(image_url, imageFile);
                                        break;
                                }
                            } else {
                                Toast.makeText(ImageViewActivity.this, "图片还未下载完成", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            imageItems.get(pager.getCurrentItem()).displayImage();
                        }
                    }

                    @Override
                    public void onMenuCollapse() {

                    }

                    @Override
                    public void onMenuExpand() {

                    }
                })
                .attach(layout)
                .build();
    }

    private void shareImage(String imageUrl, File imageFile) {
        Toolkit.SharePhoto(imageFile.getAbsolutePath(), this);
    }

    private void saveImage(String imageUrl, File imageFile) {
        FileKit.copyFile(imageFile.getAbsolutePath(),
                new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "cnBetaPlus"),
                Uri.parse(imageUrl).getLastPathSegment());
        String path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "cnBetaPlus").toString()
                + "/" + Uri.parse(imageUrl).getLastPathSegment();
        Toast.makeText(ImageViewActivity.this, String.format(Locale.CHINA, "保存成功 文件路径：%s", path), Toast.LENGTH_LONG).show();
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
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
        Crouton.clearCroutonsForActivity(this);
        super.onDestroy();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        loadAndShowPos(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    class ImageItem {
        public final int NOTSHOW = 0;
        public final int SHOWING = 1;
        public final int SHOWSUCCESS = 3;
        public final int SHOWFAILURE = 4;
        private String imageSrc;
        private View imageview;
        private ProgressWheel progress;
        private DisplayImageOptions options;
        private int showStatus = NOTSHOW;

        public ImageItem(String imageSrc, View imageview, ProgressWheel progress, DisplayImageOptions options) {
            this.imageSrc = imageSrc;
            this.imageview = imageview;
            this.progress = progress;
            this.options = options;
            this.showStatus = NOTSHOW;
        }

        public void displayImage() {
            if (showStatus == NOTSHOW || showStatus == SHOWFAILURE) {
                ImageLoader.getInstance().loadImage(imageSrc, null, options, new SimpleImageLoadingListener() {

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        if (showStatus == NOTSHOW || showStatus == SHOWFAILURE) {
                            progress.spin();
                            progress.setVisibility(View.VISIBLE);
                        }
                        showStatus = SHOWING;
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        progress.setVisibility(View.GONE);
                        File imageFile = ImageLoader.getInstance().getDiskCache().get(imageUri);
                        if (imageFile != null) {
                            if (imageview instanceof SubsamplingScaleImageView) {
                                if (loadedImage.getWidth() * 1.5 < loadedImage.getHeight()) {
                                    ((SubsamplingScaleImageView) imageview).setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);
                                }
                                ((SubsamplingScaleImageView) imageview).setImage(ImageSource.uri(Uri.fromFile(imageFile)));
                            } else {
                                try {
                                    GifDrawable g = new GifDrawable(imageFile);
                                    ((GifImageView)imageview).setImageDrawable(g);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            showStatus = SHOWSUCCESS;
                        } else {
                            showStatus = SHOWFAILURE;
                        }
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        progress.setVisibility(View.GONE);
                        showStatus = SHOWFAILURE;
                    }
                }, new ImageLoadingProgressListener() {
                    @Override
                    public void onProgressUpdate(String imageUri, View view, int current, int total) {
                        progress.setProgress((float) current / total);
                    }
                });
            }
        }
    }

    private void loadAndShowPos(int pos) {
        imageItems.get(pos).displayImage();
        if(preload_image) {
            if (pos > 1) {
                imageItems.get(pos - 1).displayImage();
            }
            if (pos < imageItems.size() - 2) {
                imageItems.get(pos + 1).displayImage();
            }
        }
        imagenum.setText(String.format(Locale.CHINA, imageNumFormate, pos + 1, imageSrcs.length));
    }

    private class MyPageTransformer implements ViewPager.PageTransformer {
        @Override
        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);
            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when
                // moving to the left page
                view.setAlpha(1);
                view.setTranslationX(0);
            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);
                // Counteract the default slide transition
                view.setTranslationX(pageWidth / 2 * -position);
            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }

    }
}
