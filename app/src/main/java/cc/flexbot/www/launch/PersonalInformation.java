package cc.flexbot.www.launch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.LogoutAPI;
import com.sina.weibo.sdk.widget.LoginoutButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

import cc.flexbot.www.HexMiniApplication;
import cc.flexbot.www.R;
import cc.flexbot.www.util.change_photo.UriUtils;
import cc.flexbot.www.util.change_photo.mImageUtils;
import cc.flexbot.www.weibo.AccessTokenKeeper;
import cc.flexbot.www.weibo.Constants;

/**
 * Created by Administrator on 2016/4/7.
 */
public class PersonalInformation extends Activity {
    private Oauth2AccessToken mAccessToken;
    private ImageView UserIcon;
    private TextView UserSex;
    private TextView UserName;
    private TextView UserEmail;
    private TextView UserLocation;
    private TextView UserConstellation;//星座
    private TextView UserIntroduction;//签名
    private Button LogOut;
    private ImageView BackBtn;
    private String murl;
    private String username;
    private String usersex;
    private String userid;
    private String userconstellation;
    private String userIntroduction;
    private String userEmail;
    private String userLcoation;
    private String information;
    private String leancloudid;
    private String myFilePath;
    private String language;
    private byte[] icondata;
    private LogOutRequestListener mLogoutListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.personal_information);
        AVAnalytics.trackAppOpened(getIntent());

        mLogoutListener = new LogOutRequestListener();
        language = Locale.getDefault().getLanguage();

        //从LaunchActivity 得到数据
        Intent intent = getIntent();
        murl = intent.getStringExtra("HeadPortrait");
        username = intent.getStringExtra("UserName");
        userid = intent.getStringExtra("UserID");
        usersex = intent.getStringExtra("UserSex");
        userEmail = intent.getStringExtra("UserEmail");
        userLcoation = intent.getStringExtra("UserLocation");
        userconstellation = intent.getStringExtra("UserConstellation");
        userIntroduction = intent.getStringExtra("UserIntroduction");
        icondata = intent.getByteArrayExtra("IconData");

        BackBtn = (ImageView) findViewById(R.id.backBtn3);
        UserIcon = (ImageView) findViewById(R.id.head_portrait1);
        UserSex = (TextView) findViewById(R.id.UserSex);
        UserName = (TextView) findViewById(R.id.Username);
        UserEmail = (TextView) findViewById(R.id.email);
        UserConstellation = (TextView) findViewById(R.id.UserConstellation);
        UserIntroduction = (TextView) findViewById(R.id.UserIntroduction);
        LogOut = (Button) findViewById(R.id.Logout);

        BackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //用户头像
        if (icondata != null) {
            Bitmap Icondata = BitmapFactory.decodeByteArray(icondata, 0, icondata.length);
            UserIcon.setImageBitmap(Icondata);
        } else {
            ImageSize mImageSize = new ImageSize(100, 100);//指定图片大小
            ImageLoader.getInstance().loadImage(murl, mImageSize, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view,
                                              Bitmap loadedImage) {
                    super.onLoadingComplete(imageUri, view, loadedImage);
                    UserIcon.setImageBitmap(loadedImage);
                }

            });
        }
        UserIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageUtils.showImagePickDialog(PersonalInformation.this);

            }
        });

        //用户性别
        UserSex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PersonalInformation.this);
                builder.setTitle(R.string.lunch_information_title_gender);
                if ("zh".equals(language)) {
                    final String[] sex = {" 男", " 女"};
                    builder.setSingleChoiceItems(sex, 1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            UserSex.setText(sex[which]);
                        }
                    });
                } else {
                    final String[] sex = {" MAN", " WOMAN"};
                    builder.setSingleChoiceItems(sex, 1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            UserSex.setText(sex[which]);
                        }
                    });
                }
                builder.setPositiveButton(R.string.dialog_btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        });
        UserSex.setText(usersex);

        //用户姓名
        UserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PersonalInformation.this, InformationEditor.class);
                i.putExtra("userid", userid);
                i.putExtra("number", 0);
                startActivityForResult(i, 0);
            }
        });
        UserName.setText(username);


        //用户邮箱
        UserEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PersonalInformation.this, InformationEditor.class);
                i.putExtra("number", 1);
                i.putExtra("userid", userid);
                startActivityForResult(i, 1);
            }
        });
        UserEmail.setText(userEmail);


        //用户地域
        UserLocation = (TextView) findViewById(R.id.UserLocation);
        UserLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PersonalInformation.this, InformationEditor.class);
                i.putExtra("number", 2);
                i.putExtra("userid", userid);
                startActivityForResult(i, 2);
            }
        });
        UserLocation.setText(userLcoation);

        //用户星座
        UserConstellation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PersonalInformation.this);
                builder.setTitle(R.string.lunch_information_title_constellation);
                if ("zh".equals(language)) {
                    final String[] constellation = {"白羊座", "狮子座", "双子座", "巨蟹座", "狮子座", "处女座",
                            "天秤座", "天蝎座", "射手座", "摩羯座", "水平座", "双鱼座"};
                    builder.setSingleChoiceItems(constellation, 1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            UserConstellation.setText(constellation[which]);
                        }
                    });
                } else {
                    final String[] constellation = {"ARIES", "TAURUS", "GEMINI", "CANCER", "LEO", "VIRGO", "LIBRA", "SCORPIO",
                            "SAGITTARIUS", "CAPRICORN", "AQUARIUS", "PISCES"};
                    builder.setSingleChoiceItems(constellation, 1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            UserConstellation.setText(constellation[which]);
                        }
                    });
                }
                builder.setPositiveButton(R.string.dialog_btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setNegativeButton(R.string.dialog_btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
        UserConstellation.setText(userconstellation);

        //个性签名
        UserIntroduction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PersonalInformation.this, InformationEditor.class);
                i.putExtra("number", 3);
                i.putExtra("userid", userid);
                startActivityForResult(i, 3);
            }
        });
        UserIntroduction.setText(userIntroduction);

        //退出登陆
        LogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HexMiniApplication.sharedApplicaion().setLogout(true);
                new LogoutAPI(PersonalInformation.this, Constants.APP_KEY,
                        AccessTokenKeeper.readAccessToken(PersonalInformation.this)).logout(mLogoutListener);
                UserIcon.setImageResource(R.drawable.head_portrait);
                UserName.setText("");
                UserEmail.setText("");
                UserLocation.setText("");
                UserIntroduction.setText("");
                Intent i = new Intent(PersonalInformation.this, LaunchActivity.class);
                setResult(RESULT_OK, i);
                finish();
            }
        });
    }


    //微博退出登陆
    private class LogOutRequestListener implements RequestListener {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                try {
                    JSONObject obj = new JSONObject(response);
                    String value = obj.getString("result");
                    if ("true".equalsIgnoreCase(value)) {
                        AccessTokenKeeper.clear(PersonalInformation.this);
                        AccessTokenKeeper.clear(getApplicationContext());
                        Toast.makeText(PersonalInformation.this, R.string.lunch_information_logout_exit, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("**PersonalInformation", " 抛出异常");
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(PersonalInformation.this, "退出失败", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        LeanCloudData();
        Intent i = new Intent(PersonalInformation.this, LaunchActivity.class);
        setResult(RESULT_OK, i);
        finish();
    }

    private void LeanCloudData() {
        final AVQuery query1 = new AVQuery("UserMess");
        query1.whereMatches("UserID", userid);
        Log.i("PersonalInformation", "userid: " + userid);
        query1.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(final AVObject avObject, AVException e) {
                if (avObject != null) {
                    leancloudid = avObject.getObjectId();
                    Log.i("PersonalInformation", "leancloudid: " + leancloudid);

                    //更新数据
                    avObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            avObject.put("UserIcon", murl);
                            avObject.put("UserName", UserName.getText().toString().trim());
                            avObject.put("UserSex", UserSex.getText().toString().trim());
                            avObject.put("UserEmail", UserEmail.getText().toString().trim());
                            avObject.put("UserConstellation", UserConstellation.getText().toString().trim());
                            avObject.put("UserIntroduction", UserIntroduction.getText().toString().trim());
                            avObject.put("UserLocation", UserLocation.getText().toString().trim());
                            avObject.saveInBackground();
                        }
                    });
                }
                //创建数据列表
                if (leancloudid == null) {
                    final AVObject userMess = new AVObject("UserMess");
                    userMess.put("UserIcon", murl);
                    userMess.put("UserName", username);
                    userMess.put("UserSex", UserSex.getText().toString().trim());
                    userMess.put("UserEmail", UserEmail.getText().toString().trim());
                    userMess.put("UserConstellation", UserConstellation.getText().toString().trim());
                    userMess.put("UserIntroduction", UserIntroduction.getText().toString().trim());
                    userMess.put("UserLocation", UserLocation.getText().toString().trim());
                    userMess.put("UserID", userid);
                    userMess.put("IconData", null);
                    userMess.saveInBackground();
                }
            }

        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("**PersonalInformation", "-----onStop");
        Uri imageUri = mImageUtils.getCurrentUri();
        //储存编辑框里的信息
        SharedPreferences sharedPreferences = getSharedPreferences("personalinformation", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (imageUri != null) {
            editor.putString("userico", String.valueOf(imageUri));
        } else {
            editor.putString("userico", murl);
        }
        editor.putString("username", UserName.getText().toString().trim());
        editor.putString("usersex", UserSex.getText().toString().trim());
        editor.putString("useremail", UserEmail.getText().toString().trim());
        editor.putString("userlocation", UserLocation.getText().toString().trim());
        editor.putString("userconstellation", UserConstellation.getText().toString().trim());
        editor.putString("userintroduction", UserIntroduction.getText().toString().trim());
        editor.commit();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //接收InformationEditor传回的消息
        if (requestCode == 3) {
            information = data.getStringExtra("information");
            UserIntroduction.setText(information);
        } else if (requestCode == 1) {
            information = data.getStringExtra("information");
            UserEmail.setText(information);
        } else if (requestCode == 2) {
            information = data.getStringExtra("information");
            UserLocation.setText(information);
        } else if (requestCode == 0) {
            information = data.getStringExtra("information");
            UserName.setText(information);
        }

        switch (requestCode) {
            case mImageUtils.REQUEST_CODE_FROM_ALBUM: {
                if (resultCode == RESULT_CANCELED) {   //取消操作
                    return;
                }
                Uri imageUri = data.getData();
                mImageUtils.copyImageUri(this, imageUri);
                mImageUtils.cropImageUri(this, mImageUtils.getCurrentUri(), 200, 200);
                break;
            }
            case mImageUtils.REQUEST_CODE_FROM_CAMERA: {

                if (resultCode == RESULT_CANCELED) {     //取消操作

                    // mImageUtils.deleteImageUri(this, mImageUtils.getCurrentUri());   //删除Uri
                    return;
                }
                mImageUtils.cropImageUri(this, mImageUtils.getCurrentUri(), 200, 200);
                break;
            }
            case mImageUtils.REQUEST_CODE_CROP: {

                if (resultCode == RESULT_CANCELED) {     //取消操作
                    return;
                }
                Uri imageUri = mImageUtils.getCurrentUri();
                myFilePath = UriUtils.getRealFilePath(this, imageUri);//获取图片路径

                if (imageUri != null) {
                    UserIcon.setImageURI(imageUri);
                }

                final AVQuery query1 = new AVQuery("UserMess");
                query1.whereMatches("UserID", userid);
                Log.i("PersonalInformation", "userid: " + userid);
                query1.getFirstInBackground(new GetCallback<AVObject>() {
                    @Override
                    public void done(final AVObject avObject, AVException e) {
                        if (avObject != null) {
                            leancloudid = avObject.getObjectId();
                            Log.i("PersonalInformation", "leancloudid: " + leancloudid);

                            //更新数据
                            avObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {

                                    if (myFilePath != null) {
                                        Log.i("PersonalInformation", "myfilepath:" + myFilePath);
                                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                                        Bitmap bitmap = BitmapFactory.decodeFile(myFilePath, bmOptions);
                                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                        byte[] data = stream.toByteArray();
                                        avObject.put("IconData", data);
                                    }
                                    avObject.saveInBackground();
                                }
                            });
                        }
                    }
                });
                break;
            }
            default:
                break;
        }

        if (LogOut instanceof LoginoutButton) {
            ((LoginoutButton) LogOut).onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
