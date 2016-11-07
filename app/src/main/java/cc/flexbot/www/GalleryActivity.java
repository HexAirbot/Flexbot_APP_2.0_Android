package cc.flexbot.www;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.lewei.config.PathConfig;
import com.lewei.config.SwitchConfig;
import com.mjpegdemo.images.PhotosAdapter;
import com.mjpegdemo.images.VideosAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.flexbot.www.launch.LaunchActivity;


/**
 * ViewPager分别用来放置Photos和videos，可以滑动切换； 容器中各放置一个gridview，用来放photos和videos的缩略图
 *
 * @author Tony
 */
public class GalleryActivity extends Activity {
    private static final String TAG = "GalleryActivity";
    private static final int SCAN_OK = 0;
    private int screenWidth;

    public static enum TYPE {
        PHOTO, VIDEO
    }

    private ViewPager mPager;
    private List<View> listViews;
    private ImageView cursor;
    private TextView t1, t2;
    private int offset = 0;
    private int currIndex = 0;
    private ProgressDialog mProgressDialog;

    private ImageView iv_Actionbar_Back;
    private ImageView iv_Actionbar_More;
    private TextView txt_Playback_Actionbar;
    private PopupWindow pWindowMenu;
    private SimpleAdapter mAdapter;
    private RelativeLayout layout_Actionbar;
    private int layout_Actionbar_Height;

    //private SdcardVideosAdapter mSdAdapter;
    //private GridView mGridView;

    private GridView gd_Photos;
    private PhotosAdapter mPhotosAdapter;

    private GridView gd_Videos;
    private VideosAdapter mVideosAdapter;

