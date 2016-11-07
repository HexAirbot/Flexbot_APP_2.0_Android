package cc.flexbot.www;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.lewei.lib.LeweiLib;
import com.lewei.lib.RecList;
import com.mjpegdemo.images.SdcardVideosAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SdcardVideoActivity extends Activity {

    private GridView mGridView;
    private SdcardVideosAdapter mAdapter;

    private GetSdcardVideoList getSdcardVideoList;

    public static RecList[] videoLists;
    private List<String> videoList = new ArrayList<String>();

    private DownloadTask downloadTask;

    private boolean isStop = false;
    private boolean running = false;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.hud_view_gallery);

        init();
        getSdcardVideoList = new GetSdcardVideoList();
        getSdcardVideoList.execute(1);
    }

    private void init() {
        mGridView = (GridView) findViewById(R.id.gd_Videos);

        mGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                // TODO Auto-generated method stub
                showDialogVideoOperation(position);

            }
        });

        mGridView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View v, int position, long arg3) {
                // TODO Auto-generated method stub
                // Intent intent = new Intent(SdcardVideoActivity.this,
                // ReplayActivity.class);
                // intent.putExtra("position", position);
                // startActivity(intent);

                return true;
            }
        });
    }

    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case 0:
                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                        // Log.d("sdcard video", "progress " + msg.arg1);
                        mProgressDialog.setProgress(msg.arg1);
                        if (msg.arg1 == 100) {

                            mProgressDialog.dismiss();
                        }
                    }

                    break;

                default:
                    break;
            }
            return true;
        }
    });

    private void startUpdateProgess() {
        if (!running) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    int file_size, recv_size;
                    running = true;
                    isStop = false;
                    try {
                        while (!isStop) {
                            file_size = LeweiLib.getDownloadFileSize();

                            recv_size = LeweiLib.getDownloadRecvSize();

                            if (recv_size > 0) {
                                Message msg = new Message();
                                msg.what = 0;
                                msg.arg1 = (int) ((float) recv_size / (float) file_size * 100);
                                // Log.e("sdcard video", recv_size + " " +
                                // file_size);
                                handler.sendMessage(msg);
                            }

                            Thread.sleep(200);

                        }
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private void showDialogVideoOperation(final int position) {
        final AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this).setIcon(android.R.drawable.btn_star).setTitle("��ʾ").setPositiveButton("����", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                String path = videoList.get(position);
                int index = path.lastIndexOf('/');
                String absPath = Environment.getExternalStorageDirectory().toString() + LeweiLib.FOLDER_PATH + path.substring(index); // ���ֻ����ļ��ľ���·��
                File file = new File(absPath);

                if (file != null && file.exists()) {
                    if (absPath.endsWith(".mp4")) {
                        Uri uri = Uri.fromFile(file);
                        // // 调用系统自带的播放器
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Log.v("URI:::::::::", uri.toString());
                        intent.setDataAndType(uri, "video/*");
                        startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(SdcardVideoActivity.this, ReplayActivity.class);
                    intent.putExtra("position", position);

                    startActivity(intent);
                }
            }
        }).setNeutralButton("下载", new DialogInterface.OnClickListener()

        {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                downloadTask = new DownloadTask();
                downloadTask.execute(videoList.get(position));

                mProgressDialog = new ProgressDialog(SdcardVideoActivity.this);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setTitle("downloading now...");
                mProgressDialog.setMax(100);
                mProgressDialog.setProgress(0);
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.show();
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        // TODO Auto-generated method stub
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            downloadTask.cancel(true);
                            LeweiLib.LW93StopDownloadFile();
                            mProgressDialog.dismiss();
                            isStop = true;
                            running = false;
                            Toast.makeText(SdcardVideoActivity.this, "取消下载", Toast.LENGTH_LONG).show();
                        }
                        return false;
                    }
                });

                startUpdateProgess();
            }
        }).setNegativeButton("ɾ��", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                if (LeweiLib.LW93SendDeleteFile(videoList.get(position)) == 0) {
                    videoList.remove(position);
                    mAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(SdcardVideoActivity.this, "文件不存在", Toast.LENGTH_LONG).show();
                }
            }
        });
        mDialogBuilder.create().show();
        mDialogBuilder.create().setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                // TODO Auto-generated method stub
                mDialogBuilder.create().dismiss();
            }
        });

    }

    public class GetSdcardVideoList extends AsyncTask<Integer, Integer, RecList[]> {

        @Override
        protected RecList[] doInBackground(Integer... params) {
            // TODO Auto-generated method stub
            videoLists = LeweiLib.LW93SendGetRecList();
            return videoLists;
        }

        @Override
        protected void onPostExecute(RecList[] result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (result != null) {
                for (int i = 0; i < result.length; i++) {
                    videoList.add(result[i].file_name);
                    Log.i("get sdcard videos", result[i].file_name + "  " + result[i].record_start_time + "  " + result[i].record_time);
                }
                mAdapter = new SdcardVideosAdapter(SdcardVideoActivity.this, videoList, mGridView);
                mGridView.setAdapter(mAdapter);
            }
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            videoList.clear();
        }
    }

    public class DownloadTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String folder = Environment.getExternalStorageDirectory().toString() + LeweiLib.FOLDER_PATH;
            File folderFile = new File(folder);
            if (!folderFile.exists())
                folderFile.mkdirs();
            String path = LeweiLib.LW93StartDownloadFile(folder, params[0], 1);
            return path;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            mAdapter.notifyDataSetChanged();
            mProgressDialog.dismiss();
            if (result != null) {
                Toast.makeText(SdcardVideoActivity.this, "下载完成", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(SdcardVideoActivity.this, "下载失败", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            // TODO Auto-generated method stub
            super.onCancelled();
            Log.e("", "cancel task");
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // TODO Auto-generated method stub
            super.onProgressUpdate(values);
        }
    }
}
