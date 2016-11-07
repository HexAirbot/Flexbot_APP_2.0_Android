package cc.flexbot.www.launch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVObject;

import cc.flexbot.www.R;

/**
 * Created by Administrator on 2016/3/4.
 */
public class FeedBackActinvity extends Activity {

    private EditText FeedEditText;
    private Button SubmintBtn;
    private ImageView BackBtn;
    private String mString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.feedback);

        AVAnalytics.trackAppOpened(getIntent());

        FeedEditText = (EditText) findViewById(R.id.feedbacktext);
        SubmintBtn = (Button) findViewById(R.id.feedbackbtn);
        BackBtn = (ImageView) findViewById(R.id.backBtn_feedback);
        BackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        SubmintBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mString = FeedEditText.getText().toString().trim();
                Log.i("FeedBackActivity", " " + mString);
                if (mString == null || mString.trim().equals("")) {
                    Toast.makeText(FeedBackActinvity.this, R.string.lunch_information_feedback, Toast.LENGTH_SHORT).show();
                } else {
                    //保存到后台LeanCloud的Feedback文件中
                    AVObject feedback = new AVObject("Feedback");
                    feedback.put("Content", mString);
                    feedback.saveInBackground();
                    Toast.makeText(FeedBackActinvity.this, R.string.lunch_information_feedback_succeed, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        super.finish();
        Intent intent = new Intent(FeedBackActinvity.this, LaunchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        FeedEditText.clearFocus();
    }

}
