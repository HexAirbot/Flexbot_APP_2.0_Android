package cc.flexbot.www;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Timer;

import cc.flexbot.www.launch.LaunchActivity;
import cc.flexbot.www.modal.Transmitter;

public class MainExActivity extends FragmentActivity     //FragmentActivity 实现滑动界面的显示
        implements SettingsDialogDelegate, OnTouchListener, HudViewControllerDelegate {

    private static final String TAG = "MainExActivity";
    public static final int REQUEST_ENABLE_BT = 1;

    private int screenWidth;
    private int screenHeight;
    private int lastX;
    private int lastY;
    private int recTime;
    private int second;
    private Timer showTimer;
    private TextView txt_Main_Recordtime;

    private ImageView photoView;
    private ImageView videoView;

    private SettingsDialog settingsDialog;
    private HudExViewController hudVC;

    private boolean isFirstIn;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        Log.d(TAG, "----onCreate");
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        this.setContentView(R.layout.hud_view_controller_framelayout);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        hudVC = new HudExViewController(this, this);
        hudVC.onCreate();
        hudVC.onResume();
        hudVC.checkConnectBle();
        Transmitter.sharedTransmitter().start();

        //新手引导界面
        //mainDialog();

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    public void mainDialog() {
        final Dialog dialog = new Dialog(this, R.style.Dialog_Fullscreen);
        dialog.setContentView(R.layout.guide_framelayout);

        final ImageView iv0 = (ImageView) dialog.findViewById(R.id.guide_0);
        final ImageView iv1 = (ImageView) dialog.findViewById(R.id.guide_1);
        final ImageView iv2 = (ImageView) dialog.findViewById(R.id.guide_2);
        final ImageView iv3 = (ImageView) dialog.findViewById(R.id.guide_3);
        final ImageView iv4 = (ImageView) dialog.findViewById(R.id.guide_4);
        final ImageView iv5 = (ImageView) dialog.findViewById(R.id.guide_5);
        final ImageView iv6 = (ImageView) dialog.findViewById(R.id.guide_6);


        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        isFirstIn = preferences.getBoolean("isFirstIn", true);
        if (isFirstIn) {
            iv0.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv0.setVisibility(View.GONE);
                    iv1.setVisibility(View.VISIBLE);
                }
            });
            iv1.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    iv1.setVisibility(View.GONE);
                    iv2.setVisibility(View.VISIBLE);
                }
            });
            iv2.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    iv2.setVisibility(View.GONE);
                    iv3.setVisibility(View.VISIBLE);
                }
            });
            iv3.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    iv3.setVisibility(View.GONE);
                    iv4.setVisibility(View.VISIBLE);
                }
            });
            iv4.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    iv4.setVisibility(View.GONE);
                    iv5.setVisibility(View.VISIBLE);
                }
            });
            iv5.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    iv5.setVisibility(View.GONE);
                    iv6.setVisibility(View.VISIBLE);
                }
            });
            iv6.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();

            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isFirstIn", false);
            editor.commit();
        } else {
            dialog.hide();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MainExActivity.this, LaunchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        MainExActivity.this.setResult(RESULT_OK, intent);
        MainExActivity.this.finish();
        super.onBackPressed();
        startActivity(intent);

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub
        int action = event.getAction();
        int left = 0, right = 0, top = 0, bottom = 0;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = (int) event.getRawX() - lastX;
                int dy = (int) event.getRawY() - lastY;

                left = v.getLeft() + dx;
                right = v.getRight() + dx;
                top = v.getTop() + dy;
                bottom = v.getBottom() + dy;

                if (left < 0) {
                    left = 0;
                    right = v.getWidth();
                }
                if (right > screenWidth) {
                    right = screenWidth;
                    left = right - v.getWidth();
                }
                if (top < 0) {
                    top = 0;
                    bottom = v.getHeight();
                }
                if (bottom > screenHeight) {
                    bottom = screenHeight;
                    top = bottom - v.getHeight();
                }
                v.layout(left, top, right, bottom);

                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                // break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    private void setTime() {
        Time t = new Time();
        t.setToNow();
        int s = t.second;
        if (second != s) {
            second = s;
            recTime++;
            int time_h = recTime / 3600;
            int time_m = (recTime % 3600) / 60;
            int time_s = recTime % 60;
            txt_Main_Recordtime.setText("REC:" + String.format("%02d", time_h) + ":" + String.format("%02d", time_m)
                    + ":" + String.format("%02d", time_s));
        }

    }

    private void initTime() {
        recTime = 0;
        Time time = new Time();
        time.setToNow();
        second = time.second;
    }

    private void clearTime() {
        second = 0;
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "MainEx Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://cc.flexbot.www/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
        Log.i("*******MainExActivity", " **********onstart");
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "MainEx Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://cc.flexbot.www/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
        super.finish();
        Log.i("*******MainExActivity", " **********onStop");

    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        Transmitter.sharedTransmitter().stop();
        hudVC.onDestroy();
        hudVC = null;

        Log.i("*******MainExActivity", " **********onDestroy");
    }

    @Override
    public void prepareDialog(SettingsDialog dialog) {

    }

    @Override
    public void onDismissed(SettingsDialog settingsDialog) {

        //hudVC.setSettingsButtonEnabled(true);
    }

    @Override
    public void settingsBtnDidClick(View settingsBtn) {
        // hudVC.setSettingsButtonEnabled(false);
        showSettingsDialog();
    }

    @Override
    public void backBtnLaunchActivity(View connectBtn) {
        onBackPressed();
    }


    public ViewController getViewController() {
        return hudVC;
    }

    protected void showSettingsDialog() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.addToBackStack(null);

        if (settingsDialog == null) {
            Log.d(TAG, "settingsDialog is null");
            settingsDialog = new SettingsDialog(this, this);
        }

        settingsDialog.show(ft, "settings");
    }

}