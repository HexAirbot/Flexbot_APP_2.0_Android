package cc.flexbot.www.launch;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import cc.flexbot.www.R;

/**
 * Created by Administrator on 2016/3/8.
 */
public class HelpviewDetail extends Activity {

    private ImageView imageViews;
    private String language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.help_detail);

        imageViews = (ImageView) findViewById(R.id.mImageview);
        language = Locale.getDefault().getLanguage();

        Intent intent = getIntent();
        int number1 = intent.getIntExtra("helpView_detail", 1);
        int number2 = intent.getIntExtra("helpView_detail", 2);
        int number3 = intent.getIntExtra("helpView_detail", 3);
        int number4 = intent.getIntExtra("helpView_detail", 4);
        int number5 = intent.getIntExtra("helpView_detail", 5);

        if (number1 == 1) {
            try {
                if ("zh".equals(language)) {
                    SelectImageBitmap("help_detail_01.png");
                } else {
                    SelectImageBitmap("help_detail_en_01.png");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (number2 == 2) {
            try {
                if ("zh".equals(language)) {
                    SelectImageBitmap("help_detail_02.png");
                } else {
                    SelectImageBitmap("help_detail_en_02.png");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (number3 == 3) {

            try {
                if ("zh".equals(language)) {
                    SelectImageBitmap("help_detail_03.png");
                } else {
                    SelectImageBitmap("help_detail_en_03.png");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (number4 == 4) {
            try {
                if ("zh".equals(language)) {
                    SelectImageBitmap("help_detail_04.png");
                } else {
                    SelectImageBitmap("help_detail_en_04.png");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (number5 == 5) {
            try {
                if ("zh".equals(language)) {
                    SelectImageBitmap("help_detail_05.png");
                } else {
                    SelectImageBitmap("help_detail_en_05.png");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void SelectImageBitmap(String fileName) throws IOException {
        InputStream inputStreamF = getAssets().open(fileName);
        //获得图片的宽、高
        BitmapFactory.Options tmpOptions = new BitmapFactory.Options();
        tmpOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStreamF, null, tmpOptions);
        int width = tmpOptions.outWidth;
        int height = tmpOptions.outHeight;
        Log.i("ImageBitmap", "width: " + width + "height: " + height);
        tmpOptions.inJustDecodeBounds = false;
        Bitmap bmp = BitmapFactory.decodeStream(inputStreamF, null, tmpOptions);
        imageViews.setImageBitmap(bmp);
    }


}