    private PathConfig mPathConfig;
    private SwitchConfig mSwitchConfig;
    private int currSdcardItem = 0;
    private String a;
    public static List<String> photoList = new ArrayList<String>();
    public static final String ACTION_DELETE_PHOTO = "com.action.send.ACTION_DELETE_PHOTO";
    private List<String> videoList = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.hud_view_gallery);

        DisplayMetrics dm = this.getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        Log.i(TAG, "*****:" + screenWidth);
        mPathConfig = new PathConfig(GalleryActivity.this);
        mSwitchConfig = new SwitchConfig(GalleryActivity.this);

        initImageView();
        initTextView();
        InitViewPager();

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        register();
    }

    private void initTextView() {
        t1 = (TextView) findViewById(R.id.text1);
        t2 = (TextView) findViewById(R.id.text2);

        t1.setOnClickListener(new MyOnClickListener(0));
        t2.setOnClickListener(new MyOnClickListener(1));
    }


    private void InitViewPager() {
        mPager = (ViewPager) findViewById(R.id.vPager);
        listViews = new ArrayList<View>();
        LayoutInflater mInflater = getLayoutInflater();
        listViews.add(mInflater.inflate(R.layout.layout_photos, null));
        listViews.add(mInflater.inflate(R.layout.layout_videos, null));
        mPager.setAdapter(new MyPagerAdapter(listViews));
        mPager.setCurrentItem(0);
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());

        gd_Photos = (GridView) listViews.get(0).findViewById(R.id.gd_Photos);
        gd_Photos.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(GalleryActivity.this, ImagePagerActivity.class);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
        gd_Photos.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                // TODO Auto-generated method stub
                dialogDelete(TYPE.PHOTO, position);
                return true;
            }
        });
        gd_Videos = (GridView) listViews.get(1).findViewById(R.id.gd_Videos);
        gd_Videos.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                // TODO Auto-generated method stub
                Uri uri = Uri.fromFile(new File(videoList.get(position)));
                // ����ϵͳ�Դ��Ĳ�����
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Log.v("URI:::::::::", uri.toString());
                intent.setDataAndType(uri, "video/*");
                startActivity(intent);
            }
        });

        gd_Videos.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                // TODO Auto-generated method stub
                dialogDelete(TYPE.VIDEO, position);
                /**
                 * return true: only run onItemLongClick return false: run
                 * onItemLongClick and onItemClick
                 */
                return true;
            }
        });
    }

    private void initImageView() {
        cursor = (ImageView) findViewById(R.id.cursor);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;// // 获取分辨率宽度

        offset = 0;
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.qrcode_scan_line);
        Bitmap newBmp = zoomImg(bmp, screenW / 2, bmp.getHeight() / 2);
        cursor.setImageBitmap(newBmp);

        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        cursor.setImageMatrix(matrix);// // 设置动画初始位置

        txt_Playback_Actionbar = (TextView) findViewById(R.id.txt_Playback_Actionbar);
        txt_Playback_Actionbar.post(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                // 在加载完这个控件之后才开始下载图片
                loadImage();

            }
        });
        layout_Actionbar = (RelativeLayout) findViewById(R.id.layout_actionbar);
        iv_Actionbar_Back = (ImageView) findViewById(R.id.iv_playback_actionbar_back);
        iv_Actionbar_Back.setOnClickListener(clickListener);
        iv_Actionbar_More = (ImageView) findViewById(R.id.iv_playback_actionbar_more);
        iv_Actionbar_More.setOnClickListener(clickListener);
        layout_Actionbar.post(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                layout_Actionbar_Height = layout_Actionbar.getHeight();
                // Log.e(TAG, "height " + layout_Actionbar_Height);
            }
        });


    }

    private void initPopupWndMenu() {
        LayoutInflater mInflater = LayoutInflater.from(this.getApplicationContext());
        View view = mInflater.inflate(R.layout.popup_playback_actionbar_more, null);
        pWindowMenu = new PopupWindow(view, screenWidth / 3, LayoutParams.WRAP_CONTENT);
        ListView mListView = (ListView) view.findViewById(R.id.listview_Popup_actionbar_more);
        mAdapter = new SimpleAdapter(this, getData(), R.layout.list_item_playback_menu, new String[]{"icon", "info"}, new int[]{R.id.icon, R.id.info});
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int item, long arg3) {
                // TODO Auto-generated method stub
                switch (item) {
                    case 0:
                        // Log.e(TAG, "1 item:" + currSdcardItem);
                        if (currSdcardItem != 0) {
                            PathConfig.sdcardItem = PathConfig.SdcardSelector.BUILT_IN;
                            loadImage();
                        }
                        pWindowMenu.dismiss();
                        break;
                    case 1:
                        // Log.e(TAG, "2 item:" + currSdcardItem);
                        if (currSdcardItem != 1) {
                            PathConfig.sdcardItem = PathConfig.SdcardSelector.EXTERNAL;

                            loadImage();
                        }
                        pWindowMenu.dismiss();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void showPopupWndMenu() {
        pWindowMenu.setFocusable(true);  // 设置PopupWindow可获得焦点
        pWindowMenu.setBackgroundDrawable(new BitmapDrawable(this.getResources(), BitmapFactory.decodeResource(this.getResources(), R.color.transparent)));
        pWindowMenu.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸
        pWindowMenu.showAtLocation(layout_Actionbar, Gravity.TOP | Gravity.RIGHT, 0, layout_Actionbar_Height);
    }

    private OnClickListener clickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.iv_playback_actionbar_back:
                    onBackPressed();
                    break;
                case R.id.iv_playback_actionbar_more:
                    initPopupWndMenu();
                    showPopupWndMenu();
                    break;
                default:
                    break;
            }
        }
    };

    private void loadImage() {
        mProgressDialog = ProgressDialog.show(this, null, "Loading...");
        if (PathConfig.sdcardItem == PathConfig.SdcardSelector.EXTERNAL) {
            if (mPathConfig.getRootPath() == null) {
                Toast.makeText(GalleryActivity.this, R.string.str_playback_notfindexcard, Toast.LENGTH_LONG).show();
                mSwitchConfig.writeSdcardChoose(false);
                PathConfig.sdcardItem = PathConfig.SdcardSelector.BUILT_IN;
                currSdcardItem = 0;
                txt_Playback_Actionbar.setText(R.string.tv_Playback_BuiltinSdcard);
            } else {
                PathConfig.sdcardItem = PathConfig.SdcardSelector.EXTERNAL;
                currSdcardItem = 1;
                txt_Playback_Actionbar.setText(R.string.tv_Playback_ExternalSdcard);
            }
        } else {
            currSdcardItem = 0;
            txt_Playback_Actionbar.setText(R.string.tv_Playback_BuiltinSdcard);
        }
        getPhotoVideoList(new File(mPathConfig.getRootPath() + PathConfig.PHOTOS_PATH), new File(mPathConfig.getRootPath() + PathConfig.VIDEOS_PATH));
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        Map<String, Object> photo = new HashMap<String, Object>();
        photo.put("icon", R.drawable.playback_menu_phone);
        photo.put("info", this.getString(R.string.tv_Playback_BuiltinSdcard));
        Log.i(TAG, "############");
        list.add(photo);

        photo = new HashMap<String, Object>();
        photo.put("icon", R.drawable.playback_menu_sdcard);
        photo.put("info", this.getString(R.string.tv_Playback_ExternalSdcard));
        list.add(photo);

        return list;
    }

    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case SCAN_OK:
                    if (mProgressDialog != null && mProgressDialog.isShowing())
                        mProgressDialog.dismiss();

                    mPhotosAdapter = new PhotosAdapter(GalleryActivity.this, photoList, gd_Photos);
                    gd_Photos.setAdapter(mPhotosAdapter);

                    mVideosAdapter = new VideosAdapter(GalleryActivity.this, videoList, gd_Videos);
                    gd_Videos.setAdapter(mVideosAdapter);

                    break;

                default:
                    break;
            }

            return true;
        }
    });

    private void getPhotoVideoList(final File photoPath, final File videoPath) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                // photo list
                photoList.clear();
                photoList = mPathConfig.getImagesList(photoPath);

                // video list
                videoList.clear();
                videoList = mPathConfig.getVideosList(videoPath);
                Log.e(TAG, "images size:" + photoList.size() + " videos size:" + videoList.size());
                handler.sendEmptyMessage(SCAN_OK);
            }
        }).start();
    }

    private Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    /**
     * ViewPager适配器
     */
    public class MyPagerAdapter extends PagerAdapter {
        public List<View> mListViews;

        public MyPagerAdapter(List<View> mListViews) {
            this.mListViews = mListViews;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(mListViews.get(arg1));
        }

        @Override
        public void finishUpdate(View arg0) {
        }

        @Override
        public int getCount() {
            return mListViews.size();
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(mListViews.get(arg1), 0);
            return mListViews.get(arg1);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == (arg1);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
        }
    }

    /**
     * 头标点击监听
     */
    public class MyOnClickListener implements View.OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            mPager.setCurrentItem(index);
        }
    }

    ;

    /**
     * 页卡切换监听
     */
    public class MyOnPageChangeListener implements OnPageChangeListener {

        int one = offset * 2 + screenWidth / 2;// ҳ��1 -> ҳ��2 ƫ����

        @Override
        public void onPageSelected(int arg0) {
            Animation animation = null;
            switch (arg0) {
                case 0:
                    if (currIndex == 1) {
                        animation = new TranslateAnimation(one, 0, 0, 0);
                    }
                    break;
                case 1:
                    if (currIndex == 0) {
                        animation = new TranslateAnimation(offset, one, 0, 0);
                        Intent intent = new Intent(GalleryActivity.this, SdcardVideoActivity.class);
                        break;
                    }
                    break;
            }
            currIndex = arg0;
            animation.setFillAfter(true);// True:图片停在动画结束位置
            animation.setDuration(300);
            cursor.startAnimation(animation);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

    private void register() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_DELETE_PHOTO);
        registerReceiver(broadcastReceivers, intentFilter);
    }

    private void unregister() {
        unregisterReceiver(broadcastReceivers);
    }

    private BroadcastReceiver broadcastReceivers = new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub
            String action = arg1.getAction();
            if (action.equals(ACTION_DELETE_PHOTO)) {
                Log.e(TAG, "get receiver");
                mPhotosAdapter.notifyDataSetChanged();
            }
        }

    };

    /**
     * 退出对话框
     */
    private void dialogDelete(final TYPE mType, final int position) {
        String msg = "";
        String title = this.getString(R.string.str_playback_warning);
        if (mType == TYPE.PHOTO) {
            msg = this.getString(R.string.str_playback_deletephoto);
        } else if (mType == TYPE.VIDEO) {
            msg = this.getString(R.string.str_playback_deletevideo);
        }
        AlertDialog.Builder builder = new Builder(GalleryActivity.this);
        builder.setMessage(msg);
        builder.setTitle(title);
        builder.setPositiveButton(R.string.str_playback_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                if (mType == TYPE.PHOTO) {
                    File file = new File(photoList.get(position));
                    file.delete();
                    photoList.remove(position);
                    mPhotosAdapter.notifyDataSetChanged();
                } else if (mType == TYPE.VIDEO) {
                    File file = new File(videoList.get(position));
                    File parentFile = file.getParentFile();
                    mPathConfig.deleteFiles(parentFile);
                    videoList.remove(position);
                    mVideosAdapter.notifyDataSetChanged();
                }
            }
        });
        builder.setNegativeButton(R.string.str_playback_cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.gridview_menu_device:
                break;
            case R.id.gridview_menu_local:
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        unregister();

        Log.d(TAG, "on stop.. unregister");
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        super.finish();
        Intent intent = new Intent(GalleryActivity.this, LaunchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            initPopupWndMenu();
            showPopupWndMenu();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 以下3个方法适用于android4.0以上版本的actionbar比较合适 为兼容android2.3.6，不使用以下方法
     */
	/*
	 * @Override public boolean onCreateOptionsMenu(android.view.Menu menu) { //
	 * android 4.0 以上必须调用以下方法才能正常显示Icon setIconEnable(menu, true);
	 * getMenuInflater().inflate(R.menu.gridview_menu, menu); return
	 * super.onCreateOptionsMenu(menu); };
	 *
	 * // enable为true时，菜单添加图标有效，enable为false时无效。4.0系统默认无效 private void
	 * setIconEnable(Menu menu, boolean enable) { try { Class<?> clazz =
	 * Class.forName("com.android.internal.view.menu.MenuBuilder"); Method m =
	 * clazz.getDeclaredMethod("setOptionalIconsVisible", boolean.class);
	 * m.setAccessible(true);
	 *
	 * // MenuBuilder实现Menu接口，创建菜单时，传进来的menu其实就是MenuBuilder对象(java的多态特征)
	 * m.invoke(menu, enable);
	 *
	 * } catch (Exception e) { e.printStackTrace(); } }
	 *
	 * // 添加此段代码后即使手机有Menu键也会使app默认为无menu键 private void
	 * setOverflowShowingAlways() { try { ViewConfiguration config =
	 * ViewConfiguration.get(this); Field menuKeyField =
	 * ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
	 * menuKeyField.setAccessible(true); menuKeyField.setBoolean(config, false);
	 * } catch (Exception e) { e.printStackTrace(); } }
	 */
}
