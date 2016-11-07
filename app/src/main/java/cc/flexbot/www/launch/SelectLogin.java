package cc.flexbot.www.launch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.User;
import com.sina.weibo.sdk.utils.LogUtil;

import cc.flexbot.www.R;
import cc.flexbot.www.weibo.AccessTokenKeeper;
import cc.flexbot.www.weibo.Constants;

/**
 * Created by Administrator on 2016/4/19.
 */
public class SelectLogin extends Activity {

    private ImageView FacebookView;
    private ImageView SinaView;
    private Oauth2AccessToken mAccessToken;
    private SsoHandler mSsoHandler;
    private AuthInfo mAuthInfo;
    private UsersAPI mUsersAPI;
    private static final String TAG = "weibo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("**SelectLogin", "-----onCreate");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.select_login);

        mAuthInfo = new AuthInfo(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        mSsoHandler = new SsoHandler(SelectLogin.this, mAuthInfo);

        FacebookView = (ImageView) findViewById(R.id.facebook);
        FacebookView.setOnClickListener(listener);
        SinaView = (ImageView) findViewById(R.id.sina);
        SinaView.setOnClickListener(listener);
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.sina:
                    mSsoHandler.authorize(new AuthListener());//sso授权（ALL IN ONE）
                    break;
                case R.id.facebook:
                    break;
            }
        }
    };

    class AuthListener implements WeiboAuthListener {
        @Override
        public void onComplete(Bundle values) {       // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken != null && mAccessToken.isSessionValid()) {
                AccessTokenKeeper.writeAccessToken(SelectLogin.this, mAccessToken); // 保存 Token 到 SharedPreferences
                // Toast.makeText(SelectLogin.this, "授权成功", Toast.LENGTH_SHORT).show();
            } else {
                String code = values.getString("code");
                String message = "授权失败";
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                Toast.makeText(SelectLogin.this, "授权失败", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(SelectLogin.this,
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCancel() {
            // Toast.makeText(SelectLogin.this,
            //     "取消授权", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(SelectLogin.this, LaunchActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
    }

    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                // 调用 User#parse 将JSON串解析成User对象
                User user = User.parse(response);
                if (user != null) {
                    String murl = user.avatar_large;//获取头像180*180
                    String mUserName = user.screen_name;
                    String mUserID = user.id;
                    Intent intent = new Intent(SelectLogin.this, LaunchActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString("HeadPortrait", murl);
                    extras.putString("UserName", mUserName);
                    extras.putString("UserID", mUserID);
                    intent.putExtras(extras);
                    setResult(1, intent);
                    finish();
                    //Toast.makeText(SelectLogin.this,"获取User信息成功，用户昵称：" + user.id,Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SelectLogin.this, response, Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            LogUtil.e(TAG, e.getMessage());
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(SelectLogin.this, info.toString(), Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        Log.i("&&Personalwei", " " + mAccessToken);
        mUsersAPI = new UsersAPI(this, Constants.APP_KEY, mAccessToken);
        try {
            if (mAccessToken.getUid() != null) {
                long uid = Long.parseLong(mAccessToken.getUid());
                mUsersAPI.show(uid, mListener);
            }
        } catch (Exception e) {
            //Toast.makeText(SelectLogin.this, "登陆异常", Toast.LENGTH_SHORT).show();
        }
    }
}
