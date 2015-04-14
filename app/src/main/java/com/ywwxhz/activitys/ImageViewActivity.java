package com.ywwxhz.activitys;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.linroid.filtermenu.library.FilterMenu;
import com.linroid.filtermenu.library.FilterMenuLayout;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.lib.ThemeManger;
import com.ywwxhz.lib.kits.FileKit;
import com.ywwxhz.lib.kits.NetKit;
import com.ywwxhz.lib.kits.Toolkit;
import com.ywwxhz.widget.FixViewPager;
import com.ywwxhz.widget.TranslucentStatus.TranslucentStatusHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * cnBetaReader
 *
 * Created by 远望の无限(ywwxhz) on 14-4-15 17:51.
 */
public class ImageViewActivity extends FragmentActivity implements ViewPager.OnPageChangeListener{
    public static final String IMAGE_URLS = "image_urls";
    public static final String CURRENT_POS = "current";
    private static final String imageNumFormate = " %d / %d ";
    private FixViewPager pager;
    private TextView imagenum;
    private String[] imageSrcs;
    private int pos;
    private List<View> views;
    private List<ImageItem> imageItems;

    public void onCreate(Bundle savedInstanceState) {
        ThemeManger.onActivityCreateSetTheme(this);
        getWindow().setBackgroundDrawableResource(R.color.gray_80);
        super.onCreate(savedInstanceState);
        if (getIntent().getExtras().containsKey(IMAGE_URLS) && getIntent().getExtras().containsKey(CURRENT_POS)) {
            this.imageSrcs = getIntent().getStringArrayExtra(IMAGE_URLS);
            this.pos = getIntent().getIntExtra(CURRENT_POS, 0);
            if(imageSrcs.length==0){
                this.finish();
                return;
            }
            TranslucentStatusHelper.from(this)
                    .setTranslucentProxy(TranslucentStatusHelper.TranslucentProxy.STATUS_BAR)
                    .builder();
            setContentView(R.layout.activity_imageview);
            initView();
            loadAndShowPos(pos);
        }else{
            this.finish();
        }
    }

    private void initView() {
        FilterMenuLayout filtermenu = (FilterMenuLayout) findViewById(R.id.filter_menu);
        this.imagenum = (TextView) findViewById(R.id.image_num);
        this.pager = (FixViewPager) findViewById(R.id.pager);
        attachMenu(filtermenu);
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheOnDisk(true).cacheInMemory(true)
                .considerExifParams(true).build();
        views = new ArrayList<>(imageSrcs.length);
        imageItems = new ArrayList<>(imageSrcs.length);
        for (String imageSrc:imageSrcs){
            View view = LayoutInflater.from(this).inflate(R.layout.image_item, pager,false);
            PhotoView pv = (PhotoView) view.findViewById(R.id.photoView);
            ProgressWheel progress = (ProgressWheel) view.findViewById(R.id.progressWheel);
            imageItems.add(new ImageItem(imageSrc,pv,progress,options));
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
        pager.setCurrentItem(pos);
    }

    private FilterMenu attachMenu(FilterMenuLayout layout){
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

    private void shareImage(String imageUrl,File imageFile) {
        Toolkit.SharePhoto(imageFile.getAbsolutePath(), this);
    }

    private void saveImage(String imageUrl,File imageFile) {
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
        NetKit.getInstance().getClient().cancelAllRequests(true);
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

    class ImageItem{
        public final int NOTSHOW = 0;
        public final int SHOWING = 1;
        public final int SHOWSUCCESS = 3;
        public final int SHOWFAILURE = 4;
        private String imageSrc;
        private PhotoView photoView;
        private ProgressWheel progress;
        private PhotoViewAttacher attacher;
        private DisplayImageOptions options;
        private int showStatus = NOTSHOW;

        public ImageItem(String imageSrc, PhotoView photoView, ProgressWheel progress,DisplayImageOptions options) {
            this.imageSrc = imageSrc;
            this.photoView = photoView;
            this.progress = progress;
            this.options = options;
            this.showStatus = NOTSHOW;
        }

        public void displayImage(){
            if(showStatus==NOTSHOW||showStatus==SHOWFAILURE) {
                if (attacher != null) {
                    attacher.cleanup();
                }
                attacher = new PhotoViewAttacher(this.photoView);
                ImageLoader.getInstance().displayImage(imageSrc, photoView, options, new SimpleImageLoadingListener() {

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        if(showStatus == NOTSHOW||showStatus == SHOWFAILURE){
                            progress.spin();
                            progress.setVisibility(View.VISIBLE);
                        }
                        showStatus = SHOWING;
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        progress.setVisibility(View.GONE);
                        attacher.update();
                        showStatus = SHOWSUCCESS;
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

    private void loadAndShowPos(int pos){
        if(pos>1){
            imageItems.get(pos-1).displayImage();
        }
        imageItems.get(pos).displayImage();
        if(pos<imageItems.size()-2){
            imageItems.get(pos+1).displayImage();
        }
        imagenum.setText(String.format(Locale.CHINA, imageNumFormate, pos + 1, imageSrcs.length));
    }
}
