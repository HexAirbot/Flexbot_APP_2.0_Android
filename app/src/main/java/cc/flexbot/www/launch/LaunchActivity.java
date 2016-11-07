package cc.flexbot.www.launch;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import com.ant.liao.GifView;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import cc.flexbot.www.GalleryActivity;
import cc.flexbot.www.HexMiniApplication;
import cc.flexbot.www.MainExActivity;
import cc.flexbot.www.R;
import cc.flexbot.www.SettingsViewControllerDelegate;
import cc.flexbot.www.ble.BleConnectinManager;
import cc.flexbot.www.ble.BleConnectinManagerDelegate;
import cc.flexbot.www.modal.ApplicationSettings;
import cc.flexbot.www.modal.Transmitter;
import cc.flexbot.www.ui.mVideoView;


/**
 * Created by Administrator on 2016/2/29.
 */
public class LaunchActivity extends Activity implements BleConnectinManagerDelegate {

    private SlidingMenu mLeftMenu;
    private Button GalleryBtn;
    private Button PrintBtn;
    private Button BuyBtn;
    private Button HelpBtn;
    private Button FeedbackBtn;
    private Button AboutBtn;
    private TextView Username;
    private TextView Location;
    private TextView Introduction;
    private GifView mGifView;

    private LinearLayout linearLayout_login;
    private FrameLayout frameLayout_info;

    private ImageView Head_portrait;
    private ImageView GalleryView;
    private ImageView PrintView;
    private ImageView HelpView;
    private ImageView FlyView;
    private mVideoView videoView;

    private boolean bleAvailabed;
    private boolean isScansucced;

    private String murl;
    private String username;
    private String userid;
    private String leancloudid;
    private String UserID;
    private String UserIcon;
    private String UserSex;
    private String UserName;
    private String UserEmail;
    private String UserLocation;
    private String UserConstellation;
    private String UserIntroduction;

    private byte[] icondata;
    private long mExitTime = 0;

    private BluetoothAdapter mBluetoothAdapter;
    private SettingsViewControllerDelegate delegate;
    private android.content.Context context;

    public static final int RESOURCE_FROM_WEIBO = 1;
    public static final int RESOURCE_FROM_INFOROMATION = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("^^^^LaunchActivity", " ----onCreate");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setContentView(R.layout.launch_main);

        Transmitter.sharedTransmitter().setBleConnectionManager(new BleConnectinManager(this));
        Transmitter.sharedTransmitter().getBleConnectionManager().setDelegate(this);

        mLeftMenu = (SlidingMenu) findViewById(R.id.id_menu);

        GalleryBtn = (Button) findViewById(R.id.gallery_btn);
        GalleryBtn.setOnClickListener(listener);
        PrintBtn = (Button) findViewById(R.id.print_btn);
        PrintBtn.setOnClickListener(listener);
        BuyBtn = (Button) findViewById(R.id.buy_btn);
        BuyBtn.setOnClickListener(listener);
        HelpBtn = (Button) findViewById(R.id.help_btn);
        HelpBtn.setOnClickListener(listener);
        FeedbackBtn = (Button) findViewById(R.id.feedback_btn);
        FeedbackBtn.setOnClickListener(listener);
        AboutBtn = (Button) findViewById(R.id.about_btn);
        AboutBtn.setOnClickListener(listener);
        GalleryView = (ImageView) findViewById(R.id.galleryView);
        GalleryView.setOnClickListener(listener);
        PrintView = (ImageView) findViewById(R.id.printview);
        PrintView.setOnClickListener(listener);
        HelpView = (ImageView) findViewById(R.id.helpView);
        HelpView.setOnClickListener(listener);
        FlyView = (ImageView) findViewById(R.id.fly_image);
        FlyView.setOnClickListener(listener);
        FlyView.setEnabled(false);
        FlyView.setVisibility(View.INVISIBLE);
        Head_portrait = (ImageView) findViewById(R.id.image1);
        Username = (TextView) findViewById(R.id.username1);
        Location = (TextView) findViewById(R.id.UserLocation1);
        Introduction = (TextView) findViewById(R.id.UserIntroduction1);
        linearLayout_login = (LinearLayout) findViewById(R.id.linearLayout_login);
        frameLayout_info = (FrameLayout) findViewById(R.id.framelayout_info);
        frameLayout_info.setVisibility(View.INVISIBLE);
        videoView = (mVideoView) findViewById(R.id.videoView);
        videoView.getRootView();
        mGifView = (GifView) findViewById(R.id.myGif);
        mGifView.setShowDimension(GifRatioWidth(), GifRatioHeight());
        mGifView.setGifImage(R.drawable.noti_text);

