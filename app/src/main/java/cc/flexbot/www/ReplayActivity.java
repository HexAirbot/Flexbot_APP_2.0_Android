package cc.flexbot.www;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.example.network.HandlerParams;
import com.lewei.lib.LeweiLib;
import com.lewei.media.ReplaySurfaceView;

public class ReplayActivity extends Activity {
    private ReplaySurfaceView replaySurfaceView;
    private int position;

    private boolean isStop = false;
    private boolean sendChangeAttr = false;


    private int videoTime;
    private String videoTimeString;
    private String currTimeString;

    private ImageView iv_Replay_Top_Back;
    private TextView txt_Replay_Top_Title;
    private TextView txt_Replay_Bottom_Time;
    private SeekBar seekBar_Replay_Play;

    //for change record attr
    private String videoAttrName;
    private int videoAttrStart;
    private int videoAttrEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.hud_view_gallery_replay);

        Intent intent = this.getIntent();
        position = intent.getIntExtra("position", 0);

        widgetInit();

        replaySurfaceView = (ReplaySurfaceView) findViewById(R.id.replaySurfaceView);
        replaySurfaceView.setHandler(handler);
        replaySurfaceView.startMySurface(SdcardVideoActivity.videoLists[position].file_name, SdcardVideoActivity.videoLists[position].record_start_time,
                SdcardVideoActivity.videoLists[position].record_start_time + SdcardVideoActivity.videoLists[position].record_time);

        startGetCurrTimestamp();
    }

    private void widgetInit() {

        iv_Replay_Top_Back = (ImageView) findViewById(R.id.iv_replay_top_back);
        txt_Replay_Top_Title = (TextView) findViewById(R.id.txt_replay_top_title);
        txt_Replay_Bottom_Time = (TextView) findViewById(R.id.txt_replay_bottom_time);
        seekBar_Replay_Play = (SeekBar) findViewById(R.id.seekbar_replay_play);

        iv_Replay_Top_Back.setOnClickListener(clickListener);
        txt_Replay_Top_Title.setText(SdcardVideoActivity.videoLists[position].file_name);
        // Log.e("", "name " +
        // SdcardVideoActivity.videoLists[position].file_name);
        seekBar_Replay_Play.setMax(SdcardVideoActivity.videoLists[position].record_time - 1);
        videoTime = SdcardVideoActivity.videoLists[position].record_time - 1;
        int time_h = videoTime / 3600;
        int time_m = (videoTime % 3600) / 60;
        int time_s = videoTime % 60;
        if (time_h > 0) {
            videoTimeString = String.format("%02d", time_h) + ":" + String.format("%02d", time_m) + ":" + String.format("%02d", time_s);
        } else {
            videoTimeString = String.format("%02d", time_m) + ":" + String.format("%02d", time_s);
        }

        seekBar_Replay_Play.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                //videoAttrName = SdcardVideoActivity.videoLists[position].file_name;
                //videoAttrStart = seekBar.getProgress();
                //videoAttrEnd = SdcardVideoActivity.videoLists[position].record_start_time + SdcardVideoActivity.videoLists[position].record_time;
                //sendChangeAttr = true;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub

            }
        });
    }

    private OnClickListener clickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.iv_replay_top_back:

                    break;

                default:
                    break;
            }
        }
    };

    public Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case HandlerParams.START_STREAM:

                    break;
                case HandlerParams.GET_FIRST_FRAME:

                    break;

                case HandlerParams.UPDATE_RECORDTIME: {
                    int timestamp = (Integer) msg.obj;
                    // Log.d("", "time " + timestamp);
                    seekBar_Replay_Play.setProgress(timestamp);

                    int time_h = timestamp / 3600;
                    int time_m = (timestamp % 3600) / 60;
                    int time_s = timestamp % 60;
                    if (time_h > 0) {
                        currTimeString = String.format("%02d", time_h) + ":" + String.format("%02d", time_m) + ":" + String.format("%02d", time_s);
                    } else {
                        currTimeString = String.format("%02d", time_m) + ":" + String.format("%02d", time_s);
                    }
                    txt_Replay_Bottom_Time.setText(currTimeString + "/" + videoTimeString);
                }
                break;
                default:
                    break;
            }
            return false;
        }
    });

    private void startGetCurrTimestamp() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (!isStop) {
                    int timestamp = LeweiLib.LW93GetCurrTimestamp();
                    Message msg = new Message();
                    msg.what = HandlerParams.UPDATE_RECORDTIME;
                    msg.obj = timestamp / 1000;
                    handler.sendMessage(msg);
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    if (sendChangeAttr) {
                        LeweiLib.LW93ChangeRecordReplayAttr(videoAttrName, videoAttrStart, videoAttrEnd);
                        sendChangeAttr = false;
                    }
                }
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        isStop = true;
        replaySurfaceView.stop();
        finish();
    }

}
