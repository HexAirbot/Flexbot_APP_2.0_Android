package cc.flexbot.www.launch;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;

import java.util.Locale;

import cc.flexbot.www.R;

/**
 * Created by Administrator on 2016/4/20.
 */
public class InformationEditor extends Activity {

    private ImageView backBtn;
    private Button SaveButton;
    private EditText editText;
    private TextView title;
    private String[] titles;
    private String userid;
    private int num0;
    private int num1;
    private int num2;
    private int num3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.personal_editor);


        title = (TextView) findViewById(R.id.title1);
        SaveButton = (Button) findViewById(R.id.Button_save);
        editText = (EditText) findViewById(R.id.editText);
        backBtn = (ImageView) findViewById(R.id.backBtn_editor);

        Intent intent = getIntent();
        num3 = intent.getIntExtra("number", 3);
        num2 = intent.getIntExtra("number", 2);
        num1 = intent.getIntExtra("number", 1);
        num0 = intent.getIntExtra("number", 0);
        userid = intent.getStringExtra("userid");

        String language = Locale.getDefault().getLanguage();
        if ("zh".equals(language)) {
            titles = new String[]{"昵称", "邮箱", "地区", "个性签名"};
            if(num3 == 0){
                title.setText(titles[0]);
            }if(num3 == 1){
                title.setText(titles[1]);
            }if(num3 == 2){
                title.setText(titles[2]);
            }if(num3 == 3){
                title.setText(titles[3]);
            }
        } else {
            titles = new String[]{"Name", "Email", "Area", "Signature"};
            if(num3 == 0){
                title.setText(titles[0]);
            }if(num3 == 1){
                title.setText(titles[1]);
            }if(num3 == 2){
                title.setText(titles[2]);
            }if(num3 == 3){
                title.setText(titles[3]);
            }
        }
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent();
                mIntent.putExtra("information", editText.getText().toString().trim());
                setResult(1, mIntent);
                finish();
            }
        });
        SaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AVQuery query1 = new AVQuery("UserMess");
                query1.whereMatches("UserID", userid);
                Log.i("PersonalInformation", "userid: " + userid);
                query1.getFirstInBackground(new GetCallback<AVObject>() {
                    @Override
                    public void done(final AVObject avObject, AVException e) {
                        if (avObject != null) {
                            //更新数据
                            avObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {
                                    if (num0 == 0) {
                                        avObject.put("UserName", editText.getText().toString().trim());
                                    } else if (num1 == 1) {
                                        avObject.put("UserEmail", editText.getText().toString().trim());
                                    } else if (num2 == 2) {
                                        avObject.put("UserLocation", editText.getText().toString().trim());
                                    } else if (num3 == 3) {
                                        avObject.put("UserIntroduction", editText.getText().toString().trim());
                                    }
                                    avObject.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(AVException e) {
                                            Toast.makeText(InformationEditor.this, R.string.lunch_information_success, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        } else {
                            final AVObject userMess = new AVObject("UserMess");
                            userMess.put("UserID", userid);
                            if (num0 == 0) {
                                userMess.put("UserName", editText.getText().toString().trim());
                            } else if (num1 == 1) {
                                userMess.put("UserEmail", editText.getText().toString().trim());
                            } else if (num2 == 2) {
                                userMess.put("UserLocation", editText.getText().toString().trim());
                            } else if (num3 == 3) {
                                userMess.put("UserIntroduction", editText.getText().toString().trim());
                            }
                            userMess.saveInBackground();
                        }
                    }
                });
                Intent mIntent = new Intent();
                mIntent.putExtra("information", editText.getText().toString().trim());
                setResult(1, mIntent);
                finish();
            }

        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences sharedPreferences = getSharedPreferences("InformationEditor", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("information", editText.getText().toString().trim());
        Log.e("InformationEditor", " " + editText.getText().toString().trim());
        editor.commit();
    }

}
