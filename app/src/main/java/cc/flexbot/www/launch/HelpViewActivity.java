package cc.flexbot.www.launch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import cc.flexbot.www.R;

/**
 * Created by Administrator on 2016/3/8.
 */
public class HelpViewActivity extends Activity {

    private ImageView helpBtn_01;
    private ImageView helpBtn_02;
    private ImageView helpBtn_03;
    private ImageView helpBtn_04;
    private ImageView helpBtn_05;
    private ImageView Back_Btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.help_view_menue);

        helpBtn_01 = (ImageView) findViewById(R.id.help_01);
        helpBtn_01.setOnClickListener(listener);
        helpBtn_02 = (ImageView) findViewById(R.id.help_02);
        helpBtn_02.setOnClickListener(listener);
        helpBtn_03 = (ImageView) findViewById(R.id.help_03);
        helpBtn_03.setOnClickListener(listener);
        helpBtn_04 = (ImageView) findViewById(R.id.help_04);
        helpBtn_04.setOnClickListener(listener);
        helpBtn_05 = (ImageView) findViewById(R.id.help_05);
        helpBtn_05.setOnClickListener(listener);

        Back_Btn = (ImageView) findViewById(R.id.backBtn_help);
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
        Intent intent = new Intent(HelpViewActivity.this, LaunchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);

    }

    private View.OnClickListener listener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.help_01:
                    Intent intent1 = new Intent(HelpViewActivity.this, HelpviewDetail.class);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent1.putExtra("helpView_detail", 1);
                    startActivity(intent1);
                    break;
                case R.id.help_02:
                    Intent intent2 = new Intent(HelpViewActivity.this, HelpviewDetail.class);
                    intent2.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent2.putExtra("helpView_detail", 2);
                    startActivity(intent2);
                    break;
                case R.id.help_03:
                    Intent intent3 = new Intent(HelpViewActivity.this, HelpviewDetail.class);
                    intent3.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent3.putExtra("helpView_detail", 3);
                    startActivity(intent3);
                    break;
                case R.id.help_04:
                    Intent intent4 = new Intent(HelpViewActivity.this, HelpviewDetail.class);
                    intent4.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent4.putExtra("helpView_detail", 4);
                    startActivity(intent4);
                    break;
                case R.id.help_05:
                    Intent intent5 = new Intent(HelpViewActivity.this, HelpviewDetail.class);
                    intent5.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent5.putExtra("helpView_detail", 5);
                    startActivity(intent5);
                    break;
            }
        }
    };
}
