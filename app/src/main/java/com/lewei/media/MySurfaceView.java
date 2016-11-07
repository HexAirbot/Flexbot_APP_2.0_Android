package com.lewei.media;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.lewei.config.PathConfig;
import com.lewei.lib.LeweiLib;

import java.io.InputStream;



public class MySurfaceView extends SurfaceView implements Callback
{
	private Context context;
	private SurfaceHolder sfh;
	private Canvas canvas;
	private Paint p;
	InputStream inputstream = null;
	private static int mScreenWidth;
	private static int mScreenHeight;
	private boolean isStop = false;
	private Rect rect;
	private Handler handler;

	private PathConfig pathConfig;

	private boolean need_take_photo = false;
	private boolean is_recording_now = false;

	private Bitmap bitmap;
	private boolean has_image = false;

	public MySurfaceView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initialize();
		p = new Paint();

		p.setAntiAlias(true);
		sfh = this.getHolder();
		sfh.addCallback(this);
		this.setKeepScreenOn(true);
		setFocusable(true);
		this.getWidth();
		this.getHeight();

		this.context = context;
		pathConfig = new PathConfig(context);

	}

	public void setHandler(Handler handler)
	{
		this.handler = handler;
	}


	public void startMySurface()
	{
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				Paint pt = new Paint();

				isStop = false;
				int ret = -1;
				int flag = -1;
				int times = 0;
				boolean has_create_bmp = false;
				boolean first_get_frame = true;
				long time = System.currentTimeMillis();
				long time_sum = 0;
				Bitmap bmp = null;
				// Bitmap bmp = Bitmap.createBitmap(640, 480, Config.ARGB_8888);

				// startH264Stream(url, 1280, 720);
//				handler.sendEmptyMessage(HandlerParams.START_STREAM);

				startDrawBmpThread();

				while (!isStop)
				{
					// long time = System.currentTimeMillis();
					flag = LeweiLib.LW93StartLiveStream(1, LeweiLib.HD_flag = 0);
					if (flag > 0)
					{
						while (!isStop)
						{
							// time = System.currentTimeMillis();
							ret = LeweiLib.LW93DrawBitmapFrame(bmp);

							if (!has_create_bmp)
							{
								if (LeweiLib.getFrameHeight() > 0 && LeweiLib.getFrameWidth() > 0)
								{
									Log.e("", "w " + LeweiLib.getFrameWidth() + " h " + LeweiLib.getFrameHeight());
									bmp = Bitmap.createBitmap(LeweiLib.getFrameWidth(), LeweiLib.getFrameHeight(), Config.ARGB_8888);
									has_create_bmp = true;
								}
							}

							// time = System.currentTimeMillis();
							// bmp = BitmapFactory.decodeByteArray(data, 0,
							// data.length);

							// Log.d("MySurfaceView",
							// Integer.toString(data.length)
							// + "  " +
							// (System.currentTimeMillis() - time));

							if (ret > 0)
							{
								time = System.currentTimeMillis();
								
								
								
								if (first_get_frame)
								{
//									handler.sendEmptyMessage(HandlerParams.GET_FIRST_FRAME);
									first_get_frame = false;
								}

								Log.d("", "ret > 0, frameWidth = " + LeweiLib.getFrameWidth());
								
								if (LeweiLib.getFrameWidth() > 1000)
								{
									if (!has_image)
									{
										setBitmap(bmp);
										has_image = true;
									}
								} else
								{
									canvas = sfh.lockCanvas(rect);
									if (canvas == null)
										continue;
									pt = new Paint();
									pt.setAntiAlias(true);
									canvas.drawBitmap(bmp, null, rect, pt);

									if (sfh != null)
										sfh.unlockCanvasAndPost(canvas);
								}
								// time = System.currentTimeMillis();

								// if (times++ < 200)
								// {
								// time_sum += System.currentTimeMillis() -
								// time;
								// Log.d("MySurfaceView", "Time " +
								// (System.currentTimeMillis() - time) +
								// "  sum " + time_sum);
								//
								// }
								// Log.d("time", "10");
								if (need_take_photo)
								{
									Log.i("surface", "take photo");
									pathConfig.savePhoto(bmp);
									need_take_photo = false;
								}

								/*
								 * pt.setColor(Color.BLUE); pt.setTextSize(20);
								 * pt.setStrokeWidth(1);
								 * pt.setTypeface(Typeface.SERIF);
								 * canvas.drawText(str_fps, 5, 20, pt);
								 */

							} else if (ret == 0)
							{
								msleep(1);
							} else
							{
								msleep(1);
								continue;
							}
						}
					} else
					{
						msleep(200);
					}

				}
				isStop = true;

				// stopH264Stream();
				LeweiLib.LW93StopLiveStream();

			}
		}).start();
	}

	private void initialize()
	{
		DisplayMetrics dm = getResources().getDisplayMetrics();
		mScreenWidth = dm.widthPixels;
		mScreenHeight = dm.heightPixels;
		rect = new Rect(0, 0, mScreenWidth, mScreenHeight);
		this.setKeepScreenOn(true);// ������Ļ����
	}

	private void startDrawBmpThread()
	{
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				while (!isStop)
				{
					if (has_image)
					{
						// Log.d("", "got a bitmap");
						if (bitmap != null)
						{
							canvas = sfh.lockCanvas(rect);
							if (canvas == null)
								return;

							canvas.drawBitmap(bitmap, null, rect, p);

							if (sfh != null)
								sfh.unlockCanvasAndPost(canvas);

							has_image = false;
						}

					} else
					{
						try
						{
							Thread.sleep(5);
						} catch (InterruptedException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}).start();
	}

	public void setBitmap(final Bitmap bmp)
	{
		this.bitmap = bmp;
	}

	private void msleep(int ms)
	{
		try
		{
			Thread.sleep(ms);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{
	}

	public void surfaceDestroyed(SurfaceHolder holder)
	{
		isStop = true;
	}

	public void surfaceCreated(SurfaceHolder holder)
	{
		// new DrawVideo().start();

	}

	public void stop()
	{
		isStop = true;
		LeweiLib.LW93StopLocalRecord();
	}

	public void takePhoto()
	{
		Log.i("**********", "*************need_take_photo");
		need_take_photo = true;
	} 

	public void takeRecord()
	{
		int ret;
		if (!is_recording_now)
		{
			String recpath = pathConfig.getVideoPath();
			ret = LeweiLib.LW93StartLocalRecord(recpath, 20);
			if (ret > 0)
			{
				is_recording_now = true;
				
//				handler.sendEmptyMessage(HandlerParams.RECORD_START_OK);
			} 
//			else
//			{
//				handler.sendEmptyMessage(HandlerParams.RECORD_START_FAIL);
//			}
		}
		else
		{
			LeweiLib.LW93StopLocalRecord();
			
//			handler.sendEmptyMessage(HandlerParams.RECORD_STOP);
			is_recording_now = false;
		}
	}



}
