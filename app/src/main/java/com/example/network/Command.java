package com.example.network;

import com.lewei.lib.LeweiLib;
import com.lewei.lib.OnTcpListener;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class Command implements OnTcpListener
{
	private static final String TAG = "Command";

	public static final short LEWEI_CMD_ERROR = 0x00;
	public static final short LEWEI_CMD_HEARTBIT = 0x01;
	public static final short LEWEI_CMD_STARTVIDEO = 0x03;
	public static final short LEWEI_CMD_STOPVIDEO = 0x04;
	public static final short LEWEI_CMD_SETTIME = 0x05;
	public static final short LEWEI_CMD_GETTIME = 0x06;

	public static final short GET_REMOTETIME_FAIL = 0x30;
	public static final short SET_REMOTETIME_FAIL = 0x31;
	public static final short GET_RECPLAN_FAIL = 0x32;
	public static final short GET_RECPLAN_NOT_RECORD = 0x33;
	public static final short GET_RECPLAN_RECORDING = 0x34;
	public static final short SET_RECPLAN_FAIL = 0x35;
	public static final short SET_RECPLAN_START = 0x36;
	public static final short SET_RECPLAN_STOP = 0x37;
	public static final short SEND_CAPTURE_PHOTO = 0x38;

	private Handler handler;
	private boolean isStop = false;
	private boolean isSerialInit = false;
	byte[] data = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h' };

	private LeweiLib mLeweiLib = new LeweiLib(this);

	public Command(Handler handler)
	{
		this.handler = handler;
	}

	public void stopThread()
	{
		isStop = true;
		mLeweiLib.LW93StopTcpThread();
	}

	public void getRecordPlan()
	{
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				while (!isStop)
				{
					int ret = LeweiLib.LW93SendGetRecPlan();
					if (ret > 0)
					{
						handler.sendEmptyMessage(GET_RECPLAN_RECORDING);
					} else if (ret == 0)
					{
						handler.sendEmptyMessage(GET_RECPLAN_NOT_RECORD);
					} else
					{
						handler.sendEmptyMessage(GET_RECPLAN_FAIL);
					}
					try
					{
						Thread.sleep(2000);
					} catch (InterruptedException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	public void takeSdcardRecord()
	{
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				int ret;
				ret = LeweiLib.LW93SendSetRemoteTime();
				if (ret <= 0)
				{
					handler.sendEmptyMessage(SET_REMOTETIME_FAIL);
					return;
				}
				ret = LeweiLib.LW93SendGetRecPlan();
				if (ret < 0)
				{
					handler.sendEmptyMessage(GET_RECPLAN_FAIL);
				} else if (ret == 0)
				{
					ret = LeweiLib.LW93SendChangeRecPlan(1);
					if (ret <= 0)
						handler.sendEmptyMessage(SET_RECPLAN_FAIL);
					else
						handler.sendEmptyMessage(SET_RECPLAN_START);
				} else
				{
					ret = LeweiLib.LW93SendChangeRecPlan(0);
					handler.sendEmptyMessage(SET_RECPLAN_STOP);
				}

			}
		}).start();
	}

//	public void startSdcardCapture()
//	{
//		new Thread(new Runnable()
//		{
//
//			@Override
//			public void run()
//			{
//				// TODO Auto-generated method stub
//				String folder = Environment.getExternalStorageDirectory().toString() + LeweiLib.FOLDER_PATH;
//				String file_name = LeweiLib.LW93SendCapturePhoto(folder);
//				Message msg = new Message();
//				msg.what = SEND_CAPTURE_PHOTO;
//				msg.obj = file_name;
//				handler.sendMessage(msg);
//			}
//		}).start();
//	}

	public void startUdpSendThread()
	{
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				isStop = false;
				int ret = 0;
				try
				{
					while (!isStop)
					{
						if (!isSerialInit)
						{
							if (LeweiLib.LW93SendGetBaudrate() != 57600)
							{
								ret = LeweiLib.LW93SendSetBaudrate(57600);
								if (ret != 0)
								{
									Thread.sleep(500);
									continue;
								}
							}
							if (LeweiLib.LW93InitUdpSocket() == 0)
							{
								isSerialInit = true;
							} else
							{
								Thread.sleep(500);

							}
						} else
						// ��ʼ���ɹ�
						{
							LeweiLib.LW93SendUdpData(data, data.length);
							Thread.sleep(500);
						}
					}
					LeweiLib.LW93CloseUdpSocket();
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void startUdpRecvThread()
	{
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				isStop = false;
				try
				{
					while (!isStop)
					{
						byte[] rbuf = LeweiLib.LW93RecvUdpData();
						if (rbuf == null || rbuf.length == 0)
						{
							Thread.sleep(100);
							continue;
						}

						Log.d("Receive data", new String(rbuf));
					}
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void startTcpSendThread()
	{
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				isStop = false;
				int ret = 0;
				try
				{
					while (!isStop)
					{
						if (!isSerialInit)
						{
							if (LeweiLib.LW93SendGetBaudrate() != 57600)
							{
								ret = LeweiLib.LW93SendSetBaudrate(57600);
								if (ret != 0)
								{
									Thread.sleep(500);
									continue;
								}
							} else
							{
								isSerialInit = true;
							}

						} else
						// ��ʼ���ɹ�
						{
							mLeweiLib.LW93SendTcpData(data, data.length);
							Thread.sleep(500);
						}
					}
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();

	}

	public void startTcpRecvThread()
	{
		mLeweiLib.LW93StartTcpThread();
	}

	@Override
	public void TcpConnected()
	{
		// TODO Auto-generated method stub
		Log.e(TAG, "TcpConnected");
	}

	@Override
	public void TcpDisconnected()
	{
		// TODO Auto-generated method stub
		Log.e(TAG, "TcpDisconnected");
	}

	@Override
	public void TcpReceive(byte[] data, int size)
	{
		// TODO ��������յ�����ͨ��TCPת������������
		Log.e(TAG, "TcpReceive  " + new String(data) + "  size" + size);
	}

}
