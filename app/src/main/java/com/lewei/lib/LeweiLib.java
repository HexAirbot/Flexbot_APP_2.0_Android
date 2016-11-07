package com.lewei.lib;

import android.graphics.Bitmap;

public class LeweiLib
{
	public static final String FOLDER_PATH = "/H264Demo/Remote";
	public static int HD_flag = 0;
	private OnTcpListener tcpListener;

	public LeweiLib(OnTcpListener tcpListener)
	{
		// TODO Auto-generated constructor stub
		this.tcpListener = tcpListener;
	}

	static
	{
		System.loadLibrary("lewei");
	}
	
	
	//stream
	public native static int LW93StartLiveStream(int interval, int hd_flag);

	public native static void LW93StopLiveStream();

	public native static int LW93StartRecordReplay(String name, int start, int end, int interval);

	public native static void LW93StopRecordReplay();

	public native static void LW93ChangeRecordReplayAttr(String name, int start, int end);

	public native static int LW93GetCurrTimestamp();

	public native static int LW93DrawBitmapFrame(Bitmap bmp);

	// for download file
	public static native String LW93StartDownloadFile(String folder, String file_name, int interval);

	public static native void LW93StopDownloadFile();

	// command
	public static native int LW93SendGetRemoteTime();

	public static native int LW93SendSetRemoteTime();

	public static native String LW93SendCapturePhoto(String folder);

	public static native RecList[] LW93SendGetRecList();
	
	public static native PicList[] LW93SendGetPicList();

	public static native int LW93SendDeleteFile(String path);

	public static native int LW93SendSdcardFormat();

	public static native int LW93SendChangeWifiName(String name);

	public static native int LW93SendChangeWifiPassword(String password);

	public static native int LW93SendChangeChannel(int channel);

	public static native int LW93SendResetWifi();

	public static native int LW93SendRebootWifi();

	public static native int LW93SendGetCameraFlip();

	public static native int LW93SendSetCameraFlip(int flip);

	// serial setting
	public static native int LW93SendGetBaudrate();

	public static native int LW93SendSetBaudrate(int baudrate);

	// udp data to serial
	public static native int LW93InitUdpSocket();

	public static native void LW93CloseUdpSocket();

	public static native int LW93SendUdpData(byte[] data, int size);

	public static native byte[] LW93RecvUdpData();

	// tcp data to serial
	public void LW93TcpConnected()
	{ 
		tcpListener.TcpConnected();
	}

	public void LW93TcpDisconnected()
	{
		tcpListener.TcpDisconnected();
	}

	public void LW93TcpReceived(byte[] data, int size)
	{
		tcpListener.TcpReceive(data, size);
	}
	
	public native int LW93StartTcpThread();
	
	public native void LW93StopTcpThread();
	
	public native int LW93SendTcpData(byte[] data, int size);

	/**
	 * @return 1: recording, 0: not recording, -1: error
	 */
	public static native int LW93SendGetRecPlan();

	public static native int LW93SendChangeRecPlan(int flag);

	// local record
	public static native int LW93StartLocalRecord(String path, int frameRate);

	public static native int LW93StopLocalRecord();

	// ��JNI���ȡһЩ����
	public static native int getSdcardStatus();

	public static native int getDownloadFileSize();

	public static native int getDownloadRecvSize();

	public static native int getFrameWidth();

	public static native int getFrameHeight();
}
