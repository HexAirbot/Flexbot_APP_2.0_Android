package com.example.network;

public class HandlerParams
{
	public static final int START_STREAM = 0x00;
	public static final int GET_FIRST_FRAME = 0x01;
	public static final int UPDATE_RECORDTIME = 0x02;
	
	public static final int RECORD_START_OK = 0x11;
	public static final int RECORD_START_FAIL = 0x12;
	public static final int RECORD_STOP = 0x13;
}
