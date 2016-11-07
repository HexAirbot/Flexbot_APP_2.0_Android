package cc.flexbot.www.launch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import cc.flexbot.www.MainExActivity;
import cc.flexbot.www.R;

/**
 * Created by Administrator on 2016/2/19.
 */
public class WelcomeActivity extends Activity {
    private ImageView welcomeImage;
    private static final int TIME = 2000;
    private static final int GO_LAUNCH = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.welcome);

        mHandler.sendEmptyMessageDelayed(GO_LAUNCH, TIME);
        welcomeImage = (ImageView) findViewById(R.id.welecome);
        welcomeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, MainExActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case GO_LAUNCH:
                    goLaunch();
                    break;

            }
        }

        ;
    };

    private void goLaunch() {
        Intent intent = new Intent(WelcomeActivity.this, LaunchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }


}