        playVideoView();
        initlogin();

    }

    @Override
    protected void onResume() {
        super.onResume();
        scanBle();
    }

    private int GifRatioWidth() {
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int width = (int) ((((float) (screenWidth) / 1080) * 460));//根据1080*1920进行适配，460*180是gif的大小
        return width;
    }

    private int GifRatioHeight() {
        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        int height = (int) ((((float) (screenHeight) / 1920) * 180));
        return height;
    }

    private void initlogin() {
        frameLayout_info.setEnabled(false);
        Head_portrait.setImageResource(R.drawable.head_portrait);
        linearLayout_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LaunchActivity.this, SelectLogin.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(intent, 1);
            }
        });
    }

    private void playVideoView() {
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.home);
        MediaController mc = new MediaController(this);
        mc.setVisibility(View.INVISIBLE);
        videoView.setMediaController(mc);
        videoView.setVideoURI(uri);
        videoView.start();
        videoView.clearFocus();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                android.util.Log.i("test", "播放完毕");
                videoView.start();
            }
        });
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.print_btn:
                    Uri uri = Uri.parse("http://www.thingiverse.com/Flexbot/designs");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivityForResult(intent, 0);
                    break;
                case R.id.buy_btn:
                    Uri uri1 = Uri.parse("https://b.mashort.cn/h.Qprni?cv=AACrQjLZ&sm=c3513b");
                    Intent intent1 = new Intent(Intent.ACTION_VIEW, uri1);
                    startActivityForResult(intent1, 0);
                    break;
                case R.id.printview:
                    Uri uri2 = Uri.parse("http://www.thingiverse.com/Flexbot/designs");
                    Intent intent2 = new Intent(Intent.ACTION_VIEW, uri2);
                    startActivityForResult(intent2, 0);
                    break;
                case R.id.gallery_btn:
                    startIntent(LaunchActivity.this, GalleryActivity.class);
                    break;
                case R.id.help_btn:
                    startIntent(LaunchActivity.this, HelpViewActivity.class);
                    break;
                case R.id.feedback_btn:
                    if (username == null) {
                        startIntent(LaunchActivity.this, SelectLogin.class);
                    } else {
                        startIntent(LaunchActivity.this, FeedBackActinvity.class);
                    }
                    break;
                case R.id.about_btn:
                    startIntent(LaunchActivity.this, AboutViewActivity.class);
                    break;
                case R.id.galleryView:
                    startIntent(LaunchActivity.this, GalleryActivity.class);
                    break;
                case R.id.helpView:
                    startIntent(LaunchActivity.this, HelpViewActivity.class);
                case R.id.fly_image:
                    if (FlyView.isEnabled()) {
                        Intent intent3 = new Intent(LaunchActivity.this, MainExActivity.class);
                        intent3.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivityForResult(intent3, 1);
                    } else {
                        FlyView.setEnabled(false);
                    }
                    break;
            }
        }
    };

    private void startIntent(Context context, Class<?> cls) {
        Intent intent = new Intent(LaunchActivity.this, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(intent, 0);
    }

    public void toggleMenu(View view) {
        mLeftMenu.toggle();
    }

    private void sendBleEnableRequest() {
        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    (this).startActivityForResult(enableBtIntent, 1);
                }
            }
        }
    }

    private boolean initBle() {
        if (mBluetoothAdapter == null) {
            if (!this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
                return false;
            }
            final BluetoothManager bluetoothManager = (BluetoothManager) this
                    .getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
            // Checks if Bluetooth is supported on the device.
            if (mBluetoothAdapter == null) {
                Toast.makeText(this, R.string.bluetooth_not_supported, Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        Log.i("initble********", "" + mBluetoothAdapter);
        sendBleEnableRequest();
        return true;
    }

    private void scanBle() {
        bleAvailabed = initBle();
        Log.i("scanBle", " " + bleAvailabed);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (bleAvailabed) {
                    BluetoothDevice currentDevice = Transmitter.sharedTransmitter().getBleConnectionManager()
                            .getCurrentDevice();
                    Log.i("*****currentDevice", " " + currentDevice);
                    if (currentDevice == null) {
                        isScansucced = mBluetoothAdapter.startLeScan(mLeScanCallback);
                    } else {
                        Transmitter.sharedTransmitter().stop();
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                        videoView.setVisibility(View.GONE);
                        mGifView.setVisibility(View.GONE);
                        HelpView.setEnabled(false);
                        FlyView.setVisibility(View.VISIBLE);
                        FlyView.setEnabled(true);
                    }
                }
            }
        }).start();
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            LaunchActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (device.getName() != null && device.getName().equals("FlexBLE")) {
                        tryingToConnect(device);
                        Transmitter.sharedTransmitter().getBleConnectionManager().connect(device);
                        BluetoothDevice currentDevice = Transmitter.sharedTransmitter().getBleConnectionManager()
                                .getCurrentDevice();
                        Log.i("currentDevice:", " " + currentDevice);
                        Log.i("device:", " " + device);
                        Log.i("blue", "连接成功");
                    }
                }
            });
        }

    };


    public void tryingToConnect(BluetoothDevice target) {
        ApplicationSettings settings = HexMiniApplication.sharedApplicaion().getAppSettings();
        if (target.getName().equals("FlexBLE")) {
            if (settings.getFlexbotVersion().equals("1.5.0") == false) {
                settings.setFlexbotVersion("1.5.0");
                settings.save();
            }
            HexMiniApplication.sharedApplicaion().setFullDuplex(true);
        } else {
            settings.getFlexbotVersion().equals("1.0.0");
            settings.save();
            HexMiniApplication.sharedApplicaion().setFullDuplex(false);

        }
    }

    @Override
    public void didConnect(BleConnectinManager manager) {
        Toast.makeText(getApplicationContext(), R.string.connection_successful, Toast.LENGTH_SHORT).show();
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        mGifView.setVisibility(View.GONE);
        videoView.setVisibility(View.GONE);
        HelpView.setEnabled(false);
        FlyView.setVisibility(View.VISIBLE);
        FlyView.setEnabled(true);
        if (delegate != null) {
            delegate.didConnect();
        }
    }

    @Override
    public void didDisconnect(BleConnectinManager manager) {
        Transmitter.sharedTransmitter().stop();
        Toast.makeText(getApplicationContext(), R.string.connection_lost, Toast.LENGTH_SHORT).show();
        if (delegate != null) {
            delegate.didDisconnect();
        }
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_title_info)
                .setMessage(R.string.disconnect_ble_info)
                .setPositiveButton(R.string.dialog_btn_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        videoView.setVisibility(View.VISIBLE);
                        mGifView.setVisibility(View.VISIBLE);
                        playVideoView();
                        HelpView.setEnabled(true);
                        FlyView.setVisibility(View.GONE);
                        FlyView.setEnabled(false);
                    }
                }).show();

    }

    @Override
    public void didFailToConnect(BleConnectinManager manager) {
        Toast.makeText(getApplicationContext(), R.string.connection_failed, Toast.LENGTH_SHORT).show();
        if (delegate != null) {
            delegate.didFailToConnect();
        }
    }

    @Override
    public void didReceiveData(BleConnectinManager manager, byte[] data) {
        Transmitter.sharedTransmitter().getOsdData().parseRawData(data);
    }

    private void getLendCloudData() {
        final AVQuery query = new AVQuery("UserMess");
        query.whereMatches("UserID", userid);
        query.getFirstInBackground(new GetCallback<AVObject>() {
                                       @Override
                                       public void done(AVObject avObject, AVException e) {
                                           if (avObject != null) {
                                               leancloudid = avObject.getObjectId(); //获取LendCloud的id
                                               Log.i("LaunchActivity", " " + avObject.getObjectId());

                                               //通过id获取到列表中的信息
                                               AVQuery avQuery = new AVQuery("UserMess");
                                               avQuery.getInBackground(leancloudid, new GetCallback<AVObject>() {
                                                   @Override
                                                   public void done(AVObject avObject, AVException e) {
                                                       UserID = avObject.getString("UserID");
                                                       UserIcon = avObject.getString("UserIcon");
                                                       UserSex = avObject.getString("UserSex");
                                                       UserName = avObject.getString("UserName");
                                                       UserEmail = avObject.getString("UserEmail");
                                                       UserLocation = avObject.getString("UserLocation");
                                                       UserConstellation = avObject.getString("UserConstellation");
                                                       UserIntroduction = avObject.getString("UserIntroduction");
                                                       icondata = avObject.getBytes("IconData");
                                                       Log.i("LaunchActivity", " " + UserLocation + " " + UserName + " " + UserIntroduction);

                                                       if (icondata == null) {
                                                           ImageSize mImageSize = new ImageSize(100, 100);//指定图片大小
                                                           ImageLoader.getInstance().loadImage(UserIcon, mImageSize, new SimpleImageLoadingListener() {
                                                               @Override
                                                               public void onLoadingComplete(String imageUri, View view,
                                                                                             Bitmap loadedImage) {
                                                                   super.onLoadingComplete(imageUri, view, loadedImage);
                                                                   Head_portrait.setImageBitmap(loadedImage);
                                                               }
                                                           });
                                                       } else {
                                                           Bitmap Icondata = BitmapFactory.decodeByteArray(icondata, 0, icondata.length);
                                                           Log.i("LaunchActivity", "done: " + Icondata);
                                                           Head_portrait.setImageBitmap(Icondata);
                                                       }
                                                       Username.setText(UserName);
                                                       Location.setText(UserLocation);
                                                       Introduction.setText(UserIntroduction);
                                                   }
                                               });
                                           }

                                           if (avObject == null) {
                                               Username.setText(username);
                                               ImageSize mImageSize = new ImageSize(100, 100);//指定图片大小
                                               ImageLoader.getInstance().loadImage(murl, mImageSize, new SimpleImageLoadingListener() {
                                                   @Override
                                                   public void onLoadingComplete(String imageUri, View view,
                                                                                 Bitmap loadedImage) {
                                                       super.onLoadingComplete(imageUri, view, loadedImage);
                                                       Head_portrait.setImageBitmap(loadedImage);
                                                   }
                                               });
                                               linearLayout_login.setVisibility(View.INVISIBLE);
                                               linearLayout_login.setEnabled(false);
                                               frameLayout_info.setVisibility(View.VISIBLE);
                                               frameLayout_info.setEnabled(true);
                                               frameLayout_info.setOnClickListener(new View.OnClickListener() {
                                                   @Override
                                                   public void onClick(View v) {
                                                       Intent intent = new Intent(LaunchActivity.this, PersonalInformation.class);
                                                       intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                                                       //将头像和用户名传递给PersonalInformation
                                                       Bundle extras = new Bundle();
                                                       extras.putString("HeadPortrait", murl);
                                                       extras.putString("UserName", username);
                                                       extras.putString("UserID", userid);
                                                       intent.putExtras(extras);
                                                       startActivityForResult(intent, 2);
                                                   }
                                               });
                                           }
                                       }
                                   }
        );
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        playVideoView();
        if (Transmitter.sharedTransmitter().getBleConnectionManager().isConnected()) {
            return;
        } else {
            scanBle();
        }
        if (username == null || HexMiniApplication.sharedApplicaion().isLogout() == true) {
            frameLayout_info.setEnabled(false);
            frameLayout_info.setVisibility(View.INVISIBLE);
            Head_portrait.setImageResource(R.drawable.head_portrait);
            linearLayout_login.setEnabled(true);
            linearLayout_login.setVisibility(View.VISIBLE);
            linearLayout_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LaunchActivity.this, SelectLogin.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivityForResult(intent, 1);
                }
            });
            HexMiniApplication.sharedApplicaion().setLogout(false);
        } else {
            getLendCloudData();
            frameLayout_info.setVisibility(View.VISIBLE);
            frameLayout_info.setEnabled(true);
            linearLayout_login.setVisibility(View.INVISIBLE);
            linearLayout_login.setEnabled(false);
            frameLayout_info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LaunchActivity.this, PersonalInformation.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    //将头像和用户名传递给PersonalInformation
                    Bundle extras = new Bundle();
                    if (murl != null) {
                        extras.putString("HeadPortrait", UserIcon);
                    }
                    if (Username == null) {
                        extras.putString("UserName", username);
                    } else {
                        extras.putString("UserName", UserName);
                    }
                    if (UserID == null) {
                        extras.putString("UserID", userid);
                    } else {
                        extras.putString("UserID", UserID);
                    }
                    if (UserEmail != null) {
                        extras.putString("UserEmail", UserEmail);
                    }
                    if (UserLocation != null) {
                        extras.putString("UserLocation", UserLocation);
                    }
                    if (UserIntroduction != null) {
                        extras.putString("UserIntroduction", UserIntroduction);
                    }
                    if (UserSex != null) {
                        extras.putString("UserSex", UserSex);
                    }
                    if (UserConstellation != null) {
                        extras.putString("UserConstellation", UserConstellation);
                    }
                    if (icondata != null) {
                        extras.putByteArray("IconData", icondata);
                    }
                    intent.putExtras(extras);
                    startActivityForResult(intent, 2);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESOURCE_FROM_WEIBO) {
            try {
                //接收从 Weibo 传来的头像和用户名
                murl = data.getStringExtra("HeadPortrait");
                username = data.getStringExtra("UserName");
                userid = data.getStringExtra("UserID");
            } catch (Exception e) {
            }
        }
        if (requestCode == RESOURCE_FROM_INFOROMATION) {

        }
        //退出控制界面后
        if (Transmitter.sharedTransmitter().getBleConnectionManager().isConnected()) {
            Transmitter.sharedTransmitter().start();
            return;
        } else {
            scanBle();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 如果两次按键时间间隔大于2000毫秒，则不退出
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();// 更新mExitTime
            } else {
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);// 否则退出程序
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}