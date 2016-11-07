package cc.flexbot.www.launch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;

import java.util.Locale;

import cc.flexbot.www.R;

/**
 * Created by Administrator on 2016/3/1.
 */
public class AboutViewActivity extends Activity {

    private ImageView Back_Btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.about_view);

        Back_Btn = (ImageView) findViewById(R.id.backBtn_about);
        WebView aboutWebView = (WebView) findViewById(R.id.aboutWebView1);

        String language = Locale.getDefault().getLanguage();
        if ("zh".equals(language)) {
            aboutWebView.loadUrl("file:///android_asset/About-zh.html");
        } else {
            aboutWebView.loadUrl("file:///android_asset/About.html");
        }

        Back_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        super.finish();
        Intent intent = new Intent(AboutViewActivity.this, LaunchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}
