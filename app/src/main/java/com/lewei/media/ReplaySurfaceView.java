package com.lewei.media;

import java.io.InputStream;

import com.example.network.HandlerParams;
import com.lewei.lib.LeweiLib;

import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder.Callback;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class ReplaySurfaceView extends SurfaceView implements Callback
{
	private SurfaceHolder sfh;
	private Canvas canvas;
	private Paint p;
	InputStream inputstream = null;
	private static int mScreenWidth;
	private static int mScreenHeight;
	private boolean isStop = false;
	private Rect rect;
	private Handler handler;


	private Bitmap bitmap;
	private boolean has_image = false;


	public ReplaySurfaceView(Context context, AttributeSet attrs)
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
	}

	public void setHandler(Handler handler)
	{
		this.handler = handler;
	}

	public void startMySurface(final String name, final int start, final int end)
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
				boolean has_create_bmp = false;
				boolean first_get_frame = true;
				//long time = System.currentTimeMillis();
				Bitmap bmp = null;

				handler.sendEmptyMessage(HandlerParams.START_STREAM);

				startDrawBmpThread();

				while (!isStop)
				{
					// long time = System.currentTimeMillis();
					flag = LeweiLib.LW93StartRecordReplay(name, start, end, 1);
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
									bmp = Bitmap.createBitmap(1280, 720, Config.ARGB_8888);
									has_create_bmp = true;
								}
							}

							if (ret > 0)
							{
								//time = System.currentTimeMillis();
								if (first_get_frame)
								{
									handler.sendEmptyMessage(HandlerParams.GET_FIRST_FRAME);
									first_get_frame = false;
								}

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
				LeweiLib.LW93StopRecordReplay();

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
	}

}
